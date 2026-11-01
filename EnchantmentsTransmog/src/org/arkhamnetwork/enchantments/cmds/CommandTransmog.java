package org.arkhamnetwork.enchantments.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cosmicpvp.cosmicutils.utils.CC;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.arkhamnetwork.enchantments.Enchantments;
import org.arkhamnetwork.enchantments.listeners.GodlyTransmogListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /transmog <player> [normal|godly] [amount]  (OP / console only, as Astro's original).
 * Rewritten 2026-11-01 from the CFR decompile: Astro's version only took <player> and always gave one normal scroll.
 * Also registers the Godly Transmog Scroll listener (Astro had the item but nothing that handled it).
 */
public class CommandTransmog implements TabExecutor {
    private static final List<String> TYPES = Arrays.asList("normal", "godly");

    public CommandTransmog() {
        Bukkit.getPluginManager().registerEvents(new GodlyTransmogListener(), Enchantments.getInstance());
    }

    private static boolean allowed(CommandSender sender) {
        return !(sender instanceof Player) || sender.isOp();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!allowed(sender)) {
            return true;
        }
        if (args.length < 1 || args.length > 3) {
            sender.sendMessage(CC.translate("&e/" + label + " <player> [normal|godly] [amount]"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(CC.translate("&c&l(!) &c" + args[0] + " is not online."));
            return true;
        }
        boolean godly = false;
        if (args.length >= 2) {
            String type = args[1].toLowerCase();
            if (!TYPES.contains(type)) {
                sender.sendMessage(CC.translate("&e/" + label + " <player> [normal|godly] [amount]"));
                return true;
            }
            godly = type.equals("godly");
        }
        int amount = 1;
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                amount = -1;
            }
            if (amount < 1 || amount > 64) {
                sender.sendMessage(CC.translate("&c&l(!) &cAmount must be 1-64. &e/" + label + " <player> [normal|godly] [amount]"));
                return true;
            }
        }
        ItemStack scroll = godly ? EnchantmentBook.getGodlyTransmog() : EnchantmentBook.getTransmogScroll();
        scroll.setAmount(amount);
        Map<Integer, ItemStack> left = target.getInventory().addItem(scroll);
        for (ItemStack drop : left.values()) {
            target.getWorld().dropItem(target.getLocation(), drop);
        }
        String name = godly ? "Godly Transmog Scroll" : "Transmog Scroll";
        target.sendMessage(CC.translate((godly ? "&6&l+ " : "&e&l+ ") + amount + "x " + name));
        sender.sendMessage(CC.translate("&a&l(!) &aGave " + target.getName() + " " + amount + "x " + name + "."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!allowed(sender)) {
            return Collections.emptyList();
        }
        List<String> out = new ArrayList<String>();
        String typed = args[args.length - 1].toLowerCase();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(typed)) {
                    out.add(p.getName());
                }
            }
        } else if (args.length == 2) {
            for (String t : TYPES) {
                if (t.startsWith(typed)) {
                    out.add(t);
                }
            }
        } else if (args.length == 3) {
            for (int i = 1; i <= 64; i++) {
                if (String.valueOf(i).startsWith(typed)) {
                    out.add(String.valueOf(i));
                }
            }
        }
        return out;
    }
}
