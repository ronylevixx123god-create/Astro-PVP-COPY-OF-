package net.astropvp.core.items.pearls;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.google.common.collect.Lists;
import net.astropvp.core.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EscapistPearlItem {

    public static ItemStack getItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CC.GoldB + "Escapist Pearl");
        List<String> lore = Lists.newArrayList();
        lore.add(CC.Yellow + "An elusive teleportation pearl");
        lore.add(CC.Yellow + "Capable of phasing through:");
        lore.add(CC.Gray + " * Cobwebs");
        lore.add(CC.Gray + " * String");
        meta.setLore(lore);
        item.setItemMeta(meta);
        NBTWrapper wrapper = new NBTWrapper(ItemUtils.addGlow(item));
        wrapper.setString("cosmicType", "escapist_pearl");
        return wrapper.build();
    }

    public static boolean isEscapistPearl(ItemStack item) {
        if (item.getType() != Material.ENDER_PEARL || !item.hasItemMeta()) return false;
        return "escapist_pearl".equals(new NBTWrapper(item).getString("cosmicType"));
    }
}
