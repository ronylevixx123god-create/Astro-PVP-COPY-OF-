package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import me.ifamasssxd.cosmicskycustoms.CosmicSkyCustomItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * /astroitem - admin command for the Astro custom items: "list" shows their names, "give <player> <item> [data...]"
 * (or just "<player> <item> [data...]") gives one through /customitems give.
 */
public class AstroItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(CC.translate("&cYou do not have permission to use this command."));
            return true;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("CosmicSkyCustomItems")) {
            sender.sendMessage(CC.translate("&cThe custom items plugin (CosmicSkyCustomItems) is not loaded."));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            List<String> names = new ArrayList<>(CosmicSkyCustomItems.get().getItemManager().getCustomRegisteredItems().keySet());
            Collections.sort(names);
            sender.sendMessage(CC.translate("&a&lAstro Items &7(" + names.size() + ")&a: &f" + String.join("&7, &f", names)));
            return true;
        }
        String[] give = args.length >= 3 && args[0].equalsIgnoreCase("give") ? Arrays.copyOfRange(args, 1, args.length) : args;
        if (give.length < 2 || give[0].equalsIgnoreCase("give")) {
            sender.sendMessage(CC.translate("&cUsage: /astroitem give <player> <item> [data...]"));
            sender.sendMessage(CC.translate("&cUsage: /astroitem list"));
            return true;
        }
        Bukkit.dispatchCommand(sender, "customitems give " + String.join(" ", give));
        return true;
    }
}
