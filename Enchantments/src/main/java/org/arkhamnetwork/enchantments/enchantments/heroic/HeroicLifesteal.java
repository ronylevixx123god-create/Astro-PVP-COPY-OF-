package org.arkhamnetwork.enchantments.enchantments.heroic;

import org.arkhamnetwork.enchantments.enchantments.swords.Lifesteal;
import org.arkhamnetwork.enchantments.util.GeneralUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HeroicLifesteal
        extends HeroicCustomEnchantment {
    // Balance fix, not Astro's original numbers: the original used a flat 1.5% chance at every level and healed
    // 2/4/4 HP (III the same as II), weaker than plain Lifesteal V (5%, 5 HP) although the description promises much
    // more healing at a greatly increased rate. Now I/II/III = 6%/7%/9% chance for 4/5/5 HP, always above Lifesteal V.
    private static final double[] CHANCE = {0.06, 0.07, 0.09};
    private static final double[] HEAL = {4.0, 5.0, 5.0};

    public HeroicLifesteal() {
        super("Demonic Lifesteal", "Lifesteal", GeneralUtil.swords, 2);
        this.max = 3;
    }

    public void applyEffect(LivingEntity user, LivingEntity target, int enchantLevel, EntityDamageByEntityEvent event) {
        int level = Math.max(1, Math.min(enchantLevel, CHANCE.length));
        if (Math.random() >= CHANCE[level - 1]) {
            return;
        }
        // same counters as normal Lifesteal (Necromancer mask and the lifesteal-blocking skins)
        if (Lifesteal.isLifestealBlocked(target)) {
            return;
        }
        // keep fractional health instead of rounding it down like the original did
        GeneralUtil.setPlayerHealth(user, Math.min(user.getHealth() + HEAL[level - 1], user.getMaxHealth()));
    }
}
