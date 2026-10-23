/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.net.astropvp.armorsets.utils.WorldUtil
 *  com.rit.sucy.CustomEnchantment
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package org.arkhamnetwork.enchantments.enchantments.armor;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.WorldUtil;
import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import me.astropvp.astropvpskins.AstroItemSkinsAPI;
import me.astropvp.astropvpskins.enums.SkinType;
import me.astropvp.astropvpskins.objects.skins.boots.SinkOrSwipFlippers;
import org.arkhamnetwork.enchantments.util.DamageIndicatorUtils;
import org.arkhamnetwork.enchantments.util.GeneralUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Frozen I-III. FIX, not original Astro: on Astro the Slowness line was commented out (only the "* FROZEN *" text
 * showed), the chance was 0.05% per level and a mob hit could throw a ClassCastException.
 * <p>
 * Now: when a player hits the wearer with a melee attack, the attacker can get Slowness I (Frozen I/II) or Slowness II
 * (Frozen III) for the same time as before (level + 0..level-1 seconds). The chance per hit starts at 2/3/4% and grows
 * by 1% with every further hit from the same attacker, up to 3x the start chance but never above 9% (I 6%, II 9%,
 * III 9%); it goes back to the start after a
 * proc (also a proc blocked by a skin) or after 5 seconds without a hit from that attacker. Build-ups are kept per
 * wearer and attacker. Only the wearer's highest Frozen piece counts, and only one roll is made per hit. Mobs are
 * ignored.
 */
public class Frozen
extends CustomEnchantment {
    private static final long RESET_MS = 5000L;
    private static final double STEP = 0.01;
    private static final double MAX_CHANCE = 0.09;
    /** wearer + attacker -> build-up */
    private static final Map<String, BuildUp> BUILD_UPS = new HashMap<String, BuildUp>();
    /** the last hit already handled; the API calls this once for every armor piece that has Frozen */
    private static WeakReference<EntityDamageEvent> lastEvent = new WeakReference<EntityDamageEvent>(null);

    private static class BuildUp {
        int hits;
        long lastHit;
    }

    public Frozen() {
        super("Frozen", GeneralUtil.armor, 2);
        this.max = 3;
        this.base = 15.0;
        this.interval = 3.0;
    }

    /** start chance: Frozen I 2%, II 3%, III 4% */
    public static double baseChance(int level) {
        return 0.01 * (level + 1);
    }

    /** the chance the next hit from this attacker would have (read-only) */
    public static double currentChance(LivingEntity wearer, LivingEntity attacker, int level) {
        BuildUp b = BUILD_UPS.get(key(wearer, attacker));
        int hits = b == null || System.currentTimeMillis() - b.lastHit > RESET_MS ? 0 : b.hits;
        return Math.min(baseChance(level) + STEP * hits, maxChance(level));
    }

    /** highest chance the build-up reaches: 3x the start chance, at most 9% (I 6%, II 9%, III 9%) */
    public static double maxChance(int level) {
        return Math.min(3 * baseChance(level), MAX_CHANCE);
    }

    /** the highest Frozen level on the wearer's armor (at least the level of the piece the API is calling for) */
    public static int highestLevel(LivingEntity wearer, int level) {
        if (!(wearer instanceof Player)) {
            return level;
        }
        CustomEnchantment frozen = EnchantmentAPI.getEnchantment("Frozen");
        int highest = level;
        for (ItemStack piece : ((Player) wearer).getInventory().getArmorContents()) {
            if (piece == null || !EnchantmentAPI.itemHasEnchantment(piece, "Frozen")) {
                continue;
            }
            Integer l = EnchantmentAPI.getEnchantments(piece).get(frozen);
            if (l != null) {
                highest = Math.max(highest, l);
            }
        }
        return Math.min(highest, 3);
    }

    private static String key(LivingEntity wearer, LivingEntity attacker) {
        return wearer.getUniqueId() + ":" + attacker.getUniqueId();
    }

    public void applyDefenseEffect(LivingEntity player, LivingEntity attacker, int level, EntityDamageEvent e) {
        if (!(attacker instanceof Player) || e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (lastEvent.get() == e) {
            return;
        }
        lastEvent = new WeakReference<EntityDamageEvent>(e);
        level = highestLevel(player, level);
        long now = System.currentTimeMillis();
        if (BUILD_UPS.size() > 256) {
            for (Iterator<BuildUp> it = BUILD_UPS.values().iterator(); it.hasNext(); ) {
                if (now - it.next().lastHit > RESET_MS) {
                    it.remove();
                }
            }
        }
        double chance = currentChance(player, attacker, level);
        BuildUp b = BUILD_UPS.get(key(player, attacker));
        if (b == null) {
            b = new BuildUp();
            BUILD_UPS.put(key(player, attacker), b);
        }
        if (now - b.lastHit > RESET_MS) {
            b.hits = 0;
        }
        b.lastHit = now;
        if (Math.random() >= chance) {
            b.hits++;
            return;
        }
        b.hits = 0;
        int time = (int)(Math.random() * (double)level) + 1 * level;
        int amp = level > 2 ? 1 : 0;
        if (AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.VALENTINES_2021_HELMET)) {
            attacker.sendMessage(CC.translate("&d&l* NEON EMOTIONS &7Blocked Slow Effect &d&l*"));
            return;
        }

        if (AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.JULY_2021_BOOTS)) {
            attacker.sendMessage(CC.translate("&c&l** BARRY's BOLTS [&7Blocked Frozen&c&l] **"));
            return;
        }

        if (((Player)attacker).getInventory().getBoots() != null && AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.HALLOWEEN_2021_BOOTS))
            return;

        if (((Player)attacker).getInventory().getChestplate() != null && AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.BACK_TO_SCHOOL_2021_AMULET))
            return;

        if (AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.CHRISTMAS_BOOTS_SLIPPERS)) {
            attacker.sendMessage(CC.translate("&c&l* SNOWFLAKE SLIPPERS &7Blocked Slow Effect &c&l*"));
            return;
        }

        if (AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.EASTER_2022_BOOTS) || AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.EASTER_2022_MUTATED_BOOTS)) {
            return;
        }


        if (AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.SPRING_BREAK_2022_BOOTS) || AstroItemSkinsAPI.hasSkinEquipped((Player) attacker, SkinType.SPRING_BREAK_2022_MUTATED_BOOTS)) {
            SinkOrSwipFlippers.addSinkStack((Player) attacker);
            attacker.sendMessage(CC.translate("&a&l* BLOCKED FREEZE &7[+1 Stack] &a&l*"));
            return;
        }
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time * 20, amp));
        Location l = player.getLocation();
        DamageIndicatorUtils.sendDamageIndicator((Player) attacker, CC.AquaB + "* FROZEN " + CC.Gray + "[" + time + "s]" + CC.AquaB + " *", "FROZEN");
        WorldUtil.sendWorldEffect((Block)l.getBlock(), (int)57, (int)2001);
    }
}
