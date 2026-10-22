package net.astropvp.customblocks.struct.impl;

import lombok.Getter;
import net.astropvp.customblocks.CustomBlocks;
import net.astropvp.customblocks.guis.XPShopGui;
import net.astropvp.customblocks.struct.CustomBlock;
import net.astropvp.customblocks.struct.CustomBlockType;
import net.astropvp.customblocks.struct.abilities.ChunkAble;
import net.astropvp.customblocks.utils.ChunkUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class DarkMatter extends CustomBlock implements ChunkAble {
    // EXP collected from this chunk, deposited and withdrawn in its menu
    private long storedExp;

    public DarkMatter() {
        super(CustomBlockType.DARK_MATTER);
    }

    @Override
    public void onClick(Player clicker) {
        XPShopGui xpShopGui = new XPShopGui(clicker, this);
        xpShopGui.getExpShopUI().open(clicker);
    }

    public void addExp(long exp) {
        if (exp > 0) storedExp = Math.min(Long.MAX_VALUE - exp, storedExp) + exp;
    }

    /** takes up to max EXP out of the storage and returns how much was taken */
    public int takeExp(int max) {
        int taken = (int) Math.min(max, storedExp);
        storedExp -= taken;
        return taken;
    }

    /** the Dark Matter in the chunk of a location, or null */
    public static DarkMatter inChunkOf(Location location) {
        for (CustomBlock block : CustomBlocks.getInstance().getCustomBlockManager().getCacheValues()) {
            if (block instanceof DarkMatter && ChunkUtil.isLocationAInSameChunkAsLocationB(block.getLocation(), location)) {
                return (DarkMatter) block;
            }
        }
        return null;
    }
}
