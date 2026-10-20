package net.astropvp.core.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;

/**
 * /clearlag (/cl) - admin only: removes dropped items and loose hostile mobs in every world. Mobs with a name
 * (stacked spawner mobs, bosses, NPCs), tamed pets, leashed or riding mobs and all passive animals are kept.
 */
public class ClearLagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("astrocore.clearlag")) {
            sender.sendMessage(CC.translate("&cYou do not have permission to use this command."));
            return true;
        }
        int items = 0;
        int mobs = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    entity.remove();
                    ++items;
                } else if (isLooseMob(entity)) {
                    entity.remove();
                    ++mobs;
                }
            }
        }
        sender.sendMessage(CC.translate("&a&l(!) &aClearLag removed &f" + items + " &adropped item(s) and &f" + mobs + " &amob(s)."));
        Bukkit.getLogger().info("[AstroCore] " + sender.getName() + " ran /clearlag: removed " + items + " dropped items and " + mobs + " mobs");
        return true;
    }

    private static boolean isLooseMob(Entity entity) {
        if (!(entity instanceof Monster || entity instanceof Slime || entity instanceof Flying)) {
            return false;
        }
        LivingEntity mob = (LivingEntity) entity;
        if (mob.getCustomName() != null || mob.hasMetadata("NPC")) {
            return false;   // stacked spawner mobs, bosses and NPCs are named
        }
        if (mob instanceof Tameable && ((Tameable) mob).isTamed()) {
            return false;
        }
        return !mob.isLeashed() && mob.getPassenger() == null && !mob.isInsideVehicle();
    }
}
