package net.crankedpvp.staff.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import net.astropvp.core.AstroCore;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FreezeListener implements Listener {

    private AstroCore plugin;
    public FreezeListener(AstroCore plugin) {
        this.plugin = plugin;
    }

    private boolean isFrozen(Player player) {
        return this.plugin.getFreezeManager().isFrozen(player);
    }

    // no damage dealt by a frozen player, directly or with a projectile
    @EventHandler
    public void damage(EntityDamageByEntityEvent event) {
        if(event.getEntity() == null) return;
        if(event.getDamager() == null) return;
        Player attacker = null;
        if(event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if(event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            attacker = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if((event.getEntity() instanceof Player && this.isFrozen((Player) event.getEntity())) || (attacker != null && this.isFrozen(attacker))) {
            event.setCancelled(true);
            return;
        }
    }

    // no damage of any kind taken by a frozen player
    @EventHandler
    public void damageTaken(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(this.isFrozen((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void splash(PotionSplashEvent event) {
        if(event.getPotion().getShooter() instanceof Player && this.isFrozen((Player) event.getPotion().getShooter())) {
            event.setCancelled(true);
            return;
        }
        for(LivingEntity entity : event.getAffectedEntities()) {
            if(entity instanceof Player && this.isFrozen((Player) entity)) {
                event.setIntensity(entity, 0.0);
            }
        }
    }

    // a frozen player stays in place; looking around is still allowed
    @EventHandler(ignoreCancelled = true)
    public void move(PlayerMoveEvent event) {
        if(!this.isFrozen(event.getPlayer())) return;
        Location from = event.getFrom();
        Location to = event.getTo();
        if(to == null || (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ())) return;
        Location back = from.clone();
        back.setYaw(to.getYaw());
        back.setPitch(to.getPitch());
        event.setTo(back);
    }

    // only FreezeTeleportTask's teleport back to the frozen spot is allowed
    @EventHandler(ignoreCancelled = true)
    public void teleport(PlayerTeleportEvent event) {
        Location frozenAt = this.plugin.getFreezeManager().getFrozenPlayers().get(event.getPlayer().getUniqueId());
        if(frozenAt == null) return;
        if(event.getTo() != null && event.getTo().getWorld() == frozenAt.getWorld() && event.getTo().distanceSquared(frozenAt) < 0.01) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(CC.translate("&b&lFreeze &8➥ &7You cannot teleport while you are frozen"));
    }

    @EventHandler(ignoreCancelled = true)
    public void portal(PlayerPortalEvent event) {
        if(this.isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        if(event.getPlayer() == null) return;
        if(this.isFrozen(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent event) {
        if(event.getPlayer() == null) return;
        if(this.isFrozen(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
    }
}
