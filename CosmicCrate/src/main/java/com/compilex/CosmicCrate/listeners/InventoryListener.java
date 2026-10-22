// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.listeners;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import com.compilex.CosmicCrate.CosmicCrate;
import org.bukkit.Bukkit;
import org.bukkit.metadata.MetadataValue;
import com.compilex.CosmicCrate.CosmicCrateLootSelector;
import com.compilex.CosmicCrate.libs.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;

public class InventoryListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCosmicCrateRewardClaim(final InventoryClickEvent e) {
        if (e.getInventory().getTitle().startsWith("Astro Crate: ")) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            final Player p = (Player)e.getWhoClicked();
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(p.getOpenInventory().getTopInventory())) {
                return;
            }
            final Version v = Version.valueOf(e.getInventory().getTitle().split(" ")[2].toUpperCase());
            if (!p.hasMetadata("ccrate_loot_selector")) {
                return;
            }
            final CosmicCrateLootSelector ccls = (CosmicCrateLootSelector)p.getMetadata("ccrate_loot_selector").get(0).value();
            if (e.getCurrentItem() != null) {
                if (ccls.phase == CosmicCrateLootSelector.CosmicCratePhase.LOOTING) {
                    ccls.conclude();
                    Bukkit.getScheduler().runTaskLater((Plugin)CosmicCrate.get(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            p.closeInventory();
                        }
                    }, 10L);
                    return;
                }
                if (e.getCurrentItem().getType() == Material.ENDER_CHEST && e.getRawSlot() != 49) {
                    if (!ccls.hasSlotAnimation(e.getRawSlot())) {
                        ccls.runSlotAnimation(e.getRawSlot());
                    }
                }
                else if (e.getRawSlot() == 49) {
                    if (ccls.displayRewards.size() > 0) {
                        p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You cannot claim this item yet!");
                        return;
                    }
                    if (ccls.phase == CosmicCrateLootSelector.CosmicCratePhase.UNLOCKING_META) {
                        ccls.lootInventory.setItem(49, ccls.metaItem);
                        ccls.phase = CosmicCrateLootSelector.CosmicCratePhase.LOOTING;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onCosmicCrateRedeemInventoryClose(final InventoryCloseEvent e) {
        if (e.getInventory().getTitle().startsWith("Astro Crate: ") && e.getPlayer().hasMetadata("ccrate_loot_selector")) {
            final Player p = (Player)e.getPlayer();
            final CosmicCrateLootSelector ccls = (CosmicCrateLootSelector)p.getMetadata("ccrate_loot_selector").get(0).value();
            ccls.conclude();
        }
    }
}
