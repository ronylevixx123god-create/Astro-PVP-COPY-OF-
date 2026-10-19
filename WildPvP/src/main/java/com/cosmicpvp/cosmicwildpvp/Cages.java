package com.cosmicpvp.cosmicwildpvp;

import com.cosmicpvp.cosmicwildpvp.util.BlockModifier;
import com.cosmicpvp.cosmicwildpvp.util.BryUtil;
import com.google.common.collect.Maps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Cages
{
    /** How long a chunk stays unclaimable after its cage is removed. */
    public static final long NO_CLAIM_AFTER_MILLIS = 90000L;
    /** Natural non-solid blocks a cage may replace; solid terrain and player-built blocks are left alone. */
    private static final Set<Material> REPLACEABLE = EnumSet.of(Material.AIR, Material.LONG_GRASS, Material.DOUBLE_PLANT,
            Material.YELLOW_FLOWER, Material.RED_ROSE, Material.DEAD_BUSH, Material.SNOW, Material.VINE,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM);

    /** "world:chunkX:chunkZ" -> no-claim expiry; Long.MAX_VALUE while the cage is up. Keyed by coordinates because a
     *  Chunk object changes when the chunk unloads and loads again. */
    public static Map<String, Long> noClaimUntil;
    public BlockModifier blockModifier;
    private final Map<String, Chunk> cageChunks;
    private final Map<String, BukkitTask> despawnTasks;
    private WildPvPPlugin plugin;

    public Cages(final WildPvPPlugin plugin) {
        this.plugin = plugin;
        this.blockModifier = new BlockModifier();
        this.cageChunks = Maps.newHashMap();
        this.despawnTasks = Maps.newHashMap();
    }

    public static String key(final Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    /** Obsidian floor 1 below the feet and ceiling 10 above, across the whole chunk; iron bars on all four edges in between. */
    public void spawnCage(final Chunk chunk, final int y) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                this.place(chunk, chunk.getBlock(x, y - 1, z), Material.OBSIDIAN);
                this.place(chunk, chunk.getBlock(x, y + 10, z), Material.OBSIDIAN);
            }
        }
        BryUtil.getChunkBorderLocations(chunk, y, 9).forEach(fence -> this.place(chunk, fence.getBlock(), Material.IRON_FENCE));
        this.cageChunks.put(key(chunk), chunk);
        Cages.noClaimUntil.put(key(chunk), Long.MAX_VALUE);
    }

    private void place(final Chunk chunk, final Block block, final Material material) {
        final Location location = block.getLocation();
        if (this.blockModifier.containsLocation(chunk, location) || !REPLACEABLE.contains(block.getType())) {
            return;
        }
        this.blockModifier.storeBlockToRemove(chunk, location);
        block.setType(material);
    }

    public boolean hasCage(final Chunk chunk) {
        return this.cageChunks.containsKey(key(chunk));
    }

    public static boolean isChunkProtected(final Chunk chunk) {
        final Long expire = Cages.noClaimUntil.get(key(chunk));
        return expire != null && expire > System.currentTimeMillis();
    }

    /** Keeps the chunk unclaimable at least until the given time (never shortens it). */
    public static void protectUntil(final Chunk chunk, final long until) {
        Cages.noClaimUntil.merge(key(chunk), until, Math::max);
    }

    public void scheduleDespawn(final Chunk chunk, final int seconds) {
        this.cancelScheduledDespawn(chunk);
        this.despawnTasks.put(key(chunk), Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.despawnCage(chunk), 20L * seconds));
    }

    public void cancelScheduledDespawn(final Chunk chunk) {
        final BukkitTask task = this.despawnTasks.remove(key(chunk));
        if (task != null) {
            task.cancel();
        }
    }

    /** Puts air back where the cage placed blocks; the chunk stays unclaimable for another 90 seconds. */
    public void despawnCage(final Chunk chunk) {
        this.cancelScheduledDespawn(chunk);
        final Chunk cageChunk = this.cageChunks.remove(key(chunk));
        if (cageChunk == null) {
            return;
        }
        if (this.blockModifier.containsChunk(cageChunk)) {
            this.blockModifier.removeBlocks(cageChunk, true, true);
        }
        Cages.noClaimUntil.put(key(chunk), System.currentTimeMillis() + NO_CLAIM_AFTER_MILLIS);
    }

    public void despawnAll() {
        new ArrayList<>(this.cageChunks.values()).forEach(this::despawnCage);
    }

    static {
        Cages.noClaimUntil = Maps.newHashMap();
    }
}
