package me.astropvp.astropvpskins.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import me.astropvp.astropvpskins.enums.ItemSkinType;
import me.astropvp.astropvpskins.enums.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CommandGiveBackpack implements CommandExecutor {

    // backpack names from Astro's old AstroCore backpacks -> the same skins in ItemSkins
    private static final Map<String, String> OLD_NAMES = new HashMap<>();

    static {
        OLD_NAMES.put("BUG_OUT_BAG", "DOOMSDAY_BACKPACK_BUGOUT");
        OLD_NAMES.put("RAINBOW_BACKPACK", "DOOMSDAY_BACKPACK_RAINBOW");
        OLD_NAMES.put("CURSED_WINGS", "HALLOWEEN_BACKPACK");
        OLD_NAMES.put("PORTABLE_PICNIC", "THANKSGIVING_BACKPACK_PICNIC");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        if (!player.isOp())
            return true;
        SkinType type = args.length == 2 ? getBackpack(args[0]) : null;
        Player target = args.length == 2 ? Bukkit.getPlayer(args[1]) : null;
        if (type == null || target == null) {
            player.sendMessage(CC.Red + "Correct Usage: /givebackpack <type> <player>");
            return true;
        }
        ItemStack item = type.getItem();
        target.getInventory().addItem(item);
        player.sendMessage(CC.Green + "Given " + item.getItemMeta().getDisplayName() + " to " + target.getDisplayName());
        return false;
    }

    private SkinType getBackpack(String name) {
        String id = name.toUpperCase();
        id = OLD_NAMES.getOrDefault(id, id);
        for (SkinType type : SkinType.values()) {
            if (type.name().equals(id) && type.getItemSkinType() == ItemSkinType.BACKPACK && type.getItemSkin() != null)
                return type;
        }
        return null;
    }
}
