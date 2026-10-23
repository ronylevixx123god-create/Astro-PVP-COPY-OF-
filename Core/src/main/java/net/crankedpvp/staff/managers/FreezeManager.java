package net.crankedpvp.staff.managers;

import com.cosmicpvp.cosmicutils.utils.CC;
import lombok.Getter;
import net.astropvp.core.AstroCore;
import net.crankedpvp.staff.utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeManager {

    private AstroCore plugin;
    public FreezeManager(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Getter
    private Map<UUID, Location> frozenPlayers = new HashMap<>();

    public boolean isFrozen(Player player) {
        return this.frozenPlayers.containsKey(player.getUniqueId());
    }

    // /freeze and the staff mode freeze tool; returns true when the player is frozen now
    public boolean toggleFreeze(Player player, Player toFreeze) {
        if(this.frozenPlayers.containsKey(toFreeze.getUniqueId())) {
            this.frozenPlayers.remove(toFreeze.getUniqueId());
            Bukkit.getOnlinePlayers().stream().filter(loopPlayer -> loopPlayer.hasPermission("staff.freeze")).forEach(loopPlayer -> loopPlayer.sendMessage(CC.translate("&b&lFreeze &8➥ &7The player &f" + toFreeze.getName() + " &7has been unfrozen by &f" + player.getName())));
            return false;
        }
        this.frozenPlayers.put(toFreeze.getUniqueId(), toFreeze.getLocation().clone());
        Bukkit.getOnlinePlayers().stream().filter(loopPlayer -> loopPlayer.hasPermission("staff.freeze")).forEach(loopPlayer -> loopPlayer.sendMessage(CC.translate("&b&lFreeze &8➥ &7The player &f" + toFreeze.getName() + " &7has been frozen by &f" + player.getName())));
        TitleUtils.sendTitle(toFreeze, CC.translate("&c&lFROZEN"), CC.translate("&7DO NOT LOG OUT"), 1, 10, 1);
        toFreeze.sendMessage(CC.translate("&b&lFreeze &8➥ &7You have been &c&lFROZEN&7. Join discord.gg/b58nePVn immediately."));
        return true;
    }
}
