package net.crankedpvp.shards.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import net.crankedpvp.shards.MobcoinAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Astro's /addmobcoins (from the AstroSpawners jar), adding to the AstroMobCoins balance players see in /mobcoins.
public class AddMobcoinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        Player receiver = args.length == 2 ? Bukkit.getPlayer(args[0]) : null;
        Integer amount = args.length == 2 ? parse(args[1]) : null;
        if (receiver == null || amount == null) {
            sender.sendMessage(CC.translate("&c/addmobcoins <player> <amount>"));
            return true;
        }
        if (amount < 0) {
            sender.sendMessage(CC.translate("Amount must me > 0"));
            return true;
        }
        MobcoinAPI.addMobcoins(receiver, amount);
        sender.sendMessage(CC.translate("&aAdded " + amount + " mobcoins to " + receiver.getName()));
        receiver.sendMessage(CC.translate("&a" + amount + " Mobcoins have been added to your balance!"));
        return true;
    }

    private static Integer parse(String amount) {
        try {
            return Integer.valueOf(amount);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
