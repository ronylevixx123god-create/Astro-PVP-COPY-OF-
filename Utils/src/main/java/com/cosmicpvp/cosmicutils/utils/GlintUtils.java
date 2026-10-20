package com.cosmicpvp.cosmicutils.utils;

import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public class GlintUtils {
    public static void addGlint(ItemStack item, Color color) {
        addGlint(item, 0xff000000 | color.asRGB());
    }

    public static void addGlint(ItemStack item, int colorInt) {
        NBTWrapper wrapper = new NBTWrapper(item);
        com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder cosmicData = wrapper.hasKey("cosmicData") ? new com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")) : new NBTTagCompoundBuilder();
        cosmicData.setString("tintColor", String.valueOf(colorInt));
        save(item, wrapper, cosmicData);
    }

    public static void removeGlint(ItemStack item) {
        NBTWrapper wrapper = new NBTWrapper(item);
        com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder cosmicData = wrapper.hasKey("cosmicData") ? new com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder(wrapper.getNBTBase("cosmicData")) : new NBTTagCompoundBuilder();
        if (cosmicData.getString("tintColor").equals("-10237440")) {
            cosmicData.remove("tintColor");
            save(item, wrapper, cosmicData);
        }
    }

    /** writes cosmicData back into the item (in place for a live CraftItemStack, via its meta otherwise) */
    private static void save(ItemStack item, NBTWrapper wrapper, NBTTagCompoundBuilder cosmicData) {
        ItemStack built = wrapper.setNBTBase("cosmicData", cosmicData.get()).build();
        if (built != null && built != item) {
            item.setItemMeta(built.getItemMeta());
        }
    }

    public static int getIntFromColor(float Red, float Green, float Blue){
        int R = Math.round(255 * Red);
        int G = Math.round(255 * Green);
        int B = Math.round(255 * Blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return 0xFF000000 | R | G | B;
    }
}
