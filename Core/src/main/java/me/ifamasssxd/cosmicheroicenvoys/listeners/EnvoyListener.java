package me.ifamasssxd.cosmicheroicenvoys.listeners;

import me.ifamasssxd.cosmicconquest.CosmicConquestAPI;
import me.ifamasssxd.cosmicheroicenvoys.managers.EnvoyManager;
import net.astropvp.core.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.concurrent.ThreadLocalRandom;

// Heroic Envoy Boss event, restored from Astro's older Core-0.1.jar (January 2021): the boss's death drops the envoy.
public class EnvoyListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        EnvoyManager manager = AstroCore.getInstance().getEnvoyManager();
        if (manager != null && manager.getBossUUID() != null && manager.getBossUUID().equals(event.getEntity().getUniqueId())) {
            manager.spawnEnvoyBlocks(manager.getBossLocation());
            if (Bukkit.getPluginManager().isPluginEnabled("CosmicConquests")) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), CosmicConquestAPI.createFactionToken(ThreadLocalRandom.current().nextInt(5) + 2));
            }
        }
    }
}
