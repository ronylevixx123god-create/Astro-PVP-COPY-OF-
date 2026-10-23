/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lilypad.client.connect.api.Connect
 *  me.ifamasssxd.cosmicskycustoms.CosmicSkyCustomItems
 *  me.ifamasssxd.cosmicskycustoms.struct.CosmicItem
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.ifamasssxd.cosmicevolutionkits;

import me.ifamasssxd.cosmicevolutionkits.commands.CommandEvolutionKit;
import me.ifamasssxd.cosmicevolutionkits.items.WildcardVkit;
import me.ifamasssxd.cosmicevolutionkits.listeners.InventoryListener;
import me.ifamasssxd.cosmicevolutionkits.listeners.ItemListener;
import me.ifamasssxd.cosmicevolutionkits.listeners.LoginListener;
import me.ifamasssxd.cosmicevolutionkits.sql.Database;
import me.ifamasssxd.cosmicevolutionkits.sql.SQLQueryCustomThread;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKitManager;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import me.ifamasssxd.cosmicskycustoms.CosmicSkyCustomItems;
import me.ifamasssxd.cosmicskycustoms.struct.CosmicItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CosmicEvolutionKits
extends JavaPlugin {
    private static CosmicEvolutionKits instance;
    private Database sqlDatabase;
    private EvolutionKitManager kitManager;
    private SQLQueryCustomThread sqlQueryThread = new SQLQueryCustomThread();
    private String serverName;

    public void onEnable() {
        instance = this;
        this.registerCommands();
        this.registerListeners();
        this.kitManager = new EvolutionKitManager();
        this.kitManager.startInventoryUpdater();
        // the SQL table is "<server>_evolutionKitData"; Astro read the server name from LilyPad Connect (its old proxy)
        this.saveDefaultConfig();
        this.serverName = this.getConfig().getString("server_name", "AstroPvP");
        this.sqlDatabase = new Database("sqlconfig.yml");
        if (!this.sqlDatabase.isConnected()) {
            Bukkit.getLogger().severe("Unable to connect to sql database! Please ensure the sql credentials are correct");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.sqlDatabase.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.serverName + "_evolutionKitData`(uuid varchar(64), name varchar(16), kitName varchar(64), kitLevel int, lastKitUse long, numberOfUses int, PRIMARY KEY (uuid, kitName));", false);
        this.sqlQueryThread = new SQLQueryCustomThread();
        this.sqlQueryThread.start();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            KitPlayer kit = new KitPlayer(pl.getUniqueId(), pl.getName());
            KitPlayer.kitPlayerMap.put(pl.getUniqueId(), kit);
            kit.loadData(false);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("CosmicSkyCustomItems")) {
            CosmicSkyCustomItems.get().getItemManager().registerCustomItem((CosmicItem)new WildcardVkit());
        }
    }

    public void onDisable() {
        if (this.sqlQueryThread != null) {
            this.sqlQueryThread.interrupt();
            this.sqlQueryThread.execute();
            this.sqlQueryThread = null;
        }
    }

    private void registerCommands() {
        this.getCommand("vkit").setExecutor((CommandExecutor)new CommandEvolutionKit());
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents((Listener)new ItemListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new LoginListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new InventoryListener(), (Plugin)this);
    }

    public static CosmicEvolutionKits get() {
        return instance;
    }

    public Database getSqlDatabase() {
        return this.sqlDatabase;
    }

    public EvolutionKitManager getKitManager() {
        return this.kitManager;
    }

    public SQLQueryCustomThread getSqlQueryThread() {
        return this.sqlQueryThread;
    }

    public String getServerName() {
        return this.serverName;
    }
}

