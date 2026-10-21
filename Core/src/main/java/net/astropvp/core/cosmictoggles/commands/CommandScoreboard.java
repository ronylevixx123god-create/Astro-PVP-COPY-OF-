package net.astropvp.core.cosmictoggles.commands;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import net.astropvp.core.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandScoreboard implements CommandExecutor {
    public static String SCOREBOARD_TOGGLE_DATA = "toggle_scoreboard";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        List<UUID> toggles = AstroCore.getInstance().getToggleManager().getScoreboardToggles();
        if (toggles.contains(player.getUniqueId())) {
            toggles.remove(player.getUniqueId());
            player.removeMetadata(SCOREBOARD_TOGGLE_DATA, AstroCore.getInstance());
            setScoreboardVisible(player, true);
            player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "Faction scoreboard toggled " + ChatColor.GREEN + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "ON");
        } else {
            toggles.add(player.getUniqueId());
            AstroCore.getInstance().getToggleManager().clearScoreboard(player);
            setScoreboardVisible(player, false);
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Faction scoreboard toggled " + ChatColor.RED + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "OFF");
        }
        return true;
    }

    // the faction scoreboard is drawn by TAB
    public static void setScoreboardVisible(Player player, boolean visible) {
        if (!Bukkit.getPluginManager().isPluginEnabled("TAB") || TabAPI.getInstance().getScoreboardManager() == null) return;
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer != null)
            TabAPI.getInstance().getScoreboardManager().setScoreboardVisible(tabPlayer, visible, false);
    }
}
