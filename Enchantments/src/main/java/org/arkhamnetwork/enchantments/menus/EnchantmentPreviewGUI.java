package org.arkhamnetwork.enchantments.menus;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.rit.sucy.CustomEnchantment;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.arkhamnetwork.enchantments.Enchantments;
import org.arkhamnetwork.enchantments.libs.EnchanterRarity;
import org.arkhamnetwork.enchantments.util.TieredEnchantments;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only list of the enchantments a tier's book can give (right-click a tier in /enchanter, or shift + right-click
 * an unopened book). Nothing here can be taken: every click is cancelled by the GUI listener. The entries are plain
 * BOOKs with Astro's book name and lore plus a hidden PREVIEW_KEY tag, which EnchantmentBook.isEnchantmentBook rejects.
 */
public class EnchantmentPreviewGUI extends CosmicGUI {
    private static final int PER_PAGE = 45;

    private final EnchanterRarity rarity;
    private final boolean fromEnchanter;
    private final int page;
    private final List<CustomEnchantment> enchantments;

    public EnchantmentPreviewGUI(Player player, EnchanterRarity rarity, boolean fromEnchanter, int page) {
        this(player, rarity, fromEnchanter, page, TieredEnchantments.getObtainableEnchantments(rarity.getTier()));
    }

    private EnchantmentPreviewGUI(Player player, EnchanterRarity rarity, boolean fromEnchanter, int page, List<CustomEnchantment> enchantments) {
        super(player, rarity.getName() + " Enchantments", sizeFor(enchantments.size()));
        this.rarity = rarity;
        this.fromEnchanter = fromEnchanter;
        this.page = page;
        this.enchantments = enchantments;
    }

    private static int sizeFor(int count) {
        int shown = Math.min(Math.max(count, 1), PER_PAGE);
        return ((shown + 8) / 9) * 9 + 9;
    }

    public static EnchanterRarity rarityForTier(int tier) {
        for (EnchanterRarity rarity : EnchanterRarity.values()) {
            if (rarity.getTier() == tier) {
                return rarity;
            }
        }
        return null;
    }

    @Override
    public void redraw() {
        int start = this.page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, this.enchantments.size());
        for (int i = start; i < end; i++) {
            setItem(i - start, new InventoryItem(previewItem(this.enchantments.get(i))));
        }
        int bottom = this.size - 9;
        if (this.page > 0) {
            setItem(bottom, new InventoryItem(button(Material.ARROW, CC.Green + "Previous Page")).click(() ->
                    new EnchantmentPreviewGUI(this.player, this.rarity, this.fromEnchanter, this.page - 1).openGUI((JavaPlugin) Enchantments.getInstance())));
        }
        if (end < this.enchantments.size()) {
            setItem(bottom + 8, new InventoryItem(button(Material.ARROW, CC.Green + "Next Page")).click(() ->
                    new EnchantmentPreviewGUI(this.player, this.rarity, this.fromEnchanter, this.page + 1).openGUI((JavaPlugin) Enchantments.getInstance())));
        }
        if (this.fromEnchanter) {
            setItem(bottom + 4, new InventoryItem(button(Material.BOOK_AND_QUILL, CC.Red + "Back to the Astro Enchanter")).click(() ->
                    new EnchanterGUI(this.player).openGUI((JavaPlugin) Enchantments.getInstance())));
        }
    }

    /** Astro's book name and description, without the success/destroy rates and the drag n' drop hint. */
    private static ItemStack previewItem(CustomEnchantment ench) {
        ItemStack book = EnchantmentBook.getEnchantmentBook(ench, ench.getMaxLevel(), 100, 0);
        List<String> lore = new ArrayList<String>();
        for (String line : book.getItemMeta().getLore()) {
            String plain = ChatColor.stripColor(line);
            if (plain.endsWith("% Success Rate") || plain.endsWith("% Destroy Rate") || plain.startsWith("Drag n' Drop")) {
                continue;
            }
            lore.add(line);
        }
        ItemStack preview = new ItemStack(Material.BOOK);
        ItemMeta meta = preview.getItemMeta();
        meta.setDisplayName(book.getItemMeta().getDisplayName());
        meta.setLore(lore);
        preview.setItemMeta(meta);
        // hidden marker: EnchantmentBook.isEnchantmentBook rejects it, so the icon can never act as a real book
        return new NBTWrapper(preview).setString(EnchantmentBook.PREVIEW_KEY, "1").build();
    }

    private static ItemStack button(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
