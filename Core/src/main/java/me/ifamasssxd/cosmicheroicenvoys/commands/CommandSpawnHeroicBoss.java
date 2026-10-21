package me.ifamasssxd.cosmicheroicenvoys.commands;

import me.ifamasssxd.cosmicheroicenvoys.managers.EnvoyManager;
import net.astropvp.core.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Astro's /spawnheroicboss from the older Core-0.1.jar (January 2021): spawns the Heroic Envoy Boss now and restarts
 * the spawn timer. In final Astro this command was empty (the event was turned off).
 */
public class CommandSpawnHeroicBoss implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        EnvoyManager manager = AstroCore.getInstance().getEnvoyManager();
        // the old command spawned at a null location (and errored) when no spawn was set yet
        if (manager.getBossLocation() == null) {
            sender.sendMessage(ChatColor.RED + "No boss spawn set! Stand at the spot and use /setheroicbossspawn first.");
            return true;
        }
        manager.spawnBoss();
        manager.startEnvoyTimer();
        return true;
    }
}
