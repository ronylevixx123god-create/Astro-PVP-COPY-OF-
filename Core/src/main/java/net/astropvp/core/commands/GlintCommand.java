package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.GlintUtils;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.WrongUsage;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command("addglint")
public class GlintCommand extends CommandBase {
    // the standard glint colour (the one GlintUtils.removeGlint takes off again)
    private static final int DEFAULT_COLOUR = -10237440;

    @Default
    @Permission("glint.*")
    @WrongUsage("&cUsage: /addglint [colour number]")
    public void glint(Player player, @Optional Integer colour) {
        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cHold the item you want to glint. &7Usage: /addglint [colour number]"));
            return;
        }
        GlintUtils.addGlint(item, colour == null ? DEFAULT_COLOUR : colour);
        player.setItemInHand(item);
        player.updateInventory();
        player.sendMessage(CC.translate("&aGlinted Item!"));
    }
}
