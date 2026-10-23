/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rit.sucy.CustomEnchantment
 *  com.rit.sucy.EnchantmentAPI
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 */
package me.ifamasssxd.cosmicevolutionkits.utils;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantedItemBuilder {
    private ItemStack item;

    public EnchantedItemBuilder(ItemStack item) {
        this.item = item;
    }

    public EnchantedItemBuilder addEnchant(Enchantment enchant, int level) {
        this.item.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public EnchantedItemBuilder addCustomEnchant(String enchant, int level) {
        CustomEnchantment customEnchant = EnchantmentAPI.getEnchantment((String)enchant);
        if (customEnchant == null) {
            return this;
        }
        this.item = customEnchant.addToItem(this.item, level);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }
}

