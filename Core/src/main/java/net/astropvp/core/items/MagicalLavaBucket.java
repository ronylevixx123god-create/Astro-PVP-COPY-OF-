package net.astropvp.core.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MagicalLavaBucket extends MagicalBucket {

    public MagicalLavaBucket() {
        super("magicalLavaBucket", "lavaBucketUses", "lavaBucketCapacity", Material.LAVA_BUCKET, Material.LAVA, Material.STATIONARY_LAVA);
    }

    public static ItemStack createMagicalLavaBucket(int lavaBucketUses) {
        return createMagicalLavaBucket(lavaBucketUses, lavaBucketUses);
    }

    public static ItemStack createMagicalLavaBucket(int lavaBucketUses, int capacity) {
        return create("magicalLavaBucket", "lavaBucketUses", "lavaBucketCapacity", Material.LAVA_BUCKET, "Lava", "&c", lavaBucketUses, capacity);
    }

    @Override
    protected ItemStack create(int uses, int capacity) {
        return createMagicalLavaBucket(uses, capacity);
    }
}
