package net.crankedpvp.staff.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import de.tr7zw.nbtapi.NBTItem;
import me.astropvp.proxypach.ProxyPatch;
import net.astropvp.core.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// the staff mode freeze tool (hotbar slot 9): right-click freezes or unfreezes the nearest player within 5 blocks
public class FreezeToolListener implements Listener {

    private static final double RANGE = 5.0;

    private final AstroCore plugin;
    private final Set<UUID> cooldown = new HashSet<>();

    public FreezeToolListener(AstroCore plugin) {
        this.plugin = plugin;
    }

    private static boolean isStaffModeItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR && !new NBTItem(itemStack).getString("staffModeItem").isEmpty();
    }

    private static boolean isFreezeTool(ItemStack itemStack) {
        return isStaffModeItem(itemStack) && new NBTItem(itemStack).getString("staffModeItem").equals("freezeTool");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void useTool(PlayerInteractEvent event) {
        if(!isFreezeTool(event.getItem())) return;
        event.setCancelled(true);
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        this.use(event.getPlayer(), null);
    }

    // right-clicking a player directly picks that player when they are within range
    @EventHandler(priority = EventPriority.LOWEST)
    public void useToolOnPlayer(PlayerInteractEntityEvent event) {
        if(!isFreezeTool(event.getPlayer().getItemInHand())) return;
        event.setCancelled(true);
        this.use(event.getPlayer(), event.getRightClicked() instanceof Player ? (Player) event.getRightClicked() : null);
    }

    private void use(Player player, Player clicked) {
        if(!this.plugin.getStaffManager().isPlayerInStaffMode(player)) {
            player.sendMessage(CC.translate(AstroCore.prefix + "&cYou can only use this while in staff mode"));
            return;
        }
        if(!player.hasPermission("staff.freeze")) {
            player.sendMessage(CC.translate(AstroCore.prefix + "&cYou do not have permission to use this"));
            return;
        }
        // one right-click can reach the server as more than one interaction
        if(!this.cooldown.add(player.getUniqueId())) return;
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.cooldown.remove(player.getUniqueId()), 5L);
        Player target = clicked != null && this.canTarget(player, clicked) ? clicked : this.nearestTarget(player);
        if(target == null) {
            player.sendMessage(CC.translate("&b&lFreeze &8➥ &7There is no player within &f5 &7blocks that you can freeze"));
            return;
        }
        this.plugin.getFreezeManager().toggleFreeze(player, target);
    }

    private boolean canTarget(Player player, Player target) {
        if(target == player || !target.isOnline() || target.isDead()) return false;
        if(target.getWorld() != player.getWorld() || target.getLocation().distanceSquared(player.getLocation()) > RANGE * RANGE) return false;
        if(target.hasMetadata("NPC")) return false;
        if(Bukkit.getPluginManager().getPlugin("ProxyPatch") != null && ProxyPatch.playerNames.contains(target.getName())) return false;
        // a frozen player can always be unfrozen; staff.freeze.bypass only stops a new freeze
        return this.plugin.getFreezeManager().isFrozen(target) || !target.hasPermission("staff.freeze.bypass");
    }

    private Player nearestTarget(Player player) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for(Player target : player.getWorld().getPlayers()) {
            if(!this.canTarget(player, target)) continue;
            double distance = target.getLocation().distanceSquared(player.getLocation());
            if(distance < nearestDistance) {
                nearest = target;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    // staff mode items stay in staff mode: never placed or dropped, also outside staff mode
    @EventHandler(priority = EventPriority.LOWEST)
    public void place(BlockPlaceEvent event) {
        if(!isStaffModeItem(event.getItemInHand())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void drop(PlayerDropItemEvent event) {
        if(event.getItemDrop() == null || !isStaffModeItem(event.getItemDrop().getItemStack())) return;
        event.setCancelled(true);
    }
}
