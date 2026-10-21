package me.ifamasssxd.cosmicheroicenvoys.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

// Heroic Envoy Boss event, restored from Astro's older Core-0.1.jar (January 2021); final Astro had switched it off.
public class LocationUtils {
    public static Location getLocation(String spawn) {
        if (spawn == null || !spawn.contains(":")) {
            return null;
        }
        String[] args = spawn.split(":");
        World bukkit = Bukkit.getWorld(args[0]);
        if (bukkit == null || args.length < 4) {
            return null;
        }
        return new Location(bukkit, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
    }

    public static String getString(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }
}
