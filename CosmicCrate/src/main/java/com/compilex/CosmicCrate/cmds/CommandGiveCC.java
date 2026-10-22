// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.cmds;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import com.compilex.CosmicCrate.api.CCAPI;
import com.compilex.CosmicCrate.libs.Version;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class CommandGiveCC implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage("/givecc <version> <player>");
            return true;
        }
        final String version = args[0];
        final String playerName = args[1];
        final ItemStack i = CCAPI.getCosmicCrate(Version.valueOf(version.toUpperCase()), playerName);
        if (i == null || Bukkit.getPlayer(playerName) == null) {
            return true;
        }
        final Player p = Bukkit.getPlayer(playerName);
        Bukkit.getLogger().info("[CosmicCrate (GiveCC)] Giving " + version.toUpperCase() + " /cc to " + p.getName() + " (" + p.getUniqueId() + ")");
        if (p.getInventory().firstEmpty() != -1) {
            p.getInventory().addItem(i);
            return true;
        }
        p.getWorld().dropItem(p.getLocation(), i);
        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) Your inventory is full, the /cc has been dropped on the floor!");
        return true;
    }
}
