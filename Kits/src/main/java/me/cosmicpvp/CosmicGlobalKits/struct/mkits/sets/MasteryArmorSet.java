package me.cosmicpvp.CosmicGlobalKits.struct.mkits.sets;

import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.google.common.collect.Lists;
import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import net.astropvp.armorsets.struct.heroicArmor.HeroicArmor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The Ghost / Necromancer / Death Knight ability armor sets the Mastery Kits give. Ported from Astro's CosmicArmorSets
 * (AbilityArmorSetInfo + abilityarmorsets/*), which is not installed on this server: the pieces are built the same way,
 * with the ability set NBT from AstroArmorSets' AntiMkit (so Mastery Shard books and Anti-Mkit crystals work on them)
 * and AstroArmorSets' heroic armor format.
 */
public abstract class MasteryArmorSet {
    protected final Random random = new Random();

    public abstract ItemStack getHelmet(Player player, boolean applyTransmog, boolean mastery);

    public abstract ItemStack getChestplate(Player player, boolean applyTransmog, boolean mastery);

    public abstract ItemStack getLeggings(Player player, boolean applyTransmog, boolean mastery);

    public abstract ItemStack getBoots(Player player, boolean applyTransmog, boolean mastery);

    // the original sets had no lore, equip text or set bonus of their own: the power is in the mastery enchants
    public List<String> getAbilityArmorLore() {
        return Lists.newArrayList();
    }

    public List<String> getEquipDescription() {
        return Lists.newArrayList();
    }

    public boolean onPlayerDamaged(Player damaged, EntityDamageByEntityEvent event) {
        return false;
    }

    public boolean onPlayerAttacked(Player damager, EntityDamageByEntityEvent event) {
        return false;
    }

    /**
     * CosmicArmorSetsAPI.convertItemToHeroic for an ability set piece: leather of the heroic type, half the heroic
     * armor value (rounded up, as the original did for "abilitySet" items) and the heroic lore. The original's
     * durability counter belonged to CosmicArmorSets; heroic armor here is unbreakable instead, like AstroArmorSets'.
     */
    protected ItemStack toHeroic(ItemStack item, HeroicArmor type) {
        item.setType(type.getType());
        int modifier = (int) Math.ceil((double) type.getReduction() / 2.0);
        ItemMeta im = item.getItemMeta();
        String strongerLine;
        List<String> lore = im.hasLore() ? im.getLore() : Lists.newArrayList();
        if (!lore.contains(strongerLine = CC.DarkRed + "This armor is stronger than diamond.")) {
            lore.add("");
            lore.add(CC.Gray + "+" + modifier + " Armor Value");
            lore.add(strongerLine);
        }
        im.setLore(lore);
        if (im instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) im).setColor(Color.RED);
        }
        im.spigot().setUnbreakable(true);
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(im);
        NBTWrapper wrapper = new NBTWrapper(item);
        NBTTagCompoundBuilder cosmicData = wrapper.getCompoundOrCreate("cosmicData");
        cosmicData.setBoolean("heroicArmor", true);
        wrapper.setNBTBase("cosmicData", cosmicData.get());
        // the armor value WineSpigot uses for heroic leather (AstroArmorSetsAPI.addWineArmorOverride)
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(wrapper.build());
        NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();
        tag.setInt("wine-armor-override", modifier);
        nms.setTag(tag);
        return CraftItemStack.asBukkitCopy(nms);
    }

    protected ItemStack setLeatherColor(ItemStack item, Color color) {
        ItemMeta im = item.getItemMeta();
        if (im instanceof LeatherArmorMeta) {
            LeatherArmorMeta lm = (LeatherArmorMeta) im;
            lm.setColor(color);
            item.setItemMeta(lm);
        }
        return item;
    }

    public ItemStack addMaxedEnchant(ItemStack item, String name) {
        CustomEnchantment enchant = EnchantmentAPI.getEnchantment(name);
        if (enchant != null) {
            enchant.addToItem(item, enchant.getMaxLevel());
        }
        return item;
    }

    public ItemStack addNearlyMaxedEnchants(ItemStack item, String... enchantNames) {
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        for (String enchant : enchantNames) {
            this.addNearlyMaxed(item, enchant);
        }
        return item;
    }

    public ItemStack addNearlyMaxed(ItemStack item, String enchantName) {
        CustomEnchantment enchantment = EnchantmentAPI.getEnchantment(enchantName);
        if (enchantment != null) {
            int level, maxLevel = enchantment.getMaxLevel();
            if (enchantment.heroicEnchantment) {
                level = 1 + ThreadLocalRandom.current().nextInt(maxLevel);
            } else if (maxLevel == 1) {
                level = maxLevel;
            } else if (maxLevel == 2) {
                level = maxLevel - ThreadLocalRandom.current().nextInt(2);
            } else if (maxLevel == 3) {
                level = maxLevel - ThreadLocalRandom.current().nextInt(3);
            } else {
                level = maxLevel - ThreadLocalRandom.current().nextInt(4);
            }
            if (level == maxLevel && level > 1 && Math.random() < 0.25D) {
                level--;
            }
            if (enchantment.heroicEnchantment && enchantment.nonHeroicVersion != null) {
                enchantment.nonHeroicVersion.addToItem(item, enchantment.nonHeroicVersion.getMaxLevel());
            }
            enchantment.addToItem(item, level);
        }
        return item;
    }
}
