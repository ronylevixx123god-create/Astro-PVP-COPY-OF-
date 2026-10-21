package me.bryside.weaponrunes.runes;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.WorldGuardUtils;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import me.bryside.weaponrunes.utils.WeaponBonus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ChampionWeaponBonus
extends WeaponBonus {
    @Override
    public List<String> getLore() {
        return Lists.newArrayList((Object[])new String[]{CC.GreenB + " \u27a5 " + CC.Gray + "50% Chance to block enemy Self Destruct", CC.GreenB + " \u27a5 " + CC.Gray + "-25% Incoming DMG from Axes", CC.GreenB + " \u27a5 " + CC.Gray + "Zeus Strike Effect"});
    }

    @Override
    public WeaponBonusType getType() {
        return WeaponBonusType.CHAMPION;
    }

    @Override
    public void onAttack(Player damager, Entity damaged, EntityDamageByEntityEvent event) {
        if (!(damager instanceof Player)) {
            return;
        }
        if (!(damaged instanceof Player)) {
            return;
        }
        if (Math.random() > 0.005) {
            return;
        }
        Random rn = new Random();
        List nearbyEnemies = damager.getNearbyEntities(8.0, 8.0, 8.0);
        for (Entity entity : nearbyEnemies) {
            Player player;
            if (!(entity instanceof Player) || !WorldGuardUtils.isPvPEnabled((Location)(player = ((Player)entity).getPlayer()).getLocation())) continue;
            player.getWorld().strikeLightningEffect(player.getLocation());
            int damage = rn.nextInt(7) + 5;
            player.damage((double)damage);
            player.sendMessage("");
            player.sendMessage(CC.translate((String)("&a&l** ZEUS WEAPON BONUS &7[&f" + damager.getName() + "&7] **")));
            player.sendMessage(CC.translate((String)("&cZeus Lightning Effect: &l&n" + damage + "&r &c&lDMG")));
            player.sendMessage("");
            damager.sendMessage("");
            damager.sendMessage(CC.translate((String)("&a&l** ZEUS WEAPON BONUS EFFECT &7[&f" + nearbyEnemies.size() + " Player(s) Struck&7]")));
            damager.sendMessage(CC.translate((String)("&cZeus Lightning Effect: &l&n" + damage + "&r &c&lDMG")));
            damager.sendMessage("");
        }
    }

    @Override
    public void onAttacked(Player damaged, Entity damager, EntityDamageByEntityEvent event) {
        if (!(damager instanceof Player)) {
            return;
        }
        if (!(damaged instanceof Player)) {
            return;
        }
        if (((Player)damager).getItemInHand().getType() == Material.DIAMOND_AXE || ((Player)damager).getItemInHand().getType() == Material.GOLD_AXE) {
            event.setDamage(event.getDamage(EntityDamageEvent.DamageModifier.BASE) * 0.75);
        }
    }
}

