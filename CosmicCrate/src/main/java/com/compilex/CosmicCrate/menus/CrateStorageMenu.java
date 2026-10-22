// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.menus;

import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.compilex.CosmicCrate.utils.LocalItemUtils;
import org.bukkit.Sound;
import com.compilex.CosmicCrate.api.CCAPI;
import org.bukkit.ChatColor;
import com.compilex.CosmicCrate.libs.Version;
import org.bukkit.plugin.java.JavaPlugin;
import com.compilex.CosmicCrate.CosmicCrate;
import java.util.List;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import org.bukkit.Material;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.compilex.CosmicCrate.libs.CrateCategory;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.entity.Player;
import com.cosmicpvp.cosmicutils.gui.CosmicGUI;

public class CrateStorageMenu extends CosmicGUI
{
    public CrateStorageMenu(final Player player) {
        super(player, "Astro Crate Storage", 27);
    }
    
    public void redraw() {
        final AtomicInteger slot = new AtomicInteger(0);
        int startIndex = 18;
        for (Version version : CrateCategory.MONTHLY_CRATES.getCrates()) {
            this.setItem(startIndex++, version.getDisplayItem(this.player), () -> {
                if (!version.hasPermission(this.player)) {
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You do not have access to this Astro Crate! Purchase it (when available) at " + ChatColor.UNDERLINE + "buy.astropvp.net");
                    return;
                }
                else {

                        if (this.player.getInventory().firstEmpty() != -1) {
                            CCAPI.claimCosmicCrate(this.player, version);
                        }
                        else {
                            this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Your inventory is full!");
                            this.player.playSound(this.player.getLocation(), Sound.VILLAGER_NO, 1.0f, 0.6f);
                        }
                        this.closeWithDelay();
                    return;
                }
            });
        }
        for (int i = 0; i < this.size; ++i) {
            final ItemStack item = this.inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                this.setItem(i, new InventoryItem(LocalItemUtils.getMysteryPlaceholder()));
            }
        }
    }
}
