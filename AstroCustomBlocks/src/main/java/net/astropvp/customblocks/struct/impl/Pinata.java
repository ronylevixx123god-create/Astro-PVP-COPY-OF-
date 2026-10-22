package net.astropvp.customblocks.struct.impl;

import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import lombok.Getter;
import net.astropvp.customblocks.CustomBlocks;
import net.astropvp.customblocks.CustomBlocksAPI;
import net.astropvp.customblocks.struct.CustomBlock;
import net.astropvp.customblocks.struct.CustomBlockType;
import net.astropvp.customblocks.struct.abilities.ChunkAble;
import net.astropvp.customblocks.utils.TimeUtil;
import net.astropvp.customblocks.utils.config.ConfigManager;
import net.astropvp.loottable.LootTableAPI;
import net.astropvp.loottable.struct.LootTable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Right-click registers a hit, one hit per Pinata every pinataHitCooldownSeconds (shared by all players). After
 * pinataHitsRequired hits it can be broken once for pinataLootRolls items from the PINATA loot table. Hits, cooldown and
 * its id travel with the item when it is picked up (sneak-break), so replacing it changes nothing; an id is only ever
 * redeemed once.
 */
@Getter
public class Pinata extends CustomBlock implements ChunkAble {
    private static final String LOOT_TABLE = "PINATA";
    private static final String REDEEMED_FILE = "pinata-redeemed.yml";

    private String pinataId;
    private int hits;
    private long lastHit;

    public Pinata() {
        super(CustomBlockType.SPRING_2021_LOOTINATA);
    }

    private static FileConfiguration settings() {
        return CustomBlocks.getInstance().getConfigManager().getConfig(ConfigManager.ConfigFile.SETTINGS);
    }

    public static int hitsRequired() {
        return Math.max(1, settings().getInt("pinataHitsRequired", 8));
    }

    public static long cooldownMillis() {
        return settings().getLong("pinataHitCooldownSeconds", 28800L) * 1000L;
    }

    private static int lootRolls() {
        return Math.max(1, settings().getInt("pinataLootRolls", 3));
    }

    public boolean isReady() {
        return hits >= hitsRequired();
    }

    private long nextHitIn() {
        return Math.max(0L, lastHit + cooldownMillis() - System.currentTimeMillis());
    }

    private String progress() {
        return "&7(&f" + Math.min(hits, hitsRequired()) + "&7/&f" + hitsRequired() + "&7 hits)";
    }

    @Override
    public void onClick(Player clicker) {
        if (pinataId == null) pinataId = UUID.randomUUID().toString();
        if (isReady()) {
            clicker.sendMessage(CC.translate("&d&l(!)&d This Pinata is ready! Break it to release its treasure. " + progress()));
            return;
        }
        long wait = nextHitIn();
        if (wait > 0) {
            clicker.sendMessage(CC.translate("&c&l(!)&c This Pinata can be hit again in &f" + TimeUtil.fancy(wait).trim() + "&c. " + progress()));
            return;
        }
        hits++;
        lastHit = System.currentTimeMillis();
        // saved right away so a crash or restart can't undo the cooldown
        CustomBlocks.getInstance().getCustomBlockManager().saveAll();
        clicker.playSound(clicker.getLocation(), Sound.ZOMBIE_WOOD, 1.0F, 1.5F);
        if (isReady()) {
            clicker.sendMessage(CC.translate("&d&l(!)&d Hit registered! The Pinata is ready, break it to release its treasure. " + progress()));
        } else {
            clicker.sendMessage(CC.translate("&d&l(!)&d Hit registered! Next hit in &f" + TimeUtil.fancy(cooldownMillis()).trim() + "&d. " + progress()));
        }
    }

    /** handles every break of a placed Pinata; always cancels the vanilla break */
    public void onBreak(BlockBreakEvent event) {
        boolean protectedHere = event.isCancelled();
        event.setCancelled(true);
        if (protectedHere) return;
        Player player = event.getPlayer();
        if (pinataId == null) pinataId = UUID.randomUUID().toString();

        if (player.isSneaking()) {
            remove();
            getLocation().getWorld().dropItemNaturally(getLocation(), toItem());
            player.sendMessage(CC.translate("&d&l(!)&d You picked up the Pinata, it keeps its hits and cooldown. " + progress()));
            return;
        }
        if (!isReady()) {
            player.sendMessage(CC.translate("&c&l(!)&c This Pinata needs &f" + (hitsRequired() - hits) + "&c more hit(s) before it can be broken. " + progress()));
            player.sendMessage(CC.translate("&7Right-Click it to hit it, Sneak + Break to pick it up."));
            return;
        }
        if (isRedeemed(pinataId)) {
            remove();
            player.sendMessage(CC.translate("&c&l(!)&c This Pinata's treasure was already claimed."));
            return;
        }
        List<ItemStack> loot = rollLoot();
        if (loot.isEmpty()) {
            player.sendMessage(CC.translate("&c&l(!)&c The Pinata's loot has not been set up yet, it can be broken once it is."));
            return;
        }
        // marked before anything is dropped, so it can never pay out twice
        markRedeemed(pinataId);
        remove();
        Location at = getLocation().clone().add(0.5, 0.5, 0.5);
        for (ItemStack item : loot) at.getWorld().dropItemNaturally(at, item);
        at.getWorld().playSound(at, Sound.FIREWORK_BLAST, 1.0F, 1.0F);
        player.sendMessage(CC.translate("&d&l(!)&d The Pinata bursts open and spills its treasure!"));
    }

