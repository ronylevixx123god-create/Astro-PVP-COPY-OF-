/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.FactionUtils
 *  com.massivecraft.factions.Faction
 *  org.bukkit.ChatColor
 */
package me.ifamasssxd.cosmicfactionboosters.tasks;

import com.cosmicpvp.cosmicutils.utils.FactionUtils;
import com.massivecraft.factions.Faction;
import java.util.Map;
import me.ifamasssxd.cosmicfactionboosters.CosmicFactionBoosters;
import me.ifamasssxd.cosmicfactionboosters.enums.BoosterType;
import me.ifamasssxd.cosmicfactionboosters.struct.ActiveBooster;
import org.bukkit.ChatColor;

public class BoosterTask
implements Runnable {
    @Override
    public void run() {
        CosmicFactionBoosters.get().getBoosterManager().getFactionBoosters().forEach((fId, boosters) -> {
            for (Map.Entry booster : boosters.entrySet()) {
                ActiveBooster boost = (ActiveBooster)booster.getValue();
                if (boost.getSecondsElapsed() >= boost.getMaxSeconds()) {
                    boosters.remove(booster.getKey());
                    Faction faction = FactionUtils.getFactionById((String)fId);
                    if (faction != null) {
                        faction.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + boost.getMultiplier() + "x " + ChatColor.RED + ((BoosterType)((Object)((Object)booster.getKey()))).getName() + " Booster from " + ChatColor.RED + ChatColor.BOLD.toString() + boost.getWhoApplied() + ChatColor.RED + " has expired.");
                    }
                    if (!boosters.isEmpty()) continue;
                    CosmicFactionBoosters.get().getBoosterManager().getFactionBoosters().remove(fId);
                    CosmicFactionBoosters.get().getBoosterManager().saveActiveBoosters();
                    continue;
                }
                boost.setSecondsElapsed(boost.getSecondsElapsed() + 1);
            }
        });
    }
}

