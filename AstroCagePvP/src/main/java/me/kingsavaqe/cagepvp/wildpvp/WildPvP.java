package me.kingsavaqe.cagepvp.wildpvp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.kingsavaqe.cagepvp.AstroCagePvP;
import me.kingsavaqe.cagepvp.utils.TitleManager;
import org.arkhamnetwork.Arkkit.patches.combatlog.CombatLog;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class WildPvP {
   private final AstroCagePvP instance;
   private final Player creator;
   private Player joined;
   private final Location location;
   private Location joinLocation;
   private boolean grace = true;
   private boolean ended = false;
   /** Non-solid blocks the cage may replace (air, plants, snow layers, liquids), so the walls have no gaps; solid
    *  terrain and player-built blocks stay and are part of the wall. */
   private static final Set<Material> CAGE_REPLACEABLE = EnumSet.of(Material.AIR, Material.LONG_GRASS, Material.DOUBLE_PLANT,
         Material.YELLOW_FLOWER, Material.RED_ROSE, Material.DEAD_BUSH, Material.SNOW, Material.VINE,
         Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.WATER, Material.STATIONARY_WATER, Material.LAVA,
         Material.STATIONARY_LAVA, Material.SUGAR_CANE_BLOCK, Material.WEB, Material.FIRE);
   /** Light gray stained glass (data 8): the gray glass Astro's own cage code used (AstroCagePvP showBorder). */
   public static final Material CAGE_MATERIAL = Material.STAINED_GLASS;
   public static final byte CAGE_DATA = 8;
   /** Every block the cage replaced with its previous state, so cleanup puts exactly that block back. */
   private final List<org.bukkit.block.BlockState> cageBlocks = new ArrayList<>();

   public WildPvP(AstroCagePvP instance, Player creator, Location location) {
      this.instance = instance;
      this.creator = creator;
      this.location = location;
   }

   public Player getCreator() {
      return this.creator;
   }

   public boolean isCreator(Player player) {
      return this.creator.getUniqueId().equals(player.getUniqueId());
   }

   public void setJoined(Player joined) {
      this.joined = joined;
   }

   public Player getJoined() {
      return this.joined;
   }

   public boolean isJoined(Player player) {
      return this.joined != null && this.joined.getUniqueId().equals(player.getUniqueId());
   }

   public boolean hasJoined() {
      return this.joined != null;
   }

   public boolean isParticipant(Player player) {
      return this.creator.getUniqueId().equals(player.getUniqueId()) || this.joined != null && this.joined.getUniqueId().equals(player.getUniqueId());
   }

   public Location getLocation() {
      return this.location;
   }

   public void setJoinLocation(Location joinLocation) {
      this.joinLocation = joinLocation;
   }

   public Location getJoinLocation() {
      return this.joinLocation;
   }

   public void teleportToJoinLocation(Player player) {
      Location location = this.joinLocation.clone();
      if (location.getBlock().getType().isSolid() || location.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid()) {
         location.setY((double)location.getWorld().getHighestBlockYAt(location));
      }

      player.teleport(location, TeleportCause.UNKNOWN);
   }

   public Chunk getChunk() {
      return this.location.getChunk();
   }

   public boolean isGrace() {
      return this.grace;
   }

   public void setEnded(boolean ended) {
      this.ended = ended;
   }

   public boolean hasEnded() {
      return this.ended;
   }

   public boolean hasStarted() {
      return this.creator != null && this.joined != null;
   }


   public void showBorder(Player player) {


      Chunk chunk = this.getChunk();
      Location corner1 = chunk.getBlock(0, 0, 0).getLocation();
      Location corner3 = chunk.getBlock(15, 0, 15).getLocation();
      // Calculate center and size based on corners
      ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

      double centerX = (corner1.getX() + corner3.getX()) / 2;
      double centerZ = (corner1.getZ() + corner3.getZ()) / 2;
      double size = Math.max(Math.abs(corner1.getX() - corner3.getX()), Math.abs(corner1.getZ() - corner3.getZ()));

      PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
      packet.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.INITIALIZE);  // Set action to initialize the border
      packet.getDoubles().write(0, centerX);                                   // Center X
      packet.getDoubles().write(1, centerZ);                                   // Center Z
      packet.getDoubles().write(2, size);                                      // Border size
      packet.getLongs().write(0, 0L);                                           // Delay to resize
      packet.getDoubles().write(3, size);                                      // Target size for animation
      packet.getIntegers().write(0, 0);                                        // Warning time in seconds
      packet.getIntegers().write(1, 0);                                        // Warning distance in blocks

      // Send the packet to the player
      try {
         protocolManager.sendServerPacket(player, packet);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void hideBorder(Player player) {
      ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

      PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
      packet.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.INITIALIZE); // Correctly set the action
      packet.getDoubles().write(0, player.getLocation().getX()); // Center X
      packet.getDoubles().write(1, player.getLocation().getZ()); // Center Z
      packet.getDoubles().write(2, 60000000D);                    // Large size
      packet.getIntegers().write(0, 0);                          // Warning time
      packet.getIntegers().write(1, 0);                          // Warning blocks
      packet.getLongs().write(0, Long.MAX_VALUE);             // Delay time

      try {
         protocolManager.sendServerPacket(player, packet);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /*
   public void showBorder(Player player) {
      Chunk chunk = this.getChunk();
      Location corner1 = chunk.getBlock(0, 0, 0).getLocation();
      Location corner2 = chunk.getBlock(15, 0, 0).getLocation();
      Location corner3 = chunk.getBlock(0, 0, 15).getLocation();
      Location corner4 = chunk.getBlock(15, 0, 15).getLocation();

      for(int y = 0; y < 130; ++y) {
         for(int xz = 0; xz < 15; ++xz) {
            corner1 = chunk.getBlock(xz, y, 0).getLocation();
            corner2 = chunk.getBlock(15, y, xz).getLocation();
            corner3 = chunk.getBlock(15 - xz, y, 15).getLocation();
            corner4 = chunk.getBlock(0, y, 15 - xz).getLocation();
            if (corner1.getBlock().getType() == Material.AIR) {
               //player.sendBlockChange(corner1, Material.STAINED_GLASS, (byte)14);
               player.sendBlockChange(corner1, Material.STAINED_GLASS, (byte)8);

            }

            if (corner2.getBlock().getType() == Material.AIR) {
               //player.sendBlockChange(corner2, Material.STAINED_GLASS, (byte)14);
               player.sendBlockChange(corner2, Material.STAINED_GLASS, (byte)8);

            }

            if (corner3.getBlock().getType() == Material.AIR) {
               //player.sendBlockChange(corner3, Material.STAINED_GLASS, (byte)14);
               player.sendBlockChange(corner3, Material.STAINED_GLASS, (byte)8);

            }

            if (corner4.getBlock().getType() == Material.AIR) {
               //player.sendBlockChange(corner4, Material.STAINED_GLASS, (byte)14);
               player.sendBlockChange(corner4, Material.STAINED_GLASS, (byte)8);

            }
         }
      }

   }

    */

   /*
   public void hideBorder(Player player) {
      Chunk chunk = this.getChunk();
      Location corner1 = chunk.getBlock(0, 0, 0).getLocation();
      Location corner2 = chunk.getBlock(15, 0, 0).getLocation();
      Location corner3 = chunk.getBlock(0, 0, 15).getLocation();
      Location corner4 = chunk.getBlock(15, 0, 15).getLocation();

      for(int y = 0; y < 130; ++y) {
         for(int xz = 0; xz < 15; ++xz) {
            corner1 = chunk.getBlock(xz, y, 0).getLocation();
            corner2 = chunk.getBlock(15, y, xz).getLocation();
            corner3 = chunk.getBlock(15 - xz, y, 15).getLocation();
            corner4 = chunk.getBlock(0, y, 15 - xz).getLocation();
            if (corner1.getBlock().getType() == Material.AIR) {
               player.sendBlockChange(corner1, Material.AIR, (byte)0);
            }

            if (corner2.getBlock().getType() == Material.AIR) {
               player.sendBlockChange(corner2, Material.AIR, (byte)0);
            }

            if (corner3.getBlock().getType() == Material.AIR) {
               player.sendBlockChange(corner3, Material.AIR, (byte)0);
            }

            if (corner4.getBlock().getType() == Material.AIR) {
               player.sendBlockChange(corner4, Material.AIR, (byte)0);
            }
         }
      }

   }

    */

   /** Light gray glass on the four edges of the match chunk, from y=1 to the top of the world: the cage is the 16x16
    *  chunk and the usable inside is the 14x14 blocks between the walls. */
   @SuppressWarnings("deprecation")
   public void buildCage() {
      Chunk chunk = this.getChunk();
      int maxY = chunk.getWorld().getMaxHeight() - 1;
      for (int x = 0; x < 16; ++x) {
         for (int z = 0; z < 16; ++z) {
            if (x != 0 && x != 15 && z != 0 && z != 15) continue;
            for (int y = 1; y <= maxY; ++y) {
               org.bukkit.block.Block block = chunk.getBlock(x, y, z);
               if (!CAGE_REPLACEABLE.contains(block.getType())) continue;
               this.cageBlocks.add(block.getState());
               // no physics: neighbouring water must not flow into, or pop, anything while the cage goes up
               block.setTypeIdAndData(CAGE_MATERIAL.getId(), CAGE_DATA, false);
            }
         }
      }
   }

   /** Puts back exactly the blocks the cage replaced (type and data), only where the cage glass is still there. */
   @SuppressWarnings("deprecation")
   public void removeCage() {
      for (int i = this.cageBlocks.size() - 1; i >= 0; --i) {
         org.bukkit.block.BlockState previous = this.cageBlocks.get(i);
         org.bukkit.block.Block block = previous.getBlock();
         if (block.getType() == CAGE_MATERIAL && block.getData() == CAGE_DATA) {
            previous.update(true, false);
         }
      }
      this.cageBlocks.clear();
   }

   public boolean isInsideChunk(Location location) {
      Chunk chunk = this.getChunk();
      return location.getWorld().equals(chunk.getWorld()) && location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ();
   }

   private static boolean passable(org.bukkit.block.Block block) {
      return !block.getType().isSolid() && block.getType() != Material.LAVA && block.getType() != Material.STATIONARY_LAVA;
   }

   /**
    * A safe spot inside the walls near the given location: x/z kept 1.3 blocks clear of the chunk edges (the wall
    * columns), and a y with two free blocks for the body that is not above the cage walls, so the player can't be
    * put inside a wall, inside the ground or on top of the cage.
    */
   public Location safeInside(Location near) {
      Chunk chunk = this.getChunk();
      double minX = (chunk.getX() << 4) + 1.3D, maxX = (chunk.getX() << 4) + 14.7D;
      double minZ = (chunk.getZ() << 4) + 1.3D, maxZ = (chunk.getZ() << 4) + 14.7D;
      Location location = near.clone();
      location.setWorld(chunk.getWorld());
      location.setX(Math.max(minX, Math.min(maxX, location.getX())));
      location.setZ(Math.max(minZ, Math.min(maxZ, location.getZ())));
      int top = chunk.getWorld().getMaxHeight() - 2;
      int startY = Math.max(1, Math.min(top, location.getBlockY()));
      for (int offset = 0; offset <= top; ++offset) {
         for (int y : new int[]{startY - offset, startY + offset}) {
            if (y < 1 || y > top) continue;
            Location candidate = location.clone();
            candidate.setY(y);
            if (passable(candidate.getBlock()) && passable(candidate.clone().add(0.0D, 1.0D, 0.0D).getBlock())) {
               return candidate;
            }
         }
      }
      location.setY(location.getWorld().getHighestBlockYAt(location));
      return location;
   }

   public void teleport(Player player) {
      Random random = new Random();
      Location location = this.getChunk().getBlock(random.nextInt(14) + 1, 0, random.nextInt(14) + 1).getLocation().clone();
      location.setY((double)location.getWorld().getHighestBlockYAt(location));
      player.teleport(location, TeleportCause.UNKNOWN);
   }

   public void startMatch() {
      if (this.joined != null) {
         final FileConfiguration configLoad = this.instance.getConfig();
         this.creator.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Joined.Creator.Message").replace("%player", this.joined.getName())));
         this.creator.playSound(this.creator.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 5.0F);
         if (configLoad.getBoolean("WildPvP.Match.Title")) {
            TitleManager.sendTitle(this.creator, ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Joined.Creator.Title.Title").replace("%player", this.joined.getName())), ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Joined.Creator.Title.SubTitle").replace("%player", this.joined.getName())), 0, 30, 10);
         }

         this.joined.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Joined.Joiner.Message").replace("%player", this.creator.getName())));
         this.joined.playSound(this.joined.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 5.0F);
         (new BukkitRunnable() {
            int countdown = 10;

            public void run() {
               if (!WildPvP.this.ended && this.countdown >= 0) {
                  if (this.countdown == 8) {
                     TitleManager.sendTitle(WildPvP.this.creator, ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Versus.Title.Title").replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName())), ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Versus.Title.SubTitle").replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName())), 10, 30, 10);
                     TitleManager.sendTitle(WildPvP.this.joined, ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Versus.Title.Title").replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName())), ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Versus.Title.SubTitle").replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName())), 10, 30, 10);
                     WildPvP.this.creator.playSound(WildPvP.this.creator.getLocation(), Sound.VILLAGER_YES, 0.5F, 1.0F);
                     WildPvP.this.joined.playSound(WildPvP.this.joined.getLocation(), Sound.VILLAGER_YES, 0.5F, 1.0F);
                  } else if (this.countdown > 0 && this.countdown <= 5) {
                     WildPvP.this.creator.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ending.Message").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.joined.getName())));
                     WildPvP.this.joined.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ending.Message").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.creator.getName())));
                     TitleManager.sendTitle(WildPvP.this.creator, ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ending.Title.Title").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.joined.getName())), ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ending.Title.SubTitle").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.joined.getName())), 0, 30, 0);
                     TitleManager.sendTitle(WildPvP.this.joined, ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ending.Title.Title").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.creator.getName())), ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ending.Title.SubTitle").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.creator.getName())), 0, 30, 0);
                     WildPvP.this.creator.playSound(WildPvP.this.creator.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                     WildPvP.this.joined.playSound(WildPvP.this.joined.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                  } else if (this.countdown == 0) {
                     WildPvP.this.grace = false;
                     this.cancel();
                     WildPvP.this.creator.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ended.Message").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.joined.getName())));
                     WildPvP.this.joined.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ended.Message").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.creator.getName())));
                     TitleManager.sendTitle(WildPvP.this.creator, ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ended.Title.Title").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.joined.getName())), ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ended.Title.SubTitle").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.joined.getName())), 0, 30, 10);
                     TitleManager.sendTitle(WildPvP.this.joined, ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ended.Title.Title").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.creator.getName())), ChatColor.translateAlternateColorCodes('&', configLoad.getString("WildPvP.Countdown.Grace.Ended.Title.SubTitle").replace("%countdown", String.valueOf(this.countdown)).replace("%creator", WildPvP.this.creator.getName()).replace("%joined", WildPvP.this.joined.getName()).replace("%versus", WildPvP.this.creator.getName())), 0, 30, 10);
                     WildPvP.this.creator.playSound(WildPvP.this.creator.getLocation(), Sound.WITHER_SHOOT, 1.0F, 10.0F);
                     WildPvP.this.joined.playSound(WildPvP.this.joined.getLocation(), Sound.WITHER_SHOOT, 1.0F, 10.0F);
                     CombatLog.flagCombat(WildPvP.this.creator);
                     CombatLog.flagCombat(WildPvP.this.joined);

                  }

                  --this.countdown;
               } else {
                  this.cancel();
               }
            }
         }).runTaskTimerAsynchronously(this.instance, 0L, 20L);
      }
   }
}
