package net.astropvp.customblocks.tasks;

import com.conaxgames.customskulls.CoreSkulls;
import com.conaxgames.customskulls.ability.AbilityData;
import com.conaxgames.customskulls.blocks.CustomBlock;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** the placed CustomSkullBlocks blocks and their type names; only loaded when CustomSkullBlocks is enabled */
final class SkullBlockLocations {

    private SkullBlockLocations() {
    }

    static Map<Location, String> all() {
        Map<Location, String> blocks = new HashMap<>();
        for (AbilityData data : new ArrayList<>(AbilityData.getAbilityDataMap().values())) {
            if (data == null || data.getAbilityType() == null || data.getLocation() == null) continue;
            if (CoreSkulls.isRunByAstroCustomBlocks(data.getAbilityType().getName())) continue;
            CustomBlock block = CoreSkulls.customBlocks.get(data.getAbilityType().getName());
            if (block != null) blocks.put(data.getLocation().getBlock().getLocation(), block.getIdentifier());
        }
        return blocks;
    }
}
