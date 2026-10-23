package me.cosmicpvp.CosmicGlobalKits.menu;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.InventoryUtils;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import com.google.common.collect.Lists;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKits;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKitsAPI;
import me.cosmicpvp.CosmicGlobalKits.libs.GlobalKits;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitData;
import me.cosmicpvp.CosmicGlobalKits.struct.GlobalKitUser;
import me.cosmicpvp.CosmicGlobalKits.utils.TitleManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class MKitMenu
extends CosmicGUI {
    public MKitMenu(Player player) {
        super(player, "Mastery Kits", InventoryUtils.fitSize((int)MasteryKits.values().length));
    }

    public void redraw() {
        GlobalKitUser user = CosmicGlobalKits.getPlugin().getGlobalKitUser(this.player.getUniqueId());
        if (user == null) {
            this.closeWithDelay();
            return;
        }
        for (MasteryKits kits : MasteryKits.values()) {
            String cooldownString;
            ArrayList lore = Lists.newArrayList(kits.getDescription());
            lore.add("");
            lore.add(CC.GoldB + "Requirements:");
            for (GlobalKits gkit : kits.getKitsRequired()) {
                boolean access = gkit.hasAccess(this.player) && CosmicGlobalKitsAPI.getKitTier(this.player, gkit) >= 3;
                lore.add(CC.GoldB + " " + (access ? CC.Strike : "") + "* " + (access ? ChatColor.stripColor((String)gkit.getItemName()) : CC.Gold + gkit.getItemName() + CC.GoldB) + " Tier III");
            }
            lore.add("");
            boolean unlocked = false;
            GlobalKitData data = user.getMasteryKits().get((Object)kits);
            String string = cooldownString = data != null ? data.getCooldownString(kits) : null;
            if (kits.hasPermission(this.player)) {
                unlocked = true;
                if (cooldownString == null) {
                    lore.add(CC.GreenB + "UNLOCKED");
                    lore.add(CC.Gray + "Left-Click to receive mastery kit items.");
                } else {
                    lore.add(CC.Yellow + "Cooldown over in .. " + CC.YellowB + cooldownString);
                }
            } else {
                lore.add(CC.RedB + "LOCKED");
                lore.add(CC.Gray + "Unlock at " + CC.GrayU + "buy.astropvp.net");
            }
            lore.add(CC.Gray + "Right-Click to preview kit items.");
            MaterialData icon = null;
            icon = unlocked && cooldownString == null ? kits.getData() : (cooldownString != null && unlocked ? new MaterialData(Material.STAINED_GLASS_PANE, DyeColor.YELLOW.getWoolData()) : new MaterialData(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData()));
            this.setItem(kits.getSlot(), new InventoryItem(new ItemBuilder(icon.getItemType()).name((cooldownString != null ? CC.YellowB : (unlocked ? CC.GreenB : CC.RedB)) + kits.getName() + " Mastery Kit").lore((List)lore).data((short)icon.getData()).build()).click(ClickType.RIGHT, () -> this.closeWithDelay(pl -> new MKitPreviewMenu((Player)pl, kits, kits.getKit().getMasteryItems((Player)pl, kits, false)).openGUI(CosmicGlobalKits.getPlugin()))).click(ClickType.LEFT, () -> {
                if (!kits.hasPermission(this.player)) {
                    this.closeWithDelay();
                    this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You have not unlocked this Mastery Kit!");
                    this.player.sendMessage(CC.Gray + "Use a Mastery Kit Gem to unlock it!");
                    return;
                }
                if (Bukkit.getPluginManager().isPluginEnabled("CosmicFund") && !MKitMenu.isFundMKitsEnabled() && !this.player.isOp()) {
                    this.player.sendMessage(ChatColor.RED.toString() + (Object)ChatColor.BOLD + "(!) " + (Object)ChatColor.RED + "Your planet has not yet unlocked access to /mkit Mastery Kits!. To unlock, your planet needs to reach the " + (Object)ChatColor.UNDERLINE + "/fund" + (Object)ChatColor.RED + " goal.");
                    this.player.sendMessage((Object)ChatColor.GRAY + "* Until the goal is met, NO PLAYERS can redeem these kits.");
                    return;
                }
                GlobalKitData gkitData = user.getMasteryKits().computeIfAbsent(kits, e -> new GlobalKitData(0L, 0));
                String cdString = gkitData.getCooldownString(kits);
                if (cdString == null) {
                    this.closeWithDelay();
                    gkitData.handleUsed(this.player, kits);
                    this.player.playSound(this.player.getLocation(), Sound.LEVEL_UP, 1.0f, 0.9f);
                    kits.giveToPlayer(this.player);


                    TitleManager.sendTitle(this.player, kits.getName(), TitleManager.TitleColor.RED);
                    TitleManager.sendSubTitle(this.player, CC.GoldB + "Mastery Kit Claimed", TitleManager.TitleColor.GOLD);
                    return;
                }
                this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You " + CC.RedU + "cannot" + CC.Red + " use that Mastery Kit for " + cdString + "!");
                this.closeWithDelay();
            }));
        }
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack item = this.inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) continue;
            this.setItem(i, new InventoryItem(new ItemBuilder(Material.STAINED_GLASS_PANE, " ", new String[0]).data((short)DyeColor.GRAY.getWoolData()).build()));
        }
    }

    // CosmicFundAPI.isMKitsEnabled(); CosmicFund (the /fund goal) is not on this server, so this only runs if it is added
    static boolean isFundMKitsEnabled() {
        try {
            return (Boolean) Class.forName("com.compilex.CosmicFund.CosmicFundAPI").getMethod("isMKitsEnabled").invoke(null);
        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            return false;
        }
    }
}
