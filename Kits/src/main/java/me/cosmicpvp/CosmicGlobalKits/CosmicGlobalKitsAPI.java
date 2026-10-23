/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.compilex.CosmicDungeons.DungeonsRewardsAPI
 *  com.cosmicpvp.CosmicSpaceChests.CosmicSpaceChestsAPI
 *  com.cosmicpvp.CosmicSpaceChests.libs.ItemRarity
 *  com.cosmicpvp.CosmicSpaceChests.me.astropvp.cratekeys.net.astropvp.armorsets.utils.ItemUtils
 *  com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder
 *  com.cosmicpvp.cosmicutils.me.astropvp.cratekeys.net.astropvp.armorsets.utils.CC
 *  com.cosmicpvp.cosmicutils.me.astropvp.cratekeys.net.astropvp.armorsets.utils.GeneralUtil
 *  com.cosmicpvp.cosmicutils.me.astropvp.cratekeys.net.astropvp.armorsets.utils.ItemBuilder
 *  com.cosmicpvp.cosmicutils.me.astropvp.cratekeys.net.astropvp.armorsets.utils.JSONMessage
 *  com.cosmicpvp.cosmicutils.me.astropvp.cratekeys.net.astropvp.armorsets.utils.NBTWrapper
 *  com.google.common.collect.Lists
 *  com.rit.sucy.CustomEnchantment
 *  com.rit.sucy.EnchantmentAPI
 *  net.minecraft.server.v1_7_R4.NBTBase
 *  org.arkhamnetwork.Arkkit.patches.Christmas
 *  org.arkhamnetwork.Arkkit.patches.Drinks
 *  org.arkhamnetwork.Arkkit.patches.Halloween
 *  org.arkhamnetwork.Arkkit.patches.ItemID
 *  org.arkhamnetwork.Arkkit.patches.NewYears
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugins
 *  org.arkhamnetwork.enchantments.EnchantmentBook
 *  org.arkhamnetwork.enchantments.EnchantmentOrb
 *  org.arkhamnetwork.enchantments.RandomizationScroll
 *  org.arkhamnetwork.enchantments.libs.OrbType
 *  org.arkhamnetwork.enchantments.util.TieredEnchantments
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.me.astropvp.cratekeys.inventory.ItemStack
 *  org.bukkit.me.astropvp.cratekeys.inventory.meta.ItemMeta
 */
package me.cosmicpvp.CosmicGlobalKits;

