package net.crankedpvp.staff.struct;

import net.crankedpvp.staff.listeners.FrozenChatListener;
import com.cosmicpvp.cosmicutils.utils.CC;
import de.tr7zw.nbtapi.NBTItem;
import net.astropvp.core.AstroCore;
import net.crankedpvp.staff.managers.StaffManager;
import net.crankedpvp.staff.utils.ItemBuilder;
import net.crankedpvp.staff.utils.PlayerInventoryContents;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StaffMode {

    private final Player player;
    private final StaffManager staffManager;
    private final PlayerInventoryContents playerInventoryContents;

    public StaffMode(Player player) {
        this.player = player;
        this.staffManager = AstroCore.getInstance().getStaffManager();
        this.playerInventoryContents = new PlayerInventoryContents(player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public void putPlayerInStaffMode() {
        this.staffManager.putPlayerInStaffMode(this.player, this);
        this.player.sendMessage(CC.translate(AstroCore.prefix + "&eYou have entered staff mode"));
        this.player.playSound(this.player.getLocation(), Sound.SUCCESSFUL_HIT, 2.0F, 2.0F);
        this.completelyClearPlayersInventory();
        this.player.setGameMode(GameMode.CREATIVE);
        this.addRandomPlayerSelector();
        this.addVanish();
        this.addFreezeTool();
        this.addFrozenChat();
    }

    public void removePlayerFromStaffMode() {
        this.player.sendMessage(CC.translate(AstroCore.prefix + "&eYou have left staff mode"));
        this.player.playSound(this.player.getLocation(), Sound.BAT_TAKEOFF, 2.0F, 2.0F);
        this.staffManager.removePlayerFromStaffMode(this.player);
        FrozenChatListener.close(this.player.getUniqueId());
        this.player.setGameMode(GameMode.SURVIVAL);
        this.reinstateInventory();
    }

    private void reinstateInventory() {
        this.playerInventoryContents.restore(this.player);
    }

    private void addRandomPlayerSelector() {
        this.player.getInventory().setItem(4, getRandomPlayerSelector());
    }

    public static ItemStack getRandomPlayerSelector() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.COMPASS, "&a&lRandom Player Teleporter",
                "&7Right-Click this compass to teleport to",
                "&7a random player outside of the safezone");
        NBTItem nbtItem = new NBTItem(itemBuilder.colorize().build());
        nbtItem.setString("staffModeItem", "randomPlayerSelector");
        return nbtItem.getItem();
    }

    private void addVanish() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.FEATHER, "&b&lVanish",
                "&7Right-Click this to become invisible",
                "&7to regular players");
        NBTItem nbtItem = new NBTItem(itemBuilder.colorize().build());
        nbtItem.setString("staffModeItem", "vanish");
        this.player.getInventory().setItem(0, nbtItem.getItem());
    }

    private void addFreezeTool() {
        if(!this.player.hasPermission("staff.freeze")) return;
        this.player.getInventory().setItem(8, getFreezeTool());
    }

    public static ItemStack getFreezeTool() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.PACKED_ICE, "&b&lFreeze",
                "&7Right-Click to freeze or unfreeze",
                "&7the nearest player within 5 blocks");
        NBTItem nbtItem = new NBTItem(itemBuilder.colorize().build());
        nbtItem.setString("staffModeItem", "freezeTool");
        return nbtItem.getItem();
    }

    // next to the freeze tool, same permission
    private void addFrozenChat() {
        if(!this.player.hasPermission("staff.freeze")) return;
        this.player.getInventory().setItem(7, FrozenChatListener.getFrozenChatTool());
    }

    private void completelyClearPlayersInventory() {
        this.player.getInventory().setArmorContents(new ItemStack[4]);
        this.player.getInventory().clear();
    }

}
