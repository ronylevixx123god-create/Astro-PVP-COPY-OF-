package me.astropvp.astropvpskins.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import me.astropvp.astropvpskins.enums.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Astro's original /giveskin (AstroItemSkins jar by Bryside): /giveskin <type of skin> <amount> <player>
public class CommandGiveSkin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skin.give"))
            return true;
        SkinType skinType = args.length < 3 ? null : getSkinType(args[0]);
        int amount = args.length < 3 ? 0 : parseAmount(args[1]);
        Player player = args.length < 3 ? null : Bukkit.getPlayer(args[2]);
        if (skinType == null || amount < 1 || amount > 64 || player == null) {
            sender.sendMessage("/giveskin <type of skin> <amount> <player>");
            for (SkinType type : SkinType.values())
                sender.sendMessage(type.name() + CC.WhiteB + " - " + type.getDisplayName());
            return true;
        }
        for (int i = 0; i < amount; i++) {
            for (ItemStack left : player.getInventory().addItem(skinType.getItem()).values())
                player.getWorld().dropItem(player.getLocation(), left);
        }
        String message = CC.Green + "Added item " + amount + "x " + skinType.getDisplayName() + CC.Green + " to " + player.getName() + "'s Inventory.";
        player.sendMessage(message);
        if (sender != player)
            sender.sendMessage(message);
        return true;
    }

    private static SkinType getSkinType(String name) {
        for (SkinType type : SkinType.values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }
        return null;
    }

    private static int parseAmount(String amount) {
        try {
            return Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
