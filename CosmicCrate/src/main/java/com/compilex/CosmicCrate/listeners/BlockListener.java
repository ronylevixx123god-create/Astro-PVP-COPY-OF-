// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.compilex.CosmicCrate.api.CCAPI;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class BlockListener implements Listener
{
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(final BlockPlaceEvent e) {
        if (CCAPI.getCosmicCrateVersion(e.getItemInHand()) != null) {
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }
    }
}
