// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.listeners;

import org.bukkit.util.Vector;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.arkhamnetwork.enchantments.util.WorldGuardUtils;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import java.util.ListIterator;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import com.compilex.CosmicCrate.CosmicCrate;
import com.compilex.CosmicCrate.CosmicCrateLootSelector;
import org.bukkit.Sound;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.bukkit.ChatColor;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Iterator;
import java.util.List;
import org.arkhamnetwork.Arkkit.patches.ItemID;
import com.compilex.CosmicCrate.libs.Version;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.compilex.CosmicCrate.api.CCAPI;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    private boolean isEmptyBesidesCCs(final Inventory inventory, final int allowed) {
        int found = 0;
        for (int i = 0; i < inventory.getSize(); ++i) {
            final ItemStack item = inventory.getItem(i);
            if (item != null) {
                if (item.getType() != Material.AIR) {
                    if (item.getType() == Material.ENDER_CHEST && CCAPI.getCosmicCrateVersion(item) != null) {
                        ++found;
                    }
                }
            }
        }
        return found <= allowed;
    }
    
    private int getSlotsRequired(final Player player, final Version version) {
        int required = 0;
        final List<ItemStack> item = CCAPI.getCosmicCrateLoot(player, version);
        if (item != null) {
            for (final ItemStack reward : item) {
                final long id = ItemID.getItemID(reward);
                if (reward.getAmount() > 1 && reward.getAmount() <= 16 && (id != -1L || ItemID.needsItemID(reward))) {
                    required += reward.getAmount();
                }
                else {
                    ++required;
                }
            }
        }
        return required;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.hasItem() && CCAPI.getCosmicCrateVersion(e.getItem()) != null) {
            final Version v = CCAPI.getCosmicCrateVersion(e.getItem());
            final ItemStack i = e.getItem();
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            if (e.getPlayer().isOp() && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
                final Inventory previewInventory = Bukkit.createInventory((InventoryHolder)null, 45, v.name());
                for (final ItemStack is : v.getCrateData().getCrateLoot(e.getPlayer())) {
                    previewInventory.addItem(is);
                }
                previewInventory.addItem(v.getCrateData().getRandomMetaItem(e.getPlayer()));
                e.getPlayer().openInventory(previewInventory);
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                final int required = this.getSlotsRequired(e.getPlayer(), v);
                if (this.getEmptySlots(e.getPlayer()) < this.getSlotsRequired(e.getPlayer(), v)) {
                    e.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You do not have enough empty inventory slots.");
                    e.getPlayer().sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "REQ: " + required + "+ slots");
                    return;
                }
                if (i.getAmount() > 1) {
                    i.setAmount(i.getAmount() - 1);
                    e.getPlayer().setItemInHand(i);
                }
                else {
                    e.getPlayer().setItemInHand((ItemStack)null);
                    if (ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
                        final long itemID = ItemID.getItemID(e.getItem());
                        if (itemID != -1L) {
                            ItemID.blacklistIID(itemID, "ccOpen/" + v.toString() + "/" + e.getPlayer().getName());
                        }
                    }
                }
                if ((v != null && v.getCrateData() != null)) {
                    final Player p = e.getPlayer();
                    final Location l = p.getEyeLocation();
                    p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 2.0f);
                    final CosmicCrateLootSelector ccls = new CosmicCrateLootSelector(e.getPlayer(), v);
                    e.getPlayer().setMetadata("ccrate_loot_selector", (MetadataValue)new FixedMetadataValue((Plugin)CosmicCrate.get(), (Object)ccls));
                    ccls.open();
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        if (e.getPlayer().getOpenInventory() != null && e.getPlayer().getOpenInventory().getTitle().startsWith("Astro Crate: ")) {
            e.setCancelled(true);
        }
    }
    
    private int getEmptySlots(final Player p) {
        int i = 0;
        for (final ItemStack is : p.getInventory()) {
            if (is == null || is.getType() == Material.AIR) {
                ++i;
            }
        }
        return i;
    }
    
    @EventHandler
    public void onProjectileThrow(final ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Snowball && !e.getEntity().getWorld().getName().contains("dungeon")) {
            final Snowball s = (Snowball)e.getEntity();
            if (s.getShooter() instanceof Player) {
                final Player p = (Player)s.getShooter();
                if (CCAPI.isExplosiveSnowball(p.getItemInHand())) {
                    s.setMetadata("explosive", (MetadataValue)new FixedMetadataValue((Plugin)CosmicCrate.get(), (Object)true));
                }
            }
        }
    }
    
    @EventHandler
    public void onProjectilHit(final ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball && e.getEntity().hasMetadata("explosive")) {
            if (e.getEntity().getLocation().getBlock().getType() == Material.WATER || e.getEntity().getLocation().getBlock().getType() == Material.STATIONARY_WATER || e.getEntity().getLocation().getBlock().getType() == Material.CHEST) {
                return;
            }
            if (WorldGuardUtils.getRegionName(e.getEntity().getLocation()).contains("warzone") || WorldGuardUtils.isPvPDisabled(e.getEntity().getLocation())) {
                return;
            }
            final Entity silverfish = e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.SILVERFISH);
            for (final Entity ent : e.getEntity().getNearbyEntities(4.0, 4.0, 4.0)) {
                if (ent instanceof LivingEntity && !WorldGuardUtils.isPvPDisabled(ent.getLocation())) {
                    this.pushAwayEntity(silverfish.getLocation(), ent, 3.8);
                }
            }
            silverfish.remove();
            final Location l = e.getEntity().getLocation().add(0.0, 1.0, 0.0);
            l.getWorld().playSound(l, Sound.EXPLODE, 0.8f, 0.5f);
            l.getWorld().playSound(l, Sound.EAT, 1.0f, 0.77f);
        }
    }
    
    public void pushAwayEntity(final Location center, final Entity entity, final double speed) {
        if (entity instanceof Horse) {
            return;
        }
        final Vector unitVector = entity.getLocation().toVector().subtract(center.toVector()).normalize();
        entity.setVelocity(unitVector.multiply(speed));
    }
}
