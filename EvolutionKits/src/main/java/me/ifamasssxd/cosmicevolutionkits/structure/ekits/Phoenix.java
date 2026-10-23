/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils
 *  com.google.common.collect.Lists
 *  com.rit.sucy.EnchantmentAPI
 *  org.arkhamnetwork.enchantments.EnchantmentBook
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.ifamasssxd.cosmicevolutionkits.structure.ekits;

import com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils;
import com.google.common.collect.Lists;
import com.rit.sucy.EnchantmentAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKit;
import me.ifamasssxd.cosmicevolutionkits.utils.EnchantedItemBuilder;
import me.ifamasssxd.cosmicevolutionkits.utils.ItemBuilder;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Phoenix
extends EvolutionKit {
    @Override
    public List<ItemStack> getItems(Player player, int kitLevel) {
        ArrayList items = Lists.newArrayList();
        EnchantedItemBuilder sandalsOfThePhoenix = new EnchantedItemBuilder(ItemBuilder.buildItem(Material.DIAMOND_BOOTS, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Sandals of the Phoenix (" + ChatColor.LIGHT_PURPLE + "LVL " + kitLevel + ChatColor.DARK_PURPLE + ChatColor.BOLD + ")", ChatColor.DARK_PURPLE + "A fabled relic that grants the owner", ChatColor.DARK_PURPLE + "control over life and death itself.", "", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Kit Level: " + ChatColor.LIGHT_PURPLE + kitLevel + " / 10"));
        double d = (double)kitLevel * 0.025;
        if (EvolutionKit.hasEnchant("Gears")) sandalsOfThePhoenix.addCustomEnchant("Gears", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Tank")) sandalsOfThePhoenix.addCustomEnchant("Tank", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Enlighted")) sandalsOfThePhoenix.addCustomEnchant("Enlighted", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Deathbringer")) sandalsOfThePhoenix.addCustomEnchant("Deathbringer", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Spirit Link")) sandalsOfThePhoenix.addCustomEnchant("Spirit Link", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirit Link").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Spirit Link").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Springs")) sandalsOfThePhoenix.addCustomEnchant("Springs", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel())));
        if (kitLevel > 2) {
            if (EvolutionKit.hasEnchant("Valor")) sandalsOfThePhoenix.addCustomEnchant("Valor", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel())));
        }
        if (kitLevel > 3) {
            if (EvolutionKit.hasEnchant("Armored")) sandalsOfThePhoenix.addCustomEnchant("Armored", Math.max(1, Math.min(kitLevel / 2 - 3 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel())));
        }
        if (kitLevel > 6) {
            if (EvolutionKit.hasEnchant("Angelic")) sandalsOfThePhoenix.addCustomEnchant("Angelic", Math.max(1, Math.min(kitLevel - 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel())));
        }
        if (kitLevel > 8) {
            if (EvolutionKit.hasEnchant("Arrow Deflect")) sandalsOfThePhoenix.addCustomEnchant("Arrow Deflect", Math.max(1, Math.min(kitLevel / 2 - 5 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Arrow Deflect").getMaxLevel()), EnchantmentAPI.getEnchantment((String)"Arrow Deflect").getMaxLevel())));
        }
        if (Math.random() > d) {
            if (EvolutionKit.hasEnchant("Ender Shift")) sandalsOfThePhoenix.addCustomEnchant("Ender Shift", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel())));
        } else {
            if (EvolutionKit.hasEnchant("Phoenix")) sandalsOfThePhoenix.addCustomEnchant("Phoenix", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Phoenix").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Phoenix").getMaxLevel())));
        }
        items.add(EnchantmentBook.applyTransmogScroll((ItemStack)sandalsOfThePhoenix.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchant(Enchantment.PROTECTION_FALL, 4).addEnchant(Enchantment.DURABILITY, 3).build()));
        EnchantedItemBuilder sikanda = new EnchantedItemBuilder(ItemBuilder.buildItem(Material.DIAMOND_SWORD, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Sikanda (" + ChatColor.LIGHT_PURPLE + "LVL " + kitLevel + ChatColor.DARK_PURPLE + ChatColor.BOLD + ")", ChatColor.DARK_PURPLE + "The legendary flaming sword of Grograman,", ChatColor.DARK_PURPLE + "AKA \"The Many colored Death\". An ancient", ChatColor.DARK_PURPLE + "celtic demon that personifies destruction.", "", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Kit Level: " + ChatColor.LIGHT_PURPLE + kitLevel + " / 10"));
        if (EvolutionKit.hasEnchant("Disintegrate")) sikanda.addCustomEnchant("Disintegrate", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Disintegrate").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Disintegrate").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Blind")) sikanda.addCustomEnchant("Blind", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blind").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Blind").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Disarmor")) sikanda.addCustomEnchant("Disarmor", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Disarmor").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Disarmor").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Execute")) sikanda.addCustomEnchant("Execute", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Execute").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Execute").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Lifesteal")) sikanda.addCustomEnchant("Lifesteal", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Rage")) sikanda.addCustomEnchant("Rage", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel())));
        if (EvolutionKit.hasEnchant("Double Strike")) sikanda.addCustomEnchant("Double Strike", Math.max(1, Math.min(kitLevel / 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel())));
        if (kitLevel > 4) {
            if (EvolutionKit.hasEnchant("Silence")) sikanda.addCustomEnchant("Silence", Math.max(1, Math.min(kitLevel - 1 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel())));
        }
        if (kitLevel > 5) {
            if (EvolutionKit.hasEnchant("Inversion")) sikanda.addCustomEnchant("Inversion", Math.max(1, Math.min(kitLevel - 1 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Inversion").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Inversion").getMaxLevel())));
        }
        if (kitLevel > 7) {
            if (EvolutionKit.hasEnchant("Solitude")) sikanda.addCustomEnchant("Solitude", Math.max(1, Math.min(kitLevel - 2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Solitude").getMaxLevel() - 2), EnchantmentAPI.getEnchantment((String)"Solitude").getMaxLevel())));
        }
        if (kitLevel >= 9 && Math.random() < 0.05 * (double)(kitLevel - 8)) {
            if (EvolutionKit.hasEnchant("Divine Immolation")) sikanda.addCustomEnchant("Divine Immolation", Math.max(1, Math.min(2 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Divine Immolation").getMaxLevel()), EnchantmentAPI.getEnchantment((String)"Divine Immolation").getMaxLevel())));
        }
        if (kitLevel == 10 && Math.random() < 0.05) {
            items.add(ItemUtils.getTitleVoucher((String)"Phoenix"));
        }
        items.add(EnchantmentBook.applyTransmogScroll((ItemStack)sikanda.addEnchant(Enchantment.DAMAGE_ALL, 5).addEnchant(Enchantment.FIRE_ASPECT, 2).addEnchant(Enchantment.DAMAGE_UNDEAD, 5).addEnchant(Enchantment.DURABILITY, 3).build()));
        return items;
    }

    @Override
    public String getItemName() {
        return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Phoenix";
    }

    @Override
    public List<String> getLore() {
        return Lists.newArrayList(new String[]{"", ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "* " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Sandals of the Phoenix", ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "* " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Sikanda", "", ChatColor.GRAY + "Return from the ashes once more.", ChatColor.GOLD + "* Cosmic Renewal Patch"});
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.BLAZE_POWDER;
    }

    @Override
    public short getDisplayData() {
        return 0;
    }

    @Override
    public int getInventorySlot() {
        return 0;
    }
}

