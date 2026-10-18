package devtest;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.PlayerList;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

/**
 * Isolated-test-server probe for HotFix auto-op. Two server-side players join (no client;
 * packets are only queued): XaitX (owner UUID configured in HotFix auto-op-uuids) and NotOwner.
 * HotFix's real AutoOP join listeners run. 3 s later it logs who is OP ("PROBE ..."), removes
 * the test players and stops the server.
 */
public class HotFixProbe extends JavaPlugin {
    static final UUID XAITX = UUID.fromString("46f220b1-e5c5-4a55-8b43-a347db8c3b35");
    static final UUID OTHER = UUID.fromString("33333333-4444-5555-6666-777777777777");
    private EntityPlayer xaitx, other;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskLater(this, this::join, 20L);
        Bukkit.getScheduler().runTaskLater(this, this::check, 80L);
    }

    private void log(String s) {
        getLogger().info("PROBE " + s);
    }

    private EntityPlayer serverPlayer(UUID id, String name) {
        MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer ws = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        EntityPlayer ep = new EntityPlayer(ms, ws, new GameProfile(id, name), new PlayerInteractManager(ws));
        ep.playerConnection = new PlayerConnection(ms, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), ep);
        ms.getPlayerList().players.add(ep);   // so Bukkit treats the player as online
        registerOnline(ms, ep, true);
        return ep;
    }

    @SuppressWarnings("unchecked")
    private static void registerOnline(MinecraftServer ms, EntityPlayer ep, boolean add) {
        try {
            PlayerList pl = ms.getPlayerList();
            Field byUuid = PlayerList.class.getDeclaredField("j");            // UUID -> EntityPlayer (used by isOnline)
            Field byName = PlayerList.class.getDeclaredField("playersByName");
            byUuid.setAccessible(true); byName.setAccessible(true);
            Map<UUID, EntityPlayer> u = (Map<UUID, EntityPlayer>) byUuid.get(pl);
            Map<String, EntityPlayer> n = (Map<String, EntityPlayer>) byName.get(pl);
            if (add) { u.put(ep.getUniqueID(), ep); n.put(ep.getName(), ep); }
            else { pl.players.remove(ep); u.remove(ep.getUniqueID()); n.remove(ep.getName()); }
        } catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }

    private void join() {
        Plugin hotfix = Bukkit.getPluginManager().getPlugin("HotFix");
        log("HotFix loaded=" + (hotfix != null) + " enabled=" + (hotfix != null && hotfix.isEnabled())
                + " auto-op-uuids=" + (hotfix == null ? "n/a" : hotfix.getConfig().getStringList("auto-op-uuids")));
        xaitx = serverPlayer(XAITX, "XaitX");
        other = serverPlayer(OTHER, "NotOwner");
        for (EntityPlayer ep : new EntityPlayer[]{xaitx, other}) {
            Player p = ep.getBukkitEntity();
            Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, null));
            log("joined " + p.getName() + " uuid=" + p.getUniqueId() + " op(right after join)=" + p.isOp());
        }
    }

    private void check() {
        Player x = xaitx.getBukkitEntity();
        Player o = other.getBukkitEntity();
        log("RESULT after 3s: XaitX op=" + x.isOp() + "  NotOwner op=" + o.isOp());
        MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
        registerOnline(ms, xaitx, false);
        registerOnline(ms, other, false);
        Bukkit.shutdown();
    }
}
