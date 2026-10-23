/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.compat.ASound
 *  com.cosmicpvp.cosmicutils.gui.CosmicGUI
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.InventoryUtils
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  com.cosmicpvp.cosmicutils.utils.NBTWrapper
 *  com.cosmicpvp.cosmicutils.utils.PlayerUtil
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package me.ifamasssxd.cosmicevolutionkits.items;

import com.cosmicpvp.cosmicutils.compat.ASound;
import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.InventoryUtils;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class WildcardMenu
extends CosmicGUI {
    private boolean unlocked = false;
    private NBTWrapper itemWrapper;

    public WildcardMenu(Player player, NBTWrapper itemWrapper) {
        super(player, "Select /vkit to unlock/level", InventoryUtils.fitSize((int)EvolutionKits.values().length));
        this.itemWrapper = itemWrapper;
    }

    public void redraw() {
        int index = 0;
        KitPlayer kitPlayer = KitPlayer.kitPlayerMap.get(this.player.getUniqueId());
        for (EvolutionKits kit : EvolutionKits.values()) {
            ItemBuilder builder = new ItemBuilder(kitPlayer.getKitItem(kit, this.player, false));
            int currentLevel = kitPlayer.getLevel(kit);
            if (currentLevel < 10) {
                builder.addLoreLine(new String[]{CC.Green + "Click to unlock or level up!"});
            } else {
                builder.addLoreLine(new String[]{CC.RedB + "MAX LEVEL"});
            }
            this.setItem(index++, builder.build(), () -> {
                if (this.unlocked) {
                    return;
                }
                if (this.itemWrapper.isBlacklistedIID()) {
                    this.unlocked = true;
                    this.close();
                    this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "Item has been claimed since you're inventory was opened!");
                    return;
                }
                if (kit.levelUpOrUnlock(this.player, CC.GoldB + "Omni Gem (" + CC.LightPurple + "/vkit" + CC.GoldB + ")")) {
                    this.unlocked = true;
                    this.itemWrapper.blacklistIID(this.player.getName() + " claimed");
                    this.closeWithDelay();
                }
            });
        }
    }

    public void onInventoryClose() {
        if (!this.unlocked) {
            this.unlocked = true;
            PlayerUtil.giveOrDropProtectedItem((Player)this.player, (ItemStack)this.itemWrapper.build(), (int)-1);
            ASound.CHICKEN_EGG_POP.play(this.player, 3.0f, 1.1f);
            Bukkit.getScheduler().runTaskLater((Plugin)CosmicEvolutionKits.get(), () -> {
                if (this.player.isValid()) {
                    this.player.updateInventory();
                }
            }, 1L);
        }
    }
}

