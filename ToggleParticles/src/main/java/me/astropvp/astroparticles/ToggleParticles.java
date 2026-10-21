package me.astropvp.astroparticles;

import me.astropvp.astroparticles.commands.CommandToggleParticles;
import me.astropvp.astroparticles.listeners.DamageIndicatorListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ToggleParticles extends JavaPlugin implements Listener {
    public static ToggleParticles DamageIndicators;

    @Override
    public void onEnable() {
        DamageIndicators = this;
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.registerListeners();
        this.registerCommands();
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    @Override
    public void onDisable() {
        DamageIndicators = null;
    }

    public static ToggleParticles getInstance() {
        return DamageIndicators;
    }

    public void registerListeners() {
        this.registerListener(new DamageIndicatorListener());
        this.registerListener(this);
    }

    public void registerCommands() {
        this.getCommand("toggleparticles").setExecutor(new CommandToggleParticles());
    }

    public void registerListener(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, this);
    }
}
