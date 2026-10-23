package org.arkhamnetwork.enchantments.enchantments.heroic;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import org.arkhamnetwork.enchantments.Enchantments;
import org.arkhamnetwork.enchantments.util.DamageIndicatorUtils;
import org.arkhamnetwork.enchantments.util.GeneralUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Shadow Assassin I-V. BALANCE FIX, not original Astro: on Astro the bonus reached 3.75 blocks (up to 1.375x at V) and
 * an unused heroicMult = 2.5 was left in the code.
 * <p>
 * Now every hit gets a bonus by distance to the target: one step for each of within 2.0, 1.6, 1.2, 0.8 and 0.4 blocks,
 * each step 0.012 x level (V: +6% / +12% / +18% / +24% / +30%, max 1.3x). Past 2 blocks the damage is normal (no
 * penalty, as on Astro). From +18% up the target shows "* SHADOW ASSASSIN [+XX%] *", at most every 2 s per attacker.
 */
public class HeroicAssassin extends CustomEnchantment {
    private static final double[] STEPS = {2.0, 1.6, 1.2, 0.8, 0.4};
    private static final double PER_STEP_PER_LEVEL = 0.012;
    private static final double TEXT_FROM = 0.18;
    private static final long TEXT_COOLDOWN_MS = 2000L;
    private static final Map<UUID, Long> lastText = new HashMap<UUID, Long>();

    public HeroicAssassin() {
        super("Shadow Assassin", GeneralUtil.swords, 5);
        this.max = 5;
        this.base = 15.0D;
        this.interval = 6.0D;
        Bukkit.getScheduler().runTaskLater((Plugin) Enchantments.getInstance(), () -> {
            this.heroicEnchantment = true;
            this.nonHeroicVersion = EnchantmentAPI.getEnchantment("Assassin");
        }, 2L);
    }

    /** the damage bonus at this distance (0.30 = +30%) */
    public static double bonus(double distance, int enchantLevel) {
        int steps = 0;
        for (double within : STEPS) {
            if (distance <= within) {
                steps++;
            }
        }
        return steps * PER_STEP_PER_LEVEL * enchantLevel;
    }

    public void applyEffect(LivingEntity user, LivingEntity target, int enchantLevel, EntityDamageByEntityEvent event) {
        if (!user.getWorld().equals(target.getWorld())) {
            return;
        }
        double bonus = bonus(user.getLocation().distance(target.getLocation()), enchantLevel);
        if (bonus <= 0.0) {
            return;
        }
        event.setDamage(event.getDamage() * (1.0 + bonus));
        if (bonus >= TEXT_FROM - 1e-9 && target instanceof Player) {
            long now = System.currentTimeMillis();
            Long last = lastText.get(user.getUniqueId());
            if (last == null || now - last >= TEXT_COOLDOWN_MS) {
                lastText.put(user.getUniqueId(), now);
                DamageIndicatorUtils.sendDamageIndicator((Player) target, CC.DarkPurpleB + "* SHADOW ASSASSIN " + CC.Gray + "[+" + Math.round(bonus * 100) + "%] " + CC.DarkPurpleB + "*", "SHADOW_ASSASSIN");
            }
        }
    }
}
