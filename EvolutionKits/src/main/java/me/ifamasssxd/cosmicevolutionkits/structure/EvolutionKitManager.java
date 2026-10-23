/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.gui.CosmicGUI
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package me.ifamasssxd.cosmicevolutionkits.structure;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.menu.EvolutionKitMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EvolutionKitManager {
    public void startInventoryUpdater() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)CosmicEvolutionKits.get(), () -> {
            for (CosmicGUI gui : CosmicGUI.activeGUIs.values()) {
                if (!(gui instanceof EvolutionKitMenu) || !((EvolutionKitMenu)gui).isOpen()) continue;
                gui.redraw();
            }
        }, 20L, 20L);
    }
}

