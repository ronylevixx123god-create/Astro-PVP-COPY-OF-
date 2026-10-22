package me.ifamasssxd.cosmicbosses.struct;

import com.cosmicpvp.cosmicutils.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/** the /collect stash (Collect plugin) for boss rewards that don't fit in a player's inventory */
final class BossCollect {

    private BossCollect() {
    }

    static boolean available() {
        return Bukkit.getPluginManager().isPluginEnabled("Collect");
    }

    static void store(UUID player, List<ItemStack> items, String bossName) {
        Stash.store(player, items, bossName);
    }

    // only loaded when Collect is installed
    private static final class Stash {
        static void store(UUID player, List<ItemStack> items, String bossName) {
            net.astropvp.struct.PlayerCollect collect = net.astropvp.CollectPlugin.getInstance().getCollectManager().getOrCreate(player.toString());
            collect.add(new net.astropvp.struct.CollectCategory(net.astropvp.struct.CollectType.SPACE, items.toArray(new ItemStack[0]),
                    CC.translate("&c&l" + bossName + " Loot"), System.currentTimeMillis()));
        }
    }
}
