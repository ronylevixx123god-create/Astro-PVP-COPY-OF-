package me.cosmicpvp.CosmicGlobalKits.menu;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.InventoryUtils;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import java.util.List;
import me.cosmicpvp.CosmicGlobalKits.CosmicGlobalKits;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import me.cosmicpvp.CosmicGlobalKits.menu.MKitMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MKitPreviewMenu
extends CosmicGUI {
    private MasteryKits kits;
    private List<ItemStack> kitItems;

    public MKitPreviewMenu(Player player, MasteryKits kit, List<ItemStack> items) {
        super(player, ChatColor.stripColor((String)kit.getName()) + " Kit Preview", InventoryUtils.fitSize((int)items.size()));
        this.kitItems = items;
        this.kits = kit;
    }

    public void redraw() {
        if (this.kits == null) {
            return;
        }
        int index = 0;
        for (ItemStack item : this.kitItems) {
            this.setItem(index++, new InventoryItem(item));
        }
        this.setItem(this.inventory.getSize() - 1, new InventoryItem(new ItemBuilder(Material.ARROW, CC.RedB + "Back", new String[]{CC.Gray + "Click to return to the /mkit menu."}).build()).click(() -> this.closeWithDelay(pl -> new MKitMenu(this.player).openGUI(CosmicGlobalKits.getPlugin()))));
    }
}

