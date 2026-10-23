package org.arkhamnetwork.Arkkit.patches.combatlog;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * Astro's original task (decompiled from Astro's own compiled Arkkit.jar; the class was missing from the source, so
 * combat-log NPCs never went away): once an NPC's combat timer (combatNPC_TimeoutTime, extended by 10 s whenever it is
 * hit) has passed, the NPC is removed and its player is safe to log back in.
 */
public class DespawnNPCTask extends BukkitRunnable {
    public void run() {
        for (Map.Entry<String, NPC> entry : CombatLogLogoutListener.combatNPCs.entrySet()) {
            NPC n = entry.getValue();
            Entity e = n.getEntity();
            if (e == null || !e.hasMetadata("combatNPC_TimeoutTime")) continue;
            long timeout = e.getMetadata("combatNPC_TimeoutTime").get(0).asLong();
            if (System.currentTimeMillis() <= timeout) continue;
            if (n.isSpawned()) {
                n.despawn();
            }
            CombatLogLogoutListener.combatNPCs.remove(entry.getKey());
            CombatLogLogoutListener.combatNPCs_loot.remove(entry.getKey());
            n.destroy();
            CitizensAPI.getNPCRegistry().deregister(n);
        }
    }
}
