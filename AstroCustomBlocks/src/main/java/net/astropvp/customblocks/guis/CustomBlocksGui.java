package net.astropvp.customblocks.guis;

import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.astropvp.customblocks.struct.CustomBlockType;
import net.astropvp.customblocks.struct.abilities.LevelAble;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class CustomBlocksGui {
    private static final String GIVE_PERMISSION = "astro.admin";
    private final PaginatedGui gui;

    public CustomBlocksGui(Player player) throws IllegalAccessException, InstantiationException {
        this.gui = new PaginatedGui(3, 27, "Astro CustomBlocks");
        this.gui.setDefaultClickAction(event -> event.setCancelled(true));
        for (CustomBlockType type : CustomBlockType.values()) {
            if(!type.isEnabled()) continue;
            this.gui.addItem(new GuiItem(type.getCustomBlockClass().newInstance() instanceof LevelAble ? type.build(1) : type.build(0), event -> {
                if (player.hasPermission(GIVE_PERMISSION)) {
                    try {
                        PlayerUtil.giveOrDropProtectedItem(player, type.getCustomBlockClass().newInstance() instanceof LevelAble ? type.build(1) : type.build(0), 60);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        // CustomSkullBlocks' grinder and seasonal blocks; the seasonal types are disabled above because they work there
        if (Bukkit.getPluginManager().isPluginEnabled("CustomSkullBlocks")) {
            SkullBlockItems.addTo(this.gui, player, GIVE_PERMISSION);
        }
    }
}
