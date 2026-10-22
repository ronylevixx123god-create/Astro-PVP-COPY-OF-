package net.astropvp.customblocks.guis;

import com.conaxgames.customskulls.CoreSkulls;
import com.conaxgames.customskulls.blocks.CustomBlock;
import com.conaxgames.customskulls.ui.CustomBlocksAdminUI;
import com.conaxgames.customskulls.util.ItemBuilderUtils;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The CustomSkullBlocks blocks (grinder and seasonal blocks) for the /customblocks menu, shown and given exactly as that
 * plugin's own /cb view menu does. Only loaded when CustomSkullBlocks is enabled.
 */
final class SkullBlockItems {
    // the order CoreSkulls registers them in
    private static final List<String> ORDER = Arrays.asList("HALLOWEEN_CAULDRON", "THANKSGIVING_PIE", "TREASURE_CHEST", "COSMIC_TROPHY",
            "LEVIATHAN_TOTEM", "HALLOWEEN_CANDLES", "COSMIC_TOKEN_GENERATOR", "BLAZE_STONE_TOTEM", "SECURITY_SYSTEM");

    private SkullBlockItems() {
    }

    static void addTo(PaginatedGui gui, Player player, String givePermission) {
        List<String> ids = new ArrayList<>(ORDER);
        for (String id : CoreSkulls.customBlocks.keySet()) {
            if (!ids.contains(id)) ids.add(id);
        }
        for (String id : ids) {
            CustomBlock block = CoreSkulls.customBlocks.get(id);
            if (block == null) continue;
            ItemStack item = new ItemBuilderUtils(block.getItem()).setLore(block.addLore()).toItemStack();
            gui.addItem(new GuiItem(item, event -> {
                if (player.hasPermission(givePermission)) {
                    CustomBlocksAdminUI.giveItem(player, block, true);
                }
            }));
        }
    }
}