import com.cosmicpvp.CosmicSpaceChests.CosmicSpaceChestsAPI;
import com.cosmicpvp.CosmicSpaceChests.libs.ItemRarity;
import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.*;
import com.google.common.collect.Lists;
import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import net.astropvp.core.AstroCore;
import me.cosmicpvp.CosmicGlobalKits.libs.GlobalKits;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKit;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitData;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitUser;
import me.cosmicpvp.CosmicGlobalKits.utils.GlobalCooldowns;
import me.cosmicpvp.CosmicGlobalKits.utils.ItemUtils;
import net.minecraft.server.v1_8_R3.NBTBase;
import org.arkhamnetwork.Arkkit.patches.*;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.arkhamnetwork.enchantments.EnchantmentOrb;
import org.arkhamnetwork.enchantments.RandomizationScroll;
import org.arkhamnetwork.enchantments.libs.OrbType;
import org.arkhamnetwork.enchantments.util.TieredEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CosmicGlobalKitsAPI {
    public static ItemStack createMasteryKitItem(MasteryKits kits, boolean addID) {
        ArrayList<String> lore = Lists.newArrayList("", CC.RedB + "NOTE: " + CC.Gray + "Once unlocked, you will lose", CC.Gray + "access to the required Global Kits", CC.Gray + "in exchange for access to this Mastery Kit!", "", CC.GoldB + "/gkit Requirements:");
        for (GlobalKits required : kits.getKitsRequired()) {
            lore.add(CC.GoldB + " * " + CC.Gold + required.getItemName() + CC.GoldB + " Tier III");
        }
        lore.add("");
        lore.add(CC.Gray + "Right-click to unlock this Mastery Kit!");
        ItemStack item = new NBTWrapper(new ItemBuilder(Material.INK_SACK, CC.LightPurpleB + "Mastery Kit " + CC.LightPurple + "(" + kits.getName() + CC.LightPurple + ")", new String[0]).lore((List)lore).data((short)DyeColor.CYAN.getDyeData()).build()).setString("cosmicType", "MASTERY_KIT").setNBTBase("conaxData", (NBTBase)new NBTTagCompoundBuilder().setString("kit", kits.name()).get()).build();
        if (addID && ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
            item = ItemID.assignItemID(null, (ItemStack)item);
        }
        return item;
    }







    public static int getKitTier(Player player, GlobalKits kit) {
        GlobalKitData found;
        GlobalKitUser user = CosmicGlobalKits.getPlugin().getGlobalKitUser(player.getUniqueId());
        if (user != null && user.getGlobalKits() != null && (found = user.getGlobalKits().get((Object)kit)) != null) {
            return found.getKitTier();
        }
        return 0;
    }

    public static boolean canLevelUpGkit(Player player, GlobalKits kit) {
        GlobalKitUser user = CosmicGlobalKits.getPlugin().getGlobalKitUser(player.getUniqueId());
        if (user == null || user.getGlobalKits() == null || user.isLoadingData()) {
            return false;
        }
        GlobalKitData da = user.getGlobalKits().get((Object)kit);
        return da == null || da.getKitTier() < 3;
    }

    public static void registerGkitLevels(Player player, GlobalKits kit) {
        String playerName = player.getName();
        GlobalKitUser user = CosmicGlobalKits.getPlugin().getGlobalKitUser(player.getUniqueId());
        if (user == null) {
            Bukkit.getLogger().info("Unable to find GKit user for " + playerName);
            return;
        }
        Map<GlobalKits, GlobalKitData> data = user.getGlobalKits();
        if (!data.containsKey((Object)kit)) {
            data.put(kit, new GlobalKitData(0L, 0));
            Bukkit.getLogger().info("Registering global kit data for new kit claim for " + playerName + "!");
        }
    }



    public static List<ItemStack> getGlobalKitItems(GlobalKits gkit, String playerName) {
        return CosmicGlobalKitsAPI.getGlobalKitItems(gkit, playerName, false);
    }

    /*
     * WARNING - void declaration
     */
    public static List<ItemStack> getGlobalKitItems(GlobalKits gkit, String playerName, boolean preview) {
        GlobalKitUser user;
        List<ItemStack> rewards = new ArrayList<ItemStack>();
        Player p = playerName != null ? Bukkit.getPlayer((String)playerName) : null;
        int kitTier = 0;
        if (p != null && (user = CosmicGlobalKits.getPlugin().getGlobalKitUser(p.getUniqueId())) != null && user.getGlobalKits() != null) {
            try {
                GlobalKitData kitData = user.getGlobalKits().get((Object)gkit);
                if (kitData != null && kitData.getKitTier() > 0) {
                    kitTier = kitData.getKitTier();
                }
            }
            catch (Exception kitData) {
                // empty catch block
            }
        }
        try {
            ItemStack boots;
            ItemStack leggings;
            Object chest;
            ItemStack helmet;
            if (gkit.getKitName().equals("spooky")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Grim Reaper Shroud", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "An onyx shroud, eminating death.");
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Curse").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Curse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Voodoo").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Voodoo").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirits").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirits").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.YELLOW.toString() + "Heroic Headless Chestplate of Nightmares", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A chestplate with a VERY spooky aura.");
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Headless Leggings of Nightmares", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A pair of leggings with a VERY spooky aura.");
                EnchantmentAPI.getEnchantment((String)"Plague Carrier").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Plague Carrier").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Wither").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Wither").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Headless Boots of Nightmares", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A pair of boots with a VERY spooky aura.");
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Molten").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Molten").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Headless Blade of Horror", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "An ancient blade imbued with the nightmares", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "of all who have fallen before it.");
                itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addEnchantment(Enchantment.DURABILITY, 2);
                EnchantmentAPI.getEnchantment((String)"Headless").addToItem(itemStack, 3);
                EnchantmentAPI.getEnchantment((String)"Blind").addToItem(itemStack, 3);
                EnchantmentAPI.getEnchantment((String)"Vampire").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Vampire").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Execute").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Execute").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Demonforged").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Demonforged").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deep Wounds").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deep Wounds").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Thundering Blow").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Thundering Blow").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                rewards.add(itemStack);
            }
            if (gkit.getKitName().equals("hyper")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Hyper Helmet of Hermes", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A legendary agile helmet.");
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trickster").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trickster").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Smoke Bomb").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Smoke Bomb").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Valor").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.YELLOW.toString() + "Heroic Hyper Chestplate of Mercury", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "An extremely light chestpiece.");
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Hyper Leggings of Nike", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "So light, you forget you are wearing them!");
                EnchantmentAPI.getEnchantment((String)"Obsidianshield").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Obsidianshield").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Hyper Boots of Hermes", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "The legendary boots of Hermes.");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Springs").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(armor, new Random().nextInt(3) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Hermes Blade of Swiftness", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A weapon unrivaled in destructive speed.");
                itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addEnchantment(Enchantment.DURABILITY, 2);
                EnchantmentAPI.getEnchantment((String)"Double Strike").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel()) + 1);
                // (Disarmor itemStack: this server has no Disarmor enchantment)
                EnchantmentAPI.getEnchantment((String)"Execute").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Execute").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Featherweight").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Featherweight").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Inquisitive").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Inquisitive").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Poison").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Poison").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Skill Swipe").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Skill Swipe").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                rewards.add(itemStack);
            }
            if (gkit.getKitName().equals("destroyer")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Demon Stalker Greathelm", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A spiked, firey helmet of destruction.");
                EnchantmentAPI.getEnchantment((String)"Destruction").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Destruction").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Commander").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Commander").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Implants").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Implants").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trickster").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trickster").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"ObsidianShield").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"ObsidianShield").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.YELLOW.toString() + "Heroic Demon Stalker Harness", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A chillingly cold, dark chestplate.");
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(3) + 1);
                EnchantmentAPI.getEnchantment((String)"Curse").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Curse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirits").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirits").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Demon Stalker Greaves", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "An extremely heavy pair of onyx leggings.");
                EnchantmentAPI.getEnchantment((String)"Nutrition").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nutrition").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Plague Carrier").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Plague Carrier").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Boots of the Destroyer", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Encrusted with onyx and black diamond.");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Springs").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Frozen").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Frozen").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.BOW, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Destructive Bow of Oblivion", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Become one with your inner darkness,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "and usher in the end of the galaxy.");
                itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_FIRE, Enchantment.ARROW_FIRE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_INFINITE, new Random().nextInt(Enchantment.ARROW_INFINITE.getMaxLevel()) + 1);
                itemStack.addEnchantment(Enchantment.ARROW_KNOCKBACK, new Random().nextInt(Enchantment.ARROW_KNOCKBACK.getMaxLevel()) + 1);
                itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Sniper").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Sniper").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blind").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blind").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Hellfire").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hellfire").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Piercing").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Piercing").getMaxLevel()) + 1);
                rewards.add(itemStack);
                rewards.add(new ItemStack(Material.ARROW, 1));
                rewards.add(EnchantmentBook.getEnchantmentBook((CustomEnchantment)EnchantmentAPI.getEnchantment((String)"Blind"), (int)(new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blind").getMaxLevel()) + 1)));
            }
            if (gkit.getKitName().equals("guardian")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Halo of the Guardian", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A glowing, warm, light halo.");
                EnchantmentAPI.getEnchantment((String)"Protection").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Protection").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Implants").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Implants").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Glowing").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Glowing").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.YELLOW.toString() + "Heroic Divine Guardian Chestplate", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A glimmering, bright chestplate.");
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Curse").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Curse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirit Link").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirit Link").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Divine Guardian Leggings", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A glowing pair of heavingly leggings.");
                EnchantmentAPI.getEnchantment((String)"Nutrition").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nutrition").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Self Destruct").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Self Destruct").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Sandals of the Divine", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Forged out of pure light essence.");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Anti Gravity").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.BOW, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Divine Bow of Valhalla", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Protect and heal the galaxy,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "and maintain law and order.");
                itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_KNOCKBACK, new Random().nextInt(Enchantment.ARROW_KNOCKBACK.getMaxLevel()) + 1);
                itemStack.addEnchantment(Enchantment.ARROW_INFINITE, new Random().nextInt(Enchantment.ARROW_INFINITE.getMaxLevel()) + 1);
                itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Sniper").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Sniper").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Snare").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Snare").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Arrow Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Arrow Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lightning").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lightning").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Piercing").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Piercing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                rewards.add(itemStack);
                rewards.add(new ItemStack(Material.ARROW, 1));
                rewards.add(EnchantmentBook.getEnchantmentBook((CustomEnchantment)EnchantmentAPI.getEnchantment((String)"Clarity"), (int)(new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1)));
            }
            if (gkit.getKitName().equals("diablo")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Executioner's Hood", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A blood-soaked silk coif.");
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Poisoned").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Poisoned").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Wither").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Wither").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Voodoo").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Voodoo").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.YELLOW.toString() + "Heroic Death Cloak", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A cloak darker than onyx.");
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Curse").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Curse").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Death Greaves", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "An ominously dark pair of greaves.");
                EnchantmentAPI.getEnchantment((String)"Plague Carrier").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Plague Carrier").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Death Grave Stompers", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A pair of boots as black as darkness.");
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Walker").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Walker").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Hardened").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"ObsidianShield").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"ObsidianShield").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.BOW, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Curse of Raven", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A darkbow forged in the void.");
                itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_INFINITE, new Random().nextInt(Enchantment.ARROW_INFINITE.getMaxLevel()) + 1);
                itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Piercing").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Piercing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Infernal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Infernal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Arrow Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Arrow Lifesteal").getMaxLevel()) + 1);
                rewards.add(itemStack);
                rewards.add(new ItemStack(Material.ARROW, 1));
                rewards.add(ItemUtils.buildItem(Material.POTION, (short)16393, (Object)ChatColor.DARK_RED + "Unholy Might", (Object)ChatColor.GRAY + "A potion bubbling with rage."));
                rewards.add(ItemUtils.buildItem(Material.POTION, (short)8257, (Object)ChatColor.DARK_GRAY + "Grim Indulgence", (Object)ChatColor.GRAY + "Cheat death with life."));
                if (p != null && !preview) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)("give " + p.getName() + " 373 1 16428 {CustomPotionEffects:[{Id:20,Amplifier:0,Duration:500}]}"));
                }
            }
            if (gkit.getKitName().equals("valentine")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.YELLOW.toString() + "Heroic Valentine Day Hat", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Immunization to false love.");
                EnchantmentAPI.getEnchantment((String)"Glowing").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Glowing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic 6-pack Abs", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Boy, you are SUPER HOT.");
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Creeper Armor").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Creeper Armor").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Short shorts", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Show off your yoked quads n glutes.");
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Implants").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Implants").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"ObsidianShield").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"ObsidianShield").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Cactus").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Cactus").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Bunny Slippers", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Comfortable AND functional!");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Anti Gravity").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Anti Gravity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ghost").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ghost").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(armor, new Random().nextInt(3) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Knife of Love", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A blade that pierces even", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "the coldest and lonely of hearts.");
                itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addEnchantment(Enchantment.DURABILITY, 2);
                EnchantmentAPI.getEnchantment((String)"Double Strike").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel()) + 1);
                // (Disarmor itemStack: this server has no Disarmor enchantment)
                EnchantmentAPI.getEnchantment((String)"Featherweight").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Featherweight").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deep Wounds").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deep Wounds").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Insomnia").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Insomnia").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Headless").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Headless").getMaxLevel()) + 1);
                rewards.add(itemStack);
                ItemStack apple = ItemUtils.buildItem(Material.GOLDEN_APPLE, (short)0, (Object)ChatColor.WHITE + "White Chocolate Apple", (Object)ChatColor.GRAY + "Rich chocolate cut with sour apple.");
                apple.setAmount(8);
                rewards.add(apple);
                rewards.add(ItemUtils.buildItem(Material.PAPER, (short)0, (Object)ChatColor.WHITE + "Epic Love Letter", (Object)ChatColor.GRAY + "Written in an alien language...", (Object)ChatColor.GRAY + "but someone loves you!"));
                rewards.add(ItemUtils.buildItem(Material.getMaterial((int)383), (short)96, (Object)ChatColor.WHITE + "Mooshroom Spawner Egg", (Object)ChatColor.GRAY + "Almost as cute as you in that outfit!"));
            }
            if (gkit.getKitName().equals("arcane")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Arcanist Hat", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A pointy, star speckled wizard hat.");
                EnchantmentAPI.getEnchantment((String)"Glowing").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Glowing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Protection").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Protection").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.YELLOW.toString() + "Arcanist Robeset", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A bright, colorful silk robe.");
                EnchantmentAPI.getEnchantment((String)"Spirit Link").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirit Link").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirits").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirits").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Arcanist Pantaloons", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Brightly colored, puffy pantaloons.");
                EnchantmentAPI.getEnchantment((String)"Lifebloom").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifebloom").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Arcanist Elf Shoes", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Pointy, jolly, and absolutely magical.");
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Walker").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Walker").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Anti Gravity").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Anti Gravity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ghost").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ghost").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.BOW, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Apollo's Longbow of the Abyss", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A translucent, abyssal longbow.");
                itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_INFINITE, new Random().nextInt(Enchantment.ARROW_INFINITE.getMaxLevel()) + 1);
                itemStack.addEnchantment(Enchantment.ARROW_FIRE, Enchantment.ARROW_FIRE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Longbow").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Longbow").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Sniper").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Sniper").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Snare").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Snare").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Piercing").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Piercing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Unfocus").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Unfocus").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Virus").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Virus").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blind").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blind").getMaxLevel()) + 1);
                rewards.add(itemStack);
                rewards.add(new ItemStack(Material.ARROW, 1));
            }
            if (gkit.getKitName().equals("gladiator")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.YELLOW.toString() + "Arena Champion Mask", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A leatherclad mask that strikes fear into all.");
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Gladiator Platemail", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Spiked shoulders, a huge hunk of metal.");
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Gladiator Platelegs", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A pair of battle-worn leather shorts.");
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Protection").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Protection").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Gladiator Warboots", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Sturdy, agile, powerful. What more do you want?");
                EnchantmentAPI.getEnchantment((String)"Rocket Escape").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rocket Escape").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Springs").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Armored").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Tank").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Ares Greatsword of Conquering", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A weapon unrivaled in conquest.");
                itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addEnchantment(Enchantment.DURABILITY, 2);
                EnchantmentAPI.getEnchantment((String)"Greatsword").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Greatsword").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                rewards.add(itemStack);
            }
            if (gkit.getKitName().equals("trickster")) {
                if (new Random().nextBoolean()) {
                    ItemStack sword2 = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Hand of Harambe", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "He may be gone, but will never be forgotten.");
                    sword2.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                    sword2.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                    sword2.addEnchantment(Enchantment.DURABILITY, 2);
                    EnchantmentAPI.getEnchantment((String)"Vampire").addToItem(sword2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Vampire").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(sword2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Inversion").addToItem(sword2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Inversion").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Blind").addToItem(sword2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blind").getMaxLevel()) + 1);
                    // (Disarmor sword2: this server has no Disarmor enchantment)
                    EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(sword2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Rage").addToItem(sword2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Silence").addToItem(sword2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                    rewards.add(sword2);
                } else {
                    ItemStack bow2 = ItemUtils.buildItem(Material.BOW, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Farcasting Bow of Nootaikok", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Ah yes, Nootaikok, the god of... bow spam?");
                    bow2.addEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                    bow2.addEnchantment(Enchantment.DURABILITY, 2);
                    bow2.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                    EnchantmentAPI.getEnchantment((String)"Farcast").addToItem(bow2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Farcast").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Hellfire").addToItem(bow2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hellfire").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Snare").addToItem(bow2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Snare").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Sniper").addToItem(bow2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Sniper").getMaxLevel()) + 1);
                    rewards.add(bow2);
                    rewards.add(ItemUtils.buildItem(Material.ARROW, (short)0, (Object)ChatColor.WHITE + "Meme Generator", (Object)ChatColor.GRAY + "A legendary arrow that turns", (Object)ChatColor.GRAY + "anything it touches into a meme...", "", (Object)ChatColor.GRAY + "lol jk"));
                }
            }
            if (gkit.getKitName().equals("templar")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.YELLOW.toString() + "Heroic Halo of Light", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "An illuminating light halo.");
                EnchantmentAPI.getEnchantment((String)"Glowing").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Glowing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lucky").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lucky").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Platemail of Aegis the Protector", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Protect your allies until your last breath.");
                EnchantmentAPI.getEnchantment((String)"Spirit Link").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirit Link").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Self Destruct").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Self Destruct").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Leadership").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Leadership").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Valor").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Commander").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Commander").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Leggings of Replenishment", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A whitemail pair of leggings.");
                EnchantmentAPI.getEnchantment((String)"Nutrition").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nutrition").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifebloom").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifebloom").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Sandals of the Angelic", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Crafted out of blessed light essence.");
                EnchantmentAPI.getEnchantment((String)"Ender Walker").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Walker").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Blade of the Knights Order", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A blade blessed by the templars with", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "protective, paladin-like properties.");
                itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addEnchantment(Enchantment.DAMAGE_UNDEAD, 5);
                itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Vampire").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Vampire").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Block").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Block").getMaxLevel()) + 1);
                // (Disarmor itemStack: this server has no Disarmor enchantment)
                EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Obliterate").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Obliterate").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                if (Math.random() <= 0.03) {
                    EnchantmentAPI.getEnchantment((String)"Divine Immolation").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Divine Immolation").getMaxLevel()) + 1);
                }
                rewards.add(itemStack);
            }
            if (gkit.getKitName().equals("viking")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + "Heroic Spiked Viking Helmet", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Iron forged, lethally intimidating.");
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Frozen").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Frozen").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Implants").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Implants").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Viking Skull Chainmail", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Boast freely, the skulls of your", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "past enemies are part of your armor.");
                EnchantmentAPI.getEnchantment((String)"Curse").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Curse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Commander").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Commander").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirits").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirits").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirit link").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirit Link").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.GOLD.toString() + "Heroic Nordic Iron Forged Leggings", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Icy cold and terrifying to the touch.");
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Frozen").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Frozen").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lucky").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lucky").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + "Heroic Steeltoe Leather Boots", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Extremely agile, yet defensible.");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Springs").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_AXE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Axe of the Dwarvish Gods", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "An ancient axe forged by dwarven gods.");
                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);
                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 5);
                itemStack.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
                EnchantmentAPI.getEnchantment((String)"Berserk").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Berserk").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Bleed").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Bleed").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Cleave").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Cleave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Reforged").addToItem(itemStack, new Random().nextInt(5) + 6);
                EnchantmentAPI.getEnchantment((String)"Blessed").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blessed").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Pummel").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Pummel").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Barbarian").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Barbarian").getMaxLevel()) + 1);
                rewards.add(itemStack);
            }
            if (gkit.getKitName().equals("tinkermaster")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.YELLOW.toString() + "Heroic Gnomish Tinkerer Hat", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "The latest and greatest in gnome technology!");
                EnchantmentAPI.getEnchantment((String)"Implants").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Implants").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Glowing").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Glowing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lucky").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lucky").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Clockwork Armor", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A true technological masterpiece,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "turn the strength of your enemies against them.");
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Heavy").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Heavy").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Pants of Steel", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Saturating pants made of steel. Gnomes, man.");
                EnchantmentAPI.getEnchantment((String)"Nutrition").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nutrition").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Frozen").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Frozen").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Self Destruct").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Self Destruct").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Heavy").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Heavy").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.YELLOW.toString() + "Heroic Gnome Escapist Shoes", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Get out of the stickiest of situations!");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Anti Gravity").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Anti Gravity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rocket Escape").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rocket Escape").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ghost").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ghost").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    armor.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
                    armor.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.BOW, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Glycon's Bow", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Glycon, the snake god of puppets...", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Why destroy your enemies when", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "you can control them?");
                itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_FIRE, Enchantment.ARROW_FIRE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_INFINITE, Enchantment.ARROW_INFINITE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Hijack").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hijack").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Arrow Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Arrow Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Farcast").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Farcast").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Sniper").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Sniper").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Snare").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Snare").getMaxLevel()) + 1);
                rewards.add(itemStack);
                rewards.add(new ItemStack(Material.ARROW, 1));
            }
            if (gkit.getKitName().equals("admiral")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + "Heroic Cosmic Admiral Military Hat", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Perfectly stitched with bright embroiders.");
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Protection").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Protection").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Frozen").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Frozen").getMaxLevel()) + 1);
                chest = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + "Heroic Cosmic Admiral Military Jacket", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "The very existence of the medals pinned", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "on your jacket ensure your victory.");
                EnchantmentAPI.getEnchantment((String)"Commander").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Commander").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blood Lust").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blood Lust").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Leadership").addToItem((ItemStack)chest, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Leadership").getMaxLevel()) + 1);
                leggings = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Cosmonaut Biotic Legs", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "War has taken its toll on you,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "but you are not one to surrender.");
                EnchantmentAPI.getEnchantment((String)"Nutrition").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nutrition").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Cactus").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Cactus").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                if (new Random().nextBoolean()) {
                    EnchantmentAPI.getEnchantment((String)"Lifebloom").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifebloom").getMaxLevel()) + 1);
                } else {
                    EnchantmentAPI.getEnchantment((String)"Plague Carrier").addToItem(leggings, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Plague Carrier").getMaxLevel()) + 1);
                }
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Boots of the Void Galaxy", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A spoil of war, but something", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "is strange about these boots.");
                EnchantmentAPI.getEnchantment((String)"Ender Walker").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Walker").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Springs").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                rewards.add(helmet);
                rewards.add((ItemStack)chest);
                rewards.add(leggings);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Excalibur", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "An electrifyingly twist on an,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "otherwise legendary holy blade.");
                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Inquisitive").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Inquisitive").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Double Strike").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Featherweight").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Featherweight").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Headless").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Headless").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Kill Aura").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Kill Aura").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Shackle").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shackle").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Reforged").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Reforged").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                rewards.add(itemStack);
            }
            if (gkit.getKitName().equals("partyanimal")) {
                helmet = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Cosmic Year #2 Party Hat", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "An extremely rare party hat, only", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "obtainable on the #2 anniversary weekend.", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Now, party like it is 1999... again!");
                EnchantmentAPI.getEnchantment((String)"Aquatic").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Aquatic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Implants").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Implants").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ghost").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ghost").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Valor").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Dodge").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Dodge").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lucky").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lucky").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Drunk").addToItem(helmet, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Drunk").getMaxLevel()) + 1);
                rewards.add(helmet);
                for (ItemStack armor : rewards) {
                    armor.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
                    armor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    armor.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
                }
                ItemStack axe2 = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "The Jug of Dionysus", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "The legendary, marble forged drinking", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Jug of Dionysus, the god of partying.");
                axe2.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                axe2.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 4);
                axe2.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                axe2.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(axe2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Double Strike").addToItem(axe2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Featherweight").addToItem(axe2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Featherweight").getMaxLevel()) + 1);
                // (Disarmor axe2: this server has no Disarmor enchantment)
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(axe2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(axe2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enrage").addToItem(axe2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enrage").getMaxLevel()) + 1);
                rewards.add(axe2);
            }
            if (gkit.getKitName().equals("enchantment")) {
                int minTier = 1;
                int maxTier = 5;
                if (kitTier == 1) {
                    minTier = 2;
                    maxTier = 5;
                } else if (kitTier == 2) {
                    minTier = 3;
                    maxTier = 5;
                } else if (kitTier == 3) {
                    minTier = 4;
                    maxTier = 6;
                }
                for (int i = new Random().nextInt(3 + kitTier) + 7; i > 0; --i) {
                    int enchantmentTier = new Random().nextInt(maxTier - minTier + 1) + minTier;
                    if (kitTier < 2 && enchantmentTier == 5 && Math.random() < 0.6) {
                        --enchantmentTier;
                    }
                    if (enchantmentTier == 6 && Math.random() < 0.85) {
                        --enchantmentTier;
                    }
                    CustomEnchantment customEnchantment = TieredEnchantments.getRandomEnchantment((int)enchantmentTier);
                    int success = new Random().nextInt(81) + 20;
                    int destroy = new Random().nextInt(80) + 1;
                    int level = new Random().nextInt(customEnchantment.getMaxLevel()) + 1;
                    rewards.add(EnchantmentBook.getEnchantmentBook((CustomEnchantment)customEnchantment, (int)level, (int)success, (int)destroy));
                }
                double f = Math.random();
                if (f < 0.2) {
                    if (kitTier == 3 && Math.random() < 0.01) {
                        rewards.add(AstroCore.getHolyWhiteScroll());   // CosmicDungeons' Holy White Scroll lives in AstroCore on this server
                    } else {
                        rewards.add(EnchantmentBook.getWhiteScroll());
                    }
                } else if (f < 0.45) {
                    if (kitTier == 3 && Math.random() < 0.025) {
                        rewards.add(EnchantmentBook.getHeroicBlackscroll((boolean)true));
                    } else {
                        rewards.add(EnchantmentBook.getBlackScroll());
                    }
                } else {
                    rewards.add(EnchantmentBook.getMagicDust((ItemRarity)ItemRarity.SIMPLE, (int)(new Random().nextInt(8) + 2)));
                    rewards.add(EnchantmentBook.getMagicDust((ItemRarity)ItemRarity.UNIQUE, (int)(new Random().nextInt(8) + 2)));
                    rewards.add(EnchantmentBook.getMagicDust((ItemRarity)ItemRarity.ELITE, (int)(new Random().nextInt(8) + 2)));
                    if (Math.random() < 0.4 || kitTier > 0) {
                        rewards.add(EnchantmentBook.getMagicDust((ItemRarity)ItemRarity.ULTIMATE, (int)(new Random().nextInt(8) + 2)));
                        if (Math.random() < 0.2 || kitTier > 1) {
                            rewards.add(EnchantmentBook.getMagicDust((ItemRarity)ItemRarity.LEGENDARY, (int)(new Random().nextInt(8) + 2)));
                            if (Math.random() < 0.05 && kitTier == 3) {
                                rewards.add(EnchantmentBook.getMagicDust((ItemRarity)ItemRarity.SOUL, (int)(new Random().nextInt(8) + 2)));
                            }
                        }
                    }
                }
                if (Math.random() < (double)kitTier * 0.0165) {
                    if (Math.random() < 0.65) {
                        rewards.add(EnchantmentBook.getEnchantmentBook((CustomEnchantment)EnchantmentAPI.getEnchantment((String)"Auto Sell"), (int)(1 + new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Auto Sell").getMaxLevel()))));
                    } else {
                        rewards.add(EnchantmentBook.getEnchantmentBook((CustomEnchantment)EnchantmentAPI.getEnchantment((String)"Explosives Expert"), (int)1));
                    }
                }
            }
            if (gkit.getKitName().equals("grandmaster")) {
                ItemStack helmet2 = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Shroud of Judgement", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Strike fear into your enemies,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "and heroism into your allies.");
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(helmet2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Arrow Deflect").addToItem(helmet2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Arrow Deflect").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(helmet2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(helmet2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                ItemStack chest2 = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + "Heroic Cloak of the Grandmaster", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A well kept, heroic black cloak.");
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(chest2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(chest2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(chest2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(chest2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                ItemStack leggings2 = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.GOLD.toString() + "Heroic Leather Combat Leggings", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Absolute mobility, complete security");
                EnchantmentAPI.getEnchantment((String)"Lifebloom").addToItem(leggings2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifebloom").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(leggings2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(leggings2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(leggings2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Sandals of Silence", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Invisible to the ears of your enemies.");
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                rewards.add(helmet2);
                rewards.add(chest2);
                rewards.add(leggings2);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Nightmare Killer", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "An onyx blade with a dark legacy,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "rumor has it this blade only", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "exists for a few fleeting moments", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "before and after each assassination.");
                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                itemStack.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Solitude").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Solitude").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Skill Swipe").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Skill Swipe").getMaxLevel()) + 1);
                // (Disarmor itemStack: this server has no Disarmor enchantment)
                EnchantmentAPI.getEnchantment((String)"Double Strike").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel()) + 1);
                rewards.add(itemStack);
                rewards.add(com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils.getXpBottle((int)(10000 + new Random().nextInt(5000)), (String)"Heroic Grandmaster Kit"));
                if (Math.random() < 0.225) {
                    rewards.add(com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils.getBlackscroll((int)(25 + new Random().nextInt(26))));
                } else {
                    rewards.add(com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils.getMysteryDust((ItemRarity)ItemRarity.ULTIMATE, (int)(4 + new Random().nextInt(7))));
                }
            }
            if (gkit.getKitName().equals("voidwalker")) {
                ItemStack boots2 = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Erebus' Sandals of the Void", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "You walk on the thin line between", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "existence and extinction, do your", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "enemies dare to follow?");
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Walker").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Walker").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Stormcaller").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Stormcaller").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Anti Gravity").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Anti Gravity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Voodoo").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Voodoo").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots2, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                rewards.add(boots2);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                }
                ItemStack axe3 = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Chaos Executioner", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A daemon forged greatsword,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "wieldable only to those the blade", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "itself deems worthy, and to kill", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "those it deems unworthy.");
                axe3.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                axe3.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
                axe3.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Demonforged").addToItem(axe3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Demonforged").getMaxLevel()) + 1);
                // (Disarmor axe3: this server has no Disarmor enchantment)
                EnchantmentAPI.getEnchantment((String)"Execute").addToItem(axe3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Execute").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(axe3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(axe3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Greatsword").addToItem(axe3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Greatsword").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(axe3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(axe3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                rewards.add(axe3);
            }
            if (gkit.getKitName().equals("masterbuilder")) {
                double chance = 0;
                ItemStack pickaxe = ItemUtils.buildItem(Material.DIAMOND_PICKAXE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Pickaxe of Hephaestus", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A legendary tool forged by", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "the cosmic god of builders.");
                pickaxe.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                pickaxe.addEnchantment(Enchantment.DIG_SPEED, 4 + new Random().nextInt(2));
                pickaxe.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, new Random().nextInt(3) + 1);
                EnchantmentAPI.getEnchantment((String)"Auto Smelt").addToItem(pickaxe, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Auto Smelt").getMaxLevel()) + 1);
                double d = kitTier == 0 ? 0.001 : (kitTier == 1 ? 0.01 : (kitTier == 2 ? 0.1 : (chance = kitTier == 3 ? 0.25 : 0.001)));
                if (Math.random() <= chance) {
                    GlobalKit.addEnchant(pickaxe, "Detonate", GlobalKit.getMaxEnchantLevel("Detonate"));
                    double rand = Math.random();
                    int level = rand <= 0.7 ? 1 : (rand <= 0.95 ? 2 : (rand <= 0.98 ? 3 : (rand >= 0.99 ? 4 : 2)));
                    GlobalKit.addEnchant(pickaxe, "Atomic Detonate", level);
                } else {
                    GlobalKit.addRandomEnchantLevel(pickaxe, "Detonate");
                }
                EnchantmentAPI.getEnchantment((String)"Experience").addToItem(pickaxe, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Experience").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Haste").addToItem(pickaxe, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Haste").getMaxLevel()) + 1);
                // (Skilling pickaxe: this server has no Skilling enchantment)
                EnchantmentAPI.getEnchantment((String)"Obsidian Destroyer").addToItem(pickaxe, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Obsidian Destroyer").getMaxLevel()) + 1);
                rewards.add(pickaxe);
                ItemStack spade = ItemUtils.buildItem(Material.DIAMOND_SPADE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Shiva's Excavator", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "An ancient spade use by", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "the cosmic god of excavation.");
                spade.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                spade.addEnchantment(Enchantment.DIG_SPEED, 5);
                spade.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, new Random().nextInt(3) + 1);
                EnchantmentAPI.getEnchantment((String)"Detonate").addToItem(spade, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Detonate").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Telepathy").addToItem(spade, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Telepathy").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Oxygenate").addToItem(spade, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Oxygenate").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Haste").addToItem(spade, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Haste").getMaxLevel()) + 1);
                // (Skilling spade: this server has no Skilling enchantment)
                rewards.add(spade);
            }
            if (gkit.getKitName().equals("independence")) {
                ItemStack helmet3 = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Guy Fawkes Mask", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Instead of trying to make your life", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "perfect, give yourself the freedom to", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "make it an adventure, and go ever upward.");
                EnchantmentAPI.getEnchantment((String)"Leadership").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Leadership").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lucky").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lucky").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Resilience").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Resilience").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Commander").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Commander").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blood Link").addToItem(helmet3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blood Link").getMaxLevel()) + 1);
                rewards.add(helmet3);
                ItemStack boots3 = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Anarchy Boots", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "You suddenly have an urge to", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "resist the status quo.");
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem(boots3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirits").addToItem(boots3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirits").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(boots3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(boots3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Metaphysical").addToItem(boots3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Metaphysical").getMaxLevel()) + 1);
                rewards.add(boots3);
                ItemStack chestplate = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Chestplate of Chaos", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "You cannot know peace without", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "first acknowledging chaos.");
                EnchantmentAPI.getEnchantment((String)"Molten").addToItem(chestplate, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Molten").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem(chestplate, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Dodge").addToItem(chestplate, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Dodge").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(chestplate, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(chestplate, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blood Lust").addToItem(chestplate, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blood Lust").getMaxLevel()) + 1);
                rewards.add(chestplate);
                for (ItemStack itemStack : rewards) {
                    itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                    itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                }
                ItemStack sword3 = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Runeblade of Liber", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A mythical runeblade imbued with,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "the spirit of Liber, god of freedom.");
                sword3.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                sword3.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
                sword3.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Featherweight").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Featherweight").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Execute").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Execute").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Inquisitive").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Inquisitive").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Block").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Block").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Inversion").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Inversion").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enrage").addToItem(sword3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enrage").getMaxLevel()) + 1);
                rewards.add(sword3);
            }
            if (gkit.getKitName().equals("goodness")) {
                ItemStack boots4 = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Erebus' Sandals of the Void", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "You walk on the thin line between", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "existence and extinction, do your", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "enemies dare to follow?");
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Walker").addToItem(boots4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Walker").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Stormcaller").addToItem(boots4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Stormcaller").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(boots4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Anti Gravity").addToItem(boots4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Anti Gravity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Voodoo").addToItem(boots4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Voodoo").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                rewards.add(boots4);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                }
                ItemStack axe4 = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Chaos Executioner", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A daemon forged greatsword,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "wieldable only to those the blade", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "itself deems worthy, and to kill", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "those it deems unworthy.");
                axe4.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                axe4.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
                axe4.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Demonforged").addToItem(axe4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Demonforged").getMaxLevel()) + 1);
                // (Disarmor axe4: this server has no Disarmor enchantment)
                EnchantmentAPI.getEnchantment((String)"Execute").addToItem(axe4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Execute").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Headless").addToItem(axe4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Headless").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(axe4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(axe4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                rewards.add(axe4);
            }
            if (gkit.getKitName().equals("duelist")) {
                ItemStack helmet4 = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Crown of the Colosseum Champion", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A golden crown embedded with bloodstones,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "beware the monstrosity whom dawns this sigil.");
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem(helmet4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(helmet4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(helmet4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Implants").addToItem(helmet4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Implants").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(helmet4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Valor").addToItem(helmet4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel()) + 1);
                rewards.add(helmet4);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    armor.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
                }
                ItemStack sword4 = ItemUtils.buildItem(Material.DIAMOND_SWORD, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Bloodstone Rapier of Kronos", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A blade with such a dark lineage", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "its very existence has been condemned", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "by the Council of Cosmonauts.");
                sword4.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                sword4.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                sword4.addEnchantment(Enchantment.DURABILITY, 2);
                EnchantmentAPI.getEnchantment((String)"Lifesteal").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lifesteal").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Paralyze").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Paralyze").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trap").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Trap").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Deep Wounds").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deep Wounds").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Double Strike").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Double Strike").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Featherweight").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Featherweight").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Vampire").addToItem(sword4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Vampire").getMaxLevel()) + 1);
                rewards.add(sword4);
            }
            if (gkit.getKitName().equals("butcher")) {
                ItemStack helmet5 = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Blood-soaked Apron of the Butcher", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A heavy leather apron, soaked with the", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "blood of all who have dared stand before you.");
                EnchantmentAPI.getEnchantment((String)"Hardened").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hardened").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Dodge").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Dodge").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blood Lust").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blood Lust").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Arrow Deflect").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Arrow Deflect").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet5, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                rewards.add(helmet5);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                }
                ItemStack sword = ItemUtils.buildItem(Material.DIAMOND_AXE, (short)0, ChatColor.RED.toString() + ChatColor.BOLD.toString() + "The Butcher's Cleaver", ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "The cleaver of The Butcher, a demonic", ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "abomination from the deepest, darkest", ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "depths of the known Nether.");
                sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                sword.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Cleave").addToItem(sword, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Cleave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Bleed").addToItem(sword, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Bleed").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Devour").addToItem(sword, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Devour").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Arrow Break").addToItem(sword, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Arrow Break").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blacksmith").addToItem(sword, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blacksmith").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blessed").addToItem(sword, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blessed").getMaxLevel()) + 1);
                if (Math.random() <= 0.1) {
                    EnchantmentAPI.getEnchantment((String)"Soul Trap").addToItem(sword, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Soul Trap").getMaxLevel()) + 1);
                }
                rewards.add(sword);
            }
            if (gkit.getKitName().equals("paladin")) {
                ItemStack helmet6 = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + "Heroic Shroud of the Holy Order", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A shiny, elegant silk shroud,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "linked together with chainmail.");
                EnchantmentAPI.getEnchantment((String)"Valor").addToItem(helmet6, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(helmet6, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(helmet6, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(helmet6, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(helmet6, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                ItemStack chest3 = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.GOLD.toString() + "Heroic Divine Paladin Chestplate", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "A blessed pair of indestructible platemail.");
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(chest3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(chest3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem(chest3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(chest3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Guardians").addToItem(chest3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirits").addToItem(chest3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirits").getMaxLevel()) + 1);
                ItemStack leggings3 = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Heroic Platelegs of Athena", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Forged in the depths of Hephasteus,", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "immuned to the weapons of mere mortals.");
                EnchantmentAPI.getEnchantment((String)"Valor").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Creeper Armor").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Creeper Armor").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Nutrition").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nutrition").getMaxLevel()) + 1);
                if (Math.random() <= 0.05) {
                    EnchantmentAPI.getEnchantment((String)"Nature Wrath").addToItem(leggings3, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nature Wrath").getMaxLevel()) + 1);
                }
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + "Heroic Boots of the Holy Order", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Smelted out of pure holy essence.");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Springs").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Springs").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Valor").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Valor").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Angelic").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Angelic").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Armored").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Tank").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Tank").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rocket Escape").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rocket Escape").getMaxLevel()) + 1);
                rewards.add(helmet6);
                rewards.add(chest3);
                rewards.add(leggings3);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                }
                if (Math.random() < 0.1) {
                    if (Math.random() < 0.3) {
                        ItemStack itemStack = EnchantmentOrb.getEnchantmentOrb((int)(new Random().nextInt(50) + 50), (int)10, (OrbType)OrbType.WEAPON);
                        rewards.add(itemStack);
                    } else {
                        ItemStack itemStack = EnchantmentOrb.getEnchantmentOrb((int)(new Random().nextInt(50) + 50), (int)10, (OrbType)OrbType.ARMOR);
                        rewards.add(itemStack);
                    }
                } else {
                    int var10_104 = 0;
                    while (var10_104++ < 2 + new Random().nextInt(2)) {
                        rewards.add(RandomizationScroll.getRandomizationScroll((ItemRarity) CosmicSpaceChestsAPI.getItemRarity((int)(2 + new Random().nextInt(5)))));
                    }
                }
            }
            if (gkit.getKitName().equals("warlock")) {
                ItemStack helmet7 = ItemUtils.buildItem(Material.DIAMOND_HELMET, (short)0, ChatColor.GOLD.toString() + (Object)ChatColor.BOLD + "Master Warlock Hat", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "You are a master of the demonic world.");
                EnchantmentAPI.getEnchantment((String)"Destruction").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Destruction").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Trickster").addToItem(helmet7, 8);
                EnchantmentAPI.getEnchantment((String)"Clarity").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Clarity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Enlighted").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Enlighted").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Lucky").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Lucky").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                if (Math.random() < 0.1) {
                    EnchantmentAPI.getEnchantment((String)"Immortal").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Immortal").getMaxLevel()) + 1);
                } else {
                    EnchantmentAPI.getEnchantment((String)"Guardians").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Guardians").getMaxLevel()) + 1);
                }
                ItemStack chest4 = ItemUtils.buildItem(Material.DIAMOND_CHESTPLATE, (short)0, ChatColor.YELLOW.toString() + "Heroic Warlock Robe", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A chillingly cold, demonic robeset.");
                EnchantmentAPI.getEnchantment((String)"Shockwave").addToItem(chest4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Shockwave").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Overload").addToItem(chest4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Overload").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Curse").addToItem(chest4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Curse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirits").addToItem(chest4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirits").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Spirit Link").addToItem(chest4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Spirit Link").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Resilience").addToItem(helmet7, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Resilience").getMaxLevel()) + 1);
                ItemStack leggings4 = ItemUtils.buildItem(Material.DIAMOND_LEGGINGS, (short)0, ChatColor.YELLOW.toString() + "Heroic Warlock Bottoms", ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "A light set of dark silk robe bottoms.");
                EnchantmentAPI.getEnchantment((String)"Nutrition").addToItem(leggings4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Nutrition").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Plague Carrier").addToItem(leggings4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Plague Carrier").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Wither").addToItem(leggings4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Wither").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(leggings4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Dodge").addToItem(leggings4, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Dodge").getMaxLevel()) + 1);
                boots = ItemUtils.buildItem(Material.DIAMOND_BOOTS, (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Ghostly Call of the Banshee", ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "Silence... then, suddenly, death.");
                EnchantmentAPI.getEnchantment((String)"Gears").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Gears").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Anti Gravity").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Anti Gravity").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Undead Ruse").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Undead Ruse").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ghost").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ghost").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rocket Escape").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rocket Escape").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Metaphysical").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Metaphysical").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Ender Shift").addToItem(boots, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Ender Shift").getMaxLevel()) + 1);
                rewards.add(helmet7);
                rewards.add(chest4);
                rewards.add(leggings4);
                rewards.add(boots);
                for (ItemStack armor : rewards) {
                    armor.addEnchantment(Enchantment.DURABILITY, 3);
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    EnchantmentAPI.getEnchantment((String)"Deathbringer").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Deathbringer").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Armored").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Armored").getMaxLevel()) + 1);
                    EnchantmentAPI.getEnchantment((String)"Marksman").addToItem(armor, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Marksman").getMaxLevel()) + 1);
                }
                ItemStack itemStack = ItemUtils.buildItem(Material.BOW, (short)0, ChatColor.RED.toString() + ChatColor.BOLD.toString() + "The Fiend", ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "A fabled entity, cursed with impossibly", ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "ancient and destructive demonic power.");
                itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_FIRE, Enchantment.ARROW_FIRE.getMaxLevel());
                itemStack.addEnchantment(Enchantment.ARROW_INFINITE, new Random().nextInt(Enchantment.ARROW_INFINITE.getMaxLevel()) + 1);
                itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                EnchantmentAPI.getEnchantment((String)"Sniper").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Sniper").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Rage").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Rage").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Piercing").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Piercing").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Blind").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Blind").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Silence").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Silence").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Pacify").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Pacify").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Eagle Eye").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Eagle Eye").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Dimension Rift").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Dimension Rift").getMaxLevel()) + 1);
                EnchantmentAPI.getEnchantment((String)"Hijack").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Hijack").getMaxLevel()) + 1);
                if (Math.random() < 0.125) {
                    EnchantmentAPI.getEnchantment((String)"Teleblock").addToItem(itemStack, new Random().nextInt(EnchantmentAPI.getEnchantment((String)"Teleblock").getMaxLevel()) + 1);
                }
                rewards.add(itemStack);
                rewards.add(new ItemStack(Material.ARROW, 1));
                rewards.add(new ItemStack(Material.ENDER_PEARL, 16));
                rewards.add(new ItemStack(Material.ENDER_PEARL, 16));
                ItemRarity ir = ItemRarity.ELITE;
                if (Math.random() < 0.25) {
                    ir = ItemRarity.ULTIMATE;
                }
                if (Math.random() < 0.1) {
                    ir = ItemRarity.LEGENDARY;
                }
                rewards.add(ItemUtils.setAmount(EnchantmentBook.getSecretDust((ItemRarity)ir, (int)(25 + new Random().nextInt(75))), 9));
            }
            if (gkit.getNewKitInstance() != null) {
                rewards = gkit.getNewKitInstance().getKitItems(p, gkit, kitTier);
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        if (kitTier > 0) {
            try {
                double chance = kitTier == 1 ? 0.3 : (kitTier == 2 ? 0.6 : (kitTier == 3 ? 0.9 : 0.0));
                for (int rewardIndex = 0; rewardIndex < rewards.size(); ++rewardIndex) {
                    Material material;
                    ItemStack item = (ItemStack)rewards.get(rewardIndex);
                    Material material2 = material = item != null ? item.getType() : null;
                    if (!GeneralUtil.isArmor((ItemStack)item) && !GeneralUtil.isWeapon((ItemStack)item) && !GeneralUtil.isTool((ItemStack)item)) continue;
                    ItemMeta im = item.getItemMeta();
                    im.setDisplayName(im.getDisplayName().trim() + " (" + JSONMessage.getNumber((int)kitTier) + ")");
                    item.setItemMeta(im);
                    for (Map.Entry levels : EnchantmentAPI.getAllEnchantments((ItemStack)item).entrySet()) {
                        int currentLevel = (Integer)levels.getValue();
                        CustomEnchantment enchant = (CustomEnchantment)levels.getKey();
                        if (enchant.heroicEnchantment || enchant.getMaxLevel() <= currentLevel) continue;
                        int toMax = enchant.getMaxLevel() - currentLevel;
                        int added = 0;
                        for (int i = 0; i < toMax && !(Math.random() > chance) && currentLevel < enchant.getMaxLevel(); ++i) {
                            item = enchant.addToItem(item, ++currentLevel);
                            ++added;
                        }
                        if (added <= 0) continue;
                        rewards.set(rewardIndex, item);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
            for (ItemStack i : rewards) {
                long id = ItemID.getItemID((ItemStack)i);
                if (id == -1L && !ItemID.needsItemID((ItemStack)i)) continue;
                if (id != -1L) {
                    i = ItemID.removeItemID((ItemStack)i);
                }
                ItemID.setItemID((ItemStack)i, (long)ItemID.generateNewItemID());
            }
        }
        for (ItemStack reward : rewards) {
            if (!GeneralUtil.isWeapon((ItemStack)reward) && !GeneralUtil.isArmor((ItemStack)reward)) continue;
            EnchantmentBook.applyTransmogScroll((ItemStack)reward);
        }
        return rewards;
    }
}
