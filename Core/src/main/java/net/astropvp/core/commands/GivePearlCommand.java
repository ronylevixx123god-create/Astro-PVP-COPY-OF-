package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import net.astropvp.core.items.pearls.EscapistPearlItem;
import net.astropvp.core.items.pearls.OmniPearlItem;
import net.astropvp.core.items.pearls.SuperPearlItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GivePearlCommand implements CommandExecutor {
    String error = CC.Red + "Correct usage: /givepearl <player> <type>";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        if (!player.isOp())
            return true;
        Player target = args.length == 2 ? Bukkit.getPlayer(args[0]) : null;
        ItemStack pearl = args.length == 2 ? getPearl(args[1]) : null;
        if (target == null || pearl == null) {
            player.sendMessage(this.error);
            return true;
        }
        target.getInventory().addItem(pearl);
        return false;
    }

    private ItemStack getPearl(String type) {
        switch (type.toLowerCase()) {
            case "escapist":
                return EscapistPearlItem.getItem();
            case "super":
                return SuperPearlItem.getItem();
            case "omni":
                return OmniPearlItem.getItem();
            default:
                return null;
        }
    }
}
