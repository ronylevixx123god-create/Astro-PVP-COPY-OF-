package net.astropvp.core.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MagicalWaterBucket extends MagicalBucket {

    public MagicalWaterBucket() {
        super("magicalWaterBucket", "waterBucketUses", "waterBucketCapacity", Material.WATER_BUCKET, Material.WATER, Material.STATIONARY_WATER);
    }

    public static ItemStack createMagicalWaterBucket(int waterBucketUses) {
        return createMagicalWaterBucket(waterBucketUses, waterBucketUses);
    }

    public static ItemStack createMagicalWaterBucket(int waterBucketUses, int capacity) {
        return create("magicalWaterBucket", "waterBucketUses", "waterBucketCapacity", Material.WATER_BUCKET, "Water", "&b", waterBucketUses, capacity);
    }

    @Override
    protected ItemStack create(int uses, int capacity) {
        return createMagicalWaterBucket(uses, capacity);
    }
}
