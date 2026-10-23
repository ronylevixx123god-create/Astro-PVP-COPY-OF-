/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Item
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryPickupItemEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.metadata.MetadataValue
 */
package me.ifamasssxd.cosmicevolutionkits.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.metadata.MetadataValue;

public class InventoryListener
implements Listener {
    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        if (event.getItem().hasMetadata("no_pickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerPick(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        if (item.hasMetadata("no_pickup")) {
            String name = ((MetadataValue)item.getMetadata("no_pickup").get(0)).asString();
            if (name.equals(event.getPlayer().getName())) {
                return;
            }
            if (item.getTicksLived() < 1200) {
                event.setCancelled(true);
            }
        }
    }
}

