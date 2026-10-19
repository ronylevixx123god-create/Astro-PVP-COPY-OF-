package me.ifamasssxd.cosmicsky.inventorypets.listeners;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import me.ifamasssxd.cosmicsky.inventorypets.struct.menu.PetHelpMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

/** CosmicGUI cancels clicks in its menus but not drags; the /pets showcase must not accept or give items. */
public class PetMenuProtectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        if (CosmicGUI.getActiveGUI(event.getWhoClicked().getUniqueId()) instanceof PetHelpMenu) {
            event.setCancelled(true);
        }
    }
}
