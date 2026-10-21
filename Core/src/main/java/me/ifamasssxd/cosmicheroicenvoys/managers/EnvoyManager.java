package me.ifamasssxd.cosmicheroicenvoys.managers;

import com.cosmicpvp.CosmicSpaceChests.libs.ItemRarity;
import com.cosmicpvp.CosmicSpaceChests.utils.MiscUtil;
import me.ifamasssxd.cosmicbosses.struct.Boss;
import me.ifamasssxd.cosmicbosses.struct.enums.BossType;
import me.ifamasssxd.cosmicheroicenvoys.utils.LocationUtils;
import me.ifamasssxd.cosmicheroicenvoys.utils.MiscUtils;
import net.astropvp.core.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

/**
 * The Heroic Envoy Boss event, restored from Astro's older Core-0.1.jar (January 2021); final Astro turned it off.
 * A Plague Bloater spawns at "bossSpawn" every spawnTimeInSeconds (+/- randomDelay) with countdown broadcasts, or
 * right away with /spawnheroicboss. When it dies, heroic envoy chests fall around it (CosmicSpaceChests turns them
 * into loot chests) and faction crystals drop (EnvoyListener).
 */
public class EnvoyManager {
    private Location bossLocation;
    private int spawnTimeInSeconds = 0;
    private int randomDelay = 0;
    private int spawnTimer = 0;
    private final Random random = new Random();
    private UUID bossUUID;
    private BukkitRunnable task;
    public long lastBossSpawn = System.currentTimeMillis();

    public void startTimer() {
        this.bossLocation = LocationUtils.getLocation(AstroCore.getInstance().getConfig().getString("bossSpawn"));
        this.randomDelay = AstroCore.getInstance().getConfig().getInt("randomDelay");
        this.spawnTimeInSeconds = AstroCore.getInstance().getConfig().getInt("spawnTimeInSeconds", 14400);
        this.startEnvoyTimer();
    }

    private ItemRarity getRandomItemRarity() {
        int x = this.random.nextInt(1000);
        if (x <= 500) {
            return ItemRarity.ELITE;
        }
        if (x <= 750) {
            return ItemRarity.ULTIMATE;
        }
        if (x <= 950) {
            return ItemRarity.LEGENDARY;
        }
        return ItemRarity.ELITE;
    }

