package net.astropvp.customblocks.listeners;

import net.astropvp.customblocks.struct.impl.DarkMatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/** EXP dropped in a chunk with a Dark Matter goes into its storage instead of onto the ground */
public class DarkMatterExpListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event) {
        if (event.getDroppedExp() <= 0) return;
        DarkMatter darkMatter = DarkMatter.inChunkOf(event.getEntity().getLocation());
        if (darkMatter == null) return;
        darkMatter.addExp(event.getDroppedExp());
        event.setDroppedExp(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getExpToDrop() <= 0) return;
        DarkMatter darkMatter = DarkMatter.inChunkOf(event.getBlock().getLocation());
        if (darkMatter == null) return;
        darkMatter.addExp(event.getExpToDrop());
        event.setExpToDrop(0);
    }
}
