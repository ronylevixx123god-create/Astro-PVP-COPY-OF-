package net.astropvp.weaponbonuses;

import me.bryside.weaponrunes.commands.CommandGiveWeaponRune;
import me.bryside.weaponrunes.commands.CommandWeaponBonus;
import me.bryside.weaponrunes.items.WeaponRuneExtractor;
import me.bryside.weaponrunes.listeners.WeaponBonusListener;
import java.util.Arrays;

import co.aikar.commands.PaperCommandManager;
import me.ifamasssxd.cosmicskycustoms.CosmicSkyCustomItems;
import net.astropvp.weaponbonuses.command.WeaponBonusCommand;
import net.astropvp.weaponbonuses.item.WeaponBonusExtractorItem;
import net.astropvp.weaponbonuses.item.WeaponBonusItem;
import net.astropvp.weaponbonuses.listener.BonusListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AstroWeaponBonuses extends JavaPlugin {
    private static AstroWeaponBonuses instance;

    public void onEnable() {
        instance = this;
        CosmicSkyCustomItems.get().getItemManager().registerCustomItem(WeaponBonusItem.getInstance());
        CosmicSkyCustomItems.get().getItemManager().registerCustomItem(WeaponBonusExtractorItem.getInstance());
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new WeaponBonusCommand());
        paperCommandManager.getCommandCompletions().registerCompletion("bonusWeaponTypes", (context) -> {
            return Arrays.asList("champion", "ultra", "superior");
        });
        this.getServer().getPluginManager().registerEvents(new BonusListener(), this);

        // Astro's weapon bonus orbs: /weaponbonus menu, /giveweaponbonus, applying them and the rune extractor
        CosmicSkyCustomItems.get().getItemManager().registerCustomItem(new WeaponRuneExtractor());
        this.getCommand("giveweaponbonus").setExecutor(new CommandGiveWeaponRune());
        this.getCommand("weaponbonus").setExecutor(new CommandWeaponBonus());
        this.getServer().getPluginManager().registerEvents(new WeaponBonusListener(), this);
    }

    public static AstroWeaponBonuses getInstance() {
        return instance;
    }

    public void onDisable() {
    }
}
