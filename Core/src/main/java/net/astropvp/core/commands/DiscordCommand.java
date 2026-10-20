package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.FactionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {
    private static final String DISCORD_URL = "https://discord.gg/b58nePVn";

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;
        p.sendMessage("");
        p.sendMessage(CC.translate("&6&lAstro&f&lPvP Discord"));
        // the link line opens the invite when clicked
        BaseComponent[] link = TextComponent.fromLegacyText(CC.translate("&f&n" + DISCORD_URL));
        for (BaseComponent part : link) {
            part.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DISCORD_URL));
        }
        p.spigot().sendMessage(link);
        p.sendMessage(CC.translate("&7Click the link above to join the discord."));
        p.sendMessage("");
        return false;

    }

}