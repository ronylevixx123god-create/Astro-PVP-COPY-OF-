package me.bryside.weaponrunes.utils;

import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WeaponBonusUtils {
    public static WeaponBonusType getWeaponBonusByItem(ItemStack i) {
        if (i == null || i.getType() == Material.AIR || !i.hasItemMeta()) {
            return null;
        }
        NBTWrapper wrapper = new NBTWrapper(i);
        NBTTagCompoundBuilder cosmicData = wrapper.hasKey("cosmicData") ? new NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")) : new NBTTagCompoundBuilder();
        return WeaponBonusType.fromId(cosmicData.getString("bonusType"));
    }

    public static boolean hasWeaponBonus(ItemStack i) {
        if (i == null) {
            return false;
        }
        if (i.getType() == Material.AIR) {
            return false;
        }
        NBTWrapper wrapper = new NBTWrapper(i);
        if (!wrapper.hasKey("cosmicData")) {
            return false;
        }
        if (wrapper.hasKey("cosmicType") && wrapper.getString("cosmicType").equals("weaponBonus")) {
            return false;
        }
        return new NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")).hasKey("bonusType");
    }

    public static boolean isWeaponBonus(ItemStack i) {
        if (i == null || i.getType() == Material.AIR) {
            return false;
        }
        NBTWrapper wrapper = new NBTWrapper(i);
        if (!wrapper.hasKey("cosmicType")) {
            return false;
        }
        return wrapper.getString("cosmicType").equals("weaponBonus");
    }

    public static boolean isHeroicWeapon(ItemStack i) {
        if (i.getType().toString().endsWith("_SWORD") || i.getType().toString().endsWith("_AXE")) {
            return i.getType().name().contains("GOLD");
        }
        return false;
    }

    public static double getWeaponBonusSuccess(ItemStack item) {
        if (!WeaponBonusUtils.isWeaponBonus(item)) {
            return 0.0;
        }
        NBTWrapper wrapper = new NBTWrapper(item);
        return new NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")).getDouble("success");
    }
}

