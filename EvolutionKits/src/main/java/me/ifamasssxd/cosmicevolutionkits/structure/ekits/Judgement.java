/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.arkhamnetwork.Arkkit.patches.ExpBottles
 *  org.arkhamnetwork.Arkkit.patches.McmmoCredits
 *  org.arkhamnetwork.enchantments.EnchantmentBook
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.ifamasssxd.cosmicevolutionkits.structure.ekits;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKit;
import me.ifamasssxd.cosmicevolutionkits.utils.EnchantedItemBuilder;
import me.ifamasssxd.cosmicevolutionkits.utils.ItemBuilder;
import org.arkhamnetwork.Arkkit.patches.ExpBottles;
import org.arkhamnetwork.Arkkit.patches.McmmoCredits;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Judgement
extends EvolutionKit {
    private ChatColor mainColor = ChatColor.DARK_GRAY;

    private int getRandomEnchantmentLevel(String name, int kitLevel) {
        int maxLevel = this.getMaxLevel(name);
        if (maxLevel == 1) {
            return 1;
        }
        return Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(maxLevel - 2), maxLevel));
    }

    @Override
    public List<ItemStack> getItems(Player player, int kitLevel) {
        ArrayList items = Lists.newArrayList();
        EnchantedItemBuilder banhammer_axe = new EnchantedItemBuilder(ItemBuilder.buildItem(Material.DIAMOND_AXE, this.mainColor + ChatColor.BOLD.toString() + "The Banhammer (" + ChatColor.RED + "LVL " + kitLevel + this.mainColor + ChatColor.BOLD + ")", this.mainColor + "For eons thought to be nothing more than Cosmonaut folklore,", this.mainColor + "the very weapon used by admins to banish criminals", this.mainColor + "to the furthest reaches of the galaxies.", "", this.mainColor + "" + ChatColor.BOLD + "Kit Level: " + ChatColor.RED + kitLevel + " / 10"));
        double d = (double)kitLevel * 0.025;
        if (EvolutionKit.hasEnchant("Rage")) banhammer_axe.addCustomEnchant("Rage", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(this.getMaxLevel("Rage") - 2), this.getMaxLevel("Rage"))));
        if (EvolutionKit.hasEnchant("Devour")) banhammer_axe.addCustomEnchant("Devour", this.getRandomEnchantmentLevel("Devour", kitLevel));
        if (EvolutionKit.hasEnchant("Bleed")) banhammer_axe.addCustomEnchant("Bleed", this.getRandomEnchantmentLevel("Bleed", kitLevel));
        if (EvolutionKit.hasEnchant("Cleave")) banhammer_axe.addCustomEnchant("Cleave", this.getRandomEnchantmentLevel("Cleave", kitLevel));
        if (EvolutionKit.hasEnchant("Blessed")) banhammer_axe.addCustomEnchant("Blessed", this.getRandomEnchantmentLevel("Blessed", kitLevel));
        if (kitLevel >= 5) {
            int maxLevel = kitLevel == 10 ? this.getMaxLevel("Insanity") - this.random.nextInt(3) : this.getMaxLevel("Insanity") - 2;
            if (EvolutionKit.hasEnchant("Insanity")) banhammer_axe.addCustomEnchant("Insanity", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(maxLevel) - 2, maxLevel)));
        }
        if (kitLevel >= 6 && this.random.nextDouble() <= 0.3 + (double)(kitLevel - 4) * 0.06) {
            if (EvolutionKit.hasEnchant("Soul Trap")) banhammer_axe.addCustomEnchant("Soul Trap", kitLevel == 10 ? this.random.nextInt(2) + 1 : 1);
        }
        if (kitLevel >= 7 && this.random.nextDouble() <= 0.35 + (double)(kitLevel - 4) * 0.05) {
            if (EvolutionKit.hasEnchant("Anti Gank")) banhammer_axe.addCustomEnchant("Anti Gank", this.getRandomEnchantmentLevel("Anti Gank", kitLevel));
        }
        if (kitLevel >= 7 && this.random.nextDouble() <= Math.min(0.7, 0.2 + (double)(kitLevel - 4) * 0.06)) {
            if (EvolutionKit.hasEnchant("Blacksmith")) banhammer_axe.addCustomEnchant("Blacksmith", this.getMaxLevel("Blacksmith"));
            if (EvolutionKit.hasEnchant("Master Blacksmith")) banhammer_axe.addCustomEnchant("Master Blacksmith", this.getRandomEnchantmentLevel("Master Blacksmith", kitLevel));
        } else {
            if (EvolutionKit.hasEnchant("Blacksmith")) banhammer_axe.addCustomEnchant("Blacksmith", this.getRandomEnchantmentLevel("Blacksmith", kitLevel));
        }
        if (kitLevel >= 8 && this.random.nextDouble() <= 0.35 + (double)(kitLevel - 4) * 0.05) {
            if (EvolutionKit.hasEnchant("Silence")) banhammer_axe.addCustomEnchant("Silence", this.getRandomEnchantmentLevel("Silence", kitLevel));
        }
        if (kitLevel >= 8) {
            if (EvolutionKit.hasEnchant("Lifesteal")) banhammer_axe.addCustomEnchant("Lifesteal", this.getRandomEnchantmentLevel("Lifesteal", kitLevel));
        }
        if (EvolutionKit.hasEnchant("Pummel")) banhammer_axe.addCustomEnchant("Pummel", this.getRandomEnchantmentLevel("Pummel", kitLevel));
        if (EvolutionKit.hasEnchant("Arrow Break")) banhammer_axe.addCustomEnchant("Arrow Break", this.getRandomEnchantmentLevel("Arrow Break", kitLevel));
        if (EvolutionKit.hasEnchant("Ravenous")) banhammer_axe.addCustomEnchant("Ravenous", this.getRandomEnchantmentLevel("Ravenous", kitLevel));
        items.add(EnchantmentBook.applyTransmogScroll((ItemStack)banhammer_axe.addEnchant(Enchantment.DAMAGE_ALL, 5).addEnchant(Enchantment.FIRE_ASPECT, 2).addEnchant(Enchantment.DURABILITY, 4).build()));
        EnchantedItemBuilder judgment_helmet = new EnchantedItemBuilder(ItemBuilder.buildItem(Material.DIAMOND_CHESTPLATE, this.mainColor + ChatColor.BOLD.toString() + "Divine Robes of Judgement (" + ChatColor.RED + "LVL " + kitLevel + this.mainColor + ChatColor.BOLD + ")", this.mainColor + "A firey shroud that echoes absolute", this.mainColor + "power and domination. No mere human", this.mainColor + "would dare stand against its owner.", "", this.mainColor + "" + ChatColor.BOLD + "Kit Level: " + ChatColor.RED + kitLevel + " / 10"));
        if (EvolutionKit.hasEnchant("Overload")) judgment_helmet.addCustomEnchant("Overload", this.getRandomEnchantmentLevel("Overload", kitLevel));
        if (EvolutionKit.hasEnchant("Enlighted")) judgment_helmet.addCustomEnchant("Enlighted", this.getRandomEnchantmentLevel("Enlighted", kitLevel));
        if (this.random.nextDouble() <= 0.03 + (double)kitLevel * 0.03 && kitLevel >= 6) {
            if (EvolutionKit.hasEnchant("Deathbringer")) judgment_helmet.addCustomEnchant("Deathbringer", this.getMaxLevel("Deathbringer"));
            if (EvolutionKit.hasEnchant("Planetary Deathbringer")) judgment_helmet.addCustomEnchant("Planetary Deathbringer", this.getRandomEnchantmentLevel("Planetary Deathbringer", kitLevel));
        } else {
            if (EvolutionKit.hasEnchant("Deathbringer")) judgment_helmet.addCustomEnchant("Deathbringer", this.getRandomEnchantmentLevel("Deathbringer", kitLevel));
        }
        if (kitLevel >= 6) {
            if (EvolutionKit.hasEnchant("Armored")) judgment_helmet.addCustomEnchant("Armored", this.getRandomEnchantmentLevel("Armored", kitLevel));
        }
        if (kitLevel >= 8) {
            int maxLevel = kitLevel == 10 ? this.getMaxLevel("Leadership") - this.random.nextInt(3) : this.getMaxLevel("Leadership") - 2;
            if (EvolutionKit.hasEnchant("Leadership")) judgment_helmet.addCustomEnchant("Leadership", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(maxLevel) - 2, maxLevel)));
        }
        if (this.random.nextDouble() <= 0.06 + (double)(kitLevel - 3) * 0.076 && kitLevel >= 6) {
            if (EvolutionKit.hasEnchant("Diminish")) judgment_helmet.addCustomEnchant("Diminish", this.getMaxLevel("Diminish"));
            if (EvolutionKit.hasEnchant("Vengeful Diminish")) judgment_helmet.addCustomEnchant("Vengeful Diminish", this.getRandomEnchantmentLevel("Vengeful Diminish", kitLevel));
        } else {
            if (EvolutionKit.hasEnchant("Diminish")) judgment_helmet.addCustomEnchant("Diminish", this.getRandomEnchantmentLevel("Diminish", kitLevel));
        }
        if (EvolutionKit.hasEnchant("Marksman")) judgment_helmet.addCustomEnchant("Marksman", this.getRandomEnchantmentLevel("Marksman", kitLevel));
        int angelicMaxLevel = kitLevel == 10 ? this.getMaxLevel("Angelic") - this.random.nextInt(3) : this.getMaxLevel("Angelic") - 2;
        if (EvolutionKit.hasEnchant("Angelic")) judgment_helmet.addCustomEnchant("Angelic", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(angelicMaxLevel) - 2, angelicMaxLevel)));
        int voodMaxLevel = kitLevel == 10 ? this.getMaxLevel("Voodoo") - this.random.nextInt(3) : this.getMaxLevel("Voodoo") - 2;
        if (EvolutionKit.hasEnchant("Voodoo")) judgment_helmet.addCustomEnchant("Voodoo", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(voodMaxLevel) - 2, voodMaxLevel)));
        int luckyMaxLevel = kitLevel == 10 ? this.getMaxLevel("Lucky") - this.random.nextInt(4) : this.getMaxLevel("Lucky") - 3;
        if (EvolutionKit.hasEnchant("Lucky")) judgment_helmet.addCustomEnchant("Lucky", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(luckyMaxLevel) - 2, luckyMaxLevel)));
        if (EvolutionKit.hasEnchant("Obsidianshield")) judgment_helmet.addCustomEnchant("Obsidianshield", this.getRandomEnchantmentLevel("Obsidianshield", kitLevel));
        if (kitLevel >= 4) {
            if (EvolutionKit.hasEnchant("Heavy")) judgment_helmet.addCustomEnchant("Heavy", this.getRandomEnchantmentLevel("Heavy", kitLevel));
        }
        if (EvolutionKit.hasEnchant("Creeper Armor")) judgment_helmet.addCustomEnchant("Creeper Armor", this.getRandomEnchantmentLevel("Creeper Armor", kitLevel));
        int destructionMaxLevel = kitLevel == 10 ? this.getMaxLevel("Destruction") - this.random.nextInt(3) : this.getMaxLevel("Destruction") - 2;
        if (EvolutionKit.hasEnchant("Destruction")) judgment_helmet.addCustomEnchant("Destruction", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(destructionMaxLevel) - 2, destructionMaxLevel)));
        items.add(EnchantmentBook.applyTransmogScroll((ItemStack)judgment_helmet.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).addEnchant(Enchantment.DURABILITY, 3).build()));
        EnchantedItemBuilder karma_boots = new EnchantedItemBuilder(ItemBuilder.buildItem(Material.DIAMOND_BOOTS, this.mainColor.toString() + ChatColor.BOLD + "Boots of Karma (" + ChatColor.RED + "LVL " + kitLevel + this.mainColor + ChatColor.BOLD + ")", this.mainColor + "Both acts of great good and great evil", this.mainColor + "have been committed in these boots.", this.mainColor + "What type of acts will you commit?", "", this.mainColor + "" + ChatColor.BOLD + "Kit Level: " + ChatColor.RED + kitLevel + " / 10"));
        if (EvolutionKit.hasEnchant("Gears")) karma_boots.addCustomEnchant("Gears", this.getRandomEnchantmentLevel("Gears", kitLevel));
        if (EvolutionKit.hasEnchant("Enlighted")) karma_boots.addCustomEnchant("Enlighted", this.getRandomEnchantmentLevel("Enlighted", kitLevel));
        if (EvolutionKit.hasEnchant("Deathbringer")) karma_boots.addCustomEnchant("Deathbringer", this.getRandomEnchantmentLevel("Deathbringer", kitLevel));
        int springMaxLevel = kitLevel == 10 ? this.getMaxLevel("Springs") - this.random.nextInt(2) : this.getMaxLevel("Springs") - 2;
        if (EvolutionKit.hasEnchant("Springs")) karma_boots.addCustomEnchant("Springs", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(springMaxLevel) - 2, springMaxLevel)));
        if (EvolutionKit.hasEnchant("Marksman")) karma_boots.addCustomEnchant("Marksman", this.getRandomEnchantmentLevel("Marksman", kitLevel));
        if (EvolutionKit.hasEnchant("Stormcaller")) karma_boots.addCustomEnchant("Stormcaller", this.getRandomEnchantmentLevel("Stormcaller", kitLevel));
        if (EvolutionKit.hasEnchant("Hardened")) karma_boots.addCustomEnchant("Hardened", this.getRandomEnchantmentLevel("Hardened", kitLevel));
        if (kitLevel >= 4) {
            if (EvolutionKit.hasEnchant("Angelic")) karma_boots.addCustomEnchant("Angelic", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(angelicMaxLevel) - 2, angelicMaxLevel)));
            int heavyMaxLevel = kitLevel == 10 ? this.getMaxLevel("Heavy") - this.random.nextInt(2) : this.getMaxLevel("Heavy") - 2;
            if (EvolutionKit.hasEnchant("Heavy")) karma_boots.addCustomEnchant("Heavy", Math.max(1, Math.min(kitLevel / 2 + this.random.nextInt(heavyMaxLevel) - 2, heavyMaxLevel)));
        }
        if (kitLevel >= 6) {
            int enderwalkerMax = kitLevel == 10 ? this.getMaxLevel("Ender Walker") - this.random.nextInt(2) : this.getMaxLevel("Ender Walker") - 2;
            if (EvolutionKit.hasEnchant("Ender Walker")) karma_boots.addCustomEnchant("Ender Walker", Math.max(1, Math.min(kitLevel / 2 + (this.random.nextInt(enderwalkerMax) - 2), enderwalkerMax)));
        }
        if (kitLevel >= 9) {
            if (EvolutionKit.hasEnchant("Rocket Escape")) karma_boots.addCustomEnchant("Rocket Escape", this.getRandomEnchantmentLevel("Rocket Escape", kitLevel));
        }
        if (EvolutionKit.hasEnchant("Tank")) karma_boots.addCustomEnchant("Tank", this.getRandomEnchantmentLevel("Tank", kitLevel));
        if (this.random.nextDouble() <= 0.7 - (double)kitLevel * 0.045 || kitLevel < 4) {
            if (EvolutionKit.hasEnchant("Resilience")) karma_boots.addCustomEnchant("Resilience", this.getRandomEnchantmentLevel("Resilience", kitLevel));
        } else {
            if (EvolutionKit.hasEnchant("Phoenix")) karma_boots.addCustomEnchant("Phoenix", this.getRandomEnchantmentLevel("Phoenix", kitLevel));
        }
        karma_boots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchant(Enchantment.PROTECTION_FALL, 2).addEnchant(Enchantment.DURABILITY, 3);
        items.add(EnchantmentBook.applyTransmogScroll((ItemStack)karma_boots.build()));
        items.add(McmmoCredits.buildMcmmoCreditItem((int)(5 * kitLevel)));
        items.add(ExpBottles.getXpBottle((int)(kitLevel == 1 ? 25000 : (kitLevel == 2 ? 50000 : (kitLevel == 3 ? 100000 : (kitLevel == 4 ? 200000 : (kitLevel == 5 ? 350000 : (kitLevel == 6 ? 500000 : (kitLevel == 7 ? 750000 : (kitLevel == 8 ? 1000000 : (kitLevel == 9 ? 1500000 : (kitLevel == 10 ? 2000000 : 25000)))))))))), (String)"Judgment VKit"));
        return items;
    }

    @Override
    public String getItemName() {
        return ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "Judgement";
    }

    @Override
    public List<String> getLore() {
        return Lists.newArrayList(new String[]{"", this.mainColor + ChatColor.BOLD.toString() + "* " + this.mainColor + ChatColor.BOLD + "The Banhammer", this.mainColor + ChatColor.BOLD.toString() + "* " + this.mainColor + ChatColor.BOLD + "Divine Robes of Judgement", this.mainColor + ChatColor.BOLD.toString() + "* " + this.mainColor + ChatColor.BOLD + "Boots of Karma", this.mainColor + ChatColor.BOLD.toString() + "* " + this.mainColor + ChatColor.BOLD + "mcMMO Credits", this.mainColor + ChatColor.BOLD.toString() + "* " + this.mainColor + ChatColor.BOLD + "EXP", "", ChatColor.GRAY + "Will you use this kit for good, or evil? ", ChatColor.GOLD + "* Spring 2018"});
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.DIAMOND_AXE;
    }

    @Override
    public short getDisplayData() {
        return 0;
    }

    @Override
    public int getInventorySlot() {
        return 5;
    }
}

