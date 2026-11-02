package net.astropvp.kitreset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * /kitreset <kit|all> <player|all>: resets kit cooldowns in every kit system at once - EssentialsX kits (/kit apollo ...),
 * CosmicGlobalKits gkits and mkits, and CosmicEvolutionKits vkits - for one player or everyone (online and offline).
 * Kit names are matched case-insensitively (spaces/underscores ignored); a name used by several systems resets all of them.
 * Each kit system lives in its own helper class, so a missing plugin never breaks the others.
 */
public class AstroKitReset extends JavaPlugin implements TabExecutor {
    static final String PERMISSION = "astrokitreset.use";

    /** one kit in one kit system */
    interface Kit {
        String name();

        /** reset this kit for these players (null = everyone, online and offline) */
        void reset(Collection<UUID> players);
    }

    /** a kit system: its kits, and resetting all of them */
    interface KitSystem {
        List<Kit> kits();

        void resetAll(Collection<UUID> players);
    }

    private final List<KitSystem> systems = new ArrayList<KitSystem>();

    @Override
    public void onEnable() {
        getCommand("kitreset").setExecutor(this);
        getCommand("kitreset").setTabCompleter(this);
        // after every plugin is enabled, so the kit plugins are ready
        Bukkit.getScheduler().runTask(this, () -> {
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                this.systems.add(new EssentialsKits());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("CosmicGlobalKits")) {
                this.systems.add(new GlobalKitsSystem());
                this.systems.add(new MasteryKitsSystem());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("CosmicEvolutionKits")) {
                this.systems.add(new EvolutionKitsSystem());
            }
            getLogger().info("Kit systems: " + this.systems.size() + " (" + allKitNames().size() + " kits)");
        });
    }

    static String key(String name) {
        return ChatColor.stripColor(name).toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private List<Kit> findKits(String name) {
        List<Kit> found = new ArrayList<Kit>();
        String key = key(name);
        for (KitSystem system : this.systems) {
            for (Kit kit : system.kits()) {
                if (key(kit.name()).equals(key)) {
                    found.add(kit);
                }
            }
        }
        return found;
    }

    private Set<String> allKitNames() {
        Set<String> names = new LinkedHashSet<String>();
        for (KitSystem system : this.systems) {
            for (Kit kit : system.kits()) {
                names.add(ChatColor.stripColor(kit.name()));
            }
        }
        return names;
    }

    @SuppressWarnings("deprecation")
    private static UUID findPlayer(String name) {
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            return online.getUniqueId();
        }
        UUID fromEssentials = Bukkit.getPluginManager().isPluginEnabled("Essentials") ? EssentialsKits.findUser(name) : null;
        if (fromEssentials != null) {
            return fromEssentials;
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        return offline != null && offline.hasPlayedBefore() ? offline.getUniqueId() : null;
    }

    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(color("&c/" + label + " <kit|all> <player|all>"));
            return true;
        }
        boolean allKits = args[0].equalsIgnoreCase("all");
        boolean everyone = args[1].equalsIgnoreCase("all");
        List<Kit> kits = allKits ? Collections.<Kit>emptyList() : findKits(args[0]);
        if (!allKits && kits.isEmpty()) {
            sender.sendMessage(color("&c&l(!) &cUnknown kit: &f" + args[0]));
            return true;
        }
        UUID target = everyone ? null : findPlayer(args[1]);
        if (!everyone && target == null) {
            sender.sendMessage(color("&c&l(!) &cPlayer &f" + args[1] + " &cnot found."));
            return true;
        }
        Collection<UUID> players = everyone ? null : Collections.singletonList(target);
        if (allKits) {
            for (KitSystem system : this.systems) {
                system.resetAll(players);
            }
        } else {
            for (Kit kit : kits) {
                kit.reset(players);
            }
        }
        String kitName = allKits ? "all" : ChatColor.stripColor(kits.get(0).name());
        String who;
        if (everyone) {
            who = "everyone";
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(target);
            who = p.getName() != null ? p.getName() : args[1];
        }
        sender.sendMessage(color("&a&l(!) &aReset the &f" + kitName + " &akit cooldown for &f" + who + "&a."));
        String told = color("&a&l(!) &aYour &f" + kitName + " &akit cooldown has been reset!");
        if (everyone) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(told);
            }
        } else {
            Player p = Bukkit.getPlayer(target);
            if (p != null) {
                p.sendMessage(told);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PERMISSION) || args.length > 2) {
            return Collections.emptyList();
        }
        List<String> options = new ArrayList<String>();
        options.add("all");
        if (args.length == 1) {
            options.addAll(allKitNames());
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                options.add(p.getName());
            }
        }
        String typed = args[args.length - 1].toLowerCase();
        List<String> out = new ArrayList<String>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(typed) && !out.contains(option)) {
                out.add(option);
            }
        }
        Collections.sort(out, String.CASE_INSENSITIVE_ORDER);
        return out;
    }
}
