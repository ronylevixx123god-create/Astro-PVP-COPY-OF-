// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.menus;

import org.bukkit.inventory.ItemStack;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.compilex.CosmicCrate.utils.LocalItemUtils;
import org.bukkit.plugin.java.JavaPlugin;
import com.compilex.CosmicCrate.CosmicCrate;
import com.cosmicpvp.cosmicutils.utils.CC;
import org.bukkit.Material;
import org.bukkit.Sound;
import com.compilex.CosmicCrate.api.CCAPI;
import org.bukkit.ChatColor;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import java.util.Arrays;
import com.compilex.CosmicCrate.libs.Version;
import java.util.concurrent.atomic.AtomicInteger;
import com.cosmicpvp.cosmicutils.utils.InventoryUtils;
import org.bukkit.entity.Player;
import com.compilex.CosmicCrate.libs.CrateCategory;
import com.cosmicpvp.cosmicutils.gui.CosmicGUI;

public class CrateCategoryMenu extends CosmicGUI
{
    private CrateCategory category;
    
    public CrateCategoryMenu(final Player player, final CrateCategory category) {
        super(player, category.getType() + " Astro Crates", InventoryUtils.fitSize(category.getCrates().size() + 1));
        this.category = category;
    }
    
    public void redraw() {
        final AtomicInteger slot = new AtomicInteger(0);
        Arrays.stream(Version.values()).filter(ver -> ver.getCategory() == this.category).forEach(ver -> this.setItem(slot.getAndIncrement(), new ItemBuilder(ver.getDisplayItem(this.player)).build(), () -> {
            if (!ver.hasPermission(this.player)) {
                this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You do not have access to this Astro Crate! Purchase it (when available) at " + ChatColor.UNDERLINE + "buy.cosmicpvp.com");
            }
            else {
                if (CCAPI.hasClaimedCosmicCrate(this.player, ver)) {
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You already claimed the " + ChatColor.UNDERLINE.toString() + ChatColor.BOLD.toString() + ver.name() + ChatColor.RED + " Astro Crate!");
                }
                else if (this.player.getInventory().firstEmpty() != -1) {
                    CCAPI.claimCosmicCrate(this.player, ver);
                }
                else {
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Your inventory is full!");
                    this.player.playSound(this.player.getLocation(), Sound.VILLAGER_NO, 1.0f, 0.6f);
                }
                this.closeWithDelay();
            }
        }));
        this.setItem(this.size - 1, new ItemBuilder(Material.ARROW, CC.Red + "Main Menu", CC.Gray + "Click to view main Astro Crate category!").build(), () -> {
            this.parentGUI.openGUI((JavaPlugin)CosmicCrate.get());
            this.parentGUI.redraw();
            return;
        });
        for (int i = 0; i < this.size; ++i) {
            final ItemStack item = this.inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                this.setItem(i, new InventoryItem(LocalItemUtils.getMysteryPlaceholder()));
            }
        }
    }
}
