// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate.libs;

import com.compilex.CosmicCrate.libs.cratedata.SOTW;
import org.bukkit.Material;
import com.compilex.CosmicCrate.api.CCAPI;
import com.compilex.CosmicCrate.utils.LocalItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public enum Version
{
    SOTW("&a&l*&6&l*&a&l* &6&lASTRO CRATE: &f&l&nSOTW&r &2&l&n2021&r &a&l*&6&l*&a&l*", new SOTW(), CrateCategory.MONTHLY_CRATES);
    
    public String crateName;
    public String rawName;
    public int guiSlot;
    private CrateCategory category;
    private CosmicCrateData crateData;
    
    private Version(final String crateName, final CrateCategory category) {
        this.guiSlot = -1;
        this.category = category;
        this.crateName = ChatColor.translateAlternateColorCodes('&', crateName);
        this.rawName = this.crateName;
        this.rawName = this.rawName.replace("ASTRO CRATE: ", "").replace("***", "");
    }
    
    private Version(final String crateName, final CosmicCrateData data, final CrateCategory category) {
        this(crateName, data, -1, category);
    }
    
    private Version(final String crateName, final CosmicCrateData data, final int guiSlot, final CrateCategory category) {
        this(crateName, category);
        this.guiSlot = guiSlot;
        this.crateData = data;
    }
    
    public boolean hasPermission(final Player player) {
        return player.hasPermission(this.getPermissionNode());
    }
    
    public String getPermissionNode() {
        return "ccrate.claim." + this.name().toLowerCase();
    }
    
    public ItemStack getDisplayItem(final Player player) {
        if (!player.hasPermission(this.getPermissionNode())) {
            return LocalItemUtils.getLockedPlaceholder(player, this);
        }
        if (CCAPI.hasClaimedCosmicCrate(player, this)) {
            return LocalItemUtils.changeType(CCAPI.getCosmicCrate(this, player.getName()), Material.STAINED_GLASS_PANE, (short)5);
        }
        return CCAPI.getCosmicCrate(this, player.getName());
    }
    
    public String getRawName() {
        return this.rawName;
    }
    
    public CrateCategory getCategory() {
        return this.category;
    }
    
    public CosmicCrateData getCrateData() {
        return this.crateData;
    }
}
