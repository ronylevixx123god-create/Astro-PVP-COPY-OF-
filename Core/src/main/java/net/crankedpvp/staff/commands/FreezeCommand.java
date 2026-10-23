package net.crankedpvp.staff.commands;

import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.astropvp.core.AstroCore;
import org.bukkit.entity.Player;

@Command("freeze")
public class FreezeCommand extends CommandBase {

    private AstroCore plugin;
    public FreezeCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission("staff.freeze")
    public void defaultCommand(Player player, @Completion("#players") Player toFreeze) {
        this.plugin.getFreezeManager().toggleFreeze(player, toFreeze);
    }
}