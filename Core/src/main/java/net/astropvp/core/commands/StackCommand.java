package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Sets the held stack to exactly 64, like Astro's /more (same item copy, dupedBy/dupedAt tag and webhook log),
 * for Sr. Mod and above (astrocore.stack).
 */
public class StackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        if (!player.hasPermission("astrocore.stack")) {
            player.sendMessage(CC.translate("&cNo permission :3"));
            return true;
        }
        ItemStack hand = player.getInventory().getItemInHand();
        if (hand == null || hand.getType() == Material.AIR) {
            player.sendMessage("hold item !");
            return true;
        }
        player.getInventory().setItemInHand(MoreCommand.addDataToItem(hand, player));
        player.updateInventory();
        MoreCommand.sendWebHook("/stack", player.getName(), CC.strip(player.getInventory().getItemInHand().getItemMeta().getDisplayName()));
        return true;
    }
}
