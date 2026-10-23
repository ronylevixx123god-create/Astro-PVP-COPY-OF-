package me.cosmicpvp.CosmicGlobalKits.struct;

import java.util.List;
import me.cosmicpvp.CosmicGlobalKits.libs.MasteryKits;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class MasteryGlobalKit {
    public abstract List<ItemStack> getMasteryItems(Player var1, MasteryKits var2, boolean var3);
}

