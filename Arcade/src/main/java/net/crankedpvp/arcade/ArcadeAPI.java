package net.crankedpvp.arcade;

import com.earth2me.essentials.Essentials;
import net.crankedpvp.arcade.language.Language;
import net.crankedpvp.arcade.struct.ArcadeManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ArcadeAPI {
    public static String WORLD_NAME = "world_arcade";
    public static String SAFEZONE_REGION_NAME = "arcade_safezone";


    private static ArcadeManager arcadeManager = ArcadePlugin.getInstance().getArcadeManager();

    public static boolean canPvP(UUID uuid1, UUID uuid2) {
        return arcadeManager.inSameArcadeMatch(uuid1, uuid2);
    }

    /** Players in the arcade world; empty when the arcade map isn't installed. */
    public static List<Player> getArcadeWorldPlayers() {
        World world = Bukkit.getWorld(WORLD_NAME);
        return world == null ? Collections.emptyList() : world.getPlayers();
    }

    public static boolean inArcadeWorld(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return Bukkit.getPlayer(uuid).getWorld().getName().equals(WORLD_NAME);
    }

    public static boolean inArcadeMatch(UUID uuid) {
        return inArcadeWorld(uuid) && arcadeManager.inArcadeMatch(uuid);
    }



}