    public void spawnBoss() {
        this.lastBossSpawn = System.currentTimeMillis();
        Boss boss = BossType.PLAGUE_BLOATER.createInstance(this.bossLocation);
        boss.onBossSpawn();
        LivingEntity entity = boss.getEntity();
        entity.setMetadata("heroicEnvoyBoss", new FixedMetadataValue(AstroCore.getInstance(), ""));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "** HEROIC ENVOY BOSS SPAWNED AT BATTLEFIELD! **");
        Bukkit.broadcastMessage(ChatColor.GRAY + "Kill the boss at " + ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "/warp battlefield" + ChatColor.GRAY + " to spawn a Heroic Envoy and unlock " + ChatColor.GRAY + ChatColor.UNDERLINE + "faction crystals" + ChatColor.GRAY + "!");
        Bukkit.broadcastMessage("");
        this.bossUUID = boss.getUuid();
    }

    public void startEnvoyTimer() {
        // the old code called nextInt(randomDelay), which throws when randomDelay is 0 or missing
        int spread = this.randomDelay > 0 ? this.random.nextInt(this.randomDelay) : 0;
        this.spawnTimer = this.spawnTimeInSeconds + (this.random.nextBoolean() ? -1 : 1) * spread;
        if (this.task != null) {
            this.task.cancel();
        }
        System.out.println("Spawning envoy boss in " + this.spawnTimer + " seconds ( " + this.spawnTimer / 60 + "mins).");
        this.task = new BukkitRunnable() {
            private boolean warnedNoLocation = false;

            @Override
            public void run() {
                if (EnvoyManager.this.bossLocation == null) {
                    // the old code logged this every second; once is enough until /setheroicbossspawn is used
                    if (!this.warnedNoLocation) {
                        Bukkit.getLogger().info("No valid boss location found (use /setheroicbossspawn), waiting.");
                        this.warnedNoLocation = true;
                    }
                    return;
                }
                if (EnvoyManager.this.spawnTimer <= 0) {
                    this.cancel();
                    if (EnvoyManager.this.bossUUID != null && MiscUtils.getEntity(EnvoyManager.this.bossUUID, EnvoyManager.this.bossLocation.getWorld()) != null) {
                        EnvoyManager.this.startEnvoyTimer();
                        return;
                    }
                    EnvoyManager.this.spawnBoss();
                    EnvoyManager.this.startEnvoyTimer();
                    return;
                }
                if (EnvoyManager.this.spawnTimer % 240 == 0 && EnvoyManager.this.bossUUID != null && MiscUtils.getEntity(EnvoyManager.this.bossUUID, EnvoyManager.this.bossLocation.getWorld()) != null) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "(!) " + ChatColor.AQUA + ChatColor.BOLD.toString() + "A Heroic Envoy Boss is at /warp battlefield!");
                    Bukkit.broadcastMessage(ChatColor.GRAY + "Defeat it to unlock a heroic envoy and " + ChatColor.GRAY + "faction crystals!");
                    Bukkit.broadcastMessage("");
                }
                if (EnvoyManager.this.spawnTimer == 300) {
                    EnvoyManager.this.notifyPlayers("5 MINUTES");
                    Bukkit.broadcastMessage(ChatColor.GRAY + "Go to " + ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "/warp battlefield" + ChatColor.GRAY + " and prepare to battle!");
                } else if (EnvoyManager.this.spawnTimer == 60) {
                    EnvoyManager.this.notifyPlayers("1 MINUTE");
                    Bukkit.broadcastMessage(ChatColor.GRAY + "Go to " + ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "/warp battlefield" + ChatColor.GRAY + " and prepare to battle!");
                } else if (EnvoyManager.this.spawnTimer == 30 || EnvoyManager.this.spawnTimer <= 5 && EnvoyManager.this.spawnTimer > 0) {
                    EnvoyManager.this.notifyPlayers(EnvoyManager.this.spawnTimer + " SECOND" + (EnvoyManager.this.spawnTimer == 1 ? "" : "S"));
                    if (EnvoyManager.this.spawnTimer == 1) {
                        Bukkit.broadcastMessage(ChatColor.GRAY + "        Use " + ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "/warp battlefield" + ChatColor.GRAY + " to fight the Heroic Boss!");
                    }
                }
                EnvoyManager.this.spawnTimer--;
            }
        };
        this.task.runTaskTimer(AstroCore.getInstance(), 20L, 20L);
    }

    public void notifyPlayers(String timer) {
        Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "*** HEROIC ENVOY BOSS SPAWNING IN " + ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + timer + ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + " ***");
    }

    public void spawnEnvoyBlocks(Location center) {
        this.bossUUID = null;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "*** THE HEROIC ENVOY BOSS HAS BEEN DEFEATED! ***");
        Bukkit.broadcastMessage(ChatColor.GRAY + "         A Heroic Envoy is now falling near its corpse!");
        Bukkit.broadcastMessage("");
        double chests_per_location = 10 + this.random.nextInt(5);
        Bukkit.getLogger().info("BattlefieldEvent: Dropping " + chests_per_location + " chests around " + center.getBlockX() + ", " + center.getBlockY() + ", " + center.getBlockZ());
        int dropped = 0;
        for (int i = 0; (double) i < chests_per_location; ++i) {
            Location nearby = EnvoyManager.getNearbyLocation(center, 30, 30);
            if (!MiscUtil.getRegionName(nearby).startsWith("spawn_")) {
                this.dropChest(nearby);
                ++dropped;
            }
        }
        Bukkit.getLogger().info("BattlefieldEvent: Managed to find " + dropped + " valid chest spawn locations");
    }

    public void dropChest(Location l) {
        ItemRarity rarity = this.getRandomItemRarity();
        AstroCore.getInstance().getLogger().info("(DE) Falling heroic envoy with rarity " + rarity.name() + " near " + l.toString());
        FallingBlock entity = l.getWorld().spawnFallingBlock(l, Material.CHEST, (byte) 0);
        entity.setMetadata("chestRarity", new FixedMetadataValue(AstroCore.getInstance(), rarity.name()));
    }

    public static Location getNearbyLocation(Location l, int radius, int y_boost) {
        Random rand = new Random();
        Location rand_loc = l.clone();
        rand_loc.add((rand.nextBoolean() ? 1 : -1) * (rand.nextInt(radius) + 8), 0.0, (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(radius) + 8));
        rand_loc.add(0.5, y_boost, 0.5);
        while ((rand_loc.getBlock().getType() != Material.AIR || rand_loc.getBlock().getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR) && rand_loc.getY() < 255.0) {
            rand_loc = rand_loc.add(0.0, 1.0, 0.0);
        }
        return rand_loc;
    }

    /** on shutdown, like the old AstroCore.onDisable: remove a boss that is still alive */
    public void removeBoss() {
        if (this.task != null) {
            this.task.cancel();
        }
        Boss bos;
        if (this.bossUUID != null && this.bossLocation != null && (bos = Boss.bosses.get(this.bossUUID)) != null) {
            try {
                bos.onDeath(null);
            } catch (RuntimeException e) {
                // never let the boss cleanup stop the rest of AstroCore's shutdown (titles, coinflips, ...)
                Bukkit.getLogger().warning("[AstroCore] Heroic envoy boss cleanup: " + e);
            }
            bos.getEntity().remove();
            Boss.bosses.remove(bos.getEntity().getUniqueId());
            System.out.println("Removing boss!");
        }
    }

    public void setBossLocation(Location bossLocation) {
        this.bossLocation = bossLocation;
    }

    public Location getBossLocation() {
        return this.bossLocation;
    }

    public UUID getBossUUID() {
        return this.bossUUID;
    }

    public long getLastBossSpawn() {
        return this.lastBossSpawn;
    }
}
