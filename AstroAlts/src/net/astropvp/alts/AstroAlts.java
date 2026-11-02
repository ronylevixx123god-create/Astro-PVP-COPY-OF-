package net.astropvp.alts;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * /alts: accounts that joined from the same IP(s), network-wide (every server records joins in the shared DB).
 * The IP is the player's real address as forwarded by the proxy (bungeecord mode + BungeeGuard; backends only
 * listen on 127.0.0.1). A shared IP is only a possible link: this plugin never punishes anyone.
 */
public class AstroAlts extends JavaPlugin implements TabExecutor, Listener {

    static final String PERM_USE = "astroalts.use";
    static final String PERM_IP = "astroalts.ip";
    static final String PERM_ALERTS = "astroalts.alerts";

    private AltStore store;
    private ExecutorService db;
    private String server;
    private int maxIps;
    private long alertCooldown;
    private volatile long lastAlertId = -1;
    private boolean rankErrorLogged;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration c = getConfig();
        this.server = c.getString("server-name", "").trim();
        if (this.server.isEmpty()) {
            this.server = luckPermsServer();
        }
        this.maxIps = Math.max(1, c.getInt("max-ips-per-account", 10));
        this.alertCooldown = TimeUnit.MINUTES.toMillis(Math.max(0, c.getInt("alert-cooldown-minutes", 10)));
        this.store = new AltStore(c.getString("database.host", "127.0.0.1"), c.getInt("database.port", 3306),
                c.getString("database.name", "astropvp"), c.getString("database.user", ""), c.getString("database.password", ""));
        this.db = Executors.newSingleThreadExecutor();

