package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Stacks every similar item in the player's inventory into stacks of up to 64, including unstackable items.
 */
public class StackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        if (!player.hasPermission("astrocore.stack"))
            return true;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() == Material.AIR || item.getAmount() >= 64) continue;
            for (int j = i + 1; j < contents.length && item.getAmount() < 64; j++) {
                ItemStack other = contents[j];
                if (other == null || !withoutStackBlockers(item).isSimilar(withoutStackBlockers(other))) continue;
                int moved = Math.min(64 - item.getAmount(), other.getAmount());
                item.setAmount(item.getAmount() + moved);
                if (other.getAmount() == moved) {
                    contents[j] = null;
                } else {
                    other.setAmount(other.getAmount() - moved);
                }
            }
        }
        player.getInventory().setContents(contents);
        player.updateInventory();
        player.sendMessage(CC.translate("&a&l(!) &aStacked all items in your inventory."));
        return true;
    }

    // a copy without the random tags some items get only so they never stack; everything else must still match
    private static ItemStack withoutStackBlockers(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.getTag();
        if (tag != null) {
            tag.remove("random");
            tag.remove("randomUUID");
            if (tag.hasKeyOfType("bucketData", 10)) tag.getCompound("bucketData").remove("randomUUID");
            if (tag.hasKeyOfType("spawnerData", 10)) tag.getCompound("spawnerData").remove("uuid");
        }
        return CraftItemStack.asBukkitCopy(nms);
    }
}
