package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * /claim - explains how to claim faction land and gives a Claim Stick; right-clicking the stick runs /f claim,
 * so all of Factions' own rules (faction, power, permissions, borders) still apply.
 */
public class ClaimCommand implements CommandExecutor, Listener {
    private static final String STICK_NAME = CC.translate("&a&lClaim Stick &7(Right Click)");

    public static ItemStack getClaimStick() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName(STICK_NAME);
        meta.setLore(Arrays.asList(
                CC.translate("&7Right-click to claim the chunk"),
                CC.translate("&7you are standing in for your faction."),
                CC.translate("&7Same as &f/f claim&7.")));
        stick.setItemMeta(meta);
        return stick;
    }

    public static boolean isClaimStick(ItemStack item) {
        return item != null && item.getType() == Material.STICK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(STICK_NAME);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used in-game.");
            return true;
        }
        Player player = (Player) sender;
        player.sendMessage("");
        player.sendMessage(CC.translate("&a&lClaiming Land"));
        player.sendMessage(CC.translate("&f/f create <name> &7- start your own faction"));
        player.sendMessage(CC.translate("&f/f claim &7- claim the chunk you are standing in"));
        player.sendMessage(CC.translate("&f/f claim <radius> &7- claim the chunks around you"));
        player.sendMessage(CC.translate("&f/f autoclaim &7- claim every chunk you walk into"));
        player.sendMessage(CC.translate("&f/f unclaim &7- give up the chunk you are standing in"));
        player.sendMessage(CC.translate("&f/f map &7- see the claims around you"));
        player.sendMessage(CC.translate("&f/f power &7- your faction's power limits how much land it can hold"));
        for (ItemStack item : player.getInventory().getContents()) {
            if (isClaimStick(item)) {
                player.sendMessage(CC.translate("&7You already have a &aClaim Stick&7."));
                player.sendMessage("");
                return true;
            }
        }
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(CC.translate("&cYour inventory is full, so no Claim Stick was given."));
        } else {
            player.getInventory().addItem(getClaimStick());
            player.sendMessage(CC.translate("&a&l+ &aClaim Stick &7(right-click to claim the chunk you stand in)"));
        }
        player.sendMessage("");
        return true;
    }

    @EventHandler
    public void onUseClaimStick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && isClaimStick(event.getItem())) {
            event.setCancelled(true);
            event.getPlayer().performCommand("f claim");
        }
    }
}
