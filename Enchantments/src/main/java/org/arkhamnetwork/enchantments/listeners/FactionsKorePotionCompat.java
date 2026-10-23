package org.arkhamnetwork.enchantments.listeners;

import org.arkhamnetwork.enchantments.Enchantments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * FactionsKore's potion-restrict feature (plugins/FactionsKore/potion-restrict.yml) cancels Strength III for everyone
 * without factionskore.admin.potion-restrict, i.e. everyone but OP. Armor enchants such as Drunk IV give Strength III
 * through the tracked-effect system, which re-applies the effect every second, so non-OP players got an endless
 * "[+] Drunk IV: applying" / "Strength 3 is disabled" loop while the effect never stuck.
 * <p>
 * Only effects applied by {@link #applyEnchantEffect} (the enchant tracker) are exempted: while one is being added, the
 * KorePotionEvent FactionsKore fires for it is emptied before the restriction sees it. Potions, splash potions, beacons
 * and every other source are still restricted, and nobody gets a permission.
 */
public class FactionsKorePotionCompat implements Listener {
    private static UUID applying;
    private static Field newEffectField;

    public static void applyEnchantEffect(Player p, PotionEffect effect) {
        applying = p.getUniqueId();
        try {
            p.addPotionEffect(effect);
        } finally {
            applying = null;
        }
    }

    @SuppressWarnings("unchecked")
    public static void hook() {
        if (!Bukkit.getPluginManager().isPluginEnabled("FactionsKore"))
            return;
        try {
            ClassLoader loader = Bukkit.getPluginManager().getPlugin("FactionsKore").getClass().getClassLoader();
            Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName("com.golfing8.nms.event.KorePotionEvent", true, loader);
            newEffectField = eventClass.getDeclaredField("newEffect");
            newEffectField.setAccessible(true);
            Bukkit.getPluginManager().registerEvent(eventClass, new FactionsKorePotionCompat(), EventPriority.LOWEST, (listener, event) -> {
                if (applying == null || !eventClass.isInstance(event))
                    return;
                Entity entity = ((EntityEvent) event).getEntity();
                if (!applying.equals(entity.getUniqueId()))
                    return;
                try {
                    newEffectField.set(event, null);
                } catch (IllegalAccessException e) {
                    Enchantments.getInstance().getLogger().warning("Could not exempt an enchant effect from FactionsKore: " + e);
                }
            }, Enchantments.getInstance(), false);
            Enchantments.getInstance().getLogger().info("Enchant armor effects are exempt from FactionsKore potion-restrict.");
        } catch (Exception e) {
            Enchantments.getInstance().getLogger().warning("FactionsKore potion-restrict compatibility unavailable: " + e);
        }
    }
}
