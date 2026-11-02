package org.arkhamnetwork.enchantments.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.ENameParser;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.arkhamnetwork.enchantments.Enchantments;
import org.arkhamnetwork.enchantments.util.TieredEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Godly Transmog Scroll (EnchantmentBook.getGodlyTransmog, NBT cosmicType=godlyTransmog): drag-and-drop it onto equipment
 * with 2+ custom enchantments in your own inventory and the "Godly Transmog Scroll" menu opens with every enchant as an
 * enchanted book, named and coloured like its enchant book. Pick a book up and drop it on the position you want; the other
 * books shift over. Positions run left to right, top to bottom. The bottom row shows the item with the new order applied
 * (built by the same code Confirm uses). Confirm saves the order in the item's lore (enchants are read from lore, so nothing
 * else changes); Cancel/closing gives the scroll back. Every other kind of click, drag or drop is cancelled, and items shown
 * in the menu carry a marker so one that ever ends up outside it is removed.
 */
public class GodlyTransmogListener implements Listener {
    private static final String MENU_ITEM = "godlyTransmogMenuItem";

    static final class Menu implements InventoryHolder {
        final int slot;
        final ItemStack snapshot;
        final List<String> order;
        Inventory inv;
        int held = -1;
        boolean done;

        Menu(int slot, ItemStack snapshot, List<String> lines) {
            this.slot = slot;
            this.snapshot = snapshot;
            this.order = new ArrayList<String>(lines);
        }

        @Override
        public Inventory getInventory() {
            return this.inv;
        }

        int bookSlots() {
            return this.inv.getSize() - 9;
        }

        int cancelSlot() {
            return this.inv.getSize() - 9;
        }

        int previewSlot() {
            return this.inv.getSize() - 5;
        }

        int confirmSlot() {
            return this.inv.getSize() - 1;
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

    static boolean isMenuItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR && new NBTWrapper(item).hasKey(MENU_ITEM);
    }

    private static ItemStack markMenuItem(ItemStack item) {
        return new NBTWrapper(item).setString(MENU_ITEM, "1").build();
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
        // one scroll is used; the rest go back into the inventory so the cursor is empty while the menu is open
        ItemStack rest = e.getCursor().clone();
        rest.setAmount(rest.getAmount() - 1);
        player.setItemOnCursor(null);
        if (rest.getAmount() > 0) {
            for (ItemStack left : player.getInventory().addItem(rest).values()) {
                player.getWorld().dropItem(player.getLocation(), left);
            }
        }
        Menu menu = new Menu(e.getSlot(), item.clone(), lines);
        int rows = Math.min(5, (lines.size() + 8) / 9) + 1;
        menu.inv = Bukkit.createInventory(menu, rows * 9, CC.translate("&6&lGodly Transmog Scroll"));
        render(menu);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.2f);
        Bukkit.getScheduler().runTask(Enchantments.getInstance(), () -> player.openInventory(menu.inv));
    }

    /** an enchant shown as an enchanted book, named and coloured like its enchant book */
    private static ItemStack book(String line, int position) {
        String text = ChatColor.stripColor(line).trim();
        CustomEnchantment ce = EnchantmentAPI.getEnchantment(ENameParser.parseName(line));
        ChatColor color = ce == null ? null : TieredEnchantments.getTierColor(ce);
        String name = ce == null ? text : ce.name() + " " + text.substring(text.lastIndexOf(' ') + 1);
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName((color == null ? ChatColor.GRAY : color) + ChatColor.BOLD.toString() + ChatColor.UNDERLINE + name);
        meta.setLore(Arrays.asList(CC.translate("&7Position &e#" + position), CC.translate("&7Drag to move")));
        book.setItemMeta(meta);
        return markMenuItem(book);
    }

    private static void render(Menu menu) {
        Inventory inv = menu.inv;
        inv.clear();
        for (int i = 0; i < menu.order.size() && i < menu.bookSlots(); i++) {
            if (i != menu.held) {
                inv.setItem(i, book(menu.order.get(i), i + 1));
            }
        }
        inv.setItem(menu.cancelSlot(), button(14, "&c&lCancel", "&7Close and get your scroll back."));
        inv.setItem(menu.previewSlot(), markMenuItem(applyOrder(menu.snapshot.clone(), menu.order)));
        inv.setItem(menu.confirmSlot(), button(5, "&a&lConfirm", "&7Save this enchant order."));
    }

