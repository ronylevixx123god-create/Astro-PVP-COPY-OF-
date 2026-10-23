/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.gui.CosmicGUI
 *  com.cosmicpvp.cosmicutils.gui.InventoryItem
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.InventoryUtils
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.ifamasssxd.cosmicevolutionkits.menu;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.InventoryUtils;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import java.util.List;
import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class KitPreviewMenu
extends CosmicGUI {
    private List<ItemStack> items;

    public KitPreviewMenu(Player player, EvolutionKits preview, List<ItemStack> items) {
        super(player, "Evolution Kit Preview", InventoryUtils.fitSize((int)(items.size() + 1)));
        this.items = items;
    }

    public void redraw() {
        int slot = 0;
        Runnable noBueno = () -> {
            this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot withdraw items from the kit preview!");
            this.player.sendMessage(CC.Gray + "Unlock access to this kit with a Fallen Hero from " + CC.GrayU + "buy.cosmicpvp.com!");
        };
        for (ItemStack item : this.items) {
            this.setItem(slot++, new InventoryItem(item).click(noBueno));
        }
        this.setItem(this.size - 1, new InventoryItem(new ItemBuilder(Material.ARROW).name(CC.RedB + "Previous Menu").lore(new String[]{CC.Gray + "Click to return to the", CC.Gray + "Evolution Kit main menu!"}).build()).click(() -> this.parentGUI.openGUI((JavaPlugin)CosmicEvolutionKits.get())));
    }
}

