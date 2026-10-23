package me.cosmicpvp.CosmicGlobalKits.libs;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.cosmicpvp.CosmicGlobalKits.libs.GlobalKits;
import me.cosmicpvp.CosmicGlobalKits.struct.MasteryGlobalKit;
import me.cosmicpvp.CosmicGlobalKits.struct.mkits.DeathKnight;
import me.cosmicpvp.CosmicGlobalKits.struct.mkits.GhostKit;
import me.cosmicpvp.CosmicGlobalKits.struct.mkits.Necromancer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public enum MasteryKits {
    GHOST(new GhostKit(), new MaterialData(Material.SUGAR), 3, Lists.newArrayList(GlobalKits.Spooky, GlobalKits.Butcher, GlobalKits.GrimReaper), TimeUnit.DAYS.toMillis(7L), CC.DarkAquaB + "Ghost", Lists.newArrayList("", CC.Gray + "The armor of an eternal entity known", CC.Gray + "only as the Ghost. It will haunt you", CC.Gray + "until you meet your inevitable fate.")),
    NECROMANCER(new Necromancer(), new MaterialData(Material.SKULL_ITEM, (byte) 1), 4, Lists.newArrayList(GlobalKits.DIABLO, GlobalKits.Voidwalker, GlobalKits.Warlock), TimeUnit.DAYS.toMillis(7L), CC.DarkGreenB + "Necromancer", Lists.newArrayList("", CC.Gray + "The cursed robes of the universally feared", CC.Gray + "puppeteer of the undead, the Necromancer.", CC.Gray + "Death is no longer your enemy.")),
    DEATH_KNIGHT(new DeathKnight(), new MaterialData(Material.EYE_OF_ENDER), 5, Lists.newArrayList(GlobalKits.GLADIATOR, GlobalKits.Loki, GlobalKits.TEMPLAR), TimeUnit.DAYS.toMillis(7L), CC.BlueB + "Death Knight", Lists.newArrayList("", CC.Gray + "The cursed runeforged set of armor", CC.Gray + "created for the Death Knight,", CC.Gray + "a master in both melee combat", CC.Gray + "and the use of offensive dark", CC.Gray + "magic. How can you kill", CC.Gray + "what is already dead?"));

    private final int slot;
    private final MasteryGlobalKit kit;
    private final List<GlobalKits> kitsRequired;
    private final long expiration;
    private final String name;
    private final List<String> description;
    private final MaterialData data;

    MasteryKits(MasteryGlobalKit kit, MaterialData data, int slot, List<GlobalKits> required, long time, String name, List<String> lore) {
        this.kitsRequired = required;
        this.kit = kit;
        this.slot = slot;
        this.expiration = time;
        this.name = name;
        this.description = lore;
        this.data = data;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(this.getPermissionString());
    }

    public String getPermissionString() {
        return "cosmicglobalkits.masterykit." + this.name().toLowerCase();
    }

    public static MasteryKits getFromName(String str) {
        return Arrays.stream(MasteryKits.values()).filter(m -> m.name().equalsIgnoreCase(str)).findFirst().orElse(null);
    }

    public void giveToPlayer(Player player) {
        for (ItemStack item : this.getKit().getMasteryItems(player, this, true)) {
            PlayerUtil.giveOrDropProtectedItem(player, item, -1, null, CC.RedB + "(!) " + CC.Red + "Mastery Kit Item dropped at your feet!");
        }
    }

    public int getSlot() {
        return this.slot;
    }

    public MasteryGlobalKit getKit() {
        return this.kit;
    }

    public List<GlobalKits> getKitsRequired() {
        return this.kitsRequired;
    }

    public long getExpiration() {
        return this.expiration;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public MaterialData getData() {
        return this.data;
    }
}

