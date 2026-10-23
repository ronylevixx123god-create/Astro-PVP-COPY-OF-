package me.ifamasssxd.cosmicduel.utils;

import me.ifamasssxd.cosmicduel.CosmicDuel;
import me.ifamasssxd.cosmicduel.DuelAPI;
import org.arkhamnetwork.Arkkit.patches.combatlog.CombatLog;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Clears everything a duel can leave running on a player: enchant damage-over-time tasks (Corrupt, legacy Bleed),
 * enchant metadata (Bleed stacks, Divine Immolation, Deep Wounds, Hex), mcMMO bleeding, Wither/Poison and the
 * Arkkit combat tag with its last-attacker kill credit.
 * <p>
 * After a duel the player also gets a short "exit guard": damage that has no attacker (the CUSTOM/WITHER/POISON/MAGIC
 * ticks those effects deal) is cancelled until the guard expires or the player is hit by someone again, so a tick that
 * was already queued cannot hurt them or put them back into combat. Players who never were in a duel are not affected.
 */
public class DuelCleanup implements Listener {
    public static final String EXIT_GUARD = "duel_exit_guard";
    private static final long EXIT_GUARD_MS = 30000L;

    private static final List<String> ENCHANT_METADATA = Arrays.asList("bleedStack", "divineFire", "divineFireLevel",
            "deepWounds", "hexEnchantExpire", "hexEnchantLevel", "corruptEnchantExpire", "corruptEnchantLevel");
    private static final List<String> ENCHANT_TASK_METADATA = Arrays.asList("corruptTaskID", "ce_bleeding");
    private static final List<String> ARKKIT_METADATA = Arrays.asList("cl_combat", "lastPlayerDamagerUUID", "lastPlayerDamagerTime", "affectedByJokerMask");
    private static final EnumSet<EntityDamageEvent.DamageCause> LINGERING_CAUSES = EnumSet.of(EntityDamageEvent.DamageCause.CUSTOM,
            EntityDamageEvent.DamageCause.WITHER, EntityDamageEvent.DamageCause.POISON, EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.FIRE_TICK);

    private static Method mcMMOBleedRemove;
    private static boolean mcMMOChecked;

    /**
     * Full cleanup for a player leaving a duel (as fighter or spectator): effects, tasks, metadata and combat tag, and
     * starts the exit guard. Safe to call more than once.
     */
    public static void cleanse(Player p) {
        if (p == null)
            return;
        clearEffects(p);
        clearCombat(p);
        p.setMetadata(EXIT_GUARD, new FixedMetadataValue(CosmicDuel.get(), System.currentTimeMillis() + EXIT_GUARD_MS));
        // a tick that was already scheduled this tick can still re-tag them; clear the tag again once it has run
        Bukkit.getScheduler().runTaskLater(CosmicDuel.get(), () -> {
            if (p.isOnline() && !DuelAPI.hasDuel(p))
                clearCombat(p);
        }, 5L);
    }

    public static void clearEffects(Player p) {
        Plugin enchantments = Bukkit.getPluginManager().getPlugin("Enchantments");
        if (enchantments != null) {
            for (String key : ENCHANT_TASK_METADATA) {
                for (MetadataValue value : p.getMetadata(key)) {
                    if (value.getOwningPlugin() == enchantments && value.value() instanceof Number)
                        Bukkit.getScheduler().cancelTask(value.asInt());
                }
                p.removeMetadata(key, enchantments);
            }
            for (String key : ENCHANT_METADATA)
                p.removeMetadata(key, enchantments);
        }
        removeMcMMOBleed(p);
        p.removePotionEffect(PotionEffectType.WITHER);
        p.removePotionEffect(PotionEffectType.POISON);
        p.setFireTicks(0);
        p.setFallDistance(0.0F);
    }

    public static void clearCombat(Player p) {
        Plugin arkkit = Bukkit.getPluginManager().getPlugin("Arkkit");
        if (arkkit == null)
            return;
        boolean tagged = p.hasMetadata("cl_combat_monitor_task");
        for (String key : ARKKIT_METADATA)
            p.removeMetadata(key, arkkit);
        if (tagged && p.isOnline())
            CombatLog.unflagCombat(p);
    }

    private static void removeMcMMOBleed(LivingEntity le) {
        if (!mcMMOChecked) {
            mcMMOChecked = true;
            try {
                if (Bukkit.getPluginManager().isPluginEnabled("mcMMO"))
                    mcMMOBleedRemove = Class.forName("com.gmail.nossr50.runnables.skills.BleedTimerTask").getMethod("remove", LivingEntity.class);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[CosmicDuel] mcMMO bleed cleanup unavailable: " + e);
            }
        }
        if (mcMMOBleedRemove == null)
            return;
        try {
            mcMMOBleedRemove.invoke(null, le);
        } catch (Exception e) {
            Bukkit.getLogger().warning("[CosmicDuel] mcMMO bleed cleanup failed for " + le.getName() + ": " + e);
        }
    }

    public static boolean hasExitGuard(Player p) {
        for (MetadataValue value : p.getMetadata(EXIT_GUARD)) {
            if (value.getOwningPlugin() == CosmicDuel.get())
                return value.asLong() > System.currentTimeMillis();
        }
        return false;
    }

    public static void removeExitGuard(Player p) {
        p.removeMetadata(EXIT_GUARD, CosmicDuel.get());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLingeringDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player p = (Player) e.getEntity();
        if (!p.hasMetadata(EXIT_GUARD))
            return;
        if (!hasExitGuard(p) || DuelAPI.hasDuel(p)) {
            removeExitGuard(p);
            return;
        }
        if (e instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
            if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Entity)
                damager = (Entity) ((Projectile) damager).getShooter();
            if (damager instanceof LivingEntity) {
                // a new fight outside the duel: everything from here on is normal-world combat
                removeExitGuard(p);
                return;
            }
        }
        if (LINGERING_CAUSES.contains(e.getCause())) {
            e.setCancelled(true);
            clearEffects(p);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        // Bukkit keeps player metadata across reconnects, so state from a duel the player left by logging out is still here
        if (DuelAPI.getDuel(p) != null || DuelAPI.getDuelSpectating(p) != null)
            return;
        boolean stale = p.hasMetadata("in_duel") || p.hasMetadata("in_team_duel") || p.hasMetadata("spectator") || p.hasMetadata(EXIT_GUARD);
        p.removeMetadata("in_duel", CosmicDuel.get());
        p.removeMetadata("in_team_duel", CosmicDuel.get());
        p.removeMetadata("spectator", CosmicDuel.get());
        if (stale)
            cleanse(p);
    }
}
