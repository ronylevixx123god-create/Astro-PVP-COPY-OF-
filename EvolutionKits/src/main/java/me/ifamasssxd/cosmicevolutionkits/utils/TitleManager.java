package me.ifamasssxd.cosmicevolutionkits.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Titles for the level-up / unlock screens. Astro's version pushed 1.8 title packets through the 1.7 netty pipeline
 * (v1_7_R4 + ProtocolInjector); this server is 1.8, so the vanilla title packet does the same thing.
 */
public class TitleManager {

    public static void sendTitle(Player player, String title, TitleColor color) {
        send(player, PacketPlayOutTitle.EnumTitleAction.TITLE, color.prefix() + title);
    }

    public static void sendSubTitle(Player player, String subtitle, TitleColor color) {
        send(player, PacketPlayOutTitle.EnumTitleAction.SUBTITLE, color.prefix() + subtitle);
    }

    private static void send(Player player, PacketPlayOutTitle.EnumTitleAction action, String text) {
        if (player == null || !player.isOnline()) return;
        IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent) null, 10, 40, 10));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(action, component));
    }

    public enum TitleColor {
        BLACK(ChatColor.BLACK), DARK_BLUE(ChatColor.DARK_BLUE), DARK_GREEN(ChatColor.DARK_GREEN), DARK_AQUA(ChatColor.DARK_AQUA),
        DARK_RED(ChatColor.DARK_RED), DARK_PURPLE(ChatColor.DARK_PURPLE), GOLD(ChatColor.GOLD), GRAY(ChatColor.GRAY),
        DARK_GRAY(ChatColor.DARK_GRAY), BLUE(ChatColor.BLUE), GREEN(ChatColor.GREEN), AQUA(ChatColor.AQUA), RED(ChatColor.RED),
        LIGHT_PURPLE(ChatColor.LIGHT_PURPLE), YELLOW(ChatColor.YELLOW), WHITE(ChatColor.WHITE);

        private final ChatColor color;

        TitleColor(ChatColor color) {
            this.color = color;
        }

        String prefix() {
            return this.color.toString();
        }
    }
}
