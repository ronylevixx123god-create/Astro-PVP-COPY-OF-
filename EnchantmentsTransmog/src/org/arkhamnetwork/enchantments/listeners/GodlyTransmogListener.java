package org.arkhamnetwork.enchantments.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.ENameParser;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.arkhamnetwork.enchantments.Enchantments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * NEW (2026-11-01): the Godly Transmog Scroll (EnchantmentBook.getGodlyTransmog, NBT cosmicType=godlyTransmog) had no handler
 * anywhere. Drag-and-drop it onto equipment with 2+ custom enchantments in your own inventory: a menu opens, you click the
 * enchants in the order you want them shown, Confirm saves that order in the item's lore (enchants are read from lore, so
 * nothing else changes). Closing/Cancel gives the scroll back. Unpicked enchants keep their old order after the picked ones.
 */
public class GodlyTransmogListener implements Listener {

    static final class Menu implements InventoryHolder {
        final int slot;
        final ItemStack snapshot;
        final List<String> lines;
        final List<Integer> picked = new ArrayList<Integer>();
        Inventory inv;
        boolean done;

        Menu(int slot, ItemStack snapshot, List<String> lines) {
            this.slot = slot;
            this.snapshot = snapshot;
            this.lines = lines;
        }

        @Override
        public Inventory getInventory() {
            return this.inv;
        }

        int confirmSlot() {
            return this.inv.getSize() - 1;
        }

        int cancelSlot() {
            return this.inv.getSize() - 9;
        }
    }

    static boolean isEnchantLine(String line) {
        String name = ENameParser.parseName(line);
        return name != null && ENameParser.parseLevel(line) > 0 && EnchantmentAPI.isRegistered(name);
    }

