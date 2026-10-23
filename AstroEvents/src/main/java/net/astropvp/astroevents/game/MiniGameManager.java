package net.astropvp.astroevents.game;

import lombok.Getter;
import lombok.Setter;
import net.astropvp.astroevents.AstroEvents;
import net.astropvp.astroevents.game.tasks.AutoMiniGameTask;
import net.astropvp.astroevents.utils.Utils;
import net.astropvp.astroevents.utils.configuration.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


@Getter
@Setter
public class MiniGameManager {

    // an automatic event that was due while the server was down waits this long after start-up
    private static final long MIN_START_DELAY_IN_SECS = 300;

    private long lastMiniGameTime;
    private final MiniGameRegister miniGameRegister;
    private final AstroEvents plugin;
    private AutoMiniGameTask autoMiniGameTask;
    private MiniGameType previousMiniGame;
    private long timeBetweenEventsInSecs;
    private final File scheduleFile;

    public MiniGameManager(AstroEvents plugin) {
        this.miniGameRegister = plugin.getMiniGameRegister();
        this.previousMiniGame = MiniGameType.SUMO;
        this.plugin = plugin;
        this.scheduleFile = new File(plugin.getDataFolder(), "auto-events-schedule.yml");
        this.timeBetweenEventsInSecs = Math.max(60, plugin.getConfigManager().getConfig(ConfigManager.ConfigFile.CONFIG).getLong("auto-events.interval-seconds", 7200));
    }

    public boolean isAutoMiniGamesEnabled() {
        return this.plugin.getConfigManager().getConfig(ConfigManager.ConfigFile.CONFIG).getBoolean("auto-events.enabled", true);
    }

    // the schedule (last automatic event and its type) is kept across restarts, so restarts don't push events back
    public void startAutoMiniGameTask() {
        long now = System.currentTimeMillis();
        this.lastMiniGameTime = now;
        FileConfiguration schedule = YamlConfiguration.loadConfiguration(this.scheduleFile);
        if (schedule.contains("last-event-time")) this.lastMiniGameTime = Math.min(now, schedule.getLong("last-event-time"));
        try {
            if (schedule.contains("previous-event")) this.previousMiniGame = MiniGameType.valueOf(schedule.getString("previous-event"));
        } catch (IllegalArgumentException ignored) {
        }
        long delayInSecs = Math.max(Math.min(MIN_START_DELAY_IN_SECS, this.timeBetweenEventsInSecs), this.getTimeInMsUntilNextMiniGame() / 1000);
        this.lastMiniGameTime = now + delayInSecs * 1000 - this.timeBetweenEventsInSecs * 1000;
        this.autoMiniGameTask = new AutoMiniGameTask(this, this.miniGameRegister);
        this.autoMiniGameTask.runTaskTimer(plugin, delayInSecs * 20, this.timeBetweenEventsInSecs * 20);
        this.plugin.getLogger().info("Automatic events every " + Utils.getSecondsAsString(this.timeBetweenEventsInSecs) + ": next is " + Utils.toNiceString(this.getNextMiniGame().name()) + " in " + this.getTimeUntilNextMiniGame());
    }

    public void saveSchedule() {
        FileConfiguration schedule = new YamlConfiguration();
        schedule.set("last-event-time", this.lastMiniGameTime);
        schedule.set("previous-event", this.previousMiniGame.name());
        try {
            schedule.save(this.scheduleFile);
        } catch (IOException e) {
            this.plugin.getLogger().warning("Could not save the automatic event schedule: " + e.getMessage());
        }
    }

    public MiniGameType getNextMiniGame() {
        return previousMiniGame == MiniGameType.PARKOUR ? MiniGameType.SUMO : MiniGameType.PARKOUR;
    }

    public long getTimeInMsUntilNextMiniGame() {
        return (lastMiniGameTime + timeBetweenEventsInSecs * 1000) - System.currentTimeMillis();
    }

    public String getTimeUntilNextMiniGame() {
        return Utils.getSecondsAsString(this.getTimeInMsUntilNextMiniGame() / 1000);
    }
}
