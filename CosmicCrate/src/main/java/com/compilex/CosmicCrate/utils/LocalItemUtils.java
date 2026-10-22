// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.utils;

import com.compilex.CosmicCrate.api.CCAPI;
import com.compilex.CosmicCrate.libs.Version;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class LocalItemUtils
{
    public static ItemStack buildItem(final Material m, final short metaData, final String name, final String... description) {
        final ItemStack is = new ItemStack(m, 1, metaData);
        final ItemMeta im = is.getItemMeta();
        if (name != null) {
            im.setDisplayName(name);
        }
        if (description != null) {
            im.setLore((List)Arrays.asList(description));
        }
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack injectLore(final ItemStack i, final String newLore) {
        final ItemMeta im = i.getItemMeta();
        final List<String> lore = (List<String>)im.getLore();
        lore.add("");
        lore.add(newLore);
        im.setLore((List)lore);
        i.setItemMeta(im);
        return i;
    }
    
    public static ItemStack changeType(final ItemStack i, final Material newType, final short newMetadata) {
        i.setType(newType);
        i.setDurability(newMetadata);
        return i;
    }
    
    public static ItemStack getMysteryPlaceholder() {
        return buildItem(Material.STAINED_GLASS_PANE, (short)7, ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "???", new String[0]);
    }
    
    public static ItemStack getLockedPlaceholder(final Player p, final Version v) {
        final String bulletpoint = ChatColor.BOLD.toString() + "* ";
        final ItemStack crateItem = CCAPI.getCosmicCrate(v, p.getName());
        crateItem.setType(Material.STAINED_GLASS_PANE);
        crateItem.setDurability((short)14);
        final ItemMeta im = crateItem.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "LOCKED: " + v.crateName.replace("*", "").replace("ASTRO CRATE: ", ""));
        final List<String> lore = (List<String>)im.getLore();
        lore.set(0, ChatColor.GRAY + "Unlock at " + ChatColor.UNDERLINE + "buy.cosmicpvp.com");
        im.setLore((List)lore);
        crateItem.setItemMeta(im);
        return crateItem;
    }
    
    public static ItemStack setAmount(final ItemStack item, final int i) {
        item.setAmount(i);
        return item;
    }
}
