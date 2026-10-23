/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.massivecraft.factions.Faction
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.ifamasssxd.cosmicfactionboosters.commands;

import com.massivecraft.factions.Faction;
import me.ifamasssxd.cosmicfactionboosters.CosmicFactionBoosters;
import me.ifamasssxd.cosmicfactionboosters.struct.FactionBoosters;
import me.ifamasssxd.cosmicfactionboosters.utils.FactionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCheckBooster
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        Faction faction = FactionUtils.getFaction(player);
        if (faction == null || !faction.isNormal()) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use that command!");
            return true;
        }
        FactionBoosters boosters = CosmicFactionBoosters.get().getBoosterManager().getFactionBooster(faction);
        if (boosters == null || boosters.isEmpty()) {
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Your faction does not have any active boosters!");
            return true;
        }
        CosmicFactionBoosters.get().getBoosterManager().showActiveBoosters(player, boosters);
        return false;
    }
}

