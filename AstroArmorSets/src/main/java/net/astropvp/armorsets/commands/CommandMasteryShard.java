package net.astropvp.armorsets.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NumberUtils;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import net.astropvp.armorsets.AstroArmorSetsAPI;
import net.astropvp.armorsets.struct.antiMkit.AntiMkit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

// /masteryshard from CosmicArmorSets (com.compilex.CosmicArmorSets.cmds.CommandMasteryShard), on the AntiMkit sets
public class CommandMasteryShard
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(CC.Red + "No permission.");
            return true;
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("give")) {
            Player pl = Bukkit.getPlayer(args[1]);
            if (pl == null) {
                sender.sendMessage(CC.Red + "Invalid online player!");
                return true;
            }
            AntiMkit set = AstroArmorSetsAPI.mkitFromName(args[2]);
            if (set == null) {
                sender.sendMessage(CC.Red + "Invalid Ability Armor set: " + Arrays.toString(AntiMkit.values()));
                return true;
            }
            if (args.length >= 4 && args[3].equalsIgnoreCase("anticrystal")) {
                Integer crystal = args.length >= 5 ? NumberUtils.parseIntOrNull(args[4]) : null;
                PlayerUtil.giveOrDropProtectedItem(pl, set.createAntiMkitShard(crystal == null || crystal <= 0 ? 1 : crystal), -1);
                return true;
            }
            PlayerUtil.giveOrDropProtectedItem(pl, set.createMasteryShard(true), -1);
            return true;
        }
        sender.sendMessage(CC.Red + "/masteryshard give <player> <abilitySet> [anticrystal]");
        return false;
    }
}
