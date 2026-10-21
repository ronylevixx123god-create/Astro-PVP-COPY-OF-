package net.astropvp.core.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.astropvp.core.guis.MerchantGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MerchantGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) throws UserDoesNotExistException, NoLoanPermittedException {
        Player player = (Player) event.getWhoClicked();
        if (player.getOpenInventory().getTitle().equals(CC.translate("&8Scratch Card Merchant"))) {
            event.setCancelled(true);
            if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()
                    && event.getClickedInventory().getHolder() instanceof MerchantGUI) {
                ((MerchantGUI) event.getClickedInventory().getHolder()).click(event);
            }
        }
    }
}
