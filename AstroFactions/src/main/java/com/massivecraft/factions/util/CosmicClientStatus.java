package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Astro's "Cosmic Client" hover line (same check as AstroCore's chat hover:
 * CosmicPlayers.get().getCosmicPlayer(player).isOnCosmicClient()). CCVerify depends on Factions, so it is looked up
 * through CCVerify's own class loader at hover time instead of being a dependency of this plugin.
 */
public final class CosmicClientStatus {
    public static final String ON = "&6* &eCosmic Client: &a&l✓";
    public static final String OFF = "&6* &eCosmic Client: &c&l✗";
    public static final String OFFLINE = "&6* &eCosmic Client: &7Offline";
    public static final String UNKNOWN = "&6* &eCosmic Client: &7Unknown";

    private CosmicClientStatus() {
    }

    public static String line(FPlayer fplayer) {
        Player player = fplayer.getPlayer();
        if (!fplayer.isOnline() || player == null) {
            return OFFLINE;
        }
        Boolean onClient = isOnCosmicClient(player);
        if (onClient == null) {
            return UNKNOWN;
        }
        return onClient ? ON : OFF;
    }

    /** true/false from CCVerify, or null when CCVerify isn't running or has no record of the player yet. */
    public static Boolean isOnCosmicClient(Player player) {
        Plugin ccverify = Bukkit.getPluginManager().getPlugin("CCVerify");
        if (ccverify == null || !ccverify.isEnabled()) {
            return null;
        }
        try {
            Class<?> players = Class.forName("com.astropvp.cc.player.CosmicPlayers", true, ccverify.getClass().getClassLoader());
            Object registry = players.getMethod("get").invoke(null);
            Object cosmicPlayer = players.getMethod("getCosmicPlayer", Player.class).invoke(registry, player);
            if (cosmicPlayer == null) {
                return null;
            }
            Method check = cosmicPlayer.getClass().getMethod("isOnCosmicClient");
            return (Boolean) check.invoke(cosmicPlayer);
        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            // a normal client returns false; isOnCosmicClient() only throws if CCVerify has no packet list for a flagged player
            return null;
        }
    }
}
