package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NumberUtils;
import net.astropvp.core.items.MagicalLavaBucket;
import net.astropvp.core.items.MagicalWaterBucket;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGiveMagicBucket implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) return true;
        Player player = (Player)commandSender;
        if (!player.isOp()) return true;
        Integer uses = args.length == 2 ? NumberUtils.parseIntOrNull(args[1]) : null;
        if (uses == null || uses < 1 || !(args[0].equalsIgnoreCase("LAVA") || args[0].equalsIgnoreCase("WATER"))) {
            player.sendMessage(CC.translate("&c/givebucket (LAVA/WATER) (uses)"));
            return true;
        }
        if (args[0].equalsIgnoreCase("LAVA")) {
            player.getInventory().addItem(MagicalLavaBucket.createMagicalLavaBucket(uses));
        } else {
            player.getInventory().addItem(MagicalWaterBucket.createMagicalWaterBucket(uses));
        }
        player.sendMessage(CC.translate("&aAdded magic bucket to inventory!"));
        return true;
    }
}
