package me.ifamasssxd.cosmicduel.listeners;

import me.ifamasssxd.cosmicduel.DuelAPI;
import me.ifamasssxd.cosmicduel.struct.newduel.NewDuel;
import me.ifamasssxd.cosmicduel.struct.newduel.TeamDuel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TeamDuelListener implements Listener {
    // no teammate damage in a team duel: melee and projectiles (bows, rods, pearls) from a teammate are cancelled;
    // enchant splash damage treats teammates as allies through the in_team_duel metadata (Enchantments GeneralUtil)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Entity source = event.getDamager();
        if (source instanceof Projectile && ((Projectile) source).getShooter() instanceof Player)
            source = (Player) ((Projectile) source).getShooter();
        if (!(source instanceof Player) || source == event.getEntity())
            return;
        Player entity = (Player) event.getEntity();
        Player damager = (Player) source;
        NewDuel duel = DuelAPI.getDuel(damager);
        if (duel instanceof TeamDuel && DuelAPI.inSameDuel(damager, entity) && ((TeamDuel) duel).onSameSide(damager, entity))
            event.setCancelled(true);
    }
}
