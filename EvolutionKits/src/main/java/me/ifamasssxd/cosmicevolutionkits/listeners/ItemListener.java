/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.customevents.CustomCosmicPlayerEvent
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.NBTTagCompoundBuilder
 *  com.cosmicpvp.cosmicutils.utils.NBTWrapper
 *  com.cosmicpvp.cosmicutils.utils.PlayerUtil
 *  org.arkhamnetwork.Arkkit.libs.ParticleEffect
 *  org.arkhamnetwork.Arkkit.patches.ItemID
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugins
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package me.ifamasssxd.cosmicevolutionkits.listeners;

import com.cosmicpvp.cosmicutils.customevents.CustomCosmicPlayerEvent;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import java.text.DecimalFormat;
import java.util.List;
import me.ifamasssxd.cosmicevolutionkits.structure.EvolutionKits;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import me.ifamasssxd.cosmicevolutionkits.structure.ekits.Lucky;
import org.bukkit.Effect;
import org.arkhamnetwork.Arkkit.patches.ItemID;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener
implements Listener {
    private DecimalFormat df = new DecimalFormat("#,###");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.hasItem()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = event.getItem();
            Material type = item.getType();
            if (type == Material.AIR) {
                return;
            }
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
                NBTWrapper wrapper;
                if (type == Material.QUARTZ || type == EvolutionKits.gemMaterial) {
                    EvolutionKits kit = EvolutionKits.getFromItemStack(item);
                    if (kit == null) {
                        return;
                    }
                    event.setCancelled(true);
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    if (event.getPlayer().getWorld().getName().startsWith("world_duels")) {
                        player.sendMessage(ChatColor.RED + "Item not claimable in the dueling arena.");
                        return;
                    }
                    String perm = "cosmicevolutionkits.kit." + kit.name().toLowerCase();
                    if (item.getAmount() > 1) {
                        player.sendMessage(ChatColor.RED + "Please unstack your gems and retry.");
                        return;
                    }
                    if (player.hasPermission(perm)) {
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You already have the " + kit.getKit().getItemName() + ChatColor.RED + " Evolution Kit unlocked!");
                        player.sendMessage(ChatColor.RED + "Access your Evolution Kit with " + ChatColor.RED + ChatColor.UNDERLINE + "/vkit!");
                        return;
                    }
                    if (kit.levelUpOrUnlock(player, null)) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    }
                } else if (type == Material.INK_SACK) {
                    EvolutionKits kitFromItem = EvolutionKits.getFromLevelUp(item);
                    Bukkit.getLogger().info("/vkit: " + (Object)((Object)kitFromItem));
                    if (kitFromItem != null) {
                        event.setCancelled(true);
                        if (event.getPlayer().getWorld().getName().startsWith("world_duels")) {
                            player.sendMessage(ChatColor.RED + "Item not claimable in the dueling arena.");
                            return;
                        }
                        String perm = "cosmicevolutionkits.kit." + kitFromItem.name().toLowerCase();
                        if (item.getAmount() > 1) {
                            player.sendMessage(ChatColor.RED + "Please unstack your gems and retry.");
                            return;
                        }
                        if (!player.hasPermission(perm)) {
                            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You do not have the " + kitFromItem.getKit().getItemName() + ChatColor.RED + " Evolution Kit unlocked!");
                            player.sendMessage(CC.Gray + "Unlock it with the " + kitFromItem.getKit().getItemName() + CC.Gray + " /vkit Gem!");
                            return;
                        }
                        KitPlayer kitPlayer = KitPlayer.kitPlayerMap.get(player.getUniqueId());
                        if (kitPlayer != null) {
                            String cdCooldown = kitPlayer.getCooldownTimer(kitFromItem);
                            if (cdCooldown != null) {
                                player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must wait " + CC.RedU + cdCooldown + CC.Red + " for your /vkit cooldown to expire before applying that!");
                                return;
                            }
                            int kitLevel = kitPlayer.getLevel(kitFromItem);
                            int newLevel = kitLevel + 1;
                            if (kitLevel < 10) {
                                if (ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
                                    long id = ItemID.getItemID((ItemStack)item);
                                    if (id != -1L && ItemID.blacklist_iid.contains(id)) {
                                        player.sendMessage(CC.RedB + "(!) " + CC.Red + "It appears you tried to claim an already claimed Evolution Gem.");
                                        player.sendMessage(CC.Gray + "It has been removed from your inventory.");
                                        player.getInventory().removeItem(new ItemStack[]{item});
                                        player.updateInventory();
                                        Bukkit.getLogger().info("[CosmicEvolutionKits] " + player.getName() + " attempted to claim an already claimed Evolution Gem, IID: " + id);
                                        return;
                                    }
                                    if (id != -1L) {
                                        ItemID.blacklistIID((long)id, (String)("evolution gem claim " + player.getName()));
                                    }
                                }
                                player.setItemInHand(null);
                                kitPlayer.levelUp(player, newLevel, kitFromItem, "Evolution Gem");
                                kitPlayer.setCooldown(kitFromItem);
                                player.updateInventory();
                                Bukkit.getPluginManager().callEvent((Event)new CustomCosmicPlayerEvent(player, "VKIT_GEM_USE", new Object[]{kitFromItem, kitLevel, newLevel}));
                            } else {
                                player.sendMessage(CC.Red + "(!) " + CC.Red + "Your " + kitFromItem.getKit().getItemName() + CC.Red + " /vkit is already MAX level!");
                                player.playSound(player.getLocation(), Sound.GHAST_MOAN, 1.0f, 1.1f);
                            }
                        }
                    }
                } else if (type == Material.CHEST && (wrapper = new NBTWrapper(item)).getString("cosmicType").equalsIgnoreCase("LUCKY_BOX") && wrapper.hasKey("cosmicData")) {
                    event.setCancelled(true);
                    int level = new NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")).getInt("level");
                    if (level <= 0) {
                        return;
                    }
                    if (event.getPlayer().getWorld().getName().startsWith("world_duels")) {
                        player.sendMessage(ChatColor.RED + "Item not claimable in the dueling arena.");
                        return;
                    }
                    if (item.getAmount() > 1) {
                        player.sendMessage(ChatColor.RED + "Please unstack your Lucky Boxes and retry.");
                        return;
                    }
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot open a Lucky Box with a full inventory!");
                        return;
                    }
                    if (ArkPlugins.ITEM_ID.getArkPlugin().isRunning()) {
                        long itemID;
                        if (wrapper.isBlacklistedIID()) {
                            player.sendMessage(CC.RedB + "(!) " + CC.Red + "Lucky Box has already been redeemed!");
                            player.getInventory().removeItem(new ItemStack[]{item});
                            player.updateInventory();
                            return;
                        }
                        if (item.getAmount() == 1 && (itemID = ItemID.getItemID((ItemStack)item)) != -1L) {
                            ItemID.blacklistIID((long)itemID, (String)("Lucky box claimed " + player.getName()));
                        }
                    }
                    player.setItemInHand(null);
                    double cash = Math.pow(1 + level, 1.0 + Math.random()) * 50000.0;
                    List<ItemStack> items = ((Lucky)EvolutionKits.LUCKY.getKit()).getLuckyBoxItems(level, cash);
                    items.forEach(is -> PlayerUtil.giveOrDropProtectedItem((Player)player, (ItemStack)is, (int)-1));
                    player.updateInventory();
                    player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 10.0f, 0.8f);
                    player.getWorld().spigot().playEffect(player.getEyeLocation().subtract(0.0, 0.5, 0.0), Effect.SPELL, 0, 0, 0.5f, 0.5f, 0.5f, 0.1f, 50, 32);
                    player.getWorld().spigot().playEffect(player.getEyeLocation().subtract(0.0, 0.5, 0.0), Effect.FIREWORKS_SPARK, 0, 0, 0.5f, 0.5f, 0.5f, 0.33f, 40, 32);
                    player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "(!) " + ChatColor.YELLOW + "The " + ChatColor.GOLD + "Lucky Box (LVL " + level + ")" + ChatColor.YELLOW + " contains " + ChatColor.GOLD + (items.size() - 1) + " item(s)" + ChatColor.YELLOW + " and " + ChatColor.GOLD + "$" + this.df.format(cash) + "!");
                    return;
                }
            }
        }
    }
}

