package net.astropvp.kitreset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKits;
import me.cosmicpvp.CosmicGlobalKits.database.SQLManager;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitData;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitUser;

/**
 * CosmicGlobalKits mkits (/mkit: Ghost, Necromancer, Death Knight). The cooldown is the cached GlobalKitUser's mastery
 * kit data (last used; the tier is kept) and the masterykit_userdata table (last_use).
 */
final class MasteryKitsSystem implements AstroKitReset.KitSystem {

    /** null mkit = all mkits */
    static void reset(MasteryKits mkit, Collection<UUID> players) {
        CosmicGlobalKits plugin = CosmicGlobalKits.getPlugin();
        Collection<UUID> cached = players != null ? players : new ArrayList<UUID>(plugin.getGlobalKitUsers().keySet());
        for (UUID uuid : cached) {
            GlobalKitUser user = plugin.getGlobalKitUser(uuid);
            if (user == null) {
                continue;
            }
            for (Map.Entry<MasteryKits, GlobalKitData> data : user.getMasteryKits().entrySet()) {
                if (mkit == null || data.getKey() == mkit) {
                    data.getValue().setLastUsed(0L);
                }
            }
        }
        String where = mkit == null ? "" : " AND masteryKit = '" + mkit.name() + "'";
        if (players == null) {
            SQLManager.sql_query.add("UPDATE " + CosmicGlobalKits.mastery_table + " SET last_use = 0 WHERE 1 = 1" + where + ";");
        } else {
            for (UUID uuid : players) {
                SQLManager.sql_query.add("UPDATE " + CosmicGlobalKits.mastery_table + " SET last_use = 0 WHERE uuid = '" + uuid + "'" + where + ";");
            }
        }
    }

    @Override
    public List<AstroKitReset.Kit> kits() {
        List<AstroKitReset.Kit> kits = new ArrayList<AstroKitReset.Kit>();
        for (MasteryKits mkit : MasteryKits.values()) {
            kits.add(new AstroKitReset.Kit() {
                @Override
                public String name() {
                    return mkit.name().toLowerCase();
                }

                @Override
                public void reset(Collection<UUID> players) {
                    MasteryKitsSystem.reset(mkit, players);
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
