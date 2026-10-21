package me.bryside.weaponrunes.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

// Astro's /giveweaponbonus <type> <success>; an unknown type now gets the "not found" list instead of an exception.
public class CommandGiveWeaponRune implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (!player.isOp()) {
            return true;
        }
        if (args.length == 2) {
            WeaponBonusType weaponBonusType = WeaponBonusType.fromId(args[0]);
            if (weaponBonusType == null) {
                player.sendMessage(CC.RedB + "(!)" + CC.Red + " Weapon Bonus not found. Available Weapon Bonuses:");
                player.sendMessage(CC.Red + Arrays.stream(WeaponBonusType.values()).map(WeaponBonusType::getId).collect(Collectors.toList()).toString() + ".");
                return true;
            }
            double success;
            try {
                success = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(CC.Red + "Correct Usage: /giveweaponbonus <type> <success>");
                return true;
            }
            player.getInventory().addItem(weaponBonusType.getInstance().getItem(success));
        } else {
            player.sendMessage(CC.Red + "Correct Usage: /giveweaponbonus <type> <success>");
        }
        return true;
    }
}
