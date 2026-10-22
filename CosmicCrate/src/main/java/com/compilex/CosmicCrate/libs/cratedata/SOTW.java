package com.compilex.CosmicCrate.libs.cratedata;

import com.compilex.CosmicCrate.CosmicCrate;
import com.compilex.CosmicCrate.libs.CosmicCrateData;
import com.cosmicpvp.CosmicSpaceChests.utils.ItemUtils;
import com.cosmicpvp.cosmicutils.itembuilder.MaskBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import io.netty.util.internal.ThreadLocalRandom;
import me.ifamasssxd.cosmicmysterychest.CosmicMysteryChest;
import me.ifamasssxd.cosmicmysterychest.struct.MysteryChest;
import me.ifamasssxd.cosmicskycustoms.struct.CustomItemBuilder;
import me.ily2bb.slotbot.CosmicSlotBotAPI;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SOTW extends CosmicCrateData {
    private ChatColor mainColor = ChatColor.GOLD;

    private ChatColor secondColor = ChatColor.GREEN;


    @Override
    public ItemStack getRandomMetaItem(Player p0) {
        double chance = Math.random();
        Map<String, MysteryChest> chests = CosmicMysteryChest.get().getChestManager().getMysteryChestMap();

        // the bonus items in the lore; each is given when this server can build it
        if (chance <= 0.05D) {
            ItemStack rank = new CustomItemBuilder("apollo_rank").build();
            if (rank != null) return rank;
        }

        if (chance <= 0.5D && chests.get("PremadeOmni") != null) {
            return chests.get("PremadeOmni").createClaimableItem(false);
        }

        return ItemUtils.setAmount(CosmicSlotBotAPI.createSlotBotItem(), 8 + (new Random()).nextInt(16));
    }

    @Override
    public List<String> getDescriptionLore(String p0) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.translate("&fUnlocked by &6" + p0 + " &fon the &6Store &7(/buy)"));
        lore.add("");
        lore.add(CC.translate("&6&l&nTREASURE ITEMS"));
        lore.add(CC.translate("&6* 8-16x Spawner Generator"));
        lore.add(CC.translate("&6* 2-4x Admin Weapon Cache"));
        lore.add(CC.translate("&6* 1-3x Astro Money Note Generator"));
        lore.add(CC.translate("&6* 4-6x Heroic Armor Generator"));
        lore.add(CC.translate("&6* 1-2x Super OP Set Cache"));
        lore.add(CC.translate("&6* 4-6x Slotbot Tickets"));
        lore.add(CC.translate("&6* 6-8x Mystery Skin Bundle"));
        lore.add(CC.translate("&6* 1-3 Mystery Backpack Bundle"));
        lore.add(CC.translate("&6* 4-8x Omni Crystal Generator"));
        lore.add("");

        lore.add(CC.translate("&8&l&nBONUS ITEMS"));
        lore.add(CC.translate("&8* Apollo Rank"));
        lore.add(CC.translate("&8* Premade Omni Set"));
        lore.add(CC.translate("&8* 8-16x Slotbot Tickets"));


        return lore;
    }

    @Override
    public List<ItemStack> getCrateLoot(Player p0) {
        List<ItemStack> items = Lists.newArrayList();
        Map<String, MysteryChest> chests = CosmicMysteryChest.get().getChestManager().getMysteryChestMap();

         items.add(ItemUtils.setAmount(chests.get("Spawner").createClaimableItem(false), 8 + (new Random()).nextInt(16)));
        items.add(ItemUtils.setAmount(chests.get("AdminWeapon").createClaimableItem(false), 2 + (new Random()).nextInt(4)));
        items.add(ItemUtils.setAmount(chests.get("AstroNote").createClaimableItem(false), 1 + (new Random()).nextInt(3)));
         items.add(ItemUtils.setAmount(chests.get("HeroicProtVI").createClaimableItem(false), 4 + (new Random()).nextInt(6)));
         items.add(ItemUtils.setAmount(chests.get("OPCache").createClaimableItem(false), 1 + (new Random()).nextInt(2)));
        items.add(ItemUtils.setAmount(CosmicSlotBotAPI.createSlotBotItem(), 4 + (new Random()).nextInt(6)));
         items.add(ItemUtils.setAmount(chests.get("SkinBundle").createClaimableItem(false), 6 + (new Random()).nextInt(8)));
         items.add(ItemUtils.setAmount(chests.get("Backpack").createClaimableItem(false), 1 + (new Random()).nextInt(3)));
        items.add(ItemUtils.setAmount(chests.get("OmniCrystal").createClaimableItem(false), 4 + (new Random()).nextInt(8)));







        return items;
    }

    @Override
    public List<String> getMissingRewardSources() {
        Map<String, MysteryChest> chests = CosmicMysteryChest.get().getChestManager().getMysteryChestMap();
        List<String> missing = new ArrayList<>();
        for (String name : new String[]{"Spawner", "AdminWeapon", "AstroNote", "HeroicProtVI", "OPCache", "SkinBundle", "Backpack", "OmniCrystal"}) {
            if (!chests.containsKey(name)) missing.add(name);
        }
        return missing;
    }

    @Override
    public short getNonColorPane() {
        return (short) DyeColor.GREEN.getWoolData();
    }

    @Override
    public short getColorPane() {
        return (short)DyeColor.ORANGE.getWoolData();
    }

    @Override
    public List<Color> getConclusionFireworkColors() {
        return Lists.newArrayList(new Color[] { Color.ORANGE, Color.GREEN });
    }


}
