package me.astropvp.astroparticles.utils;

import me.astropvp.astroparticles.ToggleParticles;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

// Players with particles on are kept in config.yml (players-indicating) and marked with the
// "indicatingparticles" metadata while online.
public class ParticleUtils {
    public static boolean isPlayerIndicating(Player player) {
        return player.hasMetadata("indicatingparticles");
    }

    public static boolean isPlayerContained(Player player) {
        return getPlayers().contains(player.getUniqueId().toString());
    }

    public static void addPlayer(Player player) {
        List<String> players = getPlayers();
        if (!players.contains(player.getUniqueId().toString()))
            players.add(player.getUniqueId().toString());
        save(players);
        player.setMetadata("indicatingparticles", new FixedMetadataValue(ToggleParticles.getInstance(), null));
    }

    public static void removePlayer(Player player) {
        List<String> players = getPlayers();
        players.remove(player.getUniqueId().toString());
        save(players);
        if (player.hasMetadata("indicatingparticles")) {
            player.removeMetadata("indicatingparticles", ToggleParticles.getInstance());
        }
    }

    private static List<String> getPlayers() {
        if (ToggleParticles.getInstance().getConfig().contains("players-indicating")) {
            return new ArrayList<>(ToggleParticles.getInstance().getConfig().getStringList("players-indicating"));
        }
        return new ArrayList<>();
    }

    private static void save(List<String> players) {
        ToggleParticles.getInstance().getConfig().set("players-indicating", players);
        ToggleParticles.getInstance().saveConfig();
        ToggleParticles.getInstance().reloadConfig();
    }
}
