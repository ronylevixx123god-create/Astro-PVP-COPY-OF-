package net.astropvp.core.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import net.astropvp.core.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 * Command tab completion for players who aren't OP: only commands they can use are suggested, and arguments are only
 * completed for those commands. OPs get the server's normal completion. Running a command is not affected.
 * <p>
 * A command is usable when it declares a permission the player has, when it is an Essentials command and the player has
 * essentials.&lt;command&gt;, when it is a mattstudios command whose @Default @Permission the player has (or that has
 * none), or when tabcomplete.yml lists it under "public" or maps it to a permission the player has. Anything else, and
 * every plugin:command name, is not suggested.
 */
public class TabCompleteFilter {

    private final AstroCore plugin;
    private Set<String> publicCommands = new HashSet<>();
    private Map<String, String> commandPermissions = new HashMap<>();
    private Set<String> hiddenCommands = new HashSet<>();
    private final Map<Command, Boolean> mfOpenCache = new WeakHashMap<>();
    private final Map<Command, List<String>> mfPermissionCache = new WeakHashMap<>();

    public TabCompleteFilter(AstroCore plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load() {
        File file = new File(this.plugin.getDataFolder(), "tabcomplete.yml");
        if (!file.exists()) this.plugin.saveResource("tabcomplete.yml", false);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> pub = new HashSet<>();
        for (String s : config.getStringList("public")) pub.add(s.toLowerCase(Locale.ROOT));
        Map<String, String> perms = new HashMap<>();
        if (config.isConfigurationSection("permissions"))
            for (String key : config.getConfigurationSection("permissions").getKeys(false)) perms.put(key.toLowerCase(Locale.ROOT), config.getString("permissions." + key));
        Set<String> hidden = new HashSet<>();
        for (String s : config.getStringList("hidden")) hidden.add(s.toLowerCase(Locale.ROOT));
        this.publicCommands = pub;
        this.commandPermissions = perms;
        this.hiddenCommands = hidden;
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPlayer() == null || event.getPlayer().isOp()) return;
                try {
                    String message = event.getPacket().getStrings().read(0);
                    // argument completion: only for commands the player may use
                    if (message.startsWith("/") && message.contains(" ")) {
                        String label = message.substring(1, message.indexOf(' '));
                        if (!TabCompleteFilter.this.canUse(event.getPlayer(), label)) event.setCancelled(true);
                    }
                } catch (FieldAccessException e) {
                    TabCompleteFilter.this.plugin.getLogger().log(Level.SEVERE, "tab complete request", e);
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;
                try {
                    String[] suggestions = event.getPacket().getStringArrays().read(0);
                    List<String> kept = new ArrayList<>(suggestions.length);
                    for (String suggestion : suggestions) {
                        if (TabCompleteFilter.this.keep(player, suggestion)) kept.add(suggestion);
                    }
                    if (kept.size() != suggestions.length) event.getPacket().getStringArrays().write(0, kept.toArray(new String[0]));
                } catch (FieldAccessException e) {
                    TabCompleteFilter.this.plugin.getLogger().log(Level.SEVERE, "tab complete reply", e);
                }
            }
        });
    }

    private boolean keep(Player player, String suggestion) {
        if (suggestion.startsWith("/")) return player.isOp() || this.canUse(player, suggestion.substring(1));
        // a player name suggestion: staff hidden with arkkit.staff.hidden stay hidden from everyone else
        Player named = Bukkit.getPlayerExact(suggestion);
        return named == null || named == player || player.hasPermission("arkkit.staff.hidden") || !named.hasPermission("arkkit.staff.hidden");
    }

    public boolean canUse(Player player, String label) {
        label = label.toLowerCase(Locale.ROOT);
        if (label.isEmpty() || label.contains(":") || this.hiddenCommands.contains(label)) return false;
        Command command = ((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(label);
        if (command == null) return false;
        String name = command.getName().toLowerCase(Locale.ROOT);
        if (this.hiddenCommands.contains(name)) return false;
        if (command.getPermission() != null && !command.getPermission().isEmpty()) return command.testPermissionSilent(player);
        if (this.commandPermissions.containsKey(name)) return player.hasPermission(this.commandPermissions.get(name));
        if (this.publicCommands.contains(name)) return true;
        if (command instanceof PluginCommand && ((PluginCommand) command).getPlugin().getName().equals("Essentials"))
            return player.hasPermission("essentials." + name);
        if (command.getClass().getSimpleName().equals("CommandHandler")) return this.mfCanUse(player, command);
        return false;
    }

    // mattstudios mf: the CommandBase methods carry @Default / @SubCommand and @Permission
    private boolean mfCanUse(Player player, Command command) {
        if (!this.mfPermissionCache.containsKey(command)) this.readMf(command);
        if (this.mfOpenCache.getOrDefault(command, false)) return true;
        for (String permission : this.mfPermissionCache.getOrDefault(command, Collections.emptyList())) {
            if (player.hasPermission(permission)) return true;
        }
        return false;
    }

    private void readMf(Command command) {
        boolean open = false;
        List<String> permissions = new ArrayList<>();
        try {
            for (Field field : command.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(command);
                Collection<?> items = value instanceof Map ? ((Map<?, ?>) value).values() : value instanceof Collection ? (Collection<?>) value : Collections.singletonList(value);
                for (Object item : items) {
                    if (item == null) continue;
                    Object base = item;
                    try {
                        Field baseField = item.getClass().getDeclaredField("commandBase");
                        baseField.setAccessible(true);
                        base = baseField.get(item);
                    } catch (NoSuchFieldException ignored) {
                    }
                    if (base == null) continue;
                    for (Method method : base.getClass().getDeclaredMethods()) {
                        boolean isCommand = false;
                        String[] needed = null;
                        for (Annotation annotation : method.getAnnotations()) {
                            String type = annotation.annotationType().getSimpleName();
                            if (type.equals("Default") || type.equals("SubCommand")) isCommand = true;
                            if (type.equals("Permission")) needed = (String[]) annotation.annotationType().getMethod("value").invoke(annotation);
                        }
                        if (!isCommand) continue;
                        if (needed == null || needed.length == 0) open = true;
                        else permissions.addAll(Arrays.asList(needed));
                    }
                }
            }
        } catch (ReflectiveOperationException | ClassCastException e) {
            this.plugin.getLogger().log(Level.WARNING, "tab complete: could not read the permissions of /" + command.getName(), e);
        }
        this.mfOpenCache.put(command, open);
        this.mfPermissionCache.put(command, permissions);
    }
}
