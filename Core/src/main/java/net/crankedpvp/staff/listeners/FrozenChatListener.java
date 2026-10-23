package net.crankedpvp.staff.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import de.tr7zw.nbtapi.NBTItem;
import net.astropvp.core.AstroCore;
import net.crankedpvp.staff.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// the staff mode Frozen Chat tool (hotbar index 7, next to the freeze tool in 8): private chat between a staff member and a player they froze
public class FrozenChatListener implements Listener {

    private static final String MENU_TITLE = "Frozen Chat";
    private static final String PREFIX = "&c&l[FROZEN CHAT] ";

    // staff -> the frozen player they are chatting with, and the reverse; one conversation per staff member and per frozen player
    private static final Map<UUID, UUID> staffToTarget = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> targetToStaff = new ConcurrentHashMap<>();

    private final AstroCore plugin;
    private final Set<UUID> cooldown = new HashSet<>();

    public FrozenChatListener(AstroCore plugin) {
        this.plugin = plugin;
    }

    public static ItemStack getFrozenChatTool() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK_AND_QUILL, "&c&lFrozen Chat",
                "&7Right-Click to chat privately with",
                "&7a player you have frozen");
        NBTItem nbtItem = new NBTItem(itemBuilder.colorize().build());
        nbtItem.setString("staffModeItem", "frozenChat");
        return nbtItem.getItem();
    }

    private static boolean isFrozenChatTool(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == Material.BOOK_AND_QUILL && new NBTItem(itemStack).getString("staffModeItem").equals("frozenChat");
    }

    // ---------------------------------------------------------------- the tool

    @EventHandler(priority = EventPriority.LOWEST)
    public void useTool(PlayerInteractEvent event) {
        if(!isFrozenChatTool(event.getItem())) return;
        event.setCancelled(true);
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        this.use(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void useToolOnPlayer(PlayerInteractEntityEvent event) {
        if(!isFrozenChatTool(event.getPlayer().getItemInHand())) return;
        event.setCancelled(true);
        this.use(event.getPlayer());
    }

    private void use(Player player) {
        // the client opens the book editor on right-click by itself; close it (a target menu replaces it anyway)
        Bukkit.getScheduler().runTask(this.plugin, () -> { if(!MENU_TITLE.equals(player.getOpenInventory().getTitle())) player.closeInventory(); });
        if(!this.plugin.getStaffManager().isPlayerInStaffMode(player)) {
            player.sendMessage(CC.translate(AstroCore.prefix + "&cYou can only use this while in staff mode"));
            return;
        }
        if(!player.hasPermission("staff.freeze")) {
            player.sendMessage(CC.translate(AstroCore.prefix + "&cYou do not have permission to use this"));
            return;
        }
        // one right-click can reach the server as more than one interaction
        if(!this.cooldown.add(player.getUniqueId())) return;
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.cooldown.remove(player.getUniqueId()), 5L);
        List<Player> targets = this.eligibleTargets(player);
        if(targets.isEmpty()) {
            player.sendMessage(CC.translate("&c&lYou have no frozen player to chat with."));
            return;
        }
        if(targets.size() == 1) {
            this.toggle(player, targets.get(0));
            return;
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.openMenu(player, targets), 1L);
    }

    // online players this staff member froze
    private List<Player> eligibleTargets(Player staff) {
        List<Player> targets = new ArrayList<>();
        for(Map.Entry<UUID, UUID> entry : this.plugin.getFreezeManager().getFrozenBy().entrySet()) {
            if(!entry.getValue().equals(staff.getUniqueId())) continue;
            Player target = Bukkit.getPlayer(entry.getKey());
            if(target != null && target.isOnline() && this.plugin.getFreezeManager().isFrozen(target)) targets.add(target);
        }
        return targets;
    }

    private void openMenu(Player staff, List<Player> targets) {
        Inventory menu = Bukkit.createInventory(null, Math.min(54, ((targets.size() + 8) / 9) * 9), MENU_TITLE);
        UUID current = staffToTarget.get(staff.getUniqueId());
        for(Player target : targets) {
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwner(target.getName());
            boolean chatting = target.getUniqueId().equals(current);
            meta.setDisplayName(CC.translate("&c" + target.getName()));
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate(chatting ? "&aChatting now - click to close" : "&7Click to chat privately"));
            meta.setLore(lore);
            head.setItemMeta(meta);
            menu.addItem(head);
        }
        staff.openInventory(menu);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void menuClick(InventoryClickEvent event) {
        if(!MENU_TITLE.equals(event.getInventory().getTitle())) return;
        event.setCancelled(true);
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player staff = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if(event.getRawSlot() >= event.getInventory().getSize() || clicked == null || clicked.getType() != Material.SKULL_ITEM || !clicked.hasItemMeta()) return;
        Player target = Bukkit.getPlayerExact(((SkullMeta) clicked.getItemMeta()).getOwner());
        staff.closeInventory();
        if(target == null || !this.eligibleTargets(staff).contains(target)) {
            staff.sendMessage(CC.translate("&c&lYou have no frozen player to chat with."));
            return;
        }
        this.toggle(staff, target);
    }

    // ---------------------------------------------------------------- conversations

    private void toggle(Player staff, Player target) {
        if(target.getUniqueId().equals(staffToTarget.get(staff.getUniqueId()))) {
            close(staff.getUniqueId());
            return;
        }
        close(staff.getUniqueId());   // switching to another frozen player closes the old conversation
        staffToTarget.put(staff.getUniqueId(), target.getUniqueId());
        targetToStaff.put(target.getUniqueId(), staff.getUniqueId());
        staff.sendMessage(CC.translate(PREFIX + "&7Private chat with &f" + target.getName() + " &7opened. Your messages now go only to them."));
        target.sendMessage(CC.translate(PREFIX + "&f" + staff.getName() + " &7opened a private chat with you. Your messages now go only to them."));
    }

    /** close the conversation of this staff member, if any (unfreeze, disconnect, leaving staff mode, toggling off) */
    public static void close(UUID staffId) {
        UUID targetId = staffToTarget.remove(staffId);
        if(targetId == null) return;
        targetToStaff.remove(targetId);
        Player staff = Bukkit.getPlayer(staffId), target = Bukkit.getPlayer(targetId);
        if(staff != null) staff.sendMessage(CC.translate(PREFIX + "&7Private chat with &f" + (target != null ? target.getName() : "your frozen player") + " &7closed."));
        if(target != null) target.sendMessage(CC.translate(PREFIX + "&7Private chat with &f" + (staff != null ? staff.getName() : "staff") + " &7closed."));
    }

    /** close the conversation this frozen player is in, if any */
    public static void closeForTarget(UUID targetId) {
        UUID staffId = targetToStaff.get(targetId);
        if(staffId != null) close(staffId);
    }

    // private messages instead of public chat: staff -> their frozen player, frozen player -> their staff member
    @EventHandler(priority = EventPriority.LOWEST)
    public void chat(AsyncPlayerChatEvent event) {
        UUID senderId = event.getPlayer().getUniqueId();
        UUID otherId = staffToTarget.containsKey(senderId) ? staffToTarget.get(senderId) : targetToStaff.get(senderId);
        if(otherId == null) return;
        Player other = Bukkit.getPlayer(otherId);
        event.setCancelled(true);
        event.getRecipients().clear();
        if(other == null) {
            close(staffToTarget.containsKey(senderId) ? senderId : otherId);
            return;
        }
        // the player's own text is not colour-translated
        String line = CC.translate(PREFIX + "&c" + event.getPlayer().getName() + " &8➥ &f") + event.getMessage();
        event.getPlayer().sendMessage(line);
        other.sendMessage(line);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void quit(PlayerQuitEvent event) {
        close(event.getPlayer().getUniqueId());
        closeForTarget(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void kick(PlayerKickEvent event) {
        if(event.isCancelled()) return;
        close(event.getPlayer().getUniqueId());
        closeForTarget(event.getPlayer().getUniqueId());
    }
}
