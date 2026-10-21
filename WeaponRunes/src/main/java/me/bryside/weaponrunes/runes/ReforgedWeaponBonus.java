package me.bryside.weaponrunes.runes;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import java.util.List;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import me.bryside.weaponrunes.utils.WeaponBonus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ReforgedWeaponBonus
extends WeaponBonus {
    @Override
    public List<String> getLore() {
        return Lists.newArrayList((Object[])new String[]{CC.AquaB + " \u27a5 " + CC.Gray + "50% Chance to negate Soul Harvester", CC.AquaB + " \u27a5 " + CC.Gray + "1.5x Silence duration multiplier", CC.AquaB + " \u27a5 " + CC.Gray + "1.25x Divine Damage multiplier"});
    }

    @Override
    public WeaponBonusType getType() {
        return WeaponBonusType.REFORGED;
    }

    @Override
    public void onAttack(Player damager, Entity damaged, EntityDamageByEntityEvent event) {
    }

    @Override
    public void onAttacked(Player damaged, Entity damager, EntityDamageByEntityEvent event) {
    }
}

