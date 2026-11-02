package net.astropvp.kitreset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;

/**
 * CosmicEvolutionKits vkits (/vkit). The cooldown is the cached KitPlayer's kit cooldowns (saved with REPLACE INTO, 0 when
 * there is none) and the <server>_evolutionKitData table (lastKitUse).
 */
final class EvolutionKitsSystem implements AstroKitReset.KitSystem {

    /** null vkit = all vkits */
    static void reset(EvolutionKits vkit, Collection<UUID> players) {
        Collection<UUID> cached = players != null ? players : new ArrayList<UUID>(KitPlayer.kitPlayerMap.keySet());
        for (UUID uuid : cached) {
            KitPlayer kitPlayer = KitPlayer.kitPlayerMap.get(uuid);
            if (kitPlayer == null || kitPlayer.isLoadingData()) {
                continue;
            }
            for (EvolutionKits kit : EvolutionKits.values()) {
                if ((vkit == null || kit == vkit) && kitPlayer.getKitCooldowns().remove(kit) != null) {
                    kitPlayer.save(kit);
                }
            }
        }
        CosmicEvolutionKits plugin = CosmicEvolutionKits.get();
        String table = "`" + plugin.getServerName() + "_evolutionKitData`";
        String where = vkit == null ? "" : " AND kitName = '" + vkit.name() + "'";
        if (players == null) {
            plugin.getSqlDatabase().executeUpdate("UPDATE " + table + " SET lastKitUse = 0 WHERE 1 = 1" + where + ";", true);
        } else {
            for (UUID uuid : players) {
                plugin.getSqlDatabase().executeUpdate("UPDATE " + table + " SET lastKitUse = 0 WHERE uuid = '" + uuid + "'" + where + ";", true);
            }
        }
    }

    @Override
    public List<AstroKitReset.Kit> kits() {
        List<AstroKitReset.Kit> kits = new ArrayList<AstroKitReset.Kit>();
        for (EvolutionKits vkit : EvolutionKits.values()) {
            kits.add(new AstroKitReset.Kit() {
                @Override
                public String name() {
                    return vkit.name().toLowerCase();
                }

                @Override
                public void reset(Collection<UUID> players) {
                    EvolutionKitsSystem.reset(vkit, players);
                }
            });
        }
        return kits;
    }

    @Override
    public void resetAll(Collection<UUID> players) {
        reset(null, players);
    }
}
