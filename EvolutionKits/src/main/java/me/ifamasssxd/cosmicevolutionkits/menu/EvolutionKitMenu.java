/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.gui.CosmicGUI
 *  com.cosmicpvp.cosmicutils.gui.InventoryItem
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.ifamasssxd.cosmicevolutionkits.menu;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.cosmicpvp.cosmicutils.utils.CC;
import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.menu.KitPreviewMenu;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKit;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Mimic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

public class EvolutionKitMenu
extends CosmicGUI {
    public EvolutionKitMenu(Player player) {
        super(player, "Evolution Kits", 9);
    }

    public void redraw() {
        this.inventory.clear();
        this.getInventoryItems().clear();
        KitPlayer kitPlayer = KitPlayer.kitPlayerMap.get(this.player.getUniqueId());
        if (kitPlayer == null) {
            this.closeWithDelay();
            return;
        }
        for (EvolutionKits kit : EvolutionKits.values()) {
            EvolutionKit internalKit = kit.getKit();
            this.setItem(internalKit.getInventorySlot(), new InventoryItem(kitPlayer.getKitItem(kit, this.player)).click(ClickType.RIGHT, () -> {
                CosmicGUI previewMenu = new KitPreviewMenu(this.player, kit, kit == EvolutionKits.MIMIC ? ((Mimic)internalKit).getRandomNotRigged(this.player, 10) : internalKit.getItems(this.player, 10)).setParentGUI(this);
                this.closeWithDelay(pl -> previewMenu.openGUI((JavaPlugin)CosmicEvolutionKits.get()));
            }).click(ClickType.LEFT, () -> {
                if (kitPlayer.isLoadingData()) {
                    this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "Please wait while your /vkit data is loaded!");
                    this.closeWithDelay();
                    return;
                }
                if (!kitPlayer.hasAccess(this.player, kit)) {
                    this.player.closeInventory();
                    this.player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You have not unlocked this Evolution Kit!");
                    this.player.sendMessage(ChatColor.GRAY + "You can preview a max level version by " + ChatColor.UNDERLINE + "right-clicking" + ChatColor.GRAY + " the /vkit item. Unlock access to this kit with a Fallen Hero from " + ChatColor.GRAY + ChatColor.UNDERLINE + "buy.cosmicpvp.com!");
                    return;
                }
                if (kit.checkFund(this.player)) {
                    return;
                }
                String cooldownString = kitPlayer.getCooldownTimer(kit);
                if (cooldownString != null) {
                    this.player.sendMessage(ChatColor.YELLOW + "You " + ChatColor.YELLOW + ChatColor.UNDERLINE + "cannot" + ChatColor.YELLOW + " use this Evolution Kit for another " + cooldownString + "!");
                    this.player.closeInventory();
                    return;
                }
                kit.getKit().onClaim(this.player, kit, kitPlayer);
            }));
        }
        this.player.updateInventory();
    }

    public boolean isOpen() {
        return this.player != null && this.player.isValid() && this.player.getOpenInventory() != null && this.player.getOpenInventory().getTopInventory() != null && this.player.getOpenInventory().getTopInventory().equals(this.inventory);
    }
}