    static List<String> enchantLines(ItemStack item) {
        List<String> out = new ArrayList<String>();
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String line : item.getItemMeta().getLore()) {
                if (isEnchantLine(line)) {
                    out.add(line);
                }
            }
        }
        return out;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onApply(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player) || e instanceof InventoryCreativeEvent) {
            return;
        }
        if (e.getView().getTopInventory().getHolder() instanceof Menu) {
            return;
        }
        if (!EnchantmentBook.isGodlyTransmog(e.getCursor())) {
            return;
        }
        if (!e.getInventory().getTitle().equals("container.crafting") || !(e.getClickedInventory() instanceof PlayerInventory)) {
            return;
        }
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !EnchantmentBook.isEquipment(item) || item.getMaxStackSize() != 1) {
            return;
        }
        Player player = (Player)e.getWhoClicked();
        e.setCancelled(true);
        List<String> lines = enchantLines(item);
        if (lines.size() < 2) {
            player.sendMessage(CC.translate("&c&l(!) &cThis item needs at least 2 custom enchantments to reorder."));
            return;
        }
        ItemStack cursor = e.getCursor().clone();
        if (cursor.getAmount() > 1) {
            cursor.setAmount(cursor.getAmount() - 1);
            player.setItemOnCursor(cursor);
        } else {
            player.setItemOnCursor(null);
        }
        Menu menu = new Menu(e.getSlot(), item.clone(), lines);
        int rows = Math.min(5, (lines.size() + 8) / 9) + 1;
        menu.inv = Bukkit.createInventory(menu, rows * 9, CC.translate("&6&lGodly Transmog"));
        render(menu);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.2f);
        Bukkit.getScheduler().runTask(Enchantments.getInstance(), () -> player.openInventory(menu.inv));
    }

    private static void render(Menu menu) {
        Inventory inv = menu.inv;
        inv.clear();
        for (int i = 0; i < menu.lines.size() && i < inv.getSize() - 9; i++) {
            int pos = menu.picked.indexOf(i) + 1;
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, Math.max(1, pos));
            ItemMeta meta = book.getItemMeta();
            if (pos > 0) {
                meta.setDisplayName(CC.translate("&6&l#" + pos + " ") + menu.lines.get(i));
                meta.setLore(Arrays.asList(CC.translate("&aPosition &f#" + pos), CC.translate("&7Click to remove.")));
            } else {
                meta.setDisplayName(menu.lines.get(i));
                meta.setLore(Arrays.asList(CC.translate("&7Click to place this enchant"), CC.translate("&7at position &e#" + (menu.picked.size() + 1) + "&7.")));
            }
            book.setItemMeta(meta);
            inv.setItem(i, book);
        }
        inv.setItem(menu.cancelSlot(), button(14, "&c&lCancel", "&7Close and get your scroll back."));
        inv.setItem(menu.cancelSlot() + 4, button(-1, "&6&lGodly Transmog",
                "&7Click the enchants in the order", "&7you want them shown on the item.",
                "&7Unpicked enchants stay after", "&7the picked ones, in their old order.", "",
                "&7Picked: &e" + menu.picked.size() + "&7/&e" + menu.lines.size()));
        inv.setItem(menu.confirmSlot(), button(5, "&a&lConfirm", "&7Save this enchant order."));
    }

    private static ItemStack button(int glassData, String name, String... lore) {
        ItemStack is = glassData < 0 ? new ItemStack(Material.PAPER) : new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)glassData);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        List<String> l = new ArrayList<String>();
        for (String s : lore) {
            l.add(CC.translate(s));
        }
        meta.setLore(l);
        is.setItemMeta(meta);
        return is;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMenuClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof Menu)) {
            return;
        }
        e.setCancelled(true);
        Menu menu = (Menu)e.getView().getTopInventory().getHolder();
        Player player = (Player)e.getWhoClicked();
        if (e.getRawSlot() < 0 || e.getRawSlot() >= menu.inv.getSize() || menu.done) {
            return;
        }
        int raw = e.getRawSlot();
        if (raw == menu.cancelSlot()) {
            Bukkit.getScheduler().runTask(Enchantments.getInstance(), player::closeInventory);
            return;
        }
        if (raw == menu.confirmSlot()) {
            confirm(player, menu);
            return;
        }
        if (raw < menu.lines.size()) {
            Integer idx = raw;
            if (menu.picked.contains(idx)) {
                menu.picked.remove(idx);
            } else {
                menu.picked.add(idx);
            }
            player.playSound(player.getLocation(), Sound.CLICK, 1.0f, menu.picked.contains(idx) ? 1.5f : 0.8f);
            render(menu);
        }
    }

    private static void confirm(Player player, Menu menu) {
        PlayerInventory pinv = player.getInventory();
        ItemStack now = pinv.getItem(menu.slot);
        if (now == null || !now.isSimilar(menu.snapshot)) {
            player.sendMessage(CC.translate("&c&l(!) &cThat item was moved or changed, nothing was applied."));
            Bukkit.getScheduler().runTask(Enchantments.getInstance(), player::closeInventory);
            return;
        }
        List<String> order = new ArrayList<String>();
        for (int i : menu.picked) {
            order.add(menu.lines.get(i));
        }
        for (int i = 0; i < menu.lines.size(); i++) {
            if (!menu.picked.contains(i)) {
                order.add(menu.lines.get(i));
            }
        }
        ItemMeta meta = now.getItemMeta();
        List<String> lore = meta.getLore();
        int next = 0;
        for (int i = 0; i < lore.size() && next < order.size(); i++) {
            if (isEnchantLine(lore.get(i))) {
                lore.set(i, order.get(next++));
            }
        }
        meta.setLore(lore);
        // same "[count]" suffix the normal Transmog Scroll adds to the name
        String name = meta.hasDisplayName() ? meta.getDisplayName() : null;
        String tag = ChatColor.LIGHT_PURPLE + "[" + ChatColor.AQUA + ChatColor.BOLD + ChatColor.UNDERLINE;
        if (name != null) {
            if (name.contains(tag)) {
                name = name.substring(0, name.lastIndexOf(" "));
            }
            meta.setDisplayName(name + " " + tag + order.size() + ChatColor.LIGHT_PURPLE + "]");
        }
        now.setItemMeta(meta);
        if (name == null) {
            now = EnchantmentBook.applyTransmogScroll(now);
            ItemMeta m2 = now.getItemMeta();
            List<String> l2 = m2.getLore();
            int n2 = 0;
            for (int i = 0; i < l2.size() && n2 < order.size(); i++) {
                if (isEnchantLine(l2.get(i))) {
                    l2.set(i, order.get(n2++));
                }
            }
            m2.setLore(l2);
            now.setItemMeta(m2);
        }
        pinv.setItem(menu.slot, now);
        menu.done = true;
        player.sendMessage(CC.translate("&6&l(!) &6Godly Transmog applied! &7Your enchant order was saved."));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 0.75f);
        Bukkit.getScheduler().runTask(Enchantments.getInstance(), player::closeInventory);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof Menu) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof Menu) || !(e.getPlayer() instanceof Player)) {
            return;
        }
        Menu menu = (Menu)e.getInventory().getHolder();
        if (menu.done) {
            return;
        }
        menu.done = true;
        Player player = (Player)e.getPlayer();
        for (ItemStack left : player.getInventory().addItem(EnchantmentBook.getGodlyTransmog()).values()) {
            player.getWorld().dropItem(player.getLocation(), left);
        }
        player.sendMessage(CC.translate("&c&l(!) &cGodly Transmog cancelled, your scroll was returned."));
    }
}
