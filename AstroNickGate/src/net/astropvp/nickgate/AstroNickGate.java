package net.astropvp.nickgate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Blocks /nick (and its Essentials aliases) for players without essentials.nick
 * with a "you need Nebula" message instead of Essentials' generic no-permission text.
 * Who may nick is decided purely by LuckPerms (essentials.nick / .color / .others).
 */
public class AstroNickGate extends JavaPlugin implements Listener {

    static final Set<String> NICK_COMMANDS = new HashSet<String>(Arrays.asList(
            "nick", "enick", "nickname", "enickname"));

    static final String DENIED = ChatColor.translateAlternateColorCodes('&',
            "&c&l(!) &cYou need the &dNebula &crank or higher to use /nick.");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    /** "/Essentials:NICK foo" -> "nick" */
    static String label(String message) {
        String cmd = message.trim();
        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }
        int space = cmd.indexOf(' ');
        if (space >= 0) {
            cmd = cmd.substring(0, space);
        }
        int colon = cmd.lastIndexOf(':');
        if (colon >= 0) {
            cmd = cmd.substring(colon + 1);
        }
        return cmd.toLowerCase(Locale.ENGLISH);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!NICK_COMMANDS.contains(label(event.getMessage()))) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("essentials.nick")) {
            event.setCancelled(true);
            player.sendMessage(DENIED);
        }
    }
}
