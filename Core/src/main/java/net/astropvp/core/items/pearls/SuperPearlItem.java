package net.astropvp.core.items.pearls;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.google.common.collect.Lists;
import net.astropvp.core.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SuperPearlItem {

    public static ItemStack getItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CC.RedB + "Super Pearl");
        List<String> lore = Lists.newArrayList();
        lore.add(CC.Gray + "A mythical enderpearl will travel up to");
        lore.add(CC.Gray + "2.5x further than a normal enderpearl.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        NBTWrapper wrapper = new NBTWrapper(ItemUtils.addGlow(item));
        wrapper.setString("cosmicType", "super_pearl");
        return wrapper.build();
    }

    public static boolean isSuperPearl(ItemStack item) {
        if (item.getType() != Material.ENDER_PEARL || !item.hasItemMeta()) return false;
        return "super_pearl".equals(new NBTWrapper(item).getString("cosmicType"));
    }
}
