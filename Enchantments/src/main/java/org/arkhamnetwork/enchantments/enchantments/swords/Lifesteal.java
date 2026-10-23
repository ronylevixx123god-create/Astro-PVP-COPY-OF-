package org.arkhamnetwork.enchantments.enchantments.swords;

import com.rit.sucy.CustomEnchantment;
import me.astropvp.astropvpskins.AstroItemSkinsAPI;
import me.astropvp.astropvpskins.enums.SkinType;
import me.ifamasssxd.cosmicmasks.MaskType;
import org.arkhamnetwork.enchantments.util.GeneralUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Lifesteal extends CustomEnchantment {
    public Lifesteal() {
        super("Lifesteal", GeneralUtil.weapons, 2);
        this.max = 5;
        this.base = 10.0;
        this.interval = 8.0;
    }

    public void applyEffect(final LivingEntity user, final LivingEntity target, final int enchantLevel, final EntityDamageByEntityEvent event) {
        final double random = Math.random();

        final double chance = enchantLevel * 0.01;
        int health = (int) user.getHealth() + enchantLevel;
        if ((double)health > user.getMaxHealth()) {
            health = (int)user.getMaxHealth();
        }
        if (random <= chance) {
            if (isLifestealBlocked(target)) {
                return;
            }
            GeneralUtil.setPlayerHealth(user, health);

        }
    }

    // Necromancer mask and the item skins that block lifesteal; shared with Demonic Lifesteal so both use the same counters
    public static boolean isLifestealBlocked(final LivingEntity target) {
        if (target instanceof Player && MaskType.hasEquipped((Player) target, "NECROMANCER")) {
            return true;
        }
        if (target instanceof Player && AstroItemSkinsAPI.hasSkinEquipped((Player) target, SkinType.SUMMER_2021_BELT)) {
            return true;
        }


        if ((target instanceof Player && AstroItemSkinsAPI.hasSkinEquipped((Player) target, SkinType.SPRING_BREAK_2022_MUTATED_BELT))) {
            return true;
        }

        if (target instanceof Player && AstroItemSkinsAPI.hasSkinEquipped((Player) target, SkinType.JULY4TH_2022_HELMET)) {
            return true;
        }

        if (target instanceof Player && AstroItemSkinsAPI.hasSkinEquipped((Player) target, SkinType.VALENTINES_2022_MUTATED_BACKPACK)) {
            return true;
        }

        if (target instanceof Player && (AstroItemSkinsAPI.hasSkinEquipped((Player) target, SkinType.EASTER_2022_BELT) || AstroItemSkinsAPI.hasSkinEquipped((Player) target, SkinType.EASTER_2022_MUTATED_BELT)))
            return true;

        return false;
    }
}
