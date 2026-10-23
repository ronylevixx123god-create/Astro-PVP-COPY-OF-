package me.cosmicpvp.CosmicGlobalKits.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import java.util.Arrays;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKits;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKitsAPI;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import me.cosmicpvp.CosmicGlobalKits.menu.MKitMenu;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandMKit
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player pl = (Player)sender;
            GlobalKitUser kitUser = CosmicGlobalKits.getPlugin().getGlobalKitUser(pl.getUniqueId());
            if (kitUser == null || kitUser.isLoadingData()) {
                pl.sendMessage(CC.RedB + "(!) " + CC.Red + "Please wait while your Mastery Kit data is loaded!");
                pl.sendMessage(CC.Gray + "Allow up to 15 seconds before attempting to relog.");
                return true;
            }
            if (args.length == 0 || !sender.isOp()) {
                new MKitMenu(pl).openGUI(CosmicGlobalKits.getPlugin());
            }
        }
        if (sender.isOp() && args.length > 0) {
            Player player;
            if (args[0].equalsIgnoreCase("givegem") && args.length == 3) {
                player = Bukkit.getPlayer((String)args[1]);
                if (player == null) {
                    sender.sendMessage(CC.Red + "Invalid player!");
                    return true;
                }
                MasteryKits kits = MasteryKits.getFromName(args[2].toUpperCase());
                if (kits == null) {
                    sender.sendMessage(CC.Red + "Invalid Mastery Kit: " + Arrays.toString(MasteryKits.values()));
                    return true;
                }
                PlayerUtil.giveOrDropProtectedItem((Player)player, (ItemStack)CosmicGlobalKitsAPI.createMasteryKitItem(kits, true), (int)-1);
                return true;
            }
            if (args[0].equalsIgnoreCase("resetcooldowns") && args.length == 2) {
                player = Bukkit.getPlayer((String)args[1]);
                if (player == null) {
                    sender.sendMessage(CC.Red + "Invalid player!");
                    return true;
                }
                GlobalKitUser user = CosmicGlobalKits.getPlugin().getGlobalKitUser(player.getUniqueId());
                if (user == null) {
                    sender.sendMessage(CC.Red + "Null GlobalKitUser for " + player.getName() + "!");
                    return true;
                }
                user.getMasteryKits().clear();
                sender.sendMessage(CC.RedB + "Mastery Kit cooldowns cleared for " + player.getName() + "!");
                return true;
            }
            sender.sendMessage(CC.Red + "/mkit givegem <player> <mkit>");
            sender.sendMessage(CC.Red + "/mkit resetcooldowns <player>");
            return true;
        }
        return false;
    }
}