        final List<Player> online = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        final List<String[]> joins = new ArrayList<String[]>();
        for (Player p : online) {
            joins.add(joinData(p));
        }
        this.db.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    store.createTables();
                    store.clearOnline(server);
                    long now = System.currentTimeMillis();
                    for (String[] j : joins) {
                        store.recordJoin(UUID.fromString(j[0]), j[1], j[2], j[3], server, now, maxIps);
                    }
                    store.pruneAlerts(now - TimeUnit.DAYS.toMillis(1));
                    lastAlertId = store.lastAlertId();
                    getLogger().info("Connected; this server records joins as '" + server + "'.");
                } catch (Exception e) {
                    getLogger().log(Level.SEVERE, "Database setup failed", e);
                }
            }
        });

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("alts").setExecutor(this);
        getCommand("alts").setTabCompleter(this);

        int banSeconds = Math.max(5, c.getInt("ban-sync-seconds", 15));
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                publishBans();
            }
        }, 40L, banSeconds * 20L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                pollAlerts();
            }
        }, 60L, Math.max(10, c.getInt("poll-ticks", 40)));
    }

    @Override
    public void onDisable() {
        if (this.db == null) {
            return;
        }
        final List<UUID> online = new ArrayList<UUID>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            online.add(p.getUniqueId());
        }
        this.db.execute(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (UUID u : online) {
                    try {
                        store.recordQuit(u, server, now);
                    } catch (Exception ignored) {
                        // cleared by clearOnline on next start
                    }
                }
                store.close();
            }
        });
        this.db.shutdown();
        try {
            this.db.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private String luckPermsServer() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                return net.luckperms.api.LuckPermsProvider.get().getServerName();
            } catch (Throwable ignored) {
                // fall through
            }
        }
        return "port-" + Bukkit.getPort();
    }

    /** uuid, name, rank, ip (main thread). */
    private String[] joinData(Player p) {
        InetSocketAddress addr = p.getAddress();
        String ip = addr == null || addr.getAddress() == null ? null : addr.getAddress().getHostAddress();
        return new String[] {p.getUniqueId().toString(), p.getName(), rankOf(p), ip};
    }

    private String rankOf(Player p) {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return null;
        }
        try {
            net.luckperms.api.LuckPerms lp = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = lp.getUserManager().getUser(p.getUniqueId());
            if (user == null) {
                return null;
            }
            String groupName = user.getPrimaryGroup();
            net.luckperms.api.model.group.Group group = lp.getGroupManager().getGroup(groupName);
            String display = group == null ? null : group.getDisplayName();
            String rank = ChatColor.stripColor(color(display != null ? display : groupName));
            if (rank.equalsIgnoreCase("default")) {
                rank = "Default";
            }
            return rank.length() > 32 ? rank.substring(0, 32) : rank;
        } catch (Throwable t) {
            if (!this.rankErrorLogged) {
                this.rankErrorLogged = true;
                getLogger().log(Level.WARNING, "Could not read ranks from LuckPerms", t);
            }
            return null;
        }
    }

    // ---------------------------------------------------------------- joins / quits / alerts

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        final String[] j = joinData(event.getPlayer());
        this.db.execute(new Runnable() {
            @Override
            public void run() {
                UUID uuid = UUID.fromString(j[0]);
                long now = System.currentTimeMillis();
                try {
                    store.recordJoin(uuid, j[1], j[2], j[3], server, now, maxIps);
                    List<AltStore.Account> flagged = new ArrayList<AltStore.Account>();
                    for (AltStore.Account acc : store.alts(uuid)) {
                        if (acc.banned || acc.muted) {
                            flagged.add(acc);
                        }
                    }
                    // a server switch is a new join on another server: alert once per cooldown, not per switch
                    if (!flagged.isEmpty() && !store.alertedSince(uuid, now - alertCooldown)) {
                        StringBuilder names = new StringBuilder();
                        for (AltStore.Account acc : flagged) {
                            if (names.length() > 0) {
                                names.append("&7, ");
                            }
                            names.append(acc.banned ? "&c" : "&e").append(acc.name);
                        }
                        store.addAlert(uuid, "&c&l(!) &f" + j[1] + " &7may be an alt of &f" + names, now);
                    }
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Could not record join of " + j[1], e);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.db.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    store.recordQuit(uuid, server, System.currentTimeMillis());
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Could not record quit", e);
                }
            }
        });
    }

    private void publishBans() {
        final List<String> names = new ArrayList<String>();
        Date now = new Date();
        for (BanEntry entry : Bukkit.getBanList(BanList.Type.NAME).getBanEntries()) {
            if (entry.getTarget() != null && (entry.getExpiration() == null || entry.getExpiration().after(now))) {
                names.add(entry.getTarget().toLowerCase(Locale.ENGLISH));
            }
        }
        this.db.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    store.publishBans(server, names);
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Could not publish the ban list", e);
                }
            }
        });
    }

    private void pollAlerts() {
        if (this.lastAlertId < 0) {
            return;
        }
        this.db.execute(new Runnable() {
            @Override
            public void run() {
                final Map<Long, String> alerts;
                try {
                    alerts = store.alertsAfter(lastAlertId);
                } catch (Exception e) {
                    return;
                }
                if (alerts.isEmpty()) {
                    return;
                }
                for (Long id : alerts.keySet()) {
                    lastAlertId = Math.max(lastAlertId, id);
                }
                Bukkit.getScheduler().runTask(AstroAlts.this, new Runnable() {
                    @Override
                    public void run() {
                        for (String msg : alerts.values()) {
                            String text = color(msg);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission(PERM_ALERTS)) {
                                    p.sendMessage(text);
                                }
                            }
                            getLogger().info(ChatColor.stripColor(text));
                        }
                    }
                });
            }
        });
    }

    // ---------------------------------------------------------------- /alts

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERM_USE)) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(color("&c/" + label + " <player>" + (sender.hasPermission(PERM_IP) ? " [ip]" : "")));
            return true;
        }
        final String query = args[0];
        final boolean showIps = args.length == 2 && args[1].equalsIgnoreCase("ip") && sender.hasPermission(PERM_IP);
        if (args.length == 2 && !showIps) {
            sender.sendMessage(color("&c/" + label + " <player>"));
            return true;
        }
        this.db.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final AltStore.Account target = store.findByName(query);
                    if (target == null) {
                        reply(sender, Collections.<BaseComponent[]>singletonList(
                                TextComponent.fromLegacyText(color("&c&l(!) &cNo player named &f" + query + " &chas joined yet."))));
                        return;
                    }
                    reply(sender, showIps ? ipLines(target, store.ips(target.uuid)) : altLines(target, store.alts(target.uuid)));
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "/alts " + query + " failed", e);
                    reply(sender, Collections.<BaseComponent[]>singletonList(
                            TextComponent.fromLegacyText(color("&c&l(!) &cCould not read the alt database, try again."))));
                }
            }
        });
        return true;
    }

    private void reply(final CommandSender sender, final List<BaseComponent[]> lines) {
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                for (BaseComponent[] line : lines) {
                    if (sender instanceof Player) {
                        if (((Player) sender).isOnline()) {
                            ((Player) sender).spigot().sendMessage(line);
                        }
                    } else {
                        sender.sendMessage(TextComponent.toLegacyText(line));
                    }
                }
            }
        });
    }

    private List<BaseComponent[]> altLines(AltStore.Account target, List<AltStore.Account> alts) {
        List<BaseComponent[]> lines = new ArrayList<BaseComponent[]>();
        lines.add(TextComponent.fromLegacyText(color("&6&lAlts of " + target.name + " &8(" + alts.size() + ")")));
        if (alts.isEmpty()) {
            lines.add(TextComponent.fromLegacyText(color("&7No other account has joined from the same IP.")));
            return lines;
        }
        List<BaseComponent> line = new ArrayList<BaseComponent>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < alts.size(); i++) {
            AltStore.Account acc = alts.get(i);
            if (i > 0) {
                Collections.addAll(line, TextComponent.fromLegacyText(color("&7, ")));
            }
            String colour = acc.banned ? "&c" : acc.muted ? "&e" : acc.onlineServer != null ? "&a" : "&7";
            StringBuilder hover = new StringBuilder("&f").append(acc.name)
                    .append("\n&7Rank: &f").append(acc.rank == null ? "Unknown" : acc.rank)
                    .append("\n&7Last seen: &f").append(acc.onlineServer != null ? "Online now (" + acc.onlineServer + ")" : ago(now - acc.lastSeen));
            if (acc.banned) {
                hover.append("\n&cBanned");
            }
            if (acc.muted) {
                hover.append("\n&eMuted");
            }
            hover.append("\n\n&7Click to check &f").append(acc.name).append("&7's alts");
            for (BaseComponent part : TextComponent.fromLegacyText(color(colour + acc.name))) {
                part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(color(hover.toString()))));
                part.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/alts " + acc.name));
                line.add(part);
            }
        }
        lines.add(line.toArray(new BaseComponent[line.size()]));
        lines.add(TextComponent.fromLegacyText(color("&8A shared IP is a possible link, not proof.")));
        return lines;
    }

    private List<BaseComponent[]> ipLines(AltStore.Account target, List<AltStore.IpUse> ips) {
        List<BaseComponent[]> lines = new ArrayList<BaseComponent[]>();
        lines.add(TextComponent.fromLegacyText(color("&6&lIPs of " + target.name + " &8(" + ips.size() + ")")));
        long now = System.currentTimeMillis();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (AltStore.IpUse use : ips) {
            lines.add(TextComponent.fromLegacyText(color("&f" + use.ip + " &7- last used &f" + ago(now - use.lastSeen)
                    + " &7(first " + date.format(new Date(use.firstSeen)) + ") &8" + use.accounts
                    + (use.accounts == 1 ? " account" : " accounts"))));
        }
        return lines;
    }

    static String ago(long ms) {
        long s = Math.max(0, ms / 1000);
        if (s < 60) {
            return "just now";
        }
        long m = s / 60, h = m / 60, d = h / 24;
        if (d > 0) {
            return d + "d " + (h % 24) + "h ago";
        }
        if (h > 0) {
            return h + "h " + (m % 60) + "m ago";
        }
        return m + "m ago";
    }

    static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if (!sender.hasPermission(PERM_USE)) {
            return out;
        }
        String prefix = args[args.length - 1].toLowerCase(Locale.ENGLISH);
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                    out.add(p.getName());
                }
            }
        } else if (args.length == 2 && sender.hasPermission(PERM_IP) && "ip".startsWith(prefix)) {
            out.add("ip");
        }
        Collections.sort(out, String.CASE_INSENSITIVE_ORDER);
        return out;
    }
}
