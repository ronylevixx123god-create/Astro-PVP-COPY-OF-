package net.crankedpvp.hypeboxes.listeners;

import net.crankedpvp.hypeboxes.struct.battle.BattleQueueManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class BattleQueueListener implements Listener {

    // a queued battle can't be joined once its owner is gone; give the boxes back before the player is saved
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BattleQueueManager.refund(event.getPlayer());
    }
}
