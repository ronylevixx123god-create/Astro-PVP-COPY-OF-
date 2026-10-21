package net.astropvp.core.items;

import com.cosmicpvp.cosmicutils.itembuilder.ItemBuilder;
import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.cosmicpvp.cosmicutils.utils.NumberUtils;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;

import java.util.UUID;

/**
 * Astro's magical lava/water buckets. A bucket holds up to the uses it was created with: placing a source uses one,
 * picking a source of the same liquid back up refills one, and at 0 uses it stays a magical (empty) bucket.
 */
public abstract class MagicalBucket implements Listener {
    // set while asking other plugins (WorldGuard, Factions) whether a pick-up is allowed
    private static boolean checkingProtection = false;

    private final String id;
    private final String usesKey;
    private final String capacityKey;
    private final Material filledBucket;
    private final Material liquid;
    private final Material stationaryLiquid;

    protected MagicalBucket(String id, String usesKey, String capacityKey, Material filledBucket, Material liquid, Material stationaryLiquid) {
        this.id = id;
        this.usesKey = usesKey;
        this.capacityKey = capacityKey;
        this.filledBucket = filledBucket;
        this.liquid = liquid;
        this.stationaryLiquid = stationaryLiquid;
    }

    protected abstract ItemStack create(int uses, int capacity);

    protected static ItemStack create(String id, String usesKey, String capacityKey, Material filledBucket, String name, String color,
                                      int uses, int capacity) {
        ItemBuilder itemBuilder = new ItemBuilder(uses > 0 ? filledBucket : Material.BUCKET, 1, (short) 0);
        itemBuilder.setName(CC.translate(color + "&lMagical " + name + " Bucket&8 (&7" + NumberUtils.formatMoney(uses) + " uses&8)"));

        itemBuilder.addLoreLines(
                "",
                CC.translate("&7This " + name.toLowerCase() + " bucket is capable of"),
                CC.translate("&7refilling its self when used"),
                "",
                CC.translate(color + "* &7Usage: &fRight-Click on a block"));
        ItemStack itemStack = itemBuilder.build();
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);

        NBTWrapper wrapper = new NBTWrapper(itemStack);
        NBTTagCompoundBuilder builder = new NBTTagCompoundBuilder();
        builder.setString("randomUUID", String.valueOf(UUID.randomUUID()));
        builder.setString("extinctCustomItem", id);
        builder.setInt(usesKey, uses);
        builder.setInt(capacityKey, capacity);
        wrapper.setNBTBase("bucketData", builder.get());

        return wrapper.build();
    }

    private NBTTagCompoundBuilder getBucketData(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        NBTWrapper wrapper = new NBTWrapper(item);
        if (!wrapper.hasKey("bucketData")) return null;
        NBTTagCompoundBuilder compound = new NBTTagCompoundBuilder(wrapper.getNBTBase("bucketData"));
        return this.id.equalsIgnoreCase(compound.getString("extinctCustomItem")) ? compound : null;
    }

    // buckets made before the capacity was stored hold what they had left
    private int getCapacity(NBTTagCompoundBuilder compound) {
        return compound.hasKey(this.capacityKey) ? compound.getInt(this.capacityKey) : compound.getInt(this.usesKey);
    }

    private boolean isOwnSource(Block block) {
        return (block.getType() == this.liquid || block.getType() == this.stationaryLiquid) && block.getData() == 0;
    }

    private boolean canUseHere(Player player, Location location) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        Faction factionAt = Board.getInstance().getFactionAt(new FLocation(location));
        return factionAt.isWilderness() || factionAt == faction;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketPlace(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getItemInHand();
        NBTTagCompoundBuilder compound = getBucketData(hand);
        if (compound == null) return;
        event.setCancelled(true);

        Block target = event.getBlockClicked().getRelative(event.getBlockFace());
        int uses = compound.getInt(this.usesKey);
        if (uses <= 0 || target.getType().isSolid() || isOwnSource(target) || !canUseHere(player, target.getLocation())) {
            player.setItemInHand(hand);
            player.updateInventory();
            return;
        }
        target.setType(this.liquid);
        replaceOne(player, hand, create(uses - 1, getCapacity(compound)));
        player.updateInventory();
    }

    // an empty (0 uses) magical bucket picks up through the normal bucket fill
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (checkingProtection) return;
        Player player = event.getPlayer();
        ItemStack hand = player.getItemInHand();
        NBTTagCompoundBuilder compound = getBucketData(hand);
        if (compound == null) return;
        event.setCancelled(true);
        pickUp(player, hand, compound, event.getBlockClicked());
    }

    // a part-filled magical bucket picks up the source the player is looking at
    @EventHandler(priority = EventPriority.HIGH)
    public void onRefill(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() != this.filledBucket) return;
        NBTTagCompoundBuilder compound = getBucketData(hand);
        if (compound == null || compound.getInt(this.usesKey) >= getCapacity(compound)) return;
        Player player = event.getPlayer();
        Block source = getTargetSource(player);
        if (source == null) return;
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);

        PlayerBucketFillEvent check = new PlayerBucketFillEvent(player, source, BlockFace.UP, Material.BUCKET, hand);
        checkingProtection = true;
        try {
            Bukkit.getPluginManager().callEvent(check);
        } finally {
            checkingProtection = false;
        }
        if (check.isCancelled()) {
            player.updateInventory();
            return;
        }
        pickUp(player, hand, compound, source);
    }

    // the first block in reach that a bucket would hit; flowing liquid is passed through like vanilla
    private Block getTargetSource(Player player) {
        BlockIterator iterator = new BlockIterator(player, 5);
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.getType() == Material.AIR) continue;
            if (block.isLiquid() && block.getData() != 0) continue;
            return isOwnSource(block) ? block : null;
        }
        return null;
    }

    private void pickUp(Player player, ItemStack hand, NBTTagCompoundBuilder compound, Block source) {
        int uses = compound.getInt(this.usesKey);
        int capacity = getCapacity(compound);
        if (isOwnSource(source) && uses < capacity && canUseHere(player, source.getLocation())) {
            source.setType(Material.AIR);
            replaceOne(player, hand, create(uses + 1, capacity));
        }
        player.updateInventory();
    }

    // only the bucket that was used changes; the rest of a stack stays in hand
    private void replaceOne(Player player, ItemStack hand, ItemStack updated) {
        if (hand.getAmount() <= 1) {
            player.setItemInHand(updated);
            return;
        }
        hand.setAmount(hand.getAmount() - 1);
        player.setItemInHand(hand);
        for (ItemStack left : player.getInventory().addItem(updated).values())
            player.getWorld().dropItemNaturally(player.getLocation(), left);
    }
}
