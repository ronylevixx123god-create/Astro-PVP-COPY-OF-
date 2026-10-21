package me.ifamasssxd.cosmicheroicenvoys.commands;

import me.ifamasssxd.cosmicheroicenvoys.utils.LocationUtils;
import net.astropvp.core.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Astro's /setheroicbossspawn (same in final Astro and the older Core-0.1.jar): saves "bossSpawn" in AstroCore's config.
public class CommandSetHeroicBossSpawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        Player pl = sender instanceof Player ? (Player) sender : null;
        String loc = args.length == 1 && args[0].contains(":") ? args[0] : (pl != null ? LocationUtils.getString(pl.getLocation()) : null);
        if (loc == null || LocationUtils.getLocation(loc) == null) {
            sender.sendMessage(ChatColor.RED + "Invalid location given!");
            return true;
        }
        AstroCore.getInstance().getConfig().set("bossSpawn", loc);
        AstroCore.getInstance().saveConfig();
        AstroCore.getInstance().getEnvoyManager().setBossLocation(LocationUtils.getLocation(loc));
        sender.sendMessage(ChatColor.RED + "Set boss spawn location at " + loc + ".");
        return true;
    }
}
