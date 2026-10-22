// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.libs;

import com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils;
import org.bukkit.plugin.Plugin;
import com.compilex.CosmicCrate.api.CCAPI;
import com.compilex.CosmicCrate.CosmicCrate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Color;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public abstract class CosmicCrateData
{
    protected Random random;
    
    public CosmicCrateData() {
        this.random = ThreadLocalRandom.current();
    }
    
    public abstract ItemStack getRandomMetaItem(final Player p0);
    
    public abstract List<String> getDescriptionLore(final String p0);
    
    public abstract List<ItemStack> getCrateLoot(final Player p0);
    
    public abstract short getNonColorPane();
    
    public abstract short getColorPane();
    
    public abstract List<Color> getConclusionFireworkColors();
    
    public void playConclusion(final Player player) {
        final Location l = player.getEyeLocation();
        this.launchFireworks(player);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2.0f);
    }
    
    public void launchFireworks(final Player player) {
        final Location l = player.getEyeLocation();
        final List<Color> fwColors = this.getConclusionFireworkColors();
        if (fwColors.isEmpty()) {
            Bukkit.getLogger().info("MISSING CONCLUSION FIREWORK COLORS FOR CC: " + player.getName());
            return;
        }
        final Color first = fwColors.get(0);
        final Color second = fwColors.get(1);
        for (int i = 0; i < 8; ++i) {
            final Location location;
            final Color c;
            final Color c2;
            Bukkit.getScheduler().runTaskLater((Plugin)CosmicCrate.get(), () -> {
                CCAPI.launchFirework(l, first);
                CCAPI.launchFirework(l, second);
            }, 20 * i);
        }
    }
    
    public ItemStack setAmount(final ItemStack item, final int amount) {
        return ItemUtils.setAmount(item, amount);
    }
}
