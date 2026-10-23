/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.google.common.collect.Lists
 *  com.massivecraft.factions.FPlayer
 *  com.massivecraft.factions.FPlayers
 *  com.massivecraft.factions.iface.RelationParticipator
 *  net.minecraft.server.v1_8_R3.ItemStack
 *  net.minecraft.server.v1_8_R3.NBTBase
 *  net.minecraft.server.v1_8_R3.NBTTagCompound
 *  net.minecraft.server.v1_8_R3.NBTTagList
 *  org.arkhamnetwork.Arkkit.utils.WorldGuardUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package me.ifamasssxd.cosmicevolutionkits.structure;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.iface.RelationParticipator;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKit;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.KitCooldowns;
import me.ifamasssxd.cosmicevolutionkits.utils.InventoryUtils;
import me.ifamasssxd.cosmicevolutionkits.utils.ItemBuilder;
import me.ifamasssxd.cosmicevolutionkits.utils.RandomFireworks;
import me.ifamasssxd.cosmicevolutionkits.utils.TimeUtils;
import me.ifamasssxd.cosmicevolutionkits.utils.TitleManager;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.arkhamnetwork.Arkkit.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class KitPlayer {
    public static Map<UUID, KitPlayer> kitPlayerMap = new HashMap<UUID, KitPlayer>();
    private KitCooldowns kitCooldowns = new KitCooldowns();
    private Map<EvolutionKits, Integer> kitLevels = new HashMap<EvolutionKits, Integer>();
    private UUID uuid;
    private String name;
    public boolean loadingData = true;
    private int amountClaimed = 0;

    public KitPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadData(boolean async) {
        if (async) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask((Plugin)CosmicEvolutionKits.get(), () -> this.loadData(false));
            return;
        }
        java.sql.PreparedStatement statement = null;
        try {
            statement = CosmicEvolutionKits.get().getSqlDatabase().getConnection().prepareStatement("SELECT kitName, lastKitUse, kitLevel, numberOfUses FROM `" + CosmicEvolutionKits.get().getServerName() + "_evolutionKitData` WHERE uuid = ?;");
            statement.setString(1, this.uuid.toString());
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                EvolutionKits kit = EvolutionKits.getFromName(results.getString("kitName"));
                if (kit == null) continue;
                this.kitCooldowns.put(kit, results.getLong("lastKitUse"));
                this.kitLevels.put(kit, results.getInt("kitLevel"));
                this.amountClaimed = results.getInt("numberOfUses");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.loadingData = false;
    }

    public int getLevel(EvolutionKits kit) {
        Integer level = this.kitLevels.get((Object)kit);
        if (level == null) {
            return 1;
        }
        return level;
    }

    public void levelUp(final Player player, int level, EvolutionKits parent, String source) {
        this.getKitLevels().put(parent, level);
        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + ">> " + ChatColor.YELLOW + "Congratulations! Your " + parent.getKit().getItemName() + ChatColor.YELLOW + " /vkit is now level " + ChatColor.YELLOW + ChatColor.UNDERLINE.toString() + level + ChatColor.YELLOW + "!");
        player.sendMessage(ChatColor.GRAY + "You will now receive higher enchantment levels the next time you claim this /vkit!");
        String msg = (ChatColor.GRAY.toString() + ChatColor.BOLD + "** ").intern() + ChatColor.WHITE + player.getName() + "'s " + parent.getKit().getItemName() + ChatColor.GRAY + " /vkit has evolved to level ".intern() + ChatColor.WHITE + ChatColor.UNDERLINE + level + ChatColor.GRAY + (source != null ? " using an " + CC.Green + source + CC.Gray + "!" : "!").intern();
        player.sendMessage("");
        if (level >= 8) {
            Bukkit.broadcastMessage((String)msg);
        } else {
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                FPlayer fp = FPlayers.getInstance().getByPlayer(p);
                if (!WorldGuardUtils.isPvPDisabled((Location)p.getLocation()) && !fplayer.getRelationTo((RelationParticipator)fp).isMember()) continue;
                p.sendMessage(msg);
            }
        }
        TitleManager.sendTitle(player, ChatColor.GREEN + ChatColor.BOLD.toString() + "Level " + level, TitleManager.TitleColor.GREEN);
        TitleManager.sendSubTitle(player, parent.getKit().getItemName() + ChatColor.GREEN + " Level UP!", TitleManager.TitleColor.GREEN);
        new BukkitRunnable(){
            int counter = 0;

            public void run() {
                if (!player.isOnline() || player.isDead() || this.counter >= 6) {
                    this.cancel();
                    return;
                }
                RandomFireworks.getManager().launchRandomFirework(player.getLocation());
                ++this.counter;
            }
        }.runTaskTimer((Plugin)CosmicEvolutionKits.get(), 20L, 5L);
    }

    public void setCooldown(EvolutionKits kit) {
        this.kitCooldowns.put(kit, System.currentTimeMillis());
        this.save(kit);
    }

    public void save(EvolutionKits kit) {
        CosmicEvolutionKits.get().getSqlQueryThread().addQuery("REPLACE INTO `" + CosmicEvolutionKits.get().getServerName() + "_evolutionKitData`(uuid, name, kitName, kitLevel, lastKitUse, numberOfUses) VALUES ('" + this.uuid.toString() + "', '" + this.name + "', '" + kit.name() + "', '" + this.getLevel(kit) + "', '" + this.kitCooldowns.getOrDefault((Object)kit, 0L) + "', '" + this.amountClaimed + "');");
    }

    public Inventory getKitInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, (int)InventoryUtils.getRoundedSize(EvolutionKits.values().length), (String)"Evolution Kits");
        for (EvolutionKits kit : EvolutionKits.values()) {
            ItemStack item = this.getKitItem(kit, player);
            inventory.setItem(kit.getKit().getInventorySlot(), item);
        }
        return inventory;
    }

    public ItemStack getKitItem(EvolutionKits kit, Player player) {
        return this.getKitItem(kit, player, true);
    }

    public ItemStack getKitItem(EvolutionKits kit, Player player, boolean unlockLore) {
        NBTTagCompound compound;
        short data;
        Material material;
        EvolutionKit kitInstance = kit.getKit();
        List<String> lore = new ArrayList<String>(kitInstance.getLore());
        if (player != null && this.hasAccess(player, kit) || player == null) {
            List<String> newLore = new ArrayList<String>();
            Integer level = this.kitLevels.get((Object)kit);
            newLore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kit Level: " + ChatColor.YELLOW + ChatColor.UNDERLINE + (level == null ? 1 : level) + ChatColor.YELLOW + " / 10");
            newLore.addAll(lore);
            lore = newLore;
            lore.add("");
            lore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "UNLOCKED");
            String cooldownTimer = this.getCooldownTimer(kit);
            if (cooldownTimer != null) {
                material = Material.STAINED_GLASS_PANE;
                data = DyeColor.YELLOW.getWoolData();
                lore.add(ChatColor.YELLOW + "Claim again in " + ChatColor.YELLOW + ChatColor.BOLD.toString() + cooldownTimer);
            } else {
                material = kit.getKit().getDisplayMaterial();
                data = kit.getKit().getDisplayData();
                if (unlockLore) {
                    lore.add(ChatColor.GRAY + "Click to receive kit.");
                }
            }
        } else {
            material = Material.STAINED_GLASS_PANE;
            data = DyeColor.RED.getWoolData();
            lore.add((String)"");
            lore.add((String)(ChatColor.RED + ChatColor.BOLD.toString() + "LOCKED"));
            if (unlockLore) {
                lore.add(ChatColor.GRAY + "Unlock at " + ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "buy.cosmicpvp.com");
            }
        }
        ItemStack item = ItemBuilder.buildItem(material, data, kitInstance.getItemName() + " Kit", lore);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy((ItemStack)item);
        if (!nmsItem.hasTag()) {
            compound = new NBTTagCompound();
            nmsItem.setTag(compound);
        } else {
            compound = nmsItem.getTag();
        }
        if (data == 4) {
            NBTTagList enchList = new NBTTagList();
            compound.set("ench", (NBTBase)enchList);
        }
        compound.setString("evolutionKit", kit.name());
        nmsItem.setTag(compound);
        item = CraftItemStack.asCraftMirror((net.minecraft.server.v1_8_R3.ItemStack)nmsItem);
        return item;
    }

    public boolean hasAccess(Player player, EvolutionKits kit) {
        return player.hasPermission("cosmicevolutionkits.kit." + kit.name().toLowerCase());
    }

    public String getCooldownTimer(EvolutionKits kit) {
        Long lastUsed = (Long)this.kitCooldowns.get((Object)kit);
        if (lastUsed == null) {
            return null;
        }
        long timePassed = System.currentTimeMillis() - lastUsed;
        long secondsLeft = (kit.getCooldown() - timePassed) / 1000L;
        if (timePassed > kit.getCooldown() || secondsLeft <= 0L) {
            this.kitCooldowns.remove((Object)kit);
            return null;
        }
        return TimeUtils.formatDifference(secondsLeft);
    }

    public KitCooldowns getKitCooldowns() {
        return this.kitCooldowns;
    }

    public Map<EvolutionKits, Integer> getKitLevels() {
        return this.kitLevels;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean isLoadingData() {
        return this.loadingData;
    }

    public void setAmountClaimed(int amountClaimed) {
        this.amountClaimed = amountClaimed;
    }

    public int getAmountClaimed() {
        return this.amountClaimed;
    }
}

