// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.api;

import com.compilex.CosmicCrate.CosmicCrate;
import com.compilex.CosmicCrate.libs.RandomFireworks;
import com.compilex.CosmicCrate.libs.Version;
import com.compilex.CosmicCrate.utils.LocalItemUtils;
import com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils;
import org.arkhamnetwork.Arkkit.patches.ItemID;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CCAPI
{
    public static ItemStack getCosmicCrate(final Version v, final String username) {
        ItemStack cc = null;
        if (v.getCrateData() != null) {
            final List<String> lore = v.getCrateData().getDescriptionLore(username);
            cc = ItemUtils.buildItem(Material.ENDER_CHEST, (short)0, ChatColor.translateAlternateColorCodes('&', v.crateName), (String[])lore.toArray(new String[lore.size()]));
        }
        if (ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
            ItemID.setItemID(cc, ItemID.generateNewItemID());
        }
        return cc;
    }
    
    public static Version getCosmicCrateVersion(final ItemStack i) {
        if (!i.hasItemMeta() || !i.getItemMeta().hasDisplayName() || i.getMaxStackSize() == 1) {
            return null;
        }
        for (final Version v : Version.values()) {
            if (v.crateName.equals(i.getItemMeta().getDisplayName())) {
                return v;
            }
        }
        return null;
    }
    
    public static String getCosmicCrateUnlocker(final ItemStack i) {
        final Version v = getCosmicCrateVersion(i);
        if (v != null) {
            return ChatColor.stripColor(i.getItemMeta().getLore().get(0).split(" ")[2]);
        }
        return null;
    }
    
    public static Inventory getCosmicCrateClaimMenu(final Player p) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, "Astro Crate Storage");
        for (final Version v : Version.values()) {
            ItemStack item;
            if (p.hasPermission(v.getPermissionNode())) {
                if (hasClaimedCosmicCrate(p, v)) {
                    item = LocalItemUtils.changeType(getCosmicCrate(v, p.getName()), Material.STAINED_GLASS_PANE, (short)5);
                }
                else {
                    item = getCosmicCrate(v, p.getName());
                }
            }
            else {
                item = LocalItemUtils.getLockedPlaceholder(p, v);
            }
            if (v.guiSlot != -1) {
                inv.setItem(v.guiSlot, item);
            }
            else {
                inv.setItem(inv.firstEmpty(), item);
            }
        }
        while (inv.firstEmpty() != -1) {
            inv.setItem(inv.firstEmpty(), LocalItemUtils.getMysteryPlaceholder());
        }
        return inv;
    }
    
    public static void claimCosmicCrate(final Player p, final Version v) {
        final String uuid = p.getUniqueId().toString();
        final List<Version> claimedCrates = CosmicCrate.claimedCosmicCrates.containsKey(uuid) ? CosmicCrate.claimedCosmicCrates.get(uuid) : new ArrayList<Version>();
        claimedCrates.add(v);
        CosmicCrate.claimedCosmicCrates.put(uuid, claimedCrates);
        p.getInventory().addItem(new ItemStack[] { getCosmicCrate(v, p.getName()) });
        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 2.0f);
        RandomFireworks.getManager().launchRandomFirework(p.getLocation());
    }
    
    public static boolean hasClaimedCosmicCrate(final Player p, final Version v) {
        return CosmicCrate.claimedCosmicCrates.containsKey(p.getUniqueId().toString()) && CosmicCrate.claimedCosmicCrates.get(p.getUniqueId().toString()).contains(v);
    }
    
    public static void launchFirework(final Location l, final Color c) {
        final Firework fw = (Firework)l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        final FireworkMeta fwm = fw.getFireworkMeta();
        final FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
        fwm.addEffect(effect);
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
    }
    
    public static ItemStack getExplosiveSnowball() {
        return ItemUtils.buildItem(Material.SNOW_BALL, (short)0, ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "Explosive Snowball", new String[] { ChatColor.GRAY + "Yes, this is a snowball with TNT inside.", ChatColor.GRAY + "Seriously, Happy Holidays!" });
    }
    
    public static boolean isExplosiveSnowball(final ItemStack i) {
        return i != null && i.getType() == Material.SNOW_BALL && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "Explosive Snowball");
    }
    
    public static List<ItemStack> getCosmicCrateLoot(final Player p, final Version v) {
        List<ItemStack> items = new ArrayList<ItemStack>();
        if (v.getCrateData() != null) {
            items = v.getCrateData().getCrateLoot(p);
        }
        return items;
    }
    
    private static int rand(final int low, final int high) {
        return new Random().nextInt(high - low) + low;
    }
}
