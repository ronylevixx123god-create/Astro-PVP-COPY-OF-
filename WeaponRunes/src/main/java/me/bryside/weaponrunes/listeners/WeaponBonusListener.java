package me.bryside.weaponrunes.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.FactionUtils;
import com.massivecraft.factions.Faction;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import me.bryside.weaponrunes.utils.WeaponBonusUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

// Astro's weapon bonus orbs (WeaponRunes.jar by Bryside). The jar cast Player to FPlayer for the faction check, which
// always threw inside an empty catch, so no bonus effect ever ran; the check below does what it meant to do.
public class WeaponBonusListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player) e.getDamager();
        Player damaged = (Player) e.getEntity();
        if (FactionUtils.getFPlayer(damager) == null || FactionUtils.getFPlayer(damaged) == null) {
            return;
        }
        if (isFriendly(damager, damaged)) {
            return;
        }
        WeaponBonusType attackBonus = WeaponBonusUtils.getWeaponBonusByItem(damager.getItemInHand());
        if (attackBonus != null) {
            attackBonus.getInstance().onAttack(damager, damaged, e);
        }
        WeaponBonusType defendBonus = WeaponBonusUtils.getWeaponBonusByItem(damaged.getItemInHand());
        if (defendBonus != null) {
            defendBonus.getInstance().onAttacked(damaged, damager, e);
        }
    }

    private static boolean isFriendly(Player a, Player b) {
        Faction fa = FactionUtils.getFaction(a, false);
        Faction fb = FactionUtils.getFaction(b, false);
        if (fa != null && fb != null && fa.getId().equals(fb.getId())) {
            return true;
        }
        return FactionUtils.isInAllyFaction(a, b);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        ItemStack cursor = e.getCursor();
        ItemStack clicked = e.getCurrentItem();
        if (e.getClickedInventory() != e.getView().getBottomInventory()) {
            return;
        }
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        if (!WeaponBonusUtils.hasWeaponBonus(clicked) && WeaponBonusUtils.isWeaponBonus(cursor) && e.getClick().isLeftClick()) {
            WeaponBonusType weaponBonusType = WeaponBonusUtils.getWeaponBonusByItem(cursor);
            if (weaponBonusType == null || !WeaponBonusUtils.isHeroicWeapon(clicked)) {
                e.getWhoClicked().sendMessage(CC.translate("&c&l(!)&c You cannot apply weapon bonuses to non-heroic weapons"));
                e.setCancelled(true);
                return;
            }
            e.setCurrentItem(weaponBonusType.getInstance().applyBonus(clicked, weaponBonusType));
            e.getWhoClicked().sendMessage(CC.translate("&a&l(!)&a Successfully Applied " + weaponBonusType.getName() + " Weapon Rune&a to your sword!"));
            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                e.setCursor(null);
            }
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.LEVEL_UP, 1.0f, 10.0f);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (p.getOpenInventory().getTitle().startsWith(CC.translate("&8Weapon Bonuses"))) {
            e.setCancelled(true);
            if (e.getClickedInventory() == p.getOpenInventory().getTopInventory() && p.isOp() && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && WeaponBonusUtils.isWeaponBonus(e.getCurrentItem())) {
                p.getInventory().addItem(WeaponBonusUtils.getWeaponBonusByItem(e.getCurrentItem()).getInstance().getItem(100.0));
            }
        }
    }
}
