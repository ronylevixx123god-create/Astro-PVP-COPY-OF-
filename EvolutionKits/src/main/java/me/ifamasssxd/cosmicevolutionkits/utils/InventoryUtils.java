/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package me.ifamasssxd.cosmicevolutionkits.utils;

import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class InventoryUtils {
    public static int getRoundedSize(int size) {
        return size <= 9 ? 9 : (size <= 18 ? 18 : (size <= 27 ? 27 : (size <= 36 ? 36 : (size <= 45 ? 45 : (size <= 54 ? 54 : 54)))));
    }

    public static boolean giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack[]{item});
            return true;
        }
        Item i = player.getWorld().dropItem(player.getLocation(), item);
        i.setMetadata("no_pickup", (MetadataValue)new FixedMetadataValue((Plugin)CosmicEvolutionKits.get(), (Object)player.getName()));
        return false;
    }
}

