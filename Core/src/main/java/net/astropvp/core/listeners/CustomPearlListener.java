package net.astropvp.core.listeners;

import net.astropvp.core.entities.EscapistPearlEntity;
import net.astropvp.core.items.pearls.EscapistPearlItem;
import net.astropvp.core.items.pearls.OmniPearlItem;
import net.astropvp.core.items.pearls.SuperPearlItem;
import net.astropvp.core.killtracker.KillTrackerAPI;
import org.arkhamnetwork.Arkkit.patches.EnderpearlCooldown;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Escapist pearls fly through cobwebs and string, Super pearls fly 1.5x as fast, and Omni pearls do both,
 * are not used up and cost 5 souls per throw (like a Soul Pearl).
 */
public class CustomPearlListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onThrow(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() != Material.ENDER_PEARL) return;
        boolean omni = OmniPearlItem.isOmniPearl(item);
        if (!omni && !EscapistPearlItem.isEscapistPearl(item)) return;
        // already denied (cooldown or teleblock, which Arkkit reports)
        boolean denied = event.useItemInHand() == Event.Result.DENY;
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        if (denied || !EnderpearlCooldown.isEnderpearlCooldownOver(player) || !canThrowPearl(player)) {
            player.updateInventory();
            return;
        }
        if (omni) {
            if (!KillTrackerAPI.hasSoulAmount(player, 5)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " *** NOT ENOUGH SOULS " + ChatColor.RED + "(REQ: 5)" + ChatColor.BOLD + " ***");
                player.updateInventory();
                return;
            }
            KillTrackerAPI.removeSoulsFromGems(player, 5);
        } else if (item.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
        EscapistPearlEntity pearl = new EscapistPearlEntity(player);
        if (omni) {
            pearl.motX *= 1.5;
            pearl.motY *= 1.5;
            pearl.motZ *= 1.5;
        }
        player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 0.5F, 0.4F / (ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F));
        ((CraftWorld) player.getWorld()).getHandle().addEntity(pearl);
        player.updateInventory();
    }

    private boolean canThrowPearl(Player player) {
        if (player.hasMetadata("lastEnderpearlThrow")) {
            long lastEnderpearlThrow = ((MetadataValue) player.getMetadata("lastEnderpearlThrow").get(0)).asLong();
            return System.currentTimeMillis() - lastEnderpearlThrow >= 400L;
        }
        return true;
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl) || !(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        if (SuperPearlItem.isSuperPearl(player.getItemInHand()))
            event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(1.5));
    }
}
