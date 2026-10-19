package me.kingsavaqe.cagepvp.listeners;

import me.kingsavaqe.cagepvp.AstroCagePvP;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Command implements Listener {
   /** Commands that always work during a match (cage fight and wild pvp, including "leave"). */
   private static final List<String> MATCH_COMMANDS = Arrays.asList("/cagefight", "/1v1", "/wildpvp", "/pvp");
   private final AstroCagePvP instance;

   public Command(AstroCagePvP instance) {
      this.instance = instance;
   }

   @EventHandler
   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
      if (!MATCH_COMMANDS.contains(event.getMessage().split(" ")[0].toLowerCase())) {
         Player player = event.getPlayer();
         FileConfiguration configLoad = this.instance.getConfig();
         Iterator<String> var4 = configLoad.getStringList("Listeners.Command.AllowedCommands").iterator();

         String allowedCommand;
         do {
            if (!var4.hasNext()) {
               if (!this.instance.getWildPvPManager().hasWildPvP(player)) {
                  return;
               }

               event.setCancelled(true);
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Listeners.Command.Match.Message")));
               player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
               return;
            }

            allowedCommand = var4.next();
         } while(!event.getMessage().split(" ")[0].toLowerCase().equalsIgnoreCase("/" + allowedCommand.toLowerCase()));

      }
   }
}
