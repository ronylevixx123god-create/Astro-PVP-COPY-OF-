package me.cosmicpvp.CosmicGlobalKits.struct.mkits.sets;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.google.common.collect.Lists;
import net.astropvp.armorsets.struct.antiMkit.AntiMkit;
import net.astropvp.armorsets.struct.heroicArmor.HeroicArmor;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Astro's Ghost ability armor set (CosmicArmorSets GhostAbilitySet), see {@link MasteryArmorSet}. */
public class GhostAbilitySet
        extends MasteryArmorSet {
    private final String color = CC.DarkAqua;
    private final String bulletPoint = this.color + CC.Bold + " * " + this.color;
    private final Color armorColor = Color.GRAY;

    @Override
    public List<String> getAbilityArmorLore() {
        return Lists.newArrayList();
    }

    @Override
    public ItemStack getHelmet(Player player, boolean applyTransmog, boolean mastery) {
        ItemStack helmet = this.setLeatherColor(this.toHeroic(AntiMkit.GHOST.applyAbilityNBT(new NBTWrapper(new ItemBuilder(Material.DIAMOND_HELMET, CC.DarkAquaB + "Ghostly Hood", CC.GrayI + "A tattered hood haunted by", CC.GrayI + "the echoes of all lost souls.").build())).build(), HeroicArmor.HELMET), this.armorColor);
        this.addNearlyMaxedEnchants(helmet, "Armored", "Implants", "Drunk", "Arrow Deflect", "Death God", "Resilience", "Protection", "Frozen");
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(helmet, "Valor");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(helmet, "Angelic");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(helmet, "Tank");
        }
        if (this.random.nextDouble() <= 0.175) {
            this.addNearlyMaxedEnchants(helmet, "Paladin Armored");
        }
        if (this.random.nextDouble() <= 0.175) {
            this.addNearlyMaxedEnchants(helmet, "Alien Implants");
        }
        if (mastery) {
            this.addRandomMasteryEnchants(helmet, 2);
        }
        return applyTransmog ? EnchantmentBook.applyTransmogScroll(helmet) : helmet;
    }

    @Override
    public ItemStack getChestplate(Player player, boolean applyTransmog, boolean mastery) {
        ItemStack chestplate = this.setLeatherColor(this.toHeroic(AntiMkit.GHOST.applyAbilityNBT(new NBTWrapper(new ItemBuilder(Material.DIAMOND_CHESTPLATE, CC.DarkAquaB + "Ghostly Shroud", CC.GrayI + "A thin, sheer shroud that seems", CC.GrayI + "to defy gravity in its movements.").build())).build(), HeroicArmor.CHESTPLATE), this.armorColor);
        this.addNearlyMaxedEnchants(chestplate, "Armored", "Overload", "Deathbringer", "Enlighted", "Diminish", "Aegis", "Leadership", "Blood Lust", "Shockwave");
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(chestplate, "Valor");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(chestplate, "Angelic");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(chestplate, "Tank");
        }
        if (this.random.nextDouble() <= 0.2) {
            this.addNearlyMaxed(chestplate, "Paladin Armored");
        }
        if (this.random.nextDouble() <= 0.175) {
            this.addNearlyMaxed(chestplate, "Godly Overload");
        }
        if (this.random.nextDouble() <= 0.2) {
            this.addNearlyMaxed(chestplate, "Planetary Deathbringer");
        }
        if (this.random.nextDouble() <= 0.2) {
            this.addNearlyMaxed(chestplate, "Divine Enlighted");
        }
        if (this.random.nextDouble() <= 0.15) {
            this.addNearlyMaxed(chestplate, "Vengeful Diminish");
        }
        if (mastery) {
            this.addRandomMasteryEnchants(chestplate, 2);
        }
        return applyTransmog ? EnchantmentBook.applyTransmogScroll(chestplate) : chestplate;
    }

    @Override
    public ItemStack getLeggings(Player player, boolean applyTransmog, boolean mastery) {
        ItemStack leggings = this.setLeatherColor(this.toHeroic(AntiMkit.GHOST.applyAbilityNBT(new NBTWrapper(new ItemBuilder(Material.DIAMOND_LEGGINGS, CC.DarkAquaB + "Ghostly Robes", CC.GrayI + "A darkly woven silk robe imbued", CC.GrayI + "with onyx gemstones that", CC.GrayI + "emanate evil power.").build())).build(), HeroicArmor.LEGGINGS), this.armorColor);
        this.addNearlyMaxedEnchants(leggings, "Armored", "Nature Wrath", "Clarity", "Obsidianshield", "Cactus", "Lucky", "Voodoo");
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(leggings, "Valor");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(leggings, "Angelic");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(leggings, "Tank");
        }
        if (this.random.nextDouble() <= 0.15) {
            this.addNearlyMaxed(leggings, "Paladin Armored");
        }
        if (this.random.nextDouble() <= 0.125) {
            this.addNearlyMaxed(leggings, "Infinite Luck");
        }
        if (mastery) {
            this.addRandomMasteryEnchants(leggings, 2);
        }
        return applyTransmog ? EnchantmentBook.applyTransmogScroll(leggings) : leggings;
    }

    @Override
    public ItemStack getBoots(Player player, boolean applyTransmog, boolean mastery) {
        ItemStack boots = this.setLeatherColor(this.toHeroic(AntiMkit.GHOST.applyAbilityNBT(new NBTWrapper(new ItemBuilder(Material.DIAMOND_BOOTS, CC.DarkAquaB + "Ghostly Whisp", CC.GrayI + "Ghost don't have feet.").build())).build(), HeroicArmor.BOOTS), this.armorColor);
        this.addNearlyMaxedEnchants(boots, "Armored", "Metaphysical", "Ender Walker", "Springs", "Rocket Escape", "Dodge", "Phoenix", "Undead Ruse");
        this.addMaxedEnchant(boots, "Gears");
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(boots, "Valor");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(boots, "Angelic");
        }
        if (this.random.nextDouble() <= 0.6) {
            this.addNearlyMaxed(boots, "Tank");
        }
        if (this.random.nextDouble() <= 0.25) {
            this.addNearlyMaxed(boots, "Paladin Armored");
        }
        if (this.random.nextDouble() <= 0.3) {
            this.addNearlyMaxed(boots, "Ethereal Dodge");
        }
        if (this.random.nextDouble() <= 0.45) {
            this.addNearlyMaxed(boots, "Phoenix");
        }
        if (mastery) {
            this.addRandomMasteryEnchants(boots, 2);
        }
        return applyTransmog ? EnchantmentBook.applyTransmogScroll(boots) : boots;
    }

    public ItemStack addRandomMasteryEnchants(ItemStack item, int count) {
        ArrayList enchants = Lists.newArrayList( new String[]{"Horrify", "Feign Death", "Poltergeist", "Mark of the Beast"});
        for (int i = 0; i < count && !enchants.isEmpty(); ++i) {
            String random = (String) enchants.get(ThreadLocalRandom.current().nextInt(enchants.size()));
            if (random != null) {
                enchants.remove(random);
            }
            this.addNearlyMaxed(item, random);
        }
        return item;
    }

    @Override
    public List<String> getEquipDescription() {
        return Lists.newArrayList();
    }

    @Override
    public boolean onPlayerDamaged(Player damaged, EntityDamageByEntityEvent event) {
        return false;
    }

    @Override
    public boolean onPlayerAttacked(Player damager, EntityDamageByEntityEvent event) {
        return false;
    }
}

