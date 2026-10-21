package me.astropvp.astropvpskins.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import me.astropvp.astropvpskins.AstroItemSkinsAPI;
import me.astropvp.astropvpskins.enums.ItemSkinType;
import me.astropvp.astropvpskins.enums.SkinType;
import me.astropvp.astropvpskins.listeners.ItemSkinListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Takes every skin off the item in hand (the item itself stays in the hand, it is never copied)
// and gives the skin items back once. Also clears skin data that no longer matches a skin.
public class RemoveBrokenSkinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(CC.translate("&cNo Perms."));
            return true;
        }
        if (!(commandSender instanceof Player))
            return true;

        Player p = (Player) commandSender;
        ItemStack item = p.getItemInHand();
        if (item == null || item.getType() == Material.AIR || !AstroItemSkinsAPI.hasItemSkin(item)) {
            p.sendMessage(CC.translate("&c&l(!)&c You must be holding an item with an item skin!"));
            return true;
        }

        List<SkinType> skins = AstroItemSkinsAPI.getItemSkinByItem(item);
        if (skins == null)
            skins = new ArrayList<>();

        ItemStack stripped = AstroItemSkinsAPI.removeNoDisplay(item);
        ItemMeta meta = stripped.getItemMeta();
        if (meta.hasLore()) {
            List<String> lore = meta.getLore().stream()
                    .filter(line -> !ChatColor.stripColor(line).startsWith("ITEM SKIN: "))
                    .collect(Collectors.toList());
            meta.setLore(lore);
            stripped.setItemMeta(meta);
        }
        if (skins.stream().anyMatch(skin -> skin.getItemSkinType() == ItemSkinType.BACKPACK))
            stripped = ItemSkinListener.removeBackpackSkin(stripped);
        p.setItemInHand(stripped);

        // Two skins of the same kind came from one multi skin, so give them back as one, like right-click detach.
        List<SkinType> left = new ArrayList<>(skins);
        while (!left.isEmpty()) {
            SkinType first = left.remove(0);
            SkinType pair = left.stream()
                    .filter(skin -> skin != first && skin.getItemSkinType() == first.getItemSkinType())
                    .findFirst().orElse(null);
            ItemStack give;
            if (pair != null) {
                left.remove(pair);
                give = first.getMultiSkin(pair);
            } else {
                give = first.getItem();
            }
            for (ItemStack drop : p.getInventory().addItem(give).values())
                p.getWorld().dropItem(p.getLocation(), drop);
        }
        p.updateInventory();
        p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 10.0F);
        p.sendMessage(CC.translate("&aDone!"));
        return true;
    }
}
