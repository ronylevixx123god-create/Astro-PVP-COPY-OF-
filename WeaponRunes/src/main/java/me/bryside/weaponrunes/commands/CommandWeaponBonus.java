package me.bryside.weaponrunes.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import java.util.List;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandWeaponBonus
implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        int inventorySize = 9;
        Player p = ((Player)commandSender).getPlayer();
        Inventory inventory = Bukkit.createInventory(null, (int)9, (String)CC.translate((String)"&8Weapon Bonuses"));
        int slot = 0;
        for (WeaponBonusType weaponBonusType : WeaponBonusType.values()) {
            inventory.addItem(new ItemStack[]{new ItemStack(weaponBonusType.getInstance().getItem(100.0))});
            ++slot;
        }
        ItemStack helpBook = new ItemStack(Material.BOOK);
        ItemMeta meta = helpBook.getItemMeta();
        meta.setDisplayName(CC.translate((String)"&b&lWeapon Bonuses"));
        meta.setLore((List)Lists.newArrayList((Object[])new String[]{CC.translate((String)"&7Weapon Bonuses are orbs which you"), CC.translate((String)"&7apply onto Heroic Swords only and"), CC.translate((String)"&7imbue the weapon with special powers.")}));
        helpBook.setItemMeta(meta);
        inventory.setItem(inventorySize - 1, helpBook);
        p.sendMessage(CC.translate((String)"&aOpening the Weapon Bonuses Menu..."));
        p.openInventory(inventory);
        return true;
    }
}

