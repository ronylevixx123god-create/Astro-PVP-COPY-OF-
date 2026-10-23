package org.arkhamnetwork.enchantments.enchantments.swords;

import org.arkhamnetwork.enchantments.enchantments.armor.Soulguard;
import me.astropvp.astropvpskins.AstroItemSkinsAPI;
import me.astropvp.astropvpskins.enums.SkinType;
import org.bukkit.event.entity.*;
import org.bukkit.metadata.*;
import org.bukkit.entity.*;
import org.arkhamnetwork.enchantments.util.*;
import org.arkhamnetwork.Arkkit.libs.*;
import org.bukkit.*;
import org.bukkit.event.*;

final class DivineImmolationListener implements Listener {
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity().hasMetadata("divineFire") && e.getCause() == EntityDamageEvent.DamageCause.WITHER && e.getEntity() instanceof LivingEntity) {
            final int enchantLevel = e.getEntity().getMetadata("divineFireLevel").get(0).asInt();
            final boolean divineWindow = System.currentTimeMillis() - e.getEntity().getMetadata("divineFire").get(0).asLong() < (4 + enchantLevel) * 1000L;
            // Soulguard: one activation per Divine wither tick reduces both the tick itself and the extra damage below
            double soulguard = 0.0;
            if (divineWindow && !e.getEntity().hasMetadata("reflectDivineTicks")) {
                soulguard = Soulguard.divineReduction((LivingEntity) e.getEntity());
                if (soulguard > 0) {
                    e.setDamage(e.getDamage() * (1 - soulguard));
                }
            }
            if (e.getEntity() instanceof Player && AstroItemSkinsAPI.hasSkinEquipped((Player) e.getEntity(), SkinType.THANKSGIVING_SWORD_HARVESTER) && Math.random() < 0.5) {
                return;
            }

            if (WorldGuardUtils.isPvPDisabled(e.getEntity().getLocation())) {
                return;
            }
            if (System.currentTimeMillis() - e.getEntity().getMetadata("divineFire").get(0).asLong() < (4 + enchantLevel) * 1000L) {
                final LivingEntity le = (LivingEntity) e.getEntity();
                double damage = (double)Math.min(4, 4 + enchantLevel) / 4.0;

                if (enchantLevel == 5) {
                    damage *= 1.25;
                }
                if (le instanceof Player) {
                    if (le.hasMetadata("reflectDivineTicks")) {
                        GeneralUtil.healPlayer(le, damage);
                    } else {
                        le.damage(damage * (1 - soulguard));
                    }
                    ((Player) le).sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "** DIVINE IMMOLATION **");
                    ((Player) le).playSound(le.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 0.6f, 0.8f);
                }
            }
        }
    }
}
