/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.UpgradeUtils
 *  com.google.common.collect.Lists
 *  com.rit.sucy.EnchantmentAPI
 *  org.arkhamnetwork.Arkkit.patches.ItemID
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugins
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.ifamasssxd.cosmicevolutionkits.structure;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.UpgradeUtils;
import com.google.common.collect.Lists;
import com.rit.sucy.EnchantmentAPI;
import java.util.List;
import java.util.Random;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import me.ifamasssxd.cosmicevolutionkits.utils.InventoryUtils;
import org.arkhamnetwork.Arkkit.patches.ItemID;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class EvolutionKit {
    protected final Random random = new Random();

    public abstract List<ItemStack> getItems(Player var1, int var2);

    public abstract String getItemName();

    public abstract List<String> getLore();

    public abstract Material getDisplayMaterial();

    public short getDisplayData() {
        return 0;
    }

    public abstract int getInventorySlot();

    public String getKitGemItemName() {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + "EVOLUTION KIT " + ChatColor.YELLOW + this.getItemName();
    }

    public List<String> getKitGemLore() {
        return Lists.newArrayList(new String[]{ChatColor.GRAY + "Right-click this gem to unlock", ChatColor.GRAY + "the power of the " + this.getItemName() + ChatColor.GRAY + " /vkit."});
    }

    public String getKitLevelUpItemName() {
        return CC.DarkGreenB + "Evolution Gem " + CC.DarkGreen + "(" + ChatColor.YELLOW + this.getItemName() + CC.DarkGreen + ")";
    }

    public List<String> getKitLevelUpLore() {
        return Lists.newArrayList(new String[]{ChatColor.GRAY + "Right-click this evolution gem to", ChatColor.GRAY + "increase the power of the", this.getItemName() + CC.Gray + " /vkit by 1 level."});
    }

    /** custom enchants this server doesn't have (Astro's "Disarmor", "Skilling") are left off the kit instead of breaking it */
    public static boolean hasEnchant(String name) {
        return EnchantmentAPI.getEnchantment(name) != null;
    }

    public int getMaxLevel(String name) {
        return EnchantmentAPI.getEnchantment((String)name).getMaxLevel();
    }

    public void onClaim(Player player, EvolutionKits parent, KitPlayer kitPlayer) {
        boolean dropped = false;
        int level = kitPlayer.getLevel(parent);
        List<ItemStack> items = this.getItems(player, level);
        if (items.size() > this.getEmptySlots(player) && parent != EvolutionKits.MIMIC) {
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Your inventory was too full to accept all of your kit items, please clear some more slots and try again.");
            return;
        }
        for (ItemStack item : items) {
            if (ArkPlugins.ITEM_ID.getArkPlugin().isRunning() && ItemID.needsItemID((ItemStack)item) && item.getAmount() == 1) {
                ItemID.setItemID((ItemStack)item, (long)ItemID.generateNewItemID());
            }
            if (InventoryUtils.giveItem(player, item)) continue;
            dropped = true;
        }
        if (dropped) {
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Your inventory was too full to accept all of your kit items, they have been dropped on the ground and are claimable by you for 30 seconds.");
        }
        kitPlayer.setAmountClaimed(kitPlayer.getAmountClaimed() + 1);
        int levelClaimed = level;
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "(!) " + ChatColor.GOLD + "You have claimed your " + ChatColor.GOLD + ChatColor.UNDERLINE + "Level " + ChatColor.GOLD + ChatColor.BOLD + ChatColor.UNDERLINE + levelClaimed + ChatColor.GOLD + " " + parent.getKit().getItemName() + ChatColor.GOLD + " Evolution Kit!");
        double levelUpChance = (10 - level) * 5;
        if (player.isOp() && !player.getName().equals("Woofless") && !player.getName().equals("TBNRfrags")) {
            levelUpChance = 100.0;
        }
        // the faction "Kit Evolution" upgrade gives a second chance
        int upgradeLevel = 0;   // UpgradeUtils.getUpgradeLevel(player, "HEROIC_VKIT_LEVELING"): no faction upgrades on this server
        double upgradeChance = upgradeLevel == 1 ? 0.1 : (upgradeLevel == 2 ? 0.15 : (upgradeLevel == 3 ? 0.2 : (upgradeLevel == 4 ? 0.3 : (upgradeLevel == 5 ? 0.45 : 0.0))));
        boolean secondUpgradeSuccess = upgradeLevel > 0 && upgradeChance >= Math.random();
        boolean originalSuccess = (double)this.random.nextInt(100) <= levelUpChance;
        if (level < 10 && (originalSuccess || secondUpgradeSuccess)) {
            player.sendMessage("");
            ++level;
            if (!originalSuccess || player.isOp() && secondUpgradeSuccess) {
                player.sendMessage(CC.YellowB + "(!) " + CC.Yellow + "You leveled up thanks to your " + CC.Gold + "Kit Evolution" + CC.Yellow + " /f upgrade!");
            }
            kitPlayer.levelUp(player, level, parent, null);
        }
        kitPlayer.setCooldown(parent);
        player.sendMessage("");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0f, 0.9f);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.3f);
    }

    private int getEmptySlots(Player p) {
        int x = 0;
        for (ItemStack i : p.getInventory()) {
            if (i != null && i.getType() != Material.AIR) continue;
            ++x;
        }
        return x;
    }
}

