package net.crankedpvp.pvptop.listeners;

import com.compilex.CosmicKOTH.events.KothCapturedEvent;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.crankedpvp.pvptop.PvPTopPlugin;
import net.crankedpvp.pvptop.data.FactionData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// KOTH captures from CosmicKOTH (the KOTH plugin the server runs); FactionsKore KOTHs go through KothCaptureListener.
public class CosmicKothCaptureListener implements Listener {

    private final PvPTopPlugin plugin;
    public CosmicKothCaptureListener(PvPTopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKothCapture(KothCapturedEvent event) {
        if(event.getCapper() == null) return;
        Faction faction = FPlayers.getInstance().getByPlayer(event.getCapper()).getFaction();
        if(this.plugin.getPvpTopManager().getOrCreate(faction) == null) return;
        FactionData.Points.KOTH_CAPTURE.add(faction);
    }
}
