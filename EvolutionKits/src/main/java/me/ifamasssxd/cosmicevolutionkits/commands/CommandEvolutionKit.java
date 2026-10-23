/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.NumberUtils
 *  com.cosmicpvp.cosmicutils.utils.PlayerUtil
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.ifamasssxd.cosmicevolutionkits.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NumberUtils;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import java.util.Arrays;
import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.menu.EvolutionKitMenu;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandEvolutionKit
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.isOp()) {
            if (args.length >= 2 && args[0].equalsIgnoreCase("giveluckybox")) {
                String username = args[1];
                Player player = Bukkit.getPlayer((String)username);
                if (player == null) {
                    sender.sendMessage(CC.Red + "No online player found!");
                    return true;
                }
                Integer level = 1;
                if (args.length == 3) {
                    level = NumberUtils.parseIntOrNull((String)args[2]);
                }
                if (level == null) {
                    level = 1;
                }
                PlayerUtil.giveOrDropProtectedItem((Player)player, (ItemStack)EvolutionKits.getLuckyBox(level, true), (int)-1);
                sender.sendMessage(CC.Green + " Lucky box added!");
                return true;
            }
            if (args.length == 2) {
                String action = args[0];
                String pName = args[1];
                Player pl = Bukkit.getPlayer((String)pName);
                if (pl == null) {
                    sender.sendMessage(ChatColor.RED + "Unable to find player!");
                    return true;
                }
                KitPlayer kitplayer = KitPlayer.kitPlayerMap.get(pl.getUniqueId());
                if (kitplayer.isLoadingData()) {
                    sender.sendMessage(CC.Red + "Please wait while their data is loaded!");
                    return true;
                }
                if (action.equalsIgnoreCase("resetlevels")) {
                    kitplayer.getKitLevels().clear();
                    CosmicEvolutionKits.get().getSqlQueryThread().addQuery("UPDATE " + CosmicEvolutionKits.get().getServerName() + "_evolutionKitData SET kitLevel = 1 WHERE uuid = '" + pl.getUniqueId() + "';");
                    sender.sendMessage(ChatColor.RED + "Reset " + pName + "'s Levels.");
                } else {
                    kitplayer.getKitCooldowns().clear();
                    CosmicEvolutionKits.get().getSqlQueryThread().addQuery("UPDATE " + CosmicEvolutionKits.get().getServerName() + "_evolutionKitData SET lastKitUse = 0 WHERE uuid = '" + pl.getUniqueId() + "';");
                    sender.sendMessage(ChatColor.RED + "Cleared " + pName + "'s Cooldowns.");
                }
                return true;
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("givegem")) {
                    Player pl = Bukkit.getPlayer((String)args[1]);
                    if (pl == null) {
                        sender.sendMessage(ChatColor.RED + "No player found.");
                        return true;
                    }
                    EvolutionKits kit = EvolutionKits.getFromName(args[2]);
                    if (kit == null) {
                        sender.sendMessage(ChatColor.RED + "Invalid Evolution Kit given: ");
                        for (EvolutionKits kits : EvolutionKits.values()) {
                            sender.sendMessage(ChatColor.RED + kits.name());
                        }
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Gem given to " + pl.getName());
                    pl.getInventory().addItem(new ItemStack[]{kit.getClaimableItem()});
                    return true;
                }
                if (args[0].equalsIgnoreCase("givelevelupgem") || args[0].equalsIgnoreCase("givelevelgem")) {
                    Player pl = Bukkit.getPlayer((String)args[1]);
                    if (pl == null) {
                        sender.sendMessage(ChatColor.RED + "No player found.");
                        return true;
                    }
                    EvolutionKits kit = EvolutionKits.getFromName(args[2]);
                    if (kit == null) {
                        sender.sendMessage(ChatColor.RED + "Invalid Evolution Kit given: " + Arrays.toString((Object[])EvolutionKits.values()));
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Level Up Gem given to " + pl.getName());
                    pl.getInventory().addItem(new ItemStack[]{kit.getLevelUpItem(true)});
                    return true;
                }
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only bro!");
            return true;
        }
        Player player = (Player)sender;
        KitPlayer kitplayer = KitPlayer.kitPlayerMap.get(player.getUniqueId());
        if (kitplayer == null || kitplayer.isLoadingData()) {
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "Please wait while your /vkit data is loaded!");
            return true;
        }
        new EvolutionKitMenu(player).openGUI(CosmicEvolutionKits.get());
        return false;
    }
}

