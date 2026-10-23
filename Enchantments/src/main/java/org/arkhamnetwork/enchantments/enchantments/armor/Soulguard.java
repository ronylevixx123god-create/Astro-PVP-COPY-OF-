package org.arkhamnetwork.enchantments.enchantments.armor;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import net.astropvp.core.killtracker.KillTrackerAPI;
import org.arkhamnetwork.enchantments.Enchantments;
import org.arkhamnetwork.enchantments.cmds.CommandEnchDust;
import org.arkhamnetwork.enchantments.util.GeneralUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Soulguard I-IV: a NEW soul armor enchant, not original Astro. Each worn piece reduces the bonus damage of an
 * enemy's Rage by 1% per level (capped at 16%) and Divine Immolation damage, including its lingering wither ticks,
 * by 1.3% per level (capped at 20.8%). Levels add up across armor pieces.
 * <p>
 * Activation follows the defensive soul armor enchants (Immortal): only while soul enchants are enabled, the wearer
 * pays souls from their soul gems each time it reduces damage (5 minus the highest Soulguard level worn, at least 1),
 * nothing when Soul Trap is on them, and "** OUT OF SOULS **" (every 15 s at most) when they cannot pay.
 * The reduction is applied by Rage and Divine Immolation themselves, never by a general damage listener, so nothing
 * is reduced twice and ordinary hits, fire, lava and other effects are untouched.
 */
public class Soulguard extends CustomEnchantment {
    public static final double RAGE_PER_LEVEL = 0.01;
    public static final double RAGE_CAP = 0.16;
    public static final double DIVINE_PER_LEVEL = 0.013;
    public static final double DIVINE_CAP = 0.208;
    private static final long MESSAGE_COOLDOWN_MS = 2000L;
    private static final DecimalFormat PERCENT = new DecimalFormat("0.#");

    public Soulguard() {
        super("Soulguard", GeneralUtil.armor, 2);
        this.max = 4;
        this.base = 15.0;
        this.interval = 3.0;
    }

    /** Fraction of Rage's bonus damage removed for this victim (0 when Soulguard does not activate). */
    public static double rageReduction(LivingEntity victim) {
        return activate(victim, RAGE_PER_LEVEL, RAGE_CAP);
    }

    /** Fraction of Divine Immolation damage removed for this victim (0 when Soulguard does not activate). */
    public static double divineReduction(LivingEntity victim) {
        return activate(victim, DIVINE_PER_LEVEL, DIVINE_CAP);
    }

    private static double activate(LivingEntity victim, double perLevel, double cap) {
        if (!(victim instanceof Player) || !CommandEnchDust.soulEnabled) {
            return 0.0;
        }
        Player p = (Player) victim;
        int totalLevels = 0;
        int highestLevel = 0;
        CustomEnchantment soulguard = EnchantmentAPI.getEnchantment("Soulguard");
        for (ItemStack piece : p.getInventory().getArmorContents()) {
            if (piece == null || !EnchantmentAPI.itemHasEnchantment(piece, "Soulguard")) {
                continue;
            }
            Map<CustomEnchantment, Integer> enchants = EnchantmentAPI.getEnchantments(piece);
            Integer level = enchants.get(soulguard);
            if (level == null || level <= 0) {
                continue;
            }
            totalLevels += level;
            highestLevel = Math.max(highestLevel, level);
        }
        if (totalLevels == 0) {
            return 0.0;
        }
        if (p.hasMetadata("soulTrap") && p.getMetadata("soulTrap").get(0).asLong() > System.currentTimeMillis()) {
            return 0.0;
        }
        int soulCost = Math.max(1, 5 - highestLevel);
        if (!KillTrackerAPI.hasSoulAmount(p, soulCost)) {
            if (!p.hasMetadata("outOfSoulsMessage") || p.getMetadata("outOfSoulsMessage").get(0).asLong() < System.currentTimeMillis()) {
                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 0.7f, 0.4f);
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "** OUT OF SOULS **");
                p.setMetadata("outOfSoulsMessage", new FixedMetadataValue(Enchantments.getInstance(), System.currentTimeMillis() + 15000L));
            }
            return 0.0;
        }
        KillTrackerAPI.removeSoulsFromGems(p, soulCost);
        double reduction = Math.min(cap, perLevel * totalLevels);
        // the message is limited to once per 2 seconds; the protection itself is not
        long now = System.currentTimeMillis();
        if (!p.hasMetadata("soulguardMessage") || p.getMetadata("soulguardMessage").get(0).asLong() <= now) {
            p.setMetadata("soulguardMessage", new FixedMetadataValue(Enchantments.getInstance(), now + MESSAGE_COOLDOWN_MS));
            p.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "** SOULGUARD " + ChatColor.GRAY + "(-"
                    + PERCENT.format(reduction * 100.0) + "%) " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "**");
        }
        return reduction;
    }
}
