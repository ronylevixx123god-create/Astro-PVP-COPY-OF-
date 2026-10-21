package net.astropvp.core.entities;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.stream.IntStream;

/**
 * Ender pearl that flies through cobwebs and string, then teleports the thrower like a normal pearl.
 */
public class EscapistPearlEntity extends EntityEnderPearl {
    private boolean passedThroughSlabOrStair = false;
    private boolean passedThroughFence = false;

    public EscapistPearlEntity(Player player) {
        super(((CraftPlayer) player).getHandle().world, ((CraftPlayer) player).getHandle());
    }

    @Override
    protected void a(MovingObjectPosition movingObjectPosition) {
        try {
            if (movingObjectPosition == null || movingObjectPosition.a() == null) return;
            Block block = this.world.getType(new BlockPosition(movingObjectPosition.a())).getBlock();
            if (block == Blocks.TRIPWIRE || block == Blocks.WEB) return;
            if (getShooter() == null || !(getShooter() instanceof EntityPlayer)) {
                this.dead = true;
                return;
            }
            EntityPlayer entityPlayer = (EntityPlayer) getShooter();
            if (!entityPlayer.playerConnection.isDisconnected() && entityPlayer.world == this.world) {
                CraftPlayer player = entityPlayer.getBukkitEntity();
                Location location = getBukkitEntity().getLocation();
                location.setPitch(player.getLocation().getPitch());
                location.setYaw(player.getLocation().getYaw());
                Location checkable = getCheckableLocation(location);
                if (checkable.add(player.getEyeLocation().getDirection().multiply(1.5)).getBlock().getType().isSolid()
                        && this.passedThroughSlabOrStair && location.getBlock().getType().isSolid()) {
                    if (getClosestSafeLocation(location) == null) {
                        this.dead = true;
                        return;
                    }
                    location = getClosestSafeLocation(location);
                }
                checkable = getCheckableLocation(location);
                if (location.getBlock().getType().isSolid() && !checkable.add(0.0, 1.0, 0.0).getBlock().getType().isSolid())
                    location.setY(location.getY() + 1.0);
                if (location.getBlock().getType().isSolid()) {
                    this.dead = true;
                    return;
                }
                PlayerTeleportEvent teleportEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                Bukkit.getPluginManager().callEvent(teleportEvent);
                if (!teleportEvent.isCancelled() && !entityPlayer.playerConnection.isDisconnected()) {
                    entityPlayer.playerConnection.teleport(teleportEvent.getTo());
                    IntStream.range(0, 32).forEach(i -> this.world.addParticle(EnumParticle.PORTAL, this.locX,
                            this.locY + this.random.nextDouble() * 2.0, this.locZ, this.random.nextGaussian(), 0.0, this.random.nextGaussian()));
                    getShooter().fallDistance = 0.0F;
                    CraftEventFactory.entityDamage = this;
                    getShooter().damageEntity(DamageSource.FALL, 5.0F);
                    CraftEventFactory.entityDamage = null;
                }
            }
            this.dead = true;
        } catch (Exception e) {
            this.dead = true;
        }
    }

    private Location getClosestSafeLocation(Location location) {
        for (int i = 0; i < 3; i++) {
            if (!location.add(location.getDirection().multiply(i)).getBlock().getType().isSolid())
                return location.add(location.getDirection().multiply(1));
        }
        return null;
    }

    private Location getCheckableLocation(Location location) {
        Location checkable = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        checkable.setDirection(location.getDirection());
        return checkable;
    }
}
