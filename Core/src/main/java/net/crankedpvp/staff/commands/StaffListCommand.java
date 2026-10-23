package net.crankedpvp.staff.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.EssentialsUtils;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import net.astropvp.core.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** /stafflist (and /staff list): online staff grouped by rank, in their rank colours. */
@Command("stafflist")
public class StaffListCommand extends CommandBase {

    /** staff groups highest first: LuckPerms group, rank colour, name (the nametag prefixes, TAB groups.yml) */
    private static final String[][] RANKS = {
            {"owner", "&4", "Owner"}, {"manager", "&c", "Manager"}, {"developer", "&c", "Dev"}, {"admin", "&c", "Admin"},
            {"banteam", "&6", "BanTeam"}, {"srmod", "&3", "SrMod"}, {"mod", "&3", "Mod"}, {"helper", "&a", "Helper"}};

    /** staff who may see vanished staff in the list (every staff rank has it) */
    private static final String SEE_VANISHED = "astro.staff";

    private final AstroCore plugin;

    public StaffListCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Default
    public void staffList(CommandSender sender) {
        sendStaffList(sender);
    }

    public static void sendStaffList(CommandSender sender) {
        boolean seeVanished = !(sender instanceof Player) || sender.hasPermission(SEE_VANISHED);
        List<String> lines = new ArrayList<>();
        int total = 0;
        for (String[] rank : RANKS) {
            List<String> names = new ArrayList<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!rank[0].equals(staffRank(online))) continue;
                boolean vanished = EssentialsUtils.isVanished(online);
                if (vanished && !seeVanished) continue;
                names.add(rank[1] + online.getName() + (vanished ? " &7(vanished)" : ""));
            }
            if (names.isEmpty()) continue;
            total += names.size();
            lines.add(" " + rank[1] + "&l" + rank[2] + " &7(" + names.size() + ")&8: " + String.join("&7, ", names));
        }
        if (total == 0) {
            sender.sendMessage(CC.translate("&c&l(!)&c There is no staff online right now."));
            return;
        }
        sender.sendMessage(CC.translate("&8&m--------------------------------"));
        sender.sendMessage(CC.translate("&6&lOnline Staff &7(" + total + ")"));
        for (String line : lines) sender.sendMessage(CC.translate(line));
        sender.sendMessage(CC.translate("&8&m--------------------------------"));
    }

    /** the player's highest staff group (groups inherit downwards, so check from the top), or null */
    private static String staffRank(Player player) {
        for (String[] rank : RANKS) {
            String node = "group." + rank[0];
            if (player.isPermissionSet(node) && player.hasPermission(node)) return rank[0];   // set by LuckPerms, not just op
        }
        return null;
    }
}
