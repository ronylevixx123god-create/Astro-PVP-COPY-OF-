package net.astropvp.armorsets.listeners;

import com.cosmicpvp.cosmicutils.nbt.NBTTagCompoundBuilder;
import com.cosmicpvp.cosmicutils.nbt.NBTTagListBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.ItemUtils;
import com.cosmicpvp.cosmicutils.utils.NBTWrapper;
import com.google.common.collect.Lists;
import net.astropvp.armorsets.struct.antiMkit.AntiMkit;
import org.arkhamnetwork.enchantments.EnchantmentBook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

// a Mastery Shard dropped onto an enchantment book, from CosmicArmorSets (com.compilex.CosmicArmorSets.listeners.MasteryShardListener);
// the book's conaxData masteryShard list is what EnchantmentBookListener checks before applying it to Mastery Kit armor
public class MasteryShardListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.BOOK)
            return;
        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType() == Material.AIR)
            return;
        if (event.getWhoClicked().getInventory() == null || event.getWhoClicked().getOpenInventory() == null || !event.getWhoClicked().getInventory().equals(event.getWhoClicked().getOpenInventory().getBottomInventory()))
            return;
        if (event.getWhoClicked().getWorld().getName().startsWith("world_duels"))
            return;
        NBTWrapper cursorWrapper = new NBTWrapper(cursor);
        if (!cursorWrapper.getString("cosmicType").equals("MASTERY_SHARD") || !EnchantmentBook.isEnchantmentBook(clicked))
            return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        AntiMkit set = AntiMkit.getSetFromMasteryShard(cursor);
        if (set == null)
            return;
        // one shard imbues one book: a stack of books would all be upgraded by a single shard
        if (clicked.getAmount() > 1) {
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "You can only apply a Mastery Shard to one enchantment book at a time!");
            return;
        }
        if (cursorWrapper.isBlacklistedIID()) {
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "This Mastery Shard has already been used!");
            return;
        }
        NBTWrapper clickedWrapper = new NBTWrapper(clicked);
        NBTTagCompoundBuilder clickedData = clickedWrapper.getCompoundOrCreate("conaxData");
        NBTTagListBuilder shardList = !clickedData.hasKey("masteryShard") ? new NBTTagListBuilder() : new NBTTagListBuilder(clickedData.getNBTBase("masteryShard"));
        if (shardList.contains(set.name())) {
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "This book already has this Mastery Shard equipped!");
            return;
        }
        shardList.addString(set.name());
        clickedData.setNBTBase("masteryShard", shardList.build());
        clickedWrapper.setNBTBase("conaxData", clickedData.get());
        ItemStack newBook = clickedWrapper.build();
        ItemMeta im = newBook.getItemMeta();
        List<String> lore = im.getLore() == null ? Lists.newArrayList() : im.getLore();
        lore.add(CC.GoldB + "* Mastery Shard " + CC.Gold + "(" + set.getDisplayName() + CC.Gold + ")");
        im.setLore(lore);
        newBook.setItemMeta(im);
        cursorWrapper.blacklistIID("mastery shard player: " + player.getName());
        if (cursor.getAmount() > 1) {
            cursor.setAmount(cursor.getAmount() - 1);
            player.setItemOnCursor(cursor);
        } else {
            player.setItemOnCursor(null);
        }
        event.setCurrentItem(newBook);
        player.sendMessage(CC.GoldB + "(!) " + CC.GoldU + ChatColor.stripColor(set.getDisplayName()) + " Mastery Shard" + CC.Gold + " applied to your " + ItemUtils.getDisplayName(clicked) + CC.Gold + "!");
        player.sendMessage(CC.Gray + "This book is now powerful enough to attempt to apply to the " + set.getDisplayName() + CC.Gray + " /mkit items!");
        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.1f);
    }
}
