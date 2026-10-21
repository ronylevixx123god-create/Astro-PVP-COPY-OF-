package net.astropvp.core.commands;

import net.crankedpvp.staff.struct.StaffMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandomTpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        if(!player.hasPermission("essentials.tp"))
            return true;
        player.getInventory().addItem(StaffMode.getRandomPlayerSelector());
        return false;
    }
}
