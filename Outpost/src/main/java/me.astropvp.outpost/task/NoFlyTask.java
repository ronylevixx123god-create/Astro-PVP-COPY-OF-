package me.astropvp.outpost.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class NoFlyTask extends BukkitRunnable {

    @Override
    public void run() {
        World pluto = Bukkit.getWorld("world_outpost_pluto");
        World mars = Bukkit.getWorld("world_outpost_mars");
        // maps not installed
        if (pluto != null) for(Player player : pluto.getPlayers()) {
            if(player.isOp()) {
                continue;
            }
            if(player.isFlying()) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }
        if (mars != null) for(Player player : mars.getPlayers()) {
            if(player.isOp()) {
                continue;
            }
            if(player.isFlying()) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }


    }


}
