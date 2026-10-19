package net.astropvp.armorsets.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import net.astropvp.armorsets.AstroArmorSetsAPI;
import net.astropvp.armorsets.gui.SetGUI;
import net.astropvp.armorsets.struct.ArmorSetType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class GUIInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        // the menu is a showcase: no click may move items while it is open, also in the player's own inventory
        // (shift-click, double-click collect, number keys)
        if (isArmorSetsMenu(event.getView().getTopInventory())) {
            event.setCancelled(true);
        }
        if (inventory == null) return;
        if (inventory.getName() == null) return;
        if(inventory.getName().equals("Armor Sets")) {
            event.setCancelled(true);
            ItemStack item = inventory.getItem(event.getSlot());
            if (item == null) return;
            if(item.getType() == Material.BARRIER) p.closeInventory();
            if(item.getType() == Material.BED) {
                SetGUI gui = new SetGUI(p, "Armor Sets");
                gui.openBaseGUI();
            }

            if (event.getRawSlot() < event.getView().getTopInventory().getSize() && p.hasPermission(SetGUI.ADMIN_PERMISSION)) {
                giveFromPreview(p, event.getSlot());
            }

            for(ArmorSetType set : ArmorSetType.values()) {
                if(set.getDisplay().equals(item.getItemMeta().getDisplayName())) {
                    SetGUI gui = new SetGUI(p, "Armor Sets");
                    gui.openSubGUI(set);
                }
            }
        }
    }

    /** Staff: a freshly built copy of the clicked piece (or crystal) of the previewed set, as /givearmorset builds it. */
    private void giveFromPreview(Player p, int slot) {
        ArmorSetType set = SetGUI.PREVIEWING.get(p.getUniqueId());
        if (set == null) return;
        List<ItemStack> items = set.getItems();
        ItemStack fresh;
        if (slot < items.size()) {
            fresh = items.get(slot);
        } else if (slot == items.size()) {
            fresh = AstroArmorSetsAPI.createCrystal(Collections.singletonList(set), 100);
        } else {
            return;
        }
        PlayerUtil.giveOrDropProtectedItem(p, fresh, -1, null, CC.RedB + "(!) " + CC.Red + "Your armor has been dropped on the floor!");
        p.sendMessage(CC.GreenB + "(!)" + CC.Green + " You took " + (fresh.hasItemMeta() && fresh.getItemMeta().hasDisplayName() ? fresh.getItemMeta().getDisplayName() : fresh.getType().name()) + CC.Green + ".");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isArmorSetsMenu(event.getView().getTopInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (isArmorSetsMenu(event.getView().getTopInventory())) {
            SetGUI.PREVIEWING.remove(event.getPlayer().getUniqueId());
        }
    }

    private static boolean isArmorSetsMenu(Inventory top) {
        return top != null && "Armor Sets".equals(top.getName());
    }

}
