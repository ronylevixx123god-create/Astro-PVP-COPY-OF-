/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.compilex.CosmicFund.CosmicFundAPI
 *  com.cosmicpvp.cosmicutils.customevents.CustomCosmicPlayerEvent
 *  com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  com.cosmicpvp.cosmicutils.utils.ItemUtils
 *  com.cosmicpvp.cosmicutils.utils.NBTTagCompoundBuilder
 *  com.cosmicpvp.cosmicutils.utils.NBTWrapper
 *  com.google.common.collect.Lists
 *  net.minecraft.server.v1_8_R3.NBTBase
 *  org.arkhamnetwork.Arkkit.patches.ItemID
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugins
 *  org.arkhamnetwork.enchantments.EnchantmentBook
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.ItemStack
 */
package me.ifamasssxd.cosmicevolutionkits.structure;

import com.cosmicpvp.cosmicutils.utils.LuckPermUtils;

import com.cosmicpvp.cosmicutils.customevents.CustomCosmicPlayerEvent;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import com.cosmicpvp.cosmicutils.utils.ItemUtils;
import com.cosmicpvp.cosmicutils.utils.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKit;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Alchemist;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Judgement;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Lucky;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Mimic;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Ogre;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Phoenix;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Slayer;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Troll;
import me.ifamasssxd.cosmicevolutionkits.utils.RandomFireworks;
import me.ifamasssxd.cosmicevolutionkits.utils.TitleManager;
import net.minecraft.server.v1_8_R3.NBTBase;
import org.arkhamnetwork.Arkkit.patches.ItemID;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public enum EvolutionKits {
    PHOENIX("Phoenix", TimeUnit.SECONDS.toMillis(259200L), new Phoenix()),
    TROLL("Troll", TimeUnit.SECONDS.toMillis(259200L), new Troll()),
    ALCHEMIST("Alchemist", TimeUnit.SECONDS.toMillis(259200L), new Alchemist()),
    OGRE("Ogre", TimeUnit.SECONDS.toMillis(259200L), new Ogre()),
    SLAYER("Slayer", TimeUnit.SECONDS.toMillis(259200L), new Slayer()),
    JUDGEMENT("Judgement", TimeUnit.DAYS.toMillis(4L), new Judgement()),
    MIMIC("Mimic", TimeUnit.DAYS.toMillis(4L), new Mimic()),
    LUCKY("Lucky", TimeUnit.DAYS.toMillis(5L), new Lucky());

    String name;
    long cooldown;
    EvolutionKit kit;
    public static final Material gemMaterial;
    private static final Material levelUpMaterial;

    public static ItemStack getLuckyBox(int level, boolean applyIID) {
        ArrayList lore = Lists.newArrayList(new String[]{"", CC.Gray + "A magical box that has a chance to contain " + CC.GoldU + "1-5x", CC.Gray + "increasingly epic items the higher its level.", ""});
        EvolutionKits.addLore(lore, level, 1, "Item Nametag", "Lore Crystal", "Transmog Scroll", "Showcase Expansion", "Home Increaser", "Black Scroll", "Evolution Gem", "Kill Tracker", "XP Bottle");
        EvolutionKits.addLore(lore, level, 2, "Legendary Space Flare", "White Scroll", "mcMMO Skill Voucher", "Faction mcMMO Booster", "Reset Tool Kit");
        EvolutionKits.addLore(lore, level, 3, "Legendary Randomization Scroll", "Random /gkit Fallen Hero", "Faction XP Booster");
        EvolutionKits.addLore(lore, level, 4, "Mystery Mob Spawner", "Brood Mother Boss Egg", "King Slime Boss Egg", "Player Souls");
        EvolutionKits.addLore(lore, level, 5, "Dungeon Portal Ingredients", "Mystery /cc", "/vkit Evolution Gem");
        EvolutionKits.addLore(lore, level, 6, "Armor Enchant Orb (12 Slot)", "Undead Assassin Boss Egg", "Godly Space Flare");
        EvolutionKits.addLore(lore, level, 7, "Weapon Enchant Orb (12 Slot)", "Mystery Heroic Enchant Book", "/gkit Gem", "Plague Bloater Boss Egg");
        EvolutionKits.addLore(lore, level, 8, "Magic Soul Dust", "G-Kit Generator", "Yijki Boss Egg", "Godly Randomization Scroll", "Fat Bucket", "Stun Grenades");
        EvolutionKits.addLore(lore, level, 9, "Heroic Black Scroll", "Heroic Mystery /cc", "Equipment Lootbox", "EMP Pulse", "Phoenix Feather", "Yijki Staff", "Monster Soul Tracker");
        EvolutionKits.addLore(lore, level, 10, "Holy White Scroll", "Heroic Upgrade", "Random Mask Generator", "Weapon/Armor Enchant Orb (13 Slot)", "Omni /gkit Gem", "Random Lootbox");
        ItemStack retr = new NBTWrapper(new ItemBuilder(Material.CHEST).name(CC.GoldB + "Lucky Box (" + CC.Yellow + "LVL " + level + CC.GoldB + ") " + CC.Gray + "(Right Click)").lore((List)lore).build()).setString("cosmicType", "LUCKY_BOX").setNBTBase("cosmicData", (NBTBase)new com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder().setInt("level", Integer.valueOf(level)).get()).build();
        return applyIID ? EnchantmentBook.applyTransmogScroll((ItemStack)retr) : retr;
    }

    private static List<String> addLore(List<String> current, int currentLevel, int required, String ... lines) {
        boolean available = currentLevel >= required && (currentLevel <= 4 || currentLevel - required <= 4);
        String lineToAdd = (available ? CC.GoldB : CC.RedB + CC.Strike) + "LEVEL " + required + ": " + (available ? CC.Yellow : CC.Gray + CC.Strike);
        for (String line : lines) {
            if ((lineToAdd = lineToAdd + line + ", ").length() <= 40) continue;
            current.add(lineToAdd);
            lineToAdd = available ? CC.Yellow : CC.Gray + CC.Strike;
        }
        if (ChatColor.stripColor((String)lineToAdd).length() > 0) {
            current.add(lineToAdd.substring(0, lineToAdd.contains(",") ? lineToAdd.lastIndexOf(",") : lineToAdd.length()));
        }
        return current;
    }

    public ItemStack getClaimableItem() {
        return me.ifamasssxd.cosmicevolutionkits.utils.ItemBuilder.buildItem(gemMaterial, (short)0, this.getKit().getKitGemItemName(), this.getKit().getKitGemLore());
    }

    public ItemStack getLevelUpItem(boolean applyIID) {
        ItemStack item = me.ifamasssxd.cosmicevolutionkits.utils.ItemBuilder.buildItem(Material.INK_SACK, (short)1, this.getKit().getKitLevelUpItemName(), this.getKit().getKitLevelUpLore());
        if (applyIID && ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
            item = ItemID.assignItemID(null, (ItemStack)item);
        }
        return new NBTWrapper(item).setNBTBase("cosmicData", (NBTBase)new NBTTagCompoundBuilder().setString("vkit", this.name()).get()).setString("cosmicType", "VKIT_LEVELUP").build();
    }

    public static EvolutionKits getFromLevelUp(ItemStack item) {
        String vkitItem;
        NBTWrapper wrapper = new NBTWrapper(item);
        if (wrapper.getString("cosmicType").equals("VKIT_LEVELUP") && (vkitItem = new NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")).getString("vkit")) != null && !vkitItem.isEmpty()) {
            try {
                return EvolutionKits.valueOf(vkitItem);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean levelUpOrUnlock(Player player, String source) {
        String perm = "cosmicevolutionkits.kit." + this.name().toLowerCase();
        if (!player.hasPermission(perm)) {
            LuckPermUtils.addPermissions(player, perm);   // Astro ran "pex user <name> add <perm>"; this server uses LuckPerms
            player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "(!) " + ChatColor.GOLD + "You have UNLOCKED the " + this.kit.getItemName() + ChatColor.GOLD + " VKIT!");
            player.sendMessage(ChatColor.GRAY + "Redeem your new Evolution Kit through the /vkit menu!");
            Bukkit.getLogger().info("[EvolutionKit] Unlocked Kit " + this.name() + " for " + player.getName() + " (" + player.getUniqueId() + ")!");
            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 5.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 2.0f, 1.1f);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 1.1f);
            RandomFireworks.getManager().launchRandomFirework(player.getLocation());
            TitleManager.sendTitle(player, this.kit.getItemName(), TitleManager.TitleColor.AQUA);
            TitleManager.sendSubTitle(player, ChatColor.GREEN + "Evolution Kit Unlocked", TitleManager.TitleColor.GREEN);
            Bukkit.getPluginManager().callEvent((Event)new CustomCosmicPlayerEvent(player, "VKIT_UNLOCK", new Object[]{this.kit}));
            return true;
        }
        KitPlayer kitPlayer = KitPlayer.kitPlayerMap.get(player.getUniqueId());
        if (kitPlayer != null) {
            String cdCooldown = kitPlayer.getCooldownTimer(this);
            if (cdCooldown != null) {
                player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must wait " + CC.RedU + cdCooldown + CC.Red + " for your /vkit cooldown to expire before applying that!");
                return false;
            }
            int kitLevel = kitPlayer.getLevel(this);
            int newLevel = kitLevel + 1;
            if (kitLevel < 10) {
                player.setItemInHand(null);
                kitPlayer.levelUp(player, newLevel, this, source);
                kitPlayer.setCooldown(this);
                player.updateInventory();
                return true;
            }
            player.sendMessage(CC.Red + "(!) " + CC.Red + "Your " + this.getKit().getItemName() + CC.Red + " /vkit is already MAX level!");
            player.playSound(player.getLocation(), Sound.GHAST_MOAN, 1.0f, 1.1f);
        }
        return false;
    }

    public static EvolutionKits getFromItemStack(ItemStack item) {
        if (item.getType() != gemMaterial && item.getType() != Material.QUARTZ || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) {
            return null;
        }
        String itemName = item.getItemMeta().getDisplayName();
        for (EvolutionKits kit : EvolutionKits.values()) {
            if (!kit.getKit().getKitGemItemName().equals(itemName) || !ItemUtils.isLoreSameWithoutIID(kit.getKit().getKitGemLore(), (List)item.getItemMeta().getLore())) continue;
            return kit;
        }
        return null;
    }

    public boolean checkFund(Player player) {
        if (false) {   // CosmicFund (/fund goal) is not on this server
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "(!) " + ChatColor.RED + "Your planet has not yet unlocked access to /vkits. To unlock, your planet needs to reach the " + ChatColor.UNDERLINE + "/fund" + ChatColor.RED + " goal.");
            player.sendMessage(ChatColor.GRAY + "* Until the goal is met, NO PLAYERS can redeem or use /vkit.");
            return true;
        }
        return false;
    }

    public static EvolutionKits getFromName(String name) {
        try {
            return EvolutionKits.valueOf(name);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private EvolutionKits(String name, long cooldown, EvolutionKit kit) {
        this.name = name;
        this.cooldown = cooldown;
        this.kit = kit;
    }

    public String getName() {
        return this.name;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public EvolutionKit getKit() {
        return this.kit;
    }

    static {
        gemMaterial = Material.DIAMOND;
        levelUpMaterial = Material.INK_SACK;
    }
}

