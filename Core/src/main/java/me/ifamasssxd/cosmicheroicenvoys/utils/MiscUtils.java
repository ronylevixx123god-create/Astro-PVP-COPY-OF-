package me.ifamasssxd.cosmicheroicenvoys.utils;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

// Heroic Envoy Boss event, restored from Astro's older Core-0.1.jar (January 2021).
public class MiscUtils {
    public static Entity getEntity(UUID uuid, World world) {
        for (Entity entity : world.getEntities()) {
            if (!entity.getUniqueId().equals(uuid)) continue;
            return entity;
        }
        return null;
    }
}
