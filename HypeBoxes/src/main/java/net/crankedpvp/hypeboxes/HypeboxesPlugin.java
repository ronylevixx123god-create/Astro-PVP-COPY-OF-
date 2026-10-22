package net.crankedpvp.hypeboxes;

import lombok.Getter;
import me.mattstudios.mf.base.CommandManager;
import net.crankedpvp.hypeboxes.commands.HypeBoxCommand;
import net.crankedpvp.hypeboxes.commands.ViewBattleOpenCommand;
import net.crankedpvp.hypeboxes.commands.ViewHypeboxOpenCommand;
import net.crankedpvp.hypeboxes.inventory.HypeBoxAnimationInventory;
import net.crankedpvp.hypeboxes.inventory.HypeboxBattleCreateInventory;
import net.crankedpvp.hypeboxes.inventory.HypeboxBattleInventory;
import net.crankedpvp.hypeboxes.language.FileHandler;
import net.crankedpvp.hypeboxes.listeners.BattleQueueListener;
import net.crankedpvp.hypeboxes.listeners.HypeboxOpenListener;
import net.crankedpvp.hypeboxes.listeners.HypeboxPreviewListener;
import net.crankedpvp.hypeboxes.struct.HypeBoxManager;
import net.crankedpvp.hypeboxes.struct.HypeBoxRegister;
import net.crankedpvp.hypeboxes.struct.battle.BattleQueueManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
public class HypeboxesPlugin extends JavaPlugin {

    @Getter
    private static HypeboxesPlugin instance;

    @Getter
    private FileHandler fileHandler;

    @Getter
    private HypeBoxRegister hypeBoxRegister;

    @Getter
    private HypeBoxManager hypeBoxManager;

    @Getter
    private CommandManager commandManager;

    @Getter
    private Map<UUID, HypeBoxAnimationInventory> hypeboxesBeingOpened;

    @Getter
    private Map<UUID, HypeboxBattleInventory> hypeboxBattles;

    @Getter
    private final Set<HypeBoxAnimationInventory> activeOpenings = new HashSet<>();

    @Getter
    private final Set<HypeboxBattleCreateInventory> activeBattleCreates = new HashSet<>();


    @Override
    public void onEnable() {

        instance = this;

        this.fileHandler = new FileHandler(this);
        this.fileHandler.load();

        this.hypeBoxRegister = new HypeBoxRegister(this);
        this.hypeBoxManager = new HypeBoxManager(this);

        this.hypeBoxRegister.clearHypeboxMap();
        this.hypeBoxManager.createDefaultConfig("-example");
        this.hypeBoxManager.initializeAll();

        this.commandManager = new CommandManager(this);
        this.registerCommandCompletion();
        this.commandManager.register(new HypeBoxCommand(this));
        this.commandManager.register(new ViewHypeboxOpenCommand(this));
        this.commandManager.register(new ViewBattleOpenCommand(this));

        this.hypeboxesBeingOpened = new HashMap<>();
        this.hypeboxBattles = new HashMap<>();

        this.registerListeners();
    }

    @Override
    public void onDisable() {
        // a restart must not swallow rewards or boxes: finish openings, refund battles that haven't been played
        for (HypeBoxAnimationInventory opening : new ArrayList<>(this.activeOpenings)) {
            opening.deliver();
            opening.getPlayer().closeInventory();
        }
        for (HypeboxBattleCreateInventory create : new ArrayList<>(this.activeBattleCreates)) {
            create.refund();
            create.getPlayer().closeInventory();
        }
        for (org.bukkit.entity.Player queued : new ArrayList<>(BattleQueueManager.getBattleQueue().keySet())) {
            BattleQueueManager.refund(queued);
        }
        if (this.hypeboxBattles != null) this.hypeboxBattles.values().forEach(HypeboxBattleInventory::refundIfUnfinished);
    }

    private void registerListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new HypeboxOpenListener(this), this);
        pluginManager.registerEvents(new HypeboxPreviewListener(this), this);
        pluginManager.registerEvents(new BattleQueueListener(), this);
    }

    public void registerCommandCompletion() {
        this.commandManager.getCompletionHandler().register("#hypeboxes", input -> this.hypeBoxRegister.getHypeboxesAsIDs());
    }
}
