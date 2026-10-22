// 
// Decompiled by Procyon v0.5.36
// 

package com.compilex.CosmicCrate;

import com.compilex.CosmicCrate.api.CCAPI;
import com.compilex.CosmicCrate.libs.Version;
import com.compilex.CosmicCrate.utils.LocalItemUtils;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import org.apache.commons.lang.WordUtils;
import org.arkhamnetwork.Arkkit.patches.ItemID;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class CosmicCrateLootSelector
{
    public Player player;
    public Version crateVersion;
    public Inventory lootInventory;
    public CosmicCratePhase phase;
    public List<Integer> selectedSlots;
    public List<ItemStack> rewards;
    public volatile List<ItemStack> displayRewards;
    public CopyOnWriteArrayList<SlotAnimationTask> animationTask;
    public String crateInventoryName;
    public MetaAnimationTask metaAnimation;
    public ItemStack metaItem;
    
    public CosmicCrateLootSelector(final Player p, final Version v) {
        this.player = p;
        this.crateVersion = v;
        this.animationTask = new CopyOnWriteArrayList<SlotAnimationTask>();
        this.selectedSlots = new ArrayList<Integer>();
        this.phase = CosmicCratePhase.GENERATING;
        this.rewards = CCAPI.getCosmicCrateLoot(p, v);
        this.displayRewards = new ArrayList<ItemStack>(this.rewards);
        this.crateInventoryName = "Astro Crate: " + WordUtils.capitalize(v.name());
        if (this.crateVersion.getCrateData() != null) {
            this.metaItem = this.crateVersion.getCrateData().getRandomMetaItem(p);
        }
    }
    
    public void open() {
        this.lootInventory = Bukkit.createInventory((InventoryHolder)null, 54, this.crateInventoryName);
        final ItemStack hiddenItem = LocalItemUtils.buildItem(Material.ENDER_CHEST, (short)0, ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "???", ChatColor.GRAY + "Click to redeem an item from", ChatColor.GRAY + "your " + ChatColor.UNDERLINE + this.crateVersion.name() + ChatColor.GRAY + " Astro Crate!");
        final ItemStack lockedItem = LocalItemUtils.buildItem(Material.STAINED_GLASS_PANE, (short)14, ChatColor.RED.toString() + ChatColor.BOLD.toString() + "???", ChatColor.GRAY + "Redeem all your other rewards to", ChatColor.GRAY + "unlock this special \"META\" item.");
        final ItemStack placeholderItem = LocalItemUtils.buildItem(Material.STAINED_GLASS_PANE, (short)7, ChatColor.DARK_GRAY.toString() + ChatColor.BOLD.toString() + "...", new String[0]);
        this.lootInventory.setItem(12, hiddenItem.clone());
        this.lootInventory.setItem(13, hiddenItem.clone());
        this.lootInventory.setItem(14, hiddenItem.clone());
        this.lootInventory.setItem(21, hiddenItem.clone());
        this.lootInventory.setItem(22, hiddenItem.clone());
        this.lootInventory.setItem(23, hiddenItem.clone());
        this.lootInventory.setItem(30, hiddenItem.clone());
        this.lootInventory.setItem(31, hiddenItem.clone());
        this.lootInventory.setItem(32, hiddenItem.clone());
        this.lootInventory.setItem(49, lockedItem.clone());
        for (int i = 0; i < this.lootInventory.getSize(); ++i) {
            if (this.lootInventory.getItem(i) == null) {
                this.lootInventory.setItem(i, placeholderItem);
            }
        }
        this.phase = CosmicCratePhase.REDEEMING_REWARDS;
        this.player.openInventory(this.lootInventory);
        this.player.playSound(this.player.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
    }
    
    public boolean hasSlotAnimation(final int slot) {
        for (final SlotAnimationTask sat : this.animationTask) {
            if (sat.slot == slot) {
                return true;
            }
        }
        return false;
    }
    
    public void runSlotAnimation(final int slot) {
        final SlotAnimationTask sat = new SlotAnimationTask(this, slot);
        this.animationTask.add(sat);
        sat.runTaskTimer((Plugin)CosmicCrate.get(), 1L, 1L);
    }
    
    public void endSlotAnimation(final int slot) {
        for (final SlotAnimationTask sat : this.animationTask) {
            if (sat.slot == slot) {
                Bukkit.getScheduler().cancelTask(sat.getTaskId());
                this.animationTask.remove(sat);
            }
        }
        if (this.displayRewards.size() == 0) {
            this.phase = CosmicCratePhase.META_ANIMATION;
            this.runMetaUnlockAnimation();
        }
    }
    
    public void endAllAnimations() {
        for (final SlotAnimationTask sat : this.animationTask) {
            Bukkit.getScheduler().cancelTask(sat.getTaskId());
        }
        this.animationTask.clear();
        this.endMetaAnimation();
    }
    
    public void runMetaUnlockAnimation() {
        (this.metaAnimation = new MetaAnimationTask(this, this.crateVersion)).runTaskTimer((Plugin)CosmicCrate.get(), 4L, 4L);
    }
    
    public void endMetaAnimation() {
        if (this.metaAnimation != null) {
            this.metaAnimation.cancel();
        }
    }
    
    public void conclude() {
        this.rewards.add(this.metaItem);
        if (this.rewards.size() > 0) {
            for (ItemStack i : this.rewards) {
                if (i == null) {
                    Bukkit.getLogger().info("[CosmicCrate] NULL item in rewards list for player: " + this.player.getName() + " opening: " + this.crateVersion.toString());
                }
                else {
                    if (ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
                        if (ItemID.hasItemID(i)) {
                            i = ItemID.removeItemID(i);
                        }
                        if (ItemID.needsItemID(i) && i.getAmount() < 16) {
                            if (i.getAmount() > 1) {
                                boolean dropped = false;
                                for (int am = 0; am < i.getAmount(); ++am) {
                                    ItemStack clone = i.clone();
                                    clone.setAmount(1);
                                    clone = ItemID.assignItemID((InventoryClickEvent)null, clone);
                                    if (!PlayerUtil.giveOrDropProtectedItem(this.player, clone, -1)) {
                                        dropped = true;
                                    }
                                }
                                if (dropped) {
                                    this.player.sendRawMessage(CC.Red + "Your inventory was full! Leftover loot has been " + CC.RedU + "dropped" + CC.Red + " as a protected item at your feet.");
                                }
                                Bukkit.getLogger().info("Giving " + this.player.getName() + " " + i.getAmount() + "x " + i + " due to requiring IIDs! Dropped on ground: " + dropped);
                                continue;
                            }
                            i = ItemID.assignItemID((InventoryClickEvent)null, i);
                        }
                    }
                    PlayerUtil.giveOrDropProtectedItem(this.player, i, -1);
                }
            }
            this.rewards.clear();
        }
        this.endAllAnimations();
        this.phase = CosmicCratePhase.DONE;
        this.player.removeMetadata("ccrate_loot_selector", (Plugin)CosmicCrate.get());
        if (this.crateVersion.getCrateData() != null) {
            this.crateVersion.getCrateData().playConclusion(this.player);
        }
    }
    
    public enum CosmicCratePhase
    {
        GENERATING, 
        REDEEMING_REWARDS, 
        META_ANIMATION, 
        UNLOCKING_META, 
        LOOTING, 
        DONE;
    }
    
    public class MetaAnimationTask extends BukkitRunnable
    {
        CosmicCrateLootSelector cls;
        Version crateVersion;
        ItemStack metaItem;
        int iterations;
        int intervals;
        
        MetaAnimationTask(final CosmicCrateLootSelector cls, final Version v) {
            this.iterations = 0;
            this.intervals = 0;
            this.cls = cls;
            this.crateVersion = v;
            this.metaItem = LocalItemUtils.buildItem(Material.ENDER_CHEST, (short)0, ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "???", ChatColor.GRAY + "Click to redeem an item from", ChatColor.GRAY + "your " + ChatColor.UNDERLINE + this.crateVersion.name() + ChatColor.GRAY + " Astro Crate!");
        }
        
        public void run() {
            if (this.intervals == 6) {
                this.cls.lootInventory.setItem(49, this.metaItem);
                boolean color = false;
                for (int i = 0; i < this.cls.lootInventory.getSize(); ++i) {
                    final ItemStack is = this.cls.lootInventory.getItem(i);
                    if (is != null && is.getType() == Material.STAINED_GLASS_PANE) {
                        if (this.crateVersion.getCrateData() != null) {
                            if (color) {
                                is.setDurability(this.crateVersion.getCrateData().getColorPane());
                                color = false;
                            }
                            else {
                                is.setDurability(this.crateVersion.getCrateData().getNonColorPane());
                                color = true;
                            }
                        }
                        this.cls.lootInventory.setItem(i, is);
                    }
                }
                this.cls.phase = CosmicCratePhase.UNLOCKING_META;
                this.cls.endMetaAnimation();
                return;
            }
            if (this.intervals < 5) {
                if (this.iterations++ == 5) {
                    ++this.intervals;
                    this.iterations = 1;
                }
                final ItemStack panel = LocalItemUtils.buildItem(Material.STAINED_GLASS_PANE, this.getAnimationGlassColor(this.intervals), ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "UNLOCKING...", new String[0]);
                for (int x = 0; x < 6; ++x) {
                    final int leftSlot = this.iterations - 1 + x * 9;
                    final int rightSlot = 9 - this.iterations + x * 9;
                    final ItemStack left = this.cls.lootInventory.getItem(leftSlot);
                    if (left != null && left.getType() == Material.STAINED_GLASS_PANE && leftSlot != 49) {
                        this.cls.lootInventory.setItem(leftSlot, panel);
                    }
                    final ItemStack right = this.cls.lootInventory.getItem(rightSlot);
                    if (right != null && right.getType() == Material.STAINED_GLASS_PANE && rightSlot != 49) {
                        this.cls.lootInventory.setItem(rightSlot, panel);
                    }
                }
                this.cls.player.playSound(this.cls.player.getLocation(), Sound.ORB_PICKUP, 1.0f, 0.3f);
            }
            else if (this.intervals >= 5) {
                if (this.iterations++ == 5) {
                    ++this.intervals;
                    this.iterations = 1;
                }
            }
        }
        
        private short getAnimationGlassColor(final int interval) {
            if (interval == 0) {
                return 0;
            }
            if (interval == 1) {
                return 5;
            }
            if (interval == 2) {
                return 3;
            }
            if (interval == 3) {
                return 4;
            }
            if (interval >= 4) {
                return 1;
            }
            return 15;
        }
    }
    
    public class SlotAnimationTask extends BukkitRunnable
    {
        CosmicCrateLootSelector cls;
        int slot;
        int iterations;
        
        SlotAnimationTask(final CosmicCrateLootSelector cls, final int slot) {
            this.slot = 0;
            this.iterations = 0;
            this.cls = cls;
            this.slot = slot;
        }
        
        public void run() {
            ++this.iterations;
            if (this.iterations >= 120) {
                for (final int x : this.getRowSlots(this.slot)) {
                    if (this.cls.lootInventory.getItem(x).getType() == Material.STAINED_GLASS_PANE && x != 49) {
                        this.cls.lootInventory.setItem(x, LocalItemUtils.buildItem(Material.STAINED_GLASS_PANE, (short)7, ChatColor.DARK_GRAY.toString() + ChatColor.BOLD.toString() + "...", new String[0]));
                    }
                }
                for (final int x : this.getColumnSlots(this.slot)) {
                    if (this.cls.lootInventory.getItem(x).getType() == Material.STAINED_GLASS_PANE && x != 49) {
                        this.cls.lootInventory.setItem(x, LocalItemUtils.buildItem(Material.STAINED_GLASS_PANE, (short)7, ChatColor.DARK_GRAY.toString() + ChatColor.BOLD.toString() + "...", new String[0]));
                    }
                }
                this.cls.lootInventory.setItem(this.slot, this.getRandomLegitItem());
                this.cls.player.playSound(this.cls.player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.2f);
                this.cls.endSlotAnimation(this.slot);
                return;
            }
            if (this.iterations >= 20 && this.iterations % 2 != 0) {
                return;
            }
            if (this.iterations >= 40 && this.iterations % 3 != 0) {
                return;
            }
            for (final int x : this.getRowSlots(this.slot)) {
                if (this.cls.lootInventory.getItem(x).getType() == Material.STAINED_GLASS_PANE && x != 49) {
                    this.cls.lootInventory.setItem(x, LocalItemUtils.buildItem(Material.STAINED_GLASS_PANE, (short)new Random().nextInt(7), ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "...", new String[0]));
                }
            }
            for (final int x : this.getColumnSlots(this.slot)) {
                if (this.cls.lootInventory.getItem(x).getType() == Material.STAINED_GLASS_PANE && x != 49) {
                    this.cls.lootInventory.setItem(x, LocalItemUtils.buildItem(Material.STAINED_GLASS_PANE, (short)new Random().nextInt(7), ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "...", new String[0]));
                }
            }
            this.cls.lootInventory.setItem(this.slot, this.getRandomFluffItem());
            this.cls.player.playSound(this.cls.player.getLocation(), Sound.CLICK, 0.4f, 0.2f);
            if (this.iterations <= 40) {
                this.cls.player.playSound(this.cls.player.getLocation(), Sound.ORB_PICKUP, 0.3f, 0.3f);
            }
        }
        
        private ItemStack getRandomFluffItem() {
            return this.cls.rewards.get(new Random().nextInt(this.cls.rewards.size()));
        }
        
        private ItemStack getRandomLegitItem() {
            if (this.cls.displayRewards.size() <= 0) {
                Bukkit.getLogger().info("[CosmicCrate] EMPTY displayRewards list! DEBUG: crateVersion:" + this.cls.crateVersion + ", player:" + this.cls.player.getName() + ", inventory:" + this.cls.lootInventory);
                return new ItemStack(Material.AIR);
            }
            final ItemStack i = this.cls.displayRewards.get(new Random().nextInt(this.cls.displayRewards.size()));
            this.cls.displayRewards.remove(i);
            return i;
        }
        
        private List<Integer> getRowSlots(int slot) {
            final List<Integer> slots = new ArrayList<Integer>();
            final int x = slot;
            while (slot % 9 != 0 && slot > 0) {
                slots.add(slot--);
            }
            slots.add(slot);
            slot = x;
            while (slot % 9 != 0 && slot < 54) {
                slots.add(slot++);
            }
            return slots;
        }
        
        private List<Integer> getColumnSlots(int slot) {
            final List<Integer> slots = new ArrayList<Integer>();
            final int x = slot;
            while (slot - 9 > 0) {
                final List<Integer> list = slots;
                slot -= 9;
                list.add(slot);
            }
            slot = x;
            while (slot + 9 < 54) {
                final List<Integer> list2 = slots;
                slot += 9;
                list2.add(slot);
            }
            return slots;
        }
    }
}
