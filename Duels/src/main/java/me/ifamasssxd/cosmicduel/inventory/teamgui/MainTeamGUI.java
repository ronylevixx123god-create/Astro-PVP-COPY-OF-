package me.ifamasssxd.cosmicduel.inventory.teamgui;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.ItemBuilder;
import me.ifamasssxd.cosmicduel.CosmicDuel;
import me.ifamasssxd.cosmicduel.commands.CommandTeam;
import me.ifamasssxd.cosmicduel.struct.TeamData;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MainTeamGUI {
    private final PaginatedGui expShopUI;

    public PaginatedGui getExpShopUI() {
        return this.expShopUI;
    }


    public MainTeamGUI(Player player) {

        this.expShopUI = new PaginatedGui(3, 27, "Team Menu");
        expShopUI.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });


        ItemBuilder listButton = new ItemBuilder(Material.BOOK, CC.translate("&b&lTeams List")).addLoreLine(CC.translate("&7Click to view all teams."));
        ItemBuilder createButton = new ItemBuilder(Material.LADDER, CC.translate("&a&lCreate Team")).addLoreLine(CC.translate("&7Click to create a new team."));
        ItemBuilder disbandButton = new ItemBuilder(Material.BARRIER, CC.translate("&c&lDisband Team")).addLoreLine(CC.translate("&7Click to disband your team."));


        expShopUI.setItem(11, new GuiItem(listButton.build(), event -> {
            TeamListGUI teamListGUI = new TeamListGUI(player);
            teamListGUI.getExpShopUI().open(player);
        }));

        expShopUI.setItem(13, new GuiItem(createButton.build(), event -> {
            if (CosmicDuel.get().getTeamDataManager().tryFindTeam(player.getUniqueId()) == null) {
                TeamData data = new TeamData(player.getUniqueId());
                CosmicDuel.get().getTeamDataManager().addTeam(data);
                player.sendMessage(CC.translate("&a&l(!) &aYou have created a team!"));
                return;
            }
            player.sendMessage(CC.translate("&c&l(!) &cYou already have a team created!"));
        }));

        expShopUI.setItem(15, new GuiItem(disbandButton.build(), event -> {
            TeamData data = CosmicDuel.get().getTeamDataManager().tryFindTeam(player.getUniqueId());
            if (data == null) {
                player.sendMessage(CC.translate("&c&l(!) &cYou do not have a team!"));
                return;
            }
            if (!data.getLeaderUUID().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                player.sendMessage(CC.translate("&c&l(!) &cYou are not the leader of this team!"));
                return;
            }
            if (CosmicDuel.get().getTeamDataManager().isTeamBusy(data)) {
                player.sendMessage(CC.translate("&c&l(!) &cYou cannot change your team while it is in a team duel or has a duel challenge pending."));
                return;
            }
            CommandTeam.disband(player, data);
        }));
        }




}

