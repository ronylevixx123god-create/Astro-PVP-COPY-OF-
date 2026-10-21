package me.bryside.weaponrunes.runes;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import java.util.List;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import me.bryside.weaponrunes.utils.WeaponBonus;

public class DemonWeaponBonus
extends WeaponBonus {
    @Override
    public List<String> getLore() {
        return Lists.newArrayList(new String[]{CC.RedB + " \u27a5 " + CC.Gray + "1.5x Divine Damage Multiplier", CC.RedB + " \u27a5 " + CC.Gray + "35% Chance to Block Banshee Pets & Astro Ability", CC.RedB + " \u27a5 " + CC.Gray + "15% Chance to Block Engineer Crystal on 1 Piece"});
    }

    @Override
    public WeaponBonusType getType() {
        return WeaponBonusType.DEMON;
    }
}

