package me.ifamasssxd.cosmicduel.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import me.ifamasssxd.cosmicduel.CosmicDuel;
import me.ifamasssxd.cosmicduel.inventory.teamgui.MainTeamGUI;
import me.ifamasssxd.cosmicduel.struct.TeamData;
import me.ifamasssxd.cosmicduel.struct.TeamDataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTeam implements CommandExecutor {
    private static final String BUSY = "&c&l(!) &cYou cannot change your team while it is in a team duel or has a duel challenge pending.";

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player player = (Player)sender;
        TeamDataManager manager = CosmicDuel.get().getTeamDataManager();
        if (args.length == 0) {
            MainTeamGUI mainTeamGUI = new MainTeamGUI(player);
            mainTeamGUI.getExpShopUI().open(player);

            player.sendMessage("");
            player.sendMessage(CC.translate("&a&lAstro Team Duels"));
            player.sendMessage(CC.translate("&f&l* &a/team create &f- &7create a team"));
            player.sendMessage(CC.translate("&f&l* &a/team invite <player> &f- &7invite a player to your team"));
            player.sendMessage(CC.translate("&f&l* &a/team deinvite <player> &f- &7de-invite a player from your team"));
            player.sendMessage(CC.translate("&f&l* &a/team accept <player> &f- &7accept a team invite (same as /team join)"));
            player.sendMessage(CC.translate("&f&l* &a/team kick <player> &f- &7kick a player from your team"));
            player.sendMessage(CC.translate("&f&l* &a/team leave &f- &7leave a team"));
            player.sendMessage(CC.translate("&f&l* &a/team disband &f- &7disband a team"));
            player.sendMessage(CC.translate("&f&l* &a/team leader <player> &f- &7make your teammate the team leader"));
            player.sendMessage(CC.translate("&f&l* &a/team list &f- &7see who is apart of your team"));
            player.sendMessage(CC.translate("&f&l* &a/team who <player> &f- &7check player's team"));
            player.sendMessage(CC.translate("&f&l* &a/duel <leader> &f- &7leaders: challenge another team of 2 to a 2v2 duel"));
            player.sendMessage("");
            return true;
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("invite")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(CC.translate("&c&l(!) &cThat player is not online!"));
                return false;
            }
            if (target == player) {
                player.sendMessage(CC.translate("&c&l(!) &cYou can not invite yourself to your team!"));
                return false;
            }
            TeamData data = manager.tryFindTeam(player.getUniqueId());
            if (data != null && !data.isLeader(player.getUniqueId())) {
                player.sendMessage(CC.translate("&c&l(!) &cYou are not the leader of your team! You can not invite players!"));
                return false;
            }
            if (data != null && manager.isTeamBusy(data)) {
                player.sendMessage(CC.translate(BUSY));
                return false;
            }
            TeamData targetTeamData = manager.tryFindTeam(target.getUniqueId());
            if (targetTeamData != null) {
                if (targetTeamData == data) {
                    player.sendMessage(CC.translate("&c&l(!) &c" + target.getName() + " is already on your team!"));
                    return false;
                }
                player.sendMessage(CC.translate("&c&l(!) &cYou can not invite &f" + target.getName() + " &cto your team, as they are already part of a team!"));
                return false;
            }
            if (manager.alreadyHasTeamInvite(target)) {
                player.sendMessage(CC.translate("&c&l(!) &f" + target.getName() + "&c already has a team invite from a team!"));
                return false;
            }
            if (data != null && data.getTeamates().size() >= 1) {
                player.sendMessage(CC.translate("&c&l(!)&c The max team size is 2!"));
                return false;
            }
            if (data == null) {
                data = new TeamData(player.getUniqueId());
                manager.addTeam(data);
                player.sendMessage(CC.translate("&a&l(!) &aYou have created a team!"));
            }
            player.sendMessage(CC.translate("&a&l(!) &aYou have sent a team invite to &f" + target.getName() + "&a!"));
            manager.sendTeamInvite(data, target);
            return false;
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("deinvite")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(CC.translate("&c&l(!) &cThat player is not online!"));
                return false;
            }
            if (target == player) {
                player.sendMessage(CC.translate("&c&l(!) &cYou can not de-invite yourself!"));
                return false;
            }
            TeamData data = manager.tryFindTeam(player.getUniqueId());
            if (data == null) {
                player.sendMessage(CC.translate("&c&l(!) &CYou do not have a team! You can not de-invite players!"));
                return false;
            }
            if (!data.isLeader(player.getUniqueId())) {
                player.sendMessage(CC.translate("&c&l(!) &cYou are not the leader of your team! You can not de-invite players!"));
                return false;
            }
            if (!manager.hasInviteToTeam(target, data)) {
                player.sendMessage(CC.translate("&c&l(!) &cYou cannot deinvite a player that is not invited to your team!"));
                return false;
            }
            player.sendMessage(CC.translate("&c&l(!) &cYou have de-invited &f" + target.getName() + "&c!"));
            manager.removeInvite(data, target);
            return false;
        }
        if (args.length >= 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("accept"))) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(CC.translate("&c&l(!) &cThat player is not online!"));
                return false;
            }
            if (target == player) {
                player.sendMessage(CC.translate("&c&l(!) &cYou can not join your own team!"));
                return false;
            }
            TeamData targetTeam = manager.tryFindTeam(target.getUniqueId());
            if (targetTeam == null) {
                player.sendMessage(CC.translate("&cThat player is not apart of a team!"));
                return false;
            }
            if (!manager.hasInviteToTeam(player, targetTeam)) {
                Player leader = Bukkit.getPlayer(targetTeam.getLeaderUUID());
                player.sendMessage(CC.translate("&c&l(!) &f" + target.getName() + "'s &cteam has not invited you!"));
                if (leader != null)
                    player.sendMessage(CC.translate("&7Ask for an invite from the team leader &n" + leader.getName() + "&7."));
                return false;
            }
            if (manager.tryFindTeam(player.getUniqueId()) != null) {
                player.sendMessage(CC.translate("&c&l(!) &cYou are already part of a team! Use &f/team leave &cfirst."));
                return false;
            }
            if (manager.hitTeamInivteLimit(targetTeam)) {
                player.sendMessage(CC.translate("&c&l(!) &cThat team is full. The max team size is 2!"));
                return false;
            }
            if (manager.isTeamBusy(targetTeam)) {
                player.sendMessage(CC.translate("&c&l(!) &cThat team is in a team duel right now, try again after it ends."));
                return false;
            }
            for (UUID uuid : targetTeam.getAllMembers()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) {
                    member.sendMessage(CC.translate("&a&l(!) &f" + player.getName() + "&a has joined your team!"));
                    member.playSound(member.getLocation(), Sound.ORB_PICKUP, 10.0F, 10.0F);
                }
            }
            targetTeam.removeFromInvites(player.getUniqueId());
            targetTeam.addToTeam(player.getUniqueId());
            player.sendMessage(CC.translate("&a&l(!) &aYou have joined &f" + Bukkit.getOfflinePlayer(targetTeam.getLeaderUUID()).getName() + "'s &ateam!"));
            return false;
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("kick")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            TeamData data = manager.tryFindTeam(player.getUniqueId());
            if (data == null) {
                player.sendMessage(CC.translate("&c&l(!) &cYou do not have a team!"));
                return false;
            }
            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(CC.translate("&c&l(!) &cYou can not kick yourself from your own team!"));
                return false;
            }
            if (!data.isLeader(player.getUniqueId())) {
                player.sendMessage(CC.translate("&c&l(!) &cYou are not the leader of this team!"));
                return false;
            }
            if (!data.getTeamates().contains(target.getUniqueId())) {
                player.sendMessage(CC.translate("&c&l(!) &cThat player is not on your team!"));
                return false;
            }
            if (manager.isTeamBusy(data)) {
                player.sendMessage(CC.translate(BUSY));
                return false;
            }
            data.removeFromTeam(target.getUniqueId());
            if (target.isOnline())
                ((Player)target).sendMessage(CC.translate("&c&l(!) &cYou were kicked from " + player.getName() + "'s team!"));
            player.sendMessage(CC.translate("&c&l(!) &cYou kicked " + target.getName() + " from your team!"));
            return false;
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("leave")) {
            TeamData data = manager.tryFindTeam(player.getUniqueId());
            if (data == null) {
                player.sendMessage(CC.translate("&c&l(!) &cYou do not have a team!"));
                return false;
            }
            if (manager.isTeamBusy(data)) {
                player.sendMessage(CC.translate(BUSY));
                return false;
            }
            if (data.isLeader(player.getUniqueId())) {
                disband(player, data);
                return false;
            }
            data.removeFromTeam(player.getUniqueId());
            player.sendMessage(CC.translate("&c&l(!) &cYou have left the team!"));
            for (UUID uuid : data.getAllMembers()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) {
                    member.sendMessage(CC.translate("&c&l(!) &f" + player.getName() + "&c has left your team!"));
                    member.playSound(member.getLocation(), Sound.ORB_PICKUP, 10.0F, 10.0F);
                }
            }
            return false;
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("disband")) {
            TeamData data = manager.tryFindTeam(player.getUniqueId());
            if (data == null) {
                player.sendMessage(CC.translate("&c&l(!) &cYou do not have a team!"));
                return false;
            }
            if (!data.isLeader(player.getUniqueId())) {
                player.sendMessage(CC.translate("&c&l(!) &cYou are not the leader of this team!"));
                return false;
            }
            if (manager.isTeamBusy(data)) {
                player.sendMessage(CC.translate(BUSY));
                return false;
            }
            disband(player, data);
            return false;
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            TeamData playerTeam = manager.tryFindTeam(player.getUniqueId());
            if (playerTeam == null) {
                player.sendMessage(CC.translate("&cYou are not apart of a team!"));
                return false;
            }
            manager.showTeamMessage(player, playerTeam);
            return false;
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("who")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(CC.translate("&c&l(!) &cPlayer not found!"));
                return false;
            }
            TeamData targetTeam = manager.tryFindTeam(target.getUniqueId());
            if (targetTeam == null) {
                player.sendMessage(CC.translate("&c&l(!) &cThat player does not have a team!"));
                return false;
            }
            manager.showTeamMessage(player, targetTeam);
            return false;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("who")) {
            TeamData data = manager.tryFindTeam(player.getUniqueId());
            if (data == null) {
                player.sendMessage(CC.translate("&c&l(!) &cYou do not have a team!"));
                return false;
            }
            manager.showTeamMessage(player, data);
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
            if (manager.tryFindTeam(player.getUniqueId()) == null) {
                TeamData data = new TeamData(player.getUniqueId());
                manager.addTeam(data);
                player.sendMessage(CC.translate("&a&l(!) &aYou have created a team!"));
                return false;
            }
            player.sendMessage(CC.translate("&c&l(!) &cYou already have a team created!"));
            return false;
        }
        if (args.length == 1 && player.isOp() && args[0].equalsIgnoreCase("disbandall")) {
            manager.getTeams().clear();
            player.sendMessage(CC.translate("&cRemoved all teams"));
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("leader")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(CC.translate("&c&l(!) &cPlayer not found!"));
                return true;
            }
            TeamData senderData = manager.tryFindTeam(player.getUniqueId());
            if (senderData == null) {
                sender.sendMessage(CC.translate("&c&l(!) &cYou cannot do this because you don't have a team!"));
                return true;
            }
            if (!senderData.isLeader(player.getUniqueId())) {
                sender.sendMessage(CC.translate("&c&l(!) &cYou cannot do this command because you are not the leader of this team!"));
                return true;
            }
            if (!senderData.getTeamates().contains(target.getUniqueId())) {
                sender.sendMessage(CC.translate("&c&l(!) &cYou can only give your teamates the leadership role!"));
                return true;
            }
            if (manager.isTeamBusy(senderData)) {
                player.sendMessage(CC.translate(BUSY));
                return true;
            }
            List<UUID> teammates = Lists.newArrayList(senderData.getTeamates());
            teammates.remove(target.getUniqueId());
            teammates.add(player.getUniqueId());
            manager.removeTeam(senderData);
            TeamData newTeam = new TeamData(target.getUniqueId());
            teammates.forEach(newTeam::addToTeam);
            manager.addTeam(newTeam);
            sender.sendMessage(CC.translate("&c&l(!) &cYou have transferred your /team leadership role to &f" + target.getName() + "&c!"));
            target.sendMessage(CC.translate("&a&l(!) &aYou are now the leader of &f" + sender.getName() + "'s&a /team."));
            return true;
        }
        player.performCommand("team");
        return false;
    }

    public static void disband(Player leader, TeamData data) {
        for (UUID uuid : data.getTeamates()) {
            Player teammate = Bukkit.getPlayer(uuid);
            if (teammate != null) {
                teammate.sendMessage(CC.translate("&c&l(!) &c" + leader.getName() + " has disbanded your team!"));
                teammate.playSound(teammate.getLocation(), Sound.ORB_PICKUP, 10.0F, 10.0F);
            }
        }
        leader.sendMessage(CC.translate("&c&l(!) &cYou have disbanded your team!"));
        CosmicDuel.get().getTeamDataManager().removeTeam(data);
    }
}
