package net.crankedpvp.hypeboxes.struct.battle;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BattleQueueManager {

    @Getter
    private static final Map<Player, Map<String, Integer>> battleQueue = new HashMap<>();

    /** takes the player's battle out of the queue and gives the boxes back */
    public static void refund(Player player) {
        Map<String, Integer> hypeboxes = battleQueue.remove(player);
        if (hypeboxes == null) return;
        hypeboxes.forEach((hb, amt) -> {
            net.crankedpvp.hypeboxes.struct.HypeBox hypeBox = net.crankedpvp.hypeboxes.HypeboxesPlugin.getInstance().getHypeBoxRegister().getHypebox(hb);
            if (hypeBox != null && amt > 0) net.crankedpvp.hypeboxes.utils.InventoryUtils.giveItemToPlayer(player, hypeBox.createHypeBox(amt));
        });
    }

}
