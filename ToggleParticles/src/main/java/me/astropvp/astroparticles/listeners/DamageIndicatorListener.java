package me.astropvp.astroparticles.listeners;

import me.astropvp.astroparticles.ToggleParticles;
import me.astropvp.astroparticles.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class DamageIndicatorListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            ParticleUtils.addPlayer(player);
            player.setMetadata("indicatingparticles", new FixedMetadataValue(ToggleParticles.getInstance(), null));
        }
        if (ParticleUtils.isPlayerContained(player)) {
            player.setMetadata("indicatingparticles", new FixedMetadataValue(ToggleParticles.getInstance(), null));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("indicatingparticles")) {
            player.removeMetadata("indicatingparticles", ToggleParticles.getInstance());
        }
    }
}
