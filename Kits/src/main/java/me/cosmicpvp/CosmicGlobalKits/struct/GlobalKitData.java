/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.me.astropvp.cratekeys.net.astropvp.armorsets.utils.TimeUtils
 *  org.bukkit.entity.Player
 */
package me.cosmicpvp.CosmicGlobalKits.struct;

import com.cosmicpvp.cosmicutils.utils.TimeUtils;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKits;
import me.cosmicpvp.CosmicGlobalKits.database.SQLManager;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import org.bukkit.entity.Player;

public class GlobalKitData {
    private long lastUsed;
    private int kitTier = 0;

    public GlobalKitData(long lastUsed, int kitTier) {
        this.lastUsed = lastUsed;
        this.kitTier = kitTier;
    }





    public long getLastUsed() {
        return this.lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public int getKitTier() {
        return this.kitTier;
    }

    public void setKitTier(int kitTier) {
        this.kitTier = kitTier;
    }

    public String getCooldownString(MasteryKits kits) {
        if (this.lastUsed <= 0L) {
            return null;
        }
        long timeSinceUsed = System.currentTimeMillis() - this.getLastUsed();
        if (timeSinceUsed < kits.getExpiration()) {
            long timeTillAvailable = kits.getExpiration() - timeSinceUsed;
            if (this.getLastUsed() != 0L && timeTillAvailable > 0L) {
                return TimeUtils.formatSeconds((long)(timeTillAvailable / 1000L));
            }
        }
        return null;
    }

    public void handleUsed(Player pl, MasteryKits gkit) {
        this.lastUsed = System.currentTimeMillis();
        SQLManager.sql_query.add("INSERT INTO " + CosmicGlobalKits.mastery_table + "(uuid, last_use, masteryKit) VALUES('" + pl.getUniqueId().toString() + "', '" + this.lastUsed + "', '" + gkit.name() + "') ON DUPLICATE KEY UPDATE last_use=" + this.lastUsed + ";");
    }



}

