// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate;

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
        this.registerListeners();
        this.registerCommands();
    }
    
    public void onDisable() {

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
