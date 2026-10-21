package me.bryside.weaponrunes.utils;

import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.bryside.weaponrunes.enums.WeaponBonusType;
import net.minecraft.server.v1_8_R3.NBTBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class WeaponBonus {
    public abstract List<String> getLore();

    public abstract WeaponBonusType getType();

    public ItemStack getItem(double success) {
        ItemStack item = new ItemStack(Material.INK_SACK, 1, this.getType().getData().shortValue());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.getType().getName() + this.getType().getColor() + ChatColor.BOLD + " Weapon Bonus" + CC.Gray + " (" + (int)success + "%)");
        ArrayList lore = Lists.newArrayList();
        lore.add("");
        lore.addAll(this.getLore());
        lore.add("");
        lore.add(CC.Gray + "Weapon Bonuses can " + CC.GrayU + "ONLY" + CC.Gray + " be applied");
        lore.add(CC.Gray + "to " + this.getType().getColor() + "Heroic Weapons " + CC.Gray + "and only");
        lore.add(CC.GrayU + "1 bonus" + CC.Gray + " can be imbued per weapon");
        lore.add("");
        lore.add(CC.Gray + "To learn more about Weapon Bonuses, use /weaponbonus");
        meta.setLore((List)lore);
        item.setItemMeta(meta);
        NBTWrapper wrapper = new NBTWrapper(item);
        NBTTagCompoundBuilder cosmicData = wrapper.hasKey("cosmicData") ? new NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")) : new NBTTagCompoundBuilder();
        cosmicData.setString("bonusType", this.getType().getId());
        cosmicData.setDouble("success", Double.valueOf(success));
        wrapper.setNBTBase("cosmicData", (NBTBase)cosmicData.get());
        wrapper.setString("random", UUID.randomUUID().toString());
        wrapper.setString("cosmicType", "weaponBonus");
        return wrapper.build();
    }

    public ItemStack applyBonus(ItemStack i, WeaponBonusType bonusType) {
        ItemMeta meta = i.getItemMeta();
        List<String> lore = meta.getLore() == null ? new ArrayList() : meta.getLore();
        lore.add(this.getType().getColor() + "(" + CC.translate((String)"&f&l!") + this.getType().getColor() + ") " + this.getType().getName() + " Weapon Bonuses");
        for (String s : bonusType.getInstance().getLore()) {
            lore.add(s);
        }
        meta.setLore(lore);
        i.setItemMeta(meta);
        NBTWrapper wrapper = new NBTWrapper(i);
        NBTTagCompoundBuilder cosmicData = wrapper.hasKey("cosmicData") ? new NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")) : new NBTTagCompoundBuilder();
        cosmicData.setString("bonusType", bonusType.getId());
        wrapper.setNBTBase("cosmicData", (NBTBase)cosmicData.get());
        return wrapper.build();
    }

    public void onAttack(Player damager, Entity damaged, EntityDamageByEntityEvent event) {
    }

    public void onAttacked(Player damaged, Entity damager, EntityDamageByEntityEvent event) {
    }
}

