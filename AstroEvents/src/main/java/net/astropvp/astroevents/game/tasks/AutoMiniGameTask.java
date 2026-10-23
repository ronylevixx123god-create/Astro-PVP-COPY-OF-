package net.astropvp.astroevents.game.tasks;

import lombok.AllArgsConstructor;
import net.astropvp.astroevents.game.MiniGame;
import net.astropvp.astroevents.game.MiniGameManager;
import net.astropvp.astroevents.game.MiniGameRegister;
import net.astropvp.astroevents.game.MiniGameState;
import net.astropvp.astroevents.game.MiniGameType;
import net.astropvp.astroevents.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class AutoMiniGameTask extends BukkitRunnable {

    private final MiniGameManager miniGameManager;
    private final MiniGameRegister miniGameRegister;

    // Parkour and Sumo take turns; a turn that can't start (another event running, too few players online) is skipped
    @Override
    public void run() {
        MiniGameType nextMiniGame = this.miniGameManager.getNextMiniGame();
        MiniGame<?> miniGame = this.miniGameRegister.getMiniGame(nextMiniGame);
        this.miniGameManager.setLastMiniGameTime(System.currentTimeMillis());
        this.miniGameManager.setPreviousMiniGame(nextMiniGame);
        this.miniGameManager.saveSchedule();
        String name = Utils.toNiceString(nextMiniGame.name());
        if (miniGame == null) return;
        MiniGame<?> active = this.miniGameRegister.getMiniGames().values().stream().filter(game -> game.getMiniGameState() != MiniGameState.NOT_STARTED).findFirst().orElse(null);
        if (active != null) {
            this.miniGameManager.getPlugin().getLogger().info("Skipping the automatic " + name + " event: the " + Utils.toNiceString(active.getMiniGameType().name()) + " event is still running");
            return;
        }
        if (Bukkit.getOnlinePlayers().size() < miniGame.getMinPlayers()) {
            this.miniGameManager.getPlugin().getLogger().info("Skipping the automatic " + name + " event: " + Bukkit.getOnlinePlayers().size() + " online, it needs " + miniGame.getMinPlayers());
            return;
        }
        this.miniGameManager.getPlugin().getLogger().info("Starting the automatic " + name + " event");
        miniGame.startMiniGameCountDown();
    }
}
