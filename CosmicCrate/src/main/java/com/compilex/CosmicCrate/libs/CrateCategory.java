// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.libs;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;
import com.cosmicpvp.cosmicutils.utils.CC;
import org.bukkit.ChatColor;

public enum CrateCategory
{
    MONTHLY_CRATES("MONTHLY_CRATES", ChatColor.YELLOW);
    
    private String type;
    private ChatColor color;
    private String displayName;
    
    private CrateCategory(final String type, final ChatColor color) {
        this.type = type;
        this.color = color;
        this.displayName = this.color + CC.Bold + CC.Underline + this.type + this.color + CC.Bold + " Astro Crates";
    }
    
    public List<Version> getCrates() {
        return Arrays.stream(Version.values()).filter(f -> f.getCategory() == this).collect(Collectors.toList());
    }
    
    public String getType() { return this.type; }
    
    public ChatColor getColor() {
        return this.color;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
}
