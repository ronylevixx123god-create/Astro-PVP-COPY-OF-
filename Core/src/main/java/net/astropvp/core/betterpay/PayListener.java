package net.astropvp.core.betterpay;

import java.util.Set;
import java.util.Locale;
import java.util.HashSet;
import java.util.Arrays;
import org.bukkit.event.server.ServerCommandEvent;
import com.cosmicpvp.cosmicutils.utils.MoneyParser;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import net.astropvp.core.betterpay.commands.CommandPay;
import net.astropvp.core.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PayListener implements Listener {
    private static final Set<String> ECO_LABELS = new HashSet<>(Arrays.asList("eco", "eeco", "economy", "eeconomy"));
    private static final Set<String> ECO_AMOUNT_ACTIONS = new HashSet<>(Arrays.asList("give", "take", "set"));

    /**
     * Essentials' /eco give|take|set <player> <amount> drops every letter from the amount, so shorthand
     * (100k, 1.5m, 1b) must reach it as a plain number. Returns the command line to run, or null when the
     * amount isn't a valid amount.
     */
    static String rewriteEcoAmount(String commandLine) {
        String[] parts = commandLine.split(" ");
        String label = parts[0].toLowerCase(Locale.ROOT);
        if (label.startsWith("essentials:")) label = label.substring("essentials:".length());
        if (!ECO_LABELS.contains(label) || parts.length < 4 || !ECO_AMOUNT_ACTIONS.contains(parts[1].toLowerCase(Locale.ROOT))) {
            return commandLine;
        }
        String plain = MoneyParser.toPlain(parts[3]);
        if (plain == null) return null;
        parts[3] = plain;
        return String.join(" ", parts);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEcoCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().hasPermission("essentials.eco")) return;   // Essentials refuses it anyway
        String line = e.getMessage().substring(1);
        String rewritten = rewriteEcoAmount(line);
        if (rewritten == null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Invalid amount: " + line.split(" ")[3]);
        } else if (!rewritten.equals(line)) {
            e.setMessage("/" + rewritten);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEcoConsoleCommand(ServerCommandEvent e) {
        String line = e.getCommand().startsWith("/") ? e.getCommand().substring(1) : e.getCommand();
        String rewritten = rewriteEcoAmount(line);
        if (rewritten == null) {
            e.setCommand("null");
            e.getSender().sendMessage(ChatColor.RED + "Invalid amount: " + line.split(" ")[3]);
        } else if (!rewritten.equals(line)) {
            e.setCommand(rewritten);
        }
    }
    @EventHandler(priority= EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerCommandpre(PlayerCommandPreprocessEvent e) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            Bukkit.getLogger().severe("Disabling BetterPay due to missing Essentials...");
            return;
        }
        if (!(e.getMessage().contains(" ") || e.getMessage().equalsIgnoreCase("/pay") || e.getMessage().equalsIgnoreCase("/epay"))) {
            return;
        }
        String[] args = e.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("/pay") || args[0].equalsIgnoreCase("/epay")) {
            ArrayList<String> argList = new ArrayList<String>();
            int index = 0;
            for (String arg : args) {
                if (index++ == 0) continue;
                argList.add(arg);
            }
            e.setCancelled(true);
            e.setMessage("/null");
            CommandPay cmd = new CommandPay();
            try {
                cmd.run(Bukkit.getServer(), e.getPlayer(), AstroCore.getInstance().getEss().getUser(e.getPlayer()), "/pay", argList.toArray(new String[argList.size()]));
            }
            catch (NotEnoughArgumentsException e1) {
                e.getPlayer().sendMessage((Object) ChatColor.RED + "Invalid arguments: /pay <player> <amount>");
            }
            catch (Exception ex) {
                AstroCore.getInstance().showError(e.getPlayer(), ex, "pay");
            }
        }
    }
}
