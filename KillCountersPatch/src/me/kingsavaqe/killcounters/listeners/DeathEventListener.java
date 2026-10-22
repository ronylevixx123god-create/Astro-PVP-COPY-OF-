package me.kingsavaqe.killcounters.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.kingsavaqe.killcounters.KillCounterAPI;
import me.kingsavaqe.killcounters.KillCounterPlugin;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

// rebuilt from the AstroKillCounters-1.0 bytecode; the only change is the AIR check (an empty hand is AIR, not null,
// and NBTItem throws "ItemStack can't be null/Air!" for it)
public class DeathEventListener implements Listener {
    private final KillCounterPlugin plugin;

    public DeathEventListener(KillCounterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void killCounterIncrement(EntityDeathEvent event) {
        if (event.getEntity() == null) return;
        if (event.getEntity().getKiller() == null) return;
        if (!(event.getEntity() instanceof Player)) return;
        ItemStack item = event.getEntity().getKiller().getItemInHand();
        if (item == null || item.getType() == Material.AIR) return;
        NBTItem nbt = new NBTItem(item);
        if (!nbt.getBoolean("killCounterApplied")) return;
        if (!nbt.hasKey("killCount")) return;
        event.getEntity().getKiller().setItemInHand(KillCounterAPI.incrementKillsOnCounter(item, 1));
        event.getEntity().getKiller().playSound(event.getEntity().getKiller().getLocation(), Sound.ORB_PICKUP, 2.0F, 2.0F);
    }
}
