package net.astropvp.core.titles.listeners;

import net.astropvp.core.titles.sql.TitleDatabaseManager;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Keeps the cached unlocked titles in step with LuckPerms: titles are the titles.title.<name> permissions.
 * NodeMutateEvent is a LuckPerms event, so it is subscribed on LuckPerms' event bus (Bukkit's registerEvents
 * rejected it and the cache never updated).
 */
public class PermissionListener {
    private final TitleDatabaseManager dbM;

    public PermissionListener(TitleDatabaseManager dm) {
        this.dbM = dm;
    }

    public void register(Plugin plugin) {
        LuckPermsProvider.get().getEventBus().subscribe(plugin, NodeMutateEvent.class, this::onPermissionChange);
    }

    public void onPermissionChange(NodeMutateEvent event) {
        if (!(event.getTarget() instanceof User)) return;
        User user = (User) event.getTarget();
        // rebuilt from the user's permissions the next time /title is opened
        this.dbM.getUnlockedTitles().remove(user.getUniqueId());
        if (event instanceof NodeAddEvent && ((NodeAddEvent) event).getNode().getKey().toLowerCase().startsWith("titles.title.")) {
            Player player = Bukkit.getPlayer(user.getUniqueId());
            if (player != null)
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ">> " + ChatColor.GRAY + "Your available titles have updated. Use " + ChatColor.YELLOW + ChatColor.BOLD + "/title" + ChatColor.GRAY + " to view them.");
        }
    }
}
