package net.astropvp.kitreset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKits;
import me.cosmicpvp.CosmicGlobalKits.database.SQLManager;
import me.cosmicpvp.CosmicGlobalKits.libs.GlobalKits;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitData;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitUser;
import me.cosmicpvp.CosmicGlobalKits.utils.GlobalCooldowns;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * CosmicGlobalKits gkits (/gkit). A cooldown lives in three places: the online player's metadata (what a claim checks),
 * the cached GlobalKitUser (copied into the metadata on login) and the globalkits_userdata table (last_use).
 */
final class GlobalKitsSystem implements AstroKitReset.KitSystem {

    /** null gkit = all gkits */
    static void reset(GlobalKits gkit, Collection<UUID> players) {
        CosmicGlobalKits plugin = CosmicGlobalKits.getPlugin();
        Collection<UUID> cached = players != null ? players : new ArrayList<UUID>(plugin.getGlobalKitUsers().keySet());
        for (UUID uuid : cached) {
            Player online = Bukkit.getPlayer(uuid);
            if (online != null) {
                if (gkit == null) {
                    GlobalCooldowns.clearCooldowns(online);
                } else {
                    GlobalCooldowns.removeCooldown(online, gkit);
                }
            }
            GlobalKitUser user = plugin.getGlobalKitUser(uuid);
            if (user != null) {
                for (Map.Entry<GlobalKits, GlobalKitData> data : user.getGlobalKits().entrySet()) {
                    if (gkit == null || data.getKey() == gkit) {
                        data.getValue().setLastUsed(0L);
                    }
                }
            }
        }
        if (players == null) {
            // everyone online without a cached user still has metadata
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (gkit == null) {
                    GlobalCooldowns.clearCooldowns(online);
                } else {
                    GlobalCooldowns.removeCooldown(online, gkit);
                }
            }
        }
        String where = gkit == null ? "" : " AND global_kit = '" + gkit.name() + "'";
        if (players == null) {
            SQLManager.sql_query.add("UPDATE " + CosmicGlobalKits.sql_table + " SET last_use = 0 WHERE 1 = 1" + where + ";");
        } else {
            for (UUID uuid : players) {
                SQLManager.sql_query.add("UPDATE " + CosmicGlobalKits.sql_table + " SET last_use = 0 WHERE player_uuid = '" + uuid + "'" + where + ";");
            }
        }
    }

    @Override
    public List<AstroKitReset.Kit> kits() {
        List<AstroKitReset.Kit> kits = new ArrayList<AstroKitReset.Kit>();
        for (GlobalKits gkit : GlobalKits.values()) {
            kits.add(new AstroKitReset.Kit() {
                @Override
                public String name() {
                    return gkit.getKitName();
                }

                @Override
                public void reset(Collection<UUID> players) {
                    GlobalKitsSystem.reset(gkit, players);
                }
            });
            // the enum name too (e.g. DIABLO), when it differs from the shown name
            if (!AstroKitReset.key(gkit.getKitName()).equals(AstroKitReset.key(gkit.name()))) {
                kits.add(new AstroKitReset.Kit() {
                    @Override
                    public String name() {
                        return gkit.name();
                    }

                    @Override
                    public void reset(Collection<UUID> players) {
                        GlobalKitsSystem.reset(gkit, players);
                    }
                });
            }
        }
        return kits;
    }

    @Override
    public void resetAll(Collection<UUID> players) {
        reset(null, players);
    }
}