    private void remove() {
        getLocation().getBlock().setType(Material.AIR);
        CustomBlocks.getInstance().getCustomBlockManager().removeFromCache(this);
        // the data file goes now (not only at shutdown), so a crash can't bring a broken or picked-up Pinata back
        new File(CustomBlocks.getInstance().getDataFolder(), "data/" + getKey().replace(":", "%3A") + ".json").delete();
        CustomBlocks.getInstance().getCustomBlockManager().saveAll();
    }

    /** empty when the PINATA loot table doesn't exist or has no items */
    private static List<ItemStack> rollLoot() {
        List<ItemStack> loot = new ArrayList<>();
        LootTable table;
        try {
            table = LootTable.valueOf(LOOT_TABLE);
            if (LootTableAPI.getAllLootFromLootTable(table).isEmpty()) return loot;
        } catch (RuntimeException e) {
            return loot;
        }
        for (int i = 0; i < lootRolls(); i++) {
            ItemStack item = LootTableAPI.getRandomLootItem(table);
            if (item != null && item.getType() != Material.AIR) loot.add(item.clone());
        }
        return loot;
    }

    public static boolean isLootConfigured() {
        return !rollLoot().isEmpty();
    }

    // ---- item <-> block state
    public ItemStack toItem() {
        ItemStack item = CustomBlockType.SPRING_2021_LOOTINATA.build(0);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add(CC.translate("&dHits: &f" + Math.min(hits, hitsRequired()) + "&7/&f" + hitsRequired()));
        meta.setLore(lore);
        item.setItemMeta(meta);
        NBTWrapper nbtItem = new NBTWrapper(item);
        NBTTagCompoundBuilder cosmicData = new NBTTagCompoundBuilder(nbtItem.getNBTBase("cosmicData"));
        NBTTagCompoundBuilder data = new NBTTagCompoundBuilder(cosmicData.getNBTBase("customBlockData"));
        data.setString("pinataId", pinataId);
        data.setInt("pinataHits", hits);
        data.setLong("pinataLastHit", lastHit);
        cosmicData.setNBTBase("customBlockData", data.get());
        nbtItem.setNBTBase("cosmicData", cosmicData.get());
        return nbtItem.build();
    }

    /** takes over the state saved on a picked-up Pinata item; a brand new item gets a new id */
    public void loadFromItem(ItemStack item) {
        if (CustomBlocksAPI.isCustomBlockItem(item)) {
            NBTWrapper nbtItem = new NBTWrapper(item);
            NBTTagCompoundBuilder cosmicData = nbtItem.hasKey("cosmicData") ? new NBTTagCompoundBuilder(nbtItem.getNBTBase("cosmicData")) : new NBTTagCompoundBuilder();
            NBTTagCompoundBuilder data = cosmicData.hasKey("customBlockData") ? new NBTTagCompoundBuilder(cosmicData.getNBTBase("customBlockData")) : new NBTTagCompoundBuilder();
            if (data.hasKey("pinataId")) {
                pinataId = data.getString("pinataId");
                hits = data.getInt("pinataHits");
                lastHit = data.getLong("pinataLastHit");
            }
        }
        if (pinataId == null) pinataId = UUID.randomUUID().toString();
    }

    /** a Pinata whose id is already placed or was already redeemed (a copied item) can't be placed */
    public static boolean canPlace(String id) {
        if (id == null) return true;
        if (isRedeemed(id)) return false;
        for (CustomBlock block : CustomBlocks.getInstance().getCustomBlockManager().getCacheValues()) {
            if (block instanceof Pinata && id.equals(((Pinata) block).pinataId)) return false;
        }
        return true;
    }

    public static String idOf(ItemStack item) {
        if (!CustomBlocksAPI.isCustomBlockItem(item)) return null;
        NBTWrapper nbtItem = new NBTWrapper(item);
        if (!nbtItem.hasKey("cosmicData")) return null;
        NBTTagCompoundBuilder cosmicData = new NBTTagCompoundBuilder(nbtItem.getNBTBase("cosmicData"));
        if (!cosmicData.hasKey("customBlockData")) return null;
        NBTTagCompoundBuilder data = new NBTTagCompoundBuilder(cosmicData.getNBTBase("customBlockData"));
        return data.hasKey("pinataId") ? data.getString("pinataId") : null;
    }

    // ---- ids that already paid out
    private static File redeemedFile() {
        return new File(CustomBlocks.getInstance().getDataFolder(), REDEEMED_FILE);
    }

    private static boolean isRedeemed(String id) {
        return YamlConfiguration.loadConfiguration(redeemedFile()).getStringList("redeemed").contains(id);
    }

    private static void markRedeemed(String id) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(redeemedFile());
        List<String> ids = new ArrayList<>(yml.getStringList("redeemed"));
        ids.add(id);
        yml.set("redeemed", ids);
        try {
            yml.save(redeemedFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
