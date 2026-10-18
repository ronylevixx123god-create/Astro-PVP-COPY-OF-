package devtest;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Isolated-test-server probe for the Factions claim-protection rules. Prints one "ROW who|what|result" line per check
 * (run once on the old jar and once on the new jar, then diff), plus "PROBE ..." notes. Creates temporary factions,
 * claims and test players, removes them all afterwards, and stops the server.
 */
public class FactionsRulesProbe extends JavaPlugin {
    private final List<EntityPlayer> fakes = new ArrayList<>();
    private final List<Faction> created = new ArrayList<>();
    private final List<FLocation> claims = new ArrayList<>();

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskLater(this, this::run, 20L);
    }

    private void log(String s) {
        getLogger().info("PROBE " + s);
    }

    private void row(String who, String what, Object result) {
        getLogger().info("ROW " + who + "|" + what + "|" + result);
    }

    private void run() {
        MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
        try {
            FactionsPlugin fp = FactionsPlugin.getInstance();
            log("config: ownedArea.enabled=" + fp.conf().factions().ownedArea().isEnabled()
                    + " protectMaterials=" + fp.conf().factions().ownedArea().isProtectMaterials()
                    + " preventDuping=" + fp.conf().exploits().doPreventDuping()
                    + " separateOfflinePerms=" + fp.conf().factions().other().isSeparateOfflinePerms());
            World w = Bukkit.getWorlds().get(0);
            WorldServer ws = ((CraftWorld) w).getHandle();

            Faction home = faction("ProbeHome");
            Player leader = member(ms, ws, 1, "HomeLeader", home, Role.ADMIN);
            Player normal = member(ms, ws, 2, "HomeNormal", home, Role.NORMAL);
            Player alt = member(ms, ws, 3, "HomeAlt", home, Role.ALT);
            Map<String, Player> outsiders = new LinkedHashMap<>();
            for (Relation r : new Relation[]{Relation.ALLY, Relation.TRUCE, Relation.NEUTRAL, Relation.ENEMY}) {
                Faction other = faction("Probe" + r.name().charAt(0) + r.name().substring(1).toLowerCase());
                home.setRelationWish(other, r);
                other.setRelationWish(home, r);
                Player p = member(ms, ws, 10 + r.ordinal(), "Out" + r.name(), other, Role.ADMIN);
                log("relation " + other.getTag() + " -> " + home.getTag() + " = " + other.getRelationTo(home));
                outsiders.put(r.name().toLowerCase(), p);
            }
            outsiders.put("factionless", fake(ms, ws, 20, "OutNone"));
            log("factionless relation -> home = " + FPlayers.getInstance().getByPlayer(outsiders.get("factionless")).getFaction().getRelationTo(home));

            Location base = new Location(w, 8, 120, 8);
            FLocation claim = new FLocation(base);
            Board.getInstance().setFactionAt(home, claim);
            claims.add(claim);
            Location ownedBase = new Location(w, 24, 120, 8);
            FLocation owned = new FLocation(ownedBase);
            Board.getInstance().setFactionAt(home, owned);
            claims.add(owned);
            home.setPlayerAsOwner(FPlayers.getInstance().getByPlayer(leader), owned);

            Map<String, Player> all = new LinkedHashMap<>();
            all.put("leader", leader);
            all.put("member", normal);
            all.put("alt", alt);
            all.putAll(outsiders);

            Material[] blocks = {Material.CHEST, Material.TRAPPED_CHEST, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST,
                    Material.FURNACE, Material.BURNING_FURNACE, Material.HOPPER, Material.DISPENSER, Material.BREWING_STAND,
                    Material.BEACON, Material.ANVIL, Material.DIODE_BLOCK_OFF, Material.REDSTONE_COMPARATOR_OFF,
                    Material.WOODEN_DOOR, Material.TRAP_DOOR, Material.FENCE_GATE, Material.WOOD_BUTTON, Material.LEVER};
            Block b = base.getBlock();
            Block ob = ownedBase.getBlock();
            for (Material m : blocks) {
                b.setType(m);
                ob.setType(m);
                for (Map.Entry<String, Player> e : all.entrySet()) {
                    row(e.getKey(), "use " + m, allowed(e.getValue(), Action.RIGHT_CLICK_BLOCK, null, b));
                    row(e.getKey(), "use " + m + " (owned land)", allowed(e.getValue(), Action.RIGHT_CLICK_BLOCK, null, ob));
                }
            }
            b.setType(Material.SOIL);
            for (Map.Entry<String, Player> e : all.entrySet()) {
                row(e.getKey(), "trample SOIL", allowed(e.getValue(), Action.PHYSICAL, null, b));
            }
            b.setType(Material.AIR);
            ob.setType(Material.AIR);

            Block rail = b.getRelative(BlockFace.DOWN);
            rail.setType(Material.RAILS);
            for (Material item : new Material[]{Material.MINECART, Material.STORAGE_MINECART, Material.POWERED_MINECART,
                    Material.EXPLOSIVE_MINECART, Material.HOPPER_MINECART, Material.COMMAND_MINECART}) {
                for (Map.Entry<String, Player> e : all.entrySet()) {
                    row(e.getKey(), "place " + item, allowed(e.getValue(), Action.RIGHT_CLICK_BLOCK, new ItemStack(item), rail));
                }
            }
            rail.setType(Material.AIR);
            for (Map.Entry<String, Player> e : all.entrySet()) {
                row(e.getKey(), "build", FactionsBlockListener.playerCanBuildDestroyBlock(e.getValue(), base, PermissibleAction.BUILD, true));
            }

            // Changing relation permissions (API, GUI path) and an old saved grant
            for (Relation r : new Relation[]{Relation.ALLY, Relation.TRUCE, Relation.NEUTRAL, Relation.ENEMY}) {
                row("perms", "setPermission " + r + " BUILD allow returned", home.setPermission(true, r, PermissibleAction.BUILD, true));
                row("perms", "isLocked " + r + " BUILD", home.isLocked(true, r, PermissibleAction.BUILD));
                row("perms", "after set: " + r + " BUILD access", home.hasAccess(true, r, PermissibleAction.BUILD));
            }
            for (Map.Entry<String, Player> e : outsiders.entrySet()) {
                row(e.getKey(), "build after leader granted all relations BUILD", FactionsBlockListener.playerCanBuildDestroyBlock(e.getValue(), base, PermissibleAction.BUILD, true));
            }
            // Default access per relation for every non-faction-only permission
            for (Relation r : new Relation[]{Relation.ALLY, Relation.TRUCE, Relation.NEUTRAL, Relation.ENEMY}) {
                Faction fresh = faction("ProbeFresh" + r.ordinal());
                StringBuilder sb = new StringBuilder();
                for (PermissibleAction a : PermissibleAction.values()) {
                    if (!a.isFactionOnly() && fresh.hasAccess(true, r, a)) sb.append(a).append(' ');
                }
                row("defaults", "new faction: " + r + " allowed actions", sb.length() == 0 ? "(none)" : sb.toString().trim());
            }

            // Old saved grants, written straight into the saved map (as loading factions.json does)
            Map<Object, Map<PermissibleAction, Boolean>> saved = savedMap(home);
            for (Relation r : new Relation[]{Relation.TRUCE, Relation.NEUTRAL, Relation.ENEMY}) {
                saved.computeIfAbsent(r, k -> new HashMap<>()).put(PermissibleAction.DOOR, true);
                saved.get(r).put(PermissibleAction.BUILD, true);
                saved.get(r).put(PermissibleAction.CONTAINER, true);
                row("perms", "old saved grant " + r + " DOOR/BUILD/CONTAINER=true -> access door=" + home.hasAccess(true, r, PermissibleAction.DOOR)
                        + " build=" + home.hasAccess(true, r, PermissibleAction.BUILD) + " container=" + home.hasAccess(true, r, PermissibleAction.CONTAINER), "");
            }
            b.setType(Material.FURNACE);
            for (String who : new String[]{"truce", "neutral", "enemy", "factionless"}) {
                row(who, "use FURNACE with old saved CONTAINER grant", allowed(outsiders.get(who), Action.RIGHT_CLICK_BLOCK, null, b));
            }
            b.setType(Material.AIR);

            // One-time ally reset of an existing faction (new jar only: field allyPermsReset)
            try {
                Faction old = faction("ProbeOldAlly");
                Field flag = Class.forName("com.massivecraft.factions.data.MemoryFaction").getDeclaredField("allyPermsReset");
                flag.setAccessible(true);
                savedMap(old).computeIfAbsent(Relation.ALLY, k -> new HashMap<>()).put(PermissibleAction.DOOR, true);
                savedMap(old).get(Relation.ALLY).put(PermissibleAction.FLY, true);
                flag.setBoolean(old, false);
                old.checkPerms();
                row("perms", "existing faction (saved ally DOOR/FLY=true, not yet reset) after load -> ally door=" + old.hasAccess(true, Relation.ALLY, PermissibleAction.DOOR)
                        + " fly=" + old.hasAccess(true, Relation.ALLY, PermissibleAction.FLY) + " flag=" + flag.getBoolean(old), "");
                old.setPermission(true, Relation.ALLY, PermissibleAction.DOOR, true);
                old.checkPerms();
                row("perms", "ally DOOR granted after the reset, then reloaded -> ally door=" + old.hasAccess(true, Relation.ALLY, PermissibleAction.DOOR), "");
            } catch (NoSuchFieldException e) {
                row("perms", "one-time ally reset", "not present in this jar");
            }
            row("perms", "member roles CONTAINER (leader-editable): normal=" + home.hasAccess(true, Role.NORMAL, PermissibleAction.CONTAINER)
                    + " alt=" + home.hasAccess(true, Role.ALT, PermissibleAction.CONTAINER), "");
            row("perms", "setPermission ALT CONTAINER allow returned", home.setPermission(true, Role.ALT, PermissibleAction.CONTAINER, true));
            b.setType(Material.CHEST);
            row("alt", "use CHEST after leader allows ALT CONTAINER", allowed(alt, Action.RIGHT_CLICK_BLOCK, null, b));
            home.setPermission(true, Role.ALT, PermissibleAction.CONTAINER, false);
            b.setType(Material.AIR);

            // /f perm command
            Bukkit.dispatchCommand(leader, "f perm enemy container allow");
            row("perms", "after '/f perm enemy container allow': enemy CONTAINER", home.hasAccess(true, Relation.ENEMY, PermissibleAction.CONTAINER));
            Bukkit.dispatchCommand(leader, "f perm ally container allow");
            row("perms", "after '/f perm ally container allow': ally CONTAINER", home.hasAccess(true, Relation.ALLY, PermissibleAction.CONTAINER));
            b.setType(Material.FURNACE);
            row("ally", "use FURNACE after ally granted CONTAINER", allowed(outsiders.get("ally"), Action.RIGHT_CLICK_BLOCK, null, b));
            row("enemy", "use FURNACE after '/f perm enemy container allow'", allowed(outsiders.get("enemy"), Action.RIGHT_CLICK_BLOCK, null, b));
            b.setType(Material.AIR);
        } catch (Throwable t) {
            log("FAIL exception " + t);
            t.printStackTrace();
        } finally {
            try {
                for (FLocation c : claims) Board.getInstance().removeAt(c);
                for (Faction f : created) {
                    for (FPlayer p : new ArrayList<>(f.getFPlayers())) p.resetFactionData();
                    Factions.getInstance().removeFaction(f.getId());
                }
            } catch (Throwable t) {
                log("FAIL cleanup " + t);
            }
            for (EntityPlayer e : fakes) registerOnline(ms, e, false);
            log("RESULT done");
            Bukkit.shutdown();
        }
    }

    private Faction faction(String tag) {
        Faction f = Factions.getInstance().createFaction();
        f.setTag(tag);
        created.add(f);
        return f;
    }

    private Player member(MinecraftServer ms, WorldServer ws, int n, String name, Faction f, Role role) {
        Player p = fake(ms, ws, n, name);
        FPlayer fpl = FPlayers.getInstance().getByPlayer(p);
        fpl.setFaction(f);
        fpl.setRole(role);
        return p;
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Map<PermissibleAction, Boolean>> savedMap(Faction f) throws ReflectiveOperationException {
        Field field = Class.forName("com.massivecraft.factions.data.MemoryFaction").getDeclaredField("permissions");
        field.setAccessible(true);
        return (Map<Object, Map<PermissibleAction, Boolean>>) field.get(f);
    }

    private boolean allowed(Player p, Action action, ItemStack item, Block b) {
        PlayerInteractEvent e = new PlayerInteractEvent(p, action, item, b, action == Action.PHYSICAL ? BlockFace.SELF : BlockFace.UP);
        Bukkit.getPluginManager().callEvent(e);
        return !e.isCancelled();
    }

    private Player fake(MinecraftServer ms, WorldServer ws, int n, String name) {
        UUID id = UUID.fromString(String.format("cccccccc-0000-0000-0000-%012d", n));
        EntityPlayer ep = new EntityPlayer(ms, ws, new GameProfile(id, name), new PlayerInteractManager(ws));
        ep.playerConnection = new PlayerConnection(ms, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), ep);
        ep.setLocation(8.5, 121, 60.5, 0, 0);
        ms.getPlayerList().players.add(ep);
        registerOnline(ms, ep, true);
        fakes.add(ep);
        return ep.getBukkitEntity();
    }

    @SuppressWarnings("unchecked")
    private static void registerOnline(MinecraftServer ms, EntityPlayer ep, boolean add) {
        try {
            PlayerList pl = ms.getPlayerList();
            Field byUuid = PlayerList.class.getDeclaredField("j");
            Field byName = PlayerList.class.getDeclaredField("playersByName");
            byUuid.setAccessible(true); byName.setAccessible(true);
            Map<UUID, EntityPlayer> u = (Map<UUID, EntityPlayer>) byUuid.get(pl);
            Map<String, EntityPlayer> n = (Map<String, EntityPlayer>) byName.get(pl);
            if (add) { u.put(ep.getUniqueID(), ep); n.put(ep.getName(), ep); }
            else { pl.players.remove(ep); u.remove(ep.getUniqueID()); n.remove(ep.getName()); }
        } catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }
}
