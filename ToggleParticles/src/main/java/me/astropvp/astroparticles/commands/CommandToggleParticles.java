package me.astropvp.astroparticles.commands;

import me.astropvp.astroparticles.utils.ParticleUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleParticles implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (ParticleUtils.isPlayerIndicating(player)) {
            player.sendMessage("§eYou have §c§lDISABLED §eParticles!");
            ParticleUtils.removePlayer(player);
            return true;
        }
        player.sendMessage("§eYou have §a§lENABLED §eParticles!");
        ParticleUtils.addPlayer(player);
        return true;
    }
}
