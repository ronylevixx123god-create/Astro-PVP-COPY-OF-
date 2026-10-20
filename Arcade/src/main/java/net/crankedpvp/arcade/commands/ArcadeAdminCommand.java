package net.crankedpvp.arcade.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.annotations.WrongUsage;
import me.mattstudios.mf.base.CommandBase;
import net.crankedpvp.arcade.ArcadeAPI;
import net.crankedpvp.arcade.ArcadePlugin;
import net.crankedpvp.arcade.language.Language;
import net.crankedpvp.arcade.language.LanguageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command("aarcade")
public class ArcadeAdminCommand extends CommandBase {

    private final LanguageHandler languageHandler;

    public ArcadeAdminCommand(ArcadePlugin plugin) {
        this.languageHandler = plugin.getLanguageHandler();
    }

    @Default
    @Permission("cranked.admin")
    public void helpCommand(Player sender) {
        sender.sendMessage(CC.translate("&3&lArcade Admin"));
        sender.sendMessage(CC.translate("&b/aarcade reload &7- reload the language file"));
        sender.sendMessage(CC.translate("&b/aarcade forceopen &7- force the arcade open / closed"));
        sender.sendMessage(CC.translate("&b/aarcade fuckup &7- make every player visible to everyone again"));
    }

    @SubCommand("reload")
    @WrongUsage("&cUsage: /aarcade reload")
    @Permission("cranked.admin")
    public void reloadCommand(Player sender) {
        Language.ADMIN_RELOADING.send(sender);
        long then = System.currentTimeMillis();

        this.languageHandler.load();

        long diff = System.currentTimeMillis() - then;
        Language.ADMIN_RELOADED.send(sender, str -> str.replace("%took%", String.valueOf(diff)));
    }

    @SubCommand("fuckup")
    @WrongUsage("&cUsage: /aarcade fuckup")
    @Permission("cranked.admin")
    public void fuckupCommand(Player sender) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if (!player.canSee(player1)) player.showPlayer(player1);
            }
        }
        sender.sendMessage(CC.translate("&cYeahhhh... We fucked up xD"));
    }

    @SubCommand("forceopen")
    @Permission("cranked.admin")
    public void openCommand(Player player) {


        if (!ArcadePlugin.getInstance().getArcadeManager().isForceOpen()) {
            player.sendMessage(CC.translate("&a&l(!)&a You have force opened arcade!"));
            ArcadePlugin.getInstance().getConfig().set("force-open", true);
            ArcadePlugin.getInstance().saveConfig();
        } else {
            player.sendMessage(CC.translate("&c&l(!)&c You have force closed arcade!"));
            ArcadePlugin.getInstance().getConfig().set("force-open", false);
            ArcadePlugin.getInstance().saveConfig();
        }
    }




}
