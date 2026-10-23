/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent$Result
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package me.ifamasssxd.cosmicevolutionkits.listeners;

import java.util.UUID;
import me.ifamasssxd.cosmicevolutionkits.structure.KitPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener
implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        KitPlayer player = new KitPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        player.loadData(true);
        KitPlayer.kitPlayerMap.put(event.getPlayer().getUniqueId(), player);
    }

    public void onAsyncPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        UUID uuid = event.getUniqueId();
        KitPlayer kitPlayer = new KitPlayer(uuid, event.getName());
        kitPlayer.loadData(false);
        KitPlayer.kitPlayerMap.put(uuid, kitPlayer);
    }
}

