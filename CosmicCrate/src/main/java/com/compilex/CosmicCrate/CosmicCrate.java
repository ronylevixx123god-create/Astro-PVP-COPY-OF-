// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate;

import com.compilex.CosmicCrate.libs.Version;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.sql.ResultSet;
import java.util.ArrayList;
import com.compilex.CosmicCrate.cmds.CommandGiveCC;
import me.ifamasssxd.cosmicmysterychest.CosmicMysteryChest;
import me.ifamasssxd.cosmicmysterychest.struct.MysteryChest;
import org.bukkit.command.CommandExecutor;
import com.compilex.CosmicCrate.cmds.CommandCosmicCrate;
import com.compilex.CosmicCrate.listeners.BlockListener;
import com.compilex.CosmicCrate.listeners.InventoryListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import com.compilex.CosmicCrate.listeners.PlayerListener;
import org.bukkit.Bukkit;
import com.compilex.CosmicCrate.libs.Version;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.plugin.java.JavaPlugin;

public class CosmicCrate extends JavaPlugin
{
    public static CopyOnWriteArrayList<String> sql_query;
    public static HashMap<String, List<Version>> claimedCosmicCrates;
    private static CosmicCrate instance;

    public void onEnable() {
        instance = this;
        this.loadClaims();
        this.registerListeners();
        this.registerCommands();
    }
    
    public void onDisable() {
        // a crate open during a restart still pays out (the close listener stops with the plugin)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("ccrate_loot_selector")) {
                ((CosmicCrateLootSelector) player.getMetadata("ccrate_loot_selector").get(0).value()).conclude();
                player.closeInventory();
            }
        }
        this.saveClaims();
    }

    /** which crate versions each player has claimed from /cc (was the SQL table Astro removed) */
    private void loadClaims() {
        claimedCosmicCrates = new HashMap<>();
        File file = new File(this.getDataFolder(), "claimed.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String uuid : yaml.getKeys(false)) {
            List<Version> versions = new ArrayList<>();
            for (String name : yaml.getStringList(uuid)) {
                try {
                    versions.add(Version.valueOf(name));
                } catch (IllegalArgumentException ignored) {
                }
            }
            claimedCosmicCrates.put(uuid, versions);
        }
    }

    public void saveClaims() {
        YamlConfiguration yaml = new YamlConfiguration();
        claimedCosmicCrates.forEach((uuid, versions) -> yaml.set(uuid, versions.stream().map(Enum::name).collect(Collectors.toList())));
        try {
            this.getDataFolder().mkdirs();
            yaml.save(new File(this.getDataFolder(), "claimed.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static CosmicCrate get() {
        return instance;
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new InventoryListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new BlockListener(), (Plugin)this);
    }


    private void registerCommands() {
        this.getCommand("cosmiccrate").setExecutor((CommandExecutor)new CommandCosmicCrate());
        this.getCommand("givecc").setExecutor((CommandExecutor)new CommandGiveCC());
    }

    

}
