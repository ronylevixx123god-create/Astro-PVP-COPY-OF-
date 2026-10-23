package me.cosmicpvp.CosmicGlobalKits.listeners;

import com.cosmicpvp.cosmicutils.customevents.CustomCosmicPlayerEvent;
import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import com.cosmicpvp.cosmicutils.utils.RandomFireworks;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKits;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKitsAPI;
import me.cosmicpvp.CosmicGlobalKits.libs.GlobalKits;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import me.cosmicpvp.CosmicGlobalKits.menu.MKitMenu;
import me.cosmicpvp.CosmicGlobalKits.menu.MKitPreviewMenu;
import me.cosmicpvp.CosmicGlobalKits.permissions.GrantResult;
import me.cosmicpvp.CosmicGlobalKits.permissions.KitPermissionGranter;
import me.cosmicpvp.CosmicGlobalKits.utils.TitleManager;
import org.arkhamnetwork.Arkkit.patches.ItemID;
import org.arkhamnetwork.Arkkit.plugins.ArkPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class MasteryKitListener
implements Listener {
    private static final String PENDING = "mkit_unlock_pending";

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack clicked = event.getItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            Player player = event.getPlayer();
            NBTWrapper wrapper = new NBTWrapper(clicked);
            if (wrapper.getString("cosmicType").equalsIgnoreCase("MASTERY_KIT")) {
                event.setCancelled(true);
                NBTTagCompoundBuilder cosmicData = new NBTTagCompoundBuilder(wrapper.getNBTBase("conaxData"));
                String masteryKit = cosmicData.getString("kit");
                if (masteryKit == null || masteryKit.isEmpty()) {
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "Invalid Mastery Kit item detected!");
                    return;
                }
                MasteryKits kit = MasteryKits.getFromName(masteryKit);
                if (kit == null) {
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "Invalid Mastery Kit item detected!");
                    return;
                }
                if (player.getWorld().getName().contains("world_duels")) {
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot redeem this item while in a duel.");
                    return;
                }
                if (player.hasMetadata(PENDING)) {
                    // the previous gem is still being saved to LuckPerms
                    return;
                }
                if (kit.hasPermission(player)) {
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "You have already unlocked access to this Mastery Kit!");
                    player.sendMessage(CC.Gray + "Use '/mkit' to claim your kit!");
                    return;
                }
                for (GlobalKits kits : kit.getKitsRequired()) {
                    if (kits.hasAccess(player) && CosmicGlobalKitsAPI.getKitTier(player, kits) >= 3) continue;
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "You do not have the required '" + kits.getItemName() + CC.Red + "' Tier III /gkit unlocked!");
                    player.sendMessage(CC.Gray + "Purchase a Fallen Hero at " + CC.GrayU + "buy.astropvp.net" + CC.Gray + " to unlock and level-up your /gkit!");
                    return;
                }
                long iid = 0L;
                boolean itemIds = ArkPlugins.ITEM_ID.getArkPlugin().isRunning();
                if (itemIds) {
                    iid = wrapper.getIID();
                    if (wrapper.isBlacklistedIID()) {
                        player.sendMessage(CC.RedB + "(!) " + CC.Red + "You attempted to use an already claimed item!");
                        player.getInventory().removeItem(new ItemStack[]{clicked});
                        player.updateInventory();
                        Bukkit.getLogger().info("Removing duped Mastery Kit Item from " + player.getName() + ": " + (Object)clicked + " IID: " + iid);
                        return;
                    }
                }
                // Astro took the gem and ran "pex user ... remove/add". The LuckPerms save is asynchronous, so the gem
                // is taken now (it cannot be dropped or used twice meanwhile) and given back if the save fails.
                ItemStack gem = clicked.clone();
                gem.setAmount(1);
                if (clicked.getAmount() > 1) {
                    clicked.setAmount(clicked.getAmount() - 1);
                } else {
                    player.setItemInHand(null);
                }
                player.updateInventory();
                List<String> revoke = new ArrayList<>();
                for (GlobalKits gkit : kit.getKitsRequired()) {
                    revoke.add(gkit.getPermissionString());
                }
                CosmicGlobalKits plugin = CosmicGlobalKits.getPlugin();
                player.setMetadata(PENDING, new FixedMetadataValue((Plugin)plugin, true));
                String scope = plugin.getConfig().getString(KitPermissionGranter.CONFIG_KEY, "");
                final long claimedIID = iid;
                KitPermissionGranter.bukkit((Plugin)plugin).exchange(player.getUniqueId(), revoke, kit.getPermissionString(), scope, result -> {
                    player.removeMetadata(PENDING, (Plugin)plugin);
                    if (result != GrantResult.GRANTED) {
                        Bukkit.getLogger().info("[MKitUnlock] " + player.getName() + " (" + player.getUniqueId() + ") could not unlock the " + kit.getName() + " kit (" + result + "), item returned");
                        if (player.isOnline()) {
                            PlayerUtil.giveOrDropProtectedItem(player, gem, -1);
                            player.sendMessage(CC.RedB + "(!) " + CC.Red + "Unable to unlock the " + kit.getName() + CC.Red + " Mastery Kit right now. Your item was not used; please try again or contact staff.");
                        } else {
                            Bukkit.getLogger().severe("[MKitUnlock] " + player.getName() + " logged out before the failed unlock returned: " + gem);
                        }
                        return;
                    }
                    if (itemIds) {
                        ItemID.blacklistIID((long)claimedIID, (String)("mastery kit claimed " + player.getName()));
                    }
                    Bukkit.getLogger().info("[MKitUnlock] " + player.getName() + " (" + player.getUniqueId() + ") redeemed a " + kit.getName() + " kit item!");
                    if (!player.isOnline()) {
                        return;
                    }
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "You have lost access to the following Global Kits:");
                    for (GlobalKits gkit : kit.getKitsRequired()) {
                        player.sendMessage(CC.Gold + " - " + gkit.getItemName());
                    }
                    player.sendMessage(CC.GoldB + "(!) " + CC.Gold + "You UNLOCKED access to the " + kit.getName() + CC.Gold + " Mastery Kit!");
                    player.sendMessage(CC.Gray + "Use /mkit to redeem your new kit!");
                    RandomFireworks.getManager().launchRandomFirework(player.getLocation());
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.1f);
                    TitleManager.sendTitle(player, kit.getName(), TitleManager.TitleColor.GOLD);
                    TitleManager.sendSubTitle(player, "Mastery Kit Unlock!", TitleManager.TitleColor.GOLD);
                    player.updateInventory();
                    Bukkit.getPluginManager().callEvent((Event)new CustomCosmicPlayerEvent(player, "ITEM_USE", new Object[]{"MASTERY_KIT", kit.name()}));
                });
            }
        }
    }

    // CosmicGUI cancels clicks but not drags: an item dragged onto the /mkit menu or a preview was lost with it
    @EventHandler
    public void onMenuDrag(InventoryDragEvent event) {
        CosmicGUI active = CosmicGUI.getActiveGUI(event.getWhoClicked().getUniqueId());
        if (active instanceof MKitMenu || active instanceof MKitPreviewMenu) {
            event.setCancelled(true);
        }
    }
}
