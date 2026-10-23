package me.kingsavaqe.cagepvp.listeners;

import me.kingsavaqe.cagepvp.AstroCagePvP;
import me.kingsavaqe.cagepvp.wildpvp.WildPvP;
import me.kingsavaqe.cagepvp.wildpvp.WildPvPManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Move implements Listener {
   private final AstroCagePvP instance;

   public Move(AstroCagePvP instance) {
      this.instance = instance;
   }

   /**
    * Astro's boundary behaviour: a participant who leaves the match chunk (by walking, falling, flying over the top of
    * the walls, or any other way) is moved 2 blocks back the way they came. That spot is clamped inside the walls and
    * checked for two free blocks, so nobody is put in a wall, in the ground or on top of the cage, and a second move
    * never repeats the teleport. Ended matches are skipped so the match-end, death and disconnect teleports work.
    */
   @EventHandler
   public void onPlayerMoveOutsideChunk(PlayerMoveEvent event) {
      Player player = event.getPlayer();
      Location fromLocation = event.getFrom();
      Location toLocation = event.getTo();
      WildPvPManager wildPvPManager = this.instance.getWildPvPManager();
      if (wildPvPManager.hasWildPvP(player)) {
         if (player.isOp()) return;
         WildPvP wildPvP = wildPvPManager.getWildPvP(player);
         if (wildPvP.hasEnded() || wildPvP.isInsideChunk(toLocation)) return;
         Location back = fromLocation.clone();
         org.bukkit.util.Vector away = fromLocation.toVector().subtract(toLocation.toVector()).setY(0);
         if (away.lengthSquared() > 1.0E-6D) {
            back.add(away.normalize().multiply(2.0D));
         }
         Location location = wildPvP.safeInside(back);
         location.setYaw(toLocation.getYaw());
         location.setPitch(toLocation.getPitch());
         event.setTo(location);
         player.setFallDistance(0.0F);
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.instance.getConfig().getString("Listeners.Move.Chunk.Participant.Message")));
         player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 5.0F);
      }
   }

   @EventHandler
   public void onPlayerMoveInsideChunk(PlayerMoveEvent event) {
      Player player = event.getPlayer();
      if (player.getGameMode() != GameMode.SPECTATOR) {
         Location fromLocation = event.getFrom();
         Location toLocation = event.getTo();
         WildPvPManager wildPvPManager = this.instance.getWildPvPManager();
         if (wildPvPManager.hasWildPvP(toLocation.getChunk())) {
            if (player.isOp()) return;
            if (player.hasPermission("essentials.tp.others")) return;

            WildPvP wildPvP = wildPvPManager.getWildPvP(toLocation.getChunk());
            if (!wildPvP.isParticipant(player)) {
               Location location = player.getLocation().clone().add(fromLocation.toVector().subtract(toLocation.toVector()).normalize().multiply(2.0D));
               if (location.getBlock().getType().isSolid()) {
                  location.setY(location.getWorld().getHighestBlockYAt(location));
               }

               player.teleport(location, TeleportCause.UNKNOWN);
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.instance.getConfig().getString("Listeners.Move.Chunk.Match.Message")));
               player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 5.0F);
            }
         }
      }
   }
}
