package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import net.astropvp.core.AstroCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoulPearlCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be used in-game.");
            return true;
        }
        if (!commandSender.isOp()) {
            return true;
        }
        Player pSender = (Player) commandSender;
        pSender.getInventory().addItem(AstroCore.getInstance().getSoulPearl());
        pSender.sendMessage(CC.translate("&aYou have received a soulpearl!"));
        return true;
    }
}