    private static ItemStack button(int glassData, String name, String... lore) {
        ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)glassData);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        List<String> l = new ArrayList<String>();
        for (String s : lore) {
            l.add(CC.translate(s));
        }
        meta.setLore(l);
        is.setItemMeta(meta);
        return markMenuItem(is);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMenuClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof Menu)) {
            return;
        }
        e.setCancelled(true);
        Menu menu = (Menu)e.getView().getTopInventory().getHolder();
        Player player = (Player)e.getWhoClicked();
        int raw = e.getRawSlot();
        boolean plainClick = e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT;
        if (menu.done || !plainClick || raw < 0 || raw >= menu.inv.getSize()) {
            resync(player);
            return;
        }
        if (raw == menu.cancelSlot()) {
            putBack(player, menu);
            Bukkit.getScheduler().runTask(Enchantments.getInstance(), player::closeInventory);
            return;
        }
        if (raw == menu.confirmSlot()) {
            putBack(player, menu);
            confirm(player, menu);
            return;
        }
        if (raw >= menu.bookSlots()) {
            resync(player);
            return;
        }
        if (menu.held < 0) {
            ItemStack cursorNow = player.getItemOnCursor();
            if (raw >= menu.order.size() || cursorNow != null && cursorNow.getType() != Material.AIR && !isMenuItem(cursorNow)) {
                resync(player);
                return;
            }
            // pick the book up
            menu.held = raw;
            ItemStack onCursor = book(menu.order.get(raw), raw + 1);
            render(menu);
            player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.5f);
            Bukkit.getScheduler().runTask(Enchantments.getInstance(), () -> {
                if (menu.held == raw && !menu.done && player.getOpenInventory().getTopInventory().getHolder() == menu) {
                    player.setItemOnCursor(onCursor);
                }
            });
            return;
        }
        // drop it here; the books in between shift over (an empty slot after the last book means "last")
        String line = menu.order.remove(menu.held);
        menu.order.add(Math.min(raw, menu.order.size()), line);
        menu.held = -1;
        player.setItemOnCursor(null);
        render(menu);
        player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 0.8f);
        resync(player);
    }

    /** the book being moved goes back to where it was */
    private static void putBack(Player player, Menu menu) {
        if (menu.held >= 0) {
            menu.held = -1;
            player.setItemOnCursor(null);
            render(menu);
        }
    }

    private static void resync(Player player) {
        Bukkit.getScheduler().runTask(Enchantments.getInstance(), () -> {
            if (isMenuItem(player.getItemOnCursor()) && !(player.getOpenInventory().getTopInventory().getHolder() instanceof Menu)) {
                player.setItemOnCursor(null);
            }
            player.updateInventory();
        });
    }

    /** puts the enchant lines in this order (and adds the Transmog count to the name); used for the preview and Confirm */
    static ItemStack applyOrder(ItemStack item, List<String> order) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        setEnchantLines(lore, order);
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
        item.setItemMeta(meta);
        if (name == null) {
            item = EnchantmentBook.applyTransmogScroll(item);
            ItemMeta m2 = item.getItemMeta();
            List<String> l2 = m2.getLore();
            setEnchantLines(l2, order);
            m2.setLore(l2);
            item.setItemMeta(m2);
        }
        return item;
    }

    private static void setEnchantLines(List<String> lore, List<String> order) {
        int next = 0;
        for (int i = 0; i < lore.size() && next < order.size(); i++) {
            if (isEnchantLine(lore.get(i))) {
                lore.set(i, order.get(next++));
            }
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
        pinv.setItem(menu.slot, applyOrder(now, menu.order));
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof Menu) || !(e.getPlayer() instanceof Player)) {
            return;
        }
        Menu menu = (Menu)e.getInventory().getHolder();
        Player player = (Player)e.getPlayer();
        // the book on the cursor must not be handed to the player when the menu closes
        menu.held = -1;
        if (isMenuItem(player.getItemOnCursor())) {
            player.setItemOnCursor(null);
        }
        if (menu.done) {
            return;
        }
        menu.done = true;
        for (ItemStack left : player.getInventory().addItem(EnchantmentBook.getGodlyTransmog()).values()) {
            player.getWorld().dropItem(player.getLocation(), left);
        }
        player.sendMessage(CC.translate("&c&l(!) &cGodly Transmog cancelled, your scroll was returned."));
    }

    // safety net: an item shown in the menu is removed wherever it turns up outside it
    @EventHandler(priority = EventPriority.LOWEST)
    public void onStrayMenuItem(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof Menu) {
            return;
        }
        if (isMenuItem(e.getCurrentItem())) {
            e.setCancelled(true);
            e.setCurrentItem(null);
        }
        if (isMenuItem(e.getCursor())) {
            e.setCancelled(true);
            e.getWhoClicked().setItemOnCursor(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (isMenuItem(e.getItemDrop().getItemStack())) {
            e.getItemDrop().remove();
        } else if (e.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof Menu) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        removeMenuItems(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (isMenuItem(e.getPlayer().getItemOnCursor())) {
            e.getPlayer().setItemOnCursor(null);
        }
        removeMenuItems(e.getPlayer());
    }

    private static void removeMenuItems(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (isMenuItem(contents[i])) {
                player.getInventory().setItem(i, null);
            }
        }
    }
}
