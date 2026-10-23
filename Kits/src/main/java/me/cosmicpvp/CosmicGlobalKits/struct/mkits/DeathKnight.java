package me.cosmicpvp.CosmicGlobalKits.struct.mkits;

import me.cosmicpvp.CosmicGlobalKits.struct.mkits.sets.MasterySet;
import com.cosmicpvp.cosmicutils.itembuilder.MaskBuilder;
import com.google.common.collect.Lists;
import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import me.cosmicpvp.CosmicGlobalKits.struct.MasteryGlobalKit;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeathKnight
extends MasteryGlobalKit {
    @Override
    public List<ItemStack> getMasteryItems(Player player, MasteryKits kit, boolean applyIID) {
        ItemStack helmet = MasterySet.DEATH_KNIGHT.getArmorSetInfo().getHelmet(player, false, false);
        ItemStack chestplate = MasterySet.DEATH_KNIGHT.getArmorSetInfo().getChestplate(player, false, false);
        ItemStack leggings = MasterySet.DEATH_KNIGHT.getArmorSetInfo().getLeggings(player, false, false);
        ItemStack boots = MasterySet.DEATH_KNIGHT.getArmorSetInfo().getBoots(player, false, false);
        ArrayList<ItemStack> armor = Lists.newArrayList(helmet, chestplate, leggings, boots);
        EnchantmentAPI.getEnchantment((String)"Chain Lifesteal").addToItem(helmet, ThreadLocalRandom.current().nextInt(EnchantmentAPI.getEnchantment((String)"Chain Lifesteal").getMaxLevel()) + 1);
        EnchantmentAPI.getEnchantment((String)"Tombstone").addToItem(leggings, ThreadLocalRandom.current().nextInt(EnchantmentAPI.getEnchantment((String)"Tombstone").getMaxLevel()) + 1);
        ArrayList enchants = Lists.newArrayList(new String[]{"Death Pact", "Permafrost"});
        while (!enchants.isEmpty()) {
            String random = (String)enchants.get(ThreadLocalRandom.current().nextInt(enchants.size()));
            enchants.remove(random);
            ItemStack item = (ItemStack)armor.get(ThreadLocalRandom.current().nextInt(armor.size()));
            CustomEnchantment enchantment = EnchantmentAPI.getEnchantment((String)random);
            if (enchantment == null) continue;
            enchantment.addToItem(item, enchantment.getMaxLevel() == 1 ? 1 : ThreadLocalRandom.current().nextInt(enchantment.getMaxLevel()) + 1);
        }
        for (ItemStack item : armor) {
            EnchantmentBook.applyTransmogScroll((ItemStack)item);
        }
        ItemStack mask = new MaskBuilder("DEATH_KNIGHT").build();
        if (mask != null) {
            armor.add(mask);
        }
        return armor;
    }
}

