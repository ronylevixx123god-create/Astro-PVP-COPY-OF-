package com.compilex.CosmicKOTH.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called after a player captures the KOTH (also for /akoth forcecap), so other plugins such as PvPTop can react.
 */
public class KothCapturedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player capper;

    public KothCapturedEvent(Player capper) {
        this.capper = capper;
    }

    public Player getCapper() {
        return this.capper;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
