package net.astropvp.customblocks.tasks;

import net.astropvp.customblocks.CustomBlocks;
import net.astropvp.customblocks.struct.CustomBlock;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;


import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Sends each player the note block tile-entity data with customBlockData{type, facing} that the Cosmic Client draws a
 * custom block from, for every custom block (AstroCustomBlocks and CustomSkullBlocks) in their view.
 * The ProtocolLib listeners that were meant to add this to the server's own packet never run here: ProtocolLib 4.5.1
 * registers TILE_ENTITY_DATA under packet id 10, which is PacketPlayOutBed on 1.8.
 * A block is sent once a player has it in view (after the server's own packet), and again after it left their view,
 * a teleport, a world change, a respawn or a rejoin.
 */
public class CustomBlockVisuals implements Runnable, Listener {
    private final Map<UUID, Set<String>> sent = new HashMap<>();

    /** location -> type name the client knows the block by */
    private static Map<Location, String> allBlocks() {
        Map<Location, String> blocks = new HashMap<>();
        for (CustomBlock block : CustomBlocks.getInstance().getCustomBlockManager().getCacheValues()) {
            if (block.getLocation() != null) blocks.put(block.getLocation(), block.getType().name());
        }
        if (Bukkit.getPluginManager().isPluginEnabled("CustomSkullBlocks")) {
            blocks.putAll(SkullBlockLocations.all());
        }
        return blocks;
    }

    @Override
    public void run() {
        Map<Location, String> blocks = allBlocks();
        double range = (Bukkit.getViewDistance() * 16) + 8;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<String> mine = sent.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
            Set<String> present = new HashSet<>();
            for (Map.Entry<Location, String> entry : blocks.entrySet()) {
                Location at = entry.getKey();
                if (at.getWorld() == null || !at.getWorld().equals(player.getWorld())) continue;
                if (Math.abs(at.getX() - player.getLocation().getX()) > range || Math.abs(at.getZ() - player.getLocation().getZ()) > range) continue;
                String key = at.getWorld().getName() + ":" + at.getBlockX() + ":" + at.getBlockY() + ":" + at.getBlockZ() + ":" + entry.getValue();
                present.add(key);
                if (!mine.contains(key) && at.getBlock().getType() == Material.NOTE_BLOCK) {
                    send(player, at, entry.getValue());
                    mine.add(key);
                }
            }
            // out of view (or removed): sent again when it comes back
            mine.retainAll(present);
        }
    }

    private static void send(Player player, Location at, String type) {
        BlockPosition pos = new BlockPosition(at.getBlockX(), at.getBlockY(), at.getBlockZ());
        NBTTagCompound compound = new NBTTagCompound();
        TileEntity tileEntity = ((CraftWorld) at.getWorld()).getHandle().getTileEntity(pos);
        if (tileEntity != null) {
            tileEntity.b(compound);
        } else {
            compound.setString("id", "Music");
            compound.setInt("x", pos.getX());
            compound.setInt("y", pos.getY());
            compound.setInt("z", pos.getZ());
            compound.setByte("note", (byte) 0);
        }
        NBTTagCompound insertTag = new NBTTagCompound();
        NBTTagCompound customData = new NBTTagCompound();
        insertTag.set("customBlockData", customData);
        customData.setString("type", type);
        customData.setString("facing", EnumDirection.fromAngle(player.getLocation().getYaw()).opposite().name());
        compound.set("tag", insertTag);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTileEntityData(pos, 10, compound));
    }

    private void forget(Player player) {
        sent.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        forget(event.getPlayer());
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        forget(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        forget(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        forget(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        forget(event.getPlayer());
    }
}
