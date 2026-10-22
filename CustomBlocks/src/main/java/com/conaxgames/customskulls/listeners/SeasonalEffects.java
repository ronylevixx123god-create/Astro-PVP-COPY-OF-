package com.conaxgames.customskulls.listeners;

import com.conaxgames.customskulls.CoreSkulls;
import com.conaxgames.customskulls.ability.AbilityData;
import com.conaxgames.customskulls.enums.AbilityType;
import com.conaxgames.customskulls.util.ChunkUtil;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The buffs of the seasonal custom blocks, for players standing in the block's chunk who are in the faction of the player
 * who placed it (as the original Cauldron / Pumpkin Pie code checked).
 * Damage and mob EXP are applied here; the effects that live in other plugins (Silence, Phoenix, Monopoly, Slot Bot,
 * lootboxes, spawner drops) read the player metadata below, whose value is their multiplier or chance. Nothing is set
 * when no such block is around, so those plugins behave as before.
 */
public class SeasonalEffects implements Listener, Runnable {
    public static final String OUTGOING_DAMAGE = "astro_cb_outgoing_damage_multiplier";
    public static final String INCOMING_DAMAGE = "astro_cb_incoming_damage_multiplier";
    public static final String SILENCE_CHANCE = "astro_cb_silence_chance_multiplier";
    public static final String MONOPOLY_CHANCE = "astro_cb_monopoly_chance_multiplier";
    public static final String PHOENIX_COOLDOWN = "astro_cb_phoenix_cooldown_multiplier";
    public static final String SLOTBOT_FREE_ROLL = "astro_cb_slotbot_free_roll_chance";
    public static final String LOOTBOX_BONUS = "astro_cb_lootbox_bonus_chance";
    public static final String SPAWNER_DOUBLE_DROPS = "astro_cb_spawner_double_drop_chance";
    // stacked spawner mobs (AstroSpawners) give their EXP through the grinding menu, not as dropped orbs
    public static final String MOB_EXP = "astro_cb_mob_exp_multiplier";
    private static final String[] KEYS = {OUTGOING_DAMAGE, INCOMING_DAMAGE, SILENCE_CHANCE, MONOPOLY_CHANCE, PHOENIX_COOLDOWN, SLOTBOT_FREE_ROLL, LOOTBOX_BONUS, SPAWNER_DOUBLE_DROPS, MOB_EXP};

    private static final Set<AbilityType> SEASONAL = EnumSet.of(AbilityType.HALLOWEEN_CAULDRON, AbilityType.THANKSGIVING_PIE, AbilityType.TREASURE_CHEST,
            AbilityType.COSMIC_TROPHY, AbilityType.LEVIATHAN_TOTEM, AbilityType.HALLOWEEN_CANDLES);

    private static double setting(String key, double def) {
        return CoreSkulls.getInstance().getConfig().getDouble("effects." + key, def);
    }

    /** the seasonal blocks in the chunk of a location whose owner is in the given faction */
    private static List<AbilityType> blocksFor(Location location, Faction faction) {
        List<AbilityType> types = new ArrayList<>();
        if (faction == null || faction.isWilderness()) return types;
        for (AbilityData data : new ArrayList<>(AbilityData.getAbilityDataMap().values())) {
            if (data == null || data.getAbilityType() == null || data.getLocation() == null || data.getOwner() == null) continue;
            if (!SEASONAL.contains(data.getAbilityType())) continue;
            if (data.getLocation().getWorld() == null || !data.getLocation().getWorld().equals(location.getWorld())) continue;
            if (!ChunkUtil.isLocationAInSameChunkAsLocationB(data.getLocation(), location)) continue;
            FPlayer owner = FPlayers.getInstance().getById(data.getOwner().toString());
            if (owner != null && owner.getFaction() == faction) types.add(data.getAbilityType());
        }
        return types;
    }

    private static Faction factionOf(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        return fPlayer == null || !fPlayer.hasFaction() ? null : fPlayer.getFaction();
    }

    /** every second: which buffs each online player has right now */
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<String, Double> values = new HashMap<>();
            for (AbilityType type : blocksFor(player.getLocation(), factionOf(player))) {
                switch (type) {
                    case THANKSGIVING_PIE:
                        values.merge(OUTGOING_DAMAGE, setting("pumpkinPieOutgoingDamage", 1.05), (a, b) -> a * b);
                        values.merge(SILENCE_CHANCE, setting("pumpkinPieSilenceChance", 1.5), (a, b) -> a * b);
                        values.merge(MONOPOLY_CHANCE, setting("pumpkinPieMonopolyChance", 1.25), (a, b) -> a * b);
                        break;
                    case HALLOWEEN_CAULDRON:
                        values.merge(OUTGOING_DAMAGE, setting("cauldronOutgoingDamage", 1.05), (a, b) -> a * b);
                        values.merge(INCOMING_DAMAGE, setting("cauldronIncomingDamage", 0.95), (a, b) -> a * b);
                        values.merge(PHOENIX_COOLDOWN, setting("cauldronPhoenixCooldown", 0.67), (a, b) -> a * b);
                        break;
                    case COSMIC_TROPHY:
                        values.put(SLOTBOT_FREE_ROLL, setting("cosmicTrophyFreeRollChance", 0.10));
                        break;
                    case HALLOWEEN_CANDLES:
                        values.put(LOOTBOX_BONUS, setting("halloweenCandleLootboxChance", 0.10));
                        break;
                    case LEVIATHAN_TOTEM:
                        values.put(SPAWNER_DOUBLE_DROPS, setting("leviathanTotemDoubleDropChance", 0.25));
                        break;
                    case TREASURE_CHEST:
                        values.put(MOB_EXP, setting("treasureChestMobExp", 1.5));
                        break;
                    default:
                        break;
                }
            }
            for (String key : KEYS) {
                if (values.containsKey(key)) {
                    player.setMetadata(key, new FixedMetadataValue(CoreSkulls.getInstance(), values.get(key)));
                } else if (player.hasMetadata(key)) {
                    player.removeMetadata(key, CoreSkulls.getInstance());
                }
            }
        }
    }

    private static double value(Entity entity, String key) {
        return entity.hasMetadata(key) ? entity.getMetadata(key).get(0).asDouble() : 1.0;
    }

    /** Pumpkin Pie / Cauldron: +5% damage dealt to players, Cauldron: -5% damage taken from players */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Entity damager = event.getDamager();
        if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            damager = (Player) ((Projectile) damager).getShooter();
        }
        if (!(damager instanceof Player)) return;
        double multiplier = value(damager, OUTGOING_DAMAGE) * value(event.getEntity(), INCOMING_DAMAGE);
        if (multiplier != 1.0) event.setDamage(event.getDamage() * multiplier);
    }

    /** Treasure Chest: +50% EXP from mobs killed in its chunk (before AstroCustomBlocks' Dark Matter collects it) */
    @EventHandler(priority = EventPriority.HIGH)
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player || event.getDroppedExp() <= 0) return;
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        if (blocksFor(event.getEntity().getLocation(), factionOf(killer)).contains(AbilityType.TREASURE_CHEST)) {
            event.setDroppedExp((int) Math.round(event.getDroppedExp() * setting("treasureChestMobExp", 1.5)));
        }
    }
}
