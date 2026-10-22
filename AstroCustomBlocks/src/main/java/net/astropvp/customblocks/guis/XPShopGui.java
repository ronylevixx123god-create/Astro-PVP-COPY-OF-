package net.astropvp.customblocks.guis;



import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.ItemUtils;
import com.google.common.collect.Lists;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.ifamasssxd.cosmicguishop.CosmicGUIShop;
import net.astropvp.customblocks.CustomBlocks;
import net.astropvp.customblocks.struct.impl.DarkMatter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class XPShopGui{

    private final PaginatedGui expShopUI;

    public PaginatedGui getExpShopUI() {
        return this.expShopUI;
    }



    public XPShopGui(Player player) {
        this(player, null);
    }

    /** storage: the clicked Dark Matter, whose stored EXP can be deposited and withdrawn here */
    public XPShopGui(Player player, DarkMatter storage) {

        this.expShopUI = new PaginatedGui(6, "EXP Space Storage");
        expShopUI.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        expShopUI.getFiller().fillBottom(new GuiItem(createFillerItem()));

        ItemStack prevPage = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prevPage.getItemMeta();
        prevMeta.setDisplayName(CC.translate("&c<- Previous Page"));
        List<String> prevLore = new ArrayList<>();
        prevLore.add(CC.translate("&7Right-Click&7 to go to the previous page."));
        prevMeta.setLore(prevLore);
        prevPage.setItemMeta(prevMeta);

        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPage.getItemMeta();
        nextMeta.setDisplayName(CC.translate("&aNext Page ->"));
        List<String> nextLore = new ArrayList<>();
        nextLore.add(CC.translate("&7Right-Click&7 to go to the next page."));
        nextMeta.setLore(nextLore);
        nextPage.setItemMeta(nextMeta);

        expShopUI.setItem(6, 4, new GuiItem(prevPage, event -> {
            expShopUI.previous();
            event.setCancelled(true);
        }));

        expShopUI.setItem(6, 6, new GuiItem(nextPage, event -> {
            expShopUI.next();
            event.setCancelled(true);
        }));

        expShopUI.setItem(6, 5, new GuiItem(createGUIItem(player, storage)));
        if (storage != null) {
            expShopUI.setItem(6, 1, new GuiItem(createStorageButton(true), event -> {
                int exp = player.getTotalExperience();
                if (exp <= 0) {
                    player.sendMessage(CC.Red + "You do not have any exp to deposit.");
                    return;
                }
                player.setTotalExperience(0);
                player.setExp(0);
                player.setLevel(0);
                storage.addExp(exp);
                CustomBlocks.getInstance().getCustomBlockManager().saveAll();
                player.sendMessage(CC.translate("&a&l(!)&a Deposited &f" + NumberFormat.getInstance().format(exp) + "&a XP into the Dark Matter."));
                expShopUI.updateItem(6, 5, createGUIItem(player, storage));
            }));
            expShopUI.setItem(6, 9, new GuiItem(createStorageButton(false), event -> {
                int exp = storage.takeExp(Integer.MAX_VALUE - Math.max(0, player.getTotalExperience()));
                if (exp <= 0) {
                    player.sendMessage(CC.Red + "There is no exp stored in this Dark Matter.");
                    return;
                }
                player.giveExp(exp);
                CustomBlocks.getInstance().getCustomBlockManager().saveAll();
                player.sendMessage(CC.translate("&a&l(!)&a Withdrew &f" + NumberFormat.getInstance().format(exp) + "&a XP from the Dark Matter."));
                expShopUI.updateItem(6, 5, createGUIItem(player, storage));
            }));
        }


        for (Map.Entry<ItemStack, Float> s : CosmicGUIShop.getInstance().getItemListSorted().entrySet()) {
            ItemStack itemStack = s.getKey();
            ItemStack guiItem = itemStack.clone();
            ItemMeta meta = guiItem.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(CC.translate("&f&l&c&b"));
            lore.add(CC.translate("&a&l* COST: " + CC.White + NumberFormat.getInstance().format(getDiscounted(s.getValue())) + CC.GreenB + " XP &7(" + NumberFormat.getInstance().format(s.getValue()*64) + " XP/stack)"));
            lore.add(CC.translate("&f&l&c&b"));
            meta.setLore(lore);
            guiItem.setItemMeta(meta);
            expShopUI.addItem(new GuiItem(guiItem.clone(), event -> {
                float cost = getDiscounted(s.getValue());
                if (cost == 0)
                    return;
                ItemStack bought = itemStack.clone();
                if (event.isShiftClick()) {
                    cost *=64;
                    bought.setAmount(64);
                }
                if (player.getTotalExperience() < cost) {
                    player.sendMessage(CC.Red + "You do not have enough exp to purchase this item.");
                } else {
                    int exp = (int) (player.getTotalExperience() - cost);
                    player.setTotalExperience(0);
                    player.setExp(0);
                    player.setLevel(0);
                    player.giveExp(exp);

                    player.getInventory().addItem(bought);
                    player.updateInventory();
                    if (event.isShiftClick()) {
                        player.sendMessage(CC.translate("&a&l(!)&a " + "Purchased " + ItemUtils.getDisplayName(itemStack) + " (x64) " + CC.Green + " for &f" + NumberFormat.getInstance().format((int) cost) + "&a XP!"));
                    } else {
                        player.sendMessage(CC.translate("&a&l(!)&a " + "Purchased " + ItemUtils.getDisplayName(itemStack) + CC.Green + " for &f" + NumberFormat.getInstance().format((int) cost) + "&a XP!"));

                    }
                }
            }));
        }



    }

    private float getDiscounted(float price) {
        return price * 0.9F;
    }




    public ItemStack createGUIItem(Player player) {
        return createGUIItem(player, null);
    }

    public ItemStack createGUIItem(Player player, DarkMatter storage) {
        ItemStack item = new ItemStack(Material.EXP_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CC.GreenB + "Astro EXP Shop");
        List<String> lore = Lists.newArrayList();
        lore.add(CC.Gray + "This shop contains various items that will help");
        lore.add(CC.Gray + "you set up on AstroPvP");
        lore.add("");
        lore.add(CC.Green + "EXP Storage: " + CC.White + NumberFormat.getInstance().format(player.getTotalExperience()));
        if (storage != null) {
            lore.add(CC.Green + "Stored in this Dark Matter: " + CC.White + NumberFormat.getInstance().format(storage.getStoredExp()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack createStorageButton(boolean deposit) {
        ItemStack item = new ItemStack(deposit ? Material.EXP_BOTTLE : Material.GLASS_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(deposit ? CC.GreenB + "Deposit EXP" : CC.YellowB + "Withdraw EXP");
        meta.setLore(Lists.newArrayList(CC.Gray + (deposit ? "Click to store all of your EXP in this Dark Matter." : "Click to take all of the stored EXP.")));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createFillerItem() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CC.translate("&f"));
        item.setItemMeta(meta);
        return item;
    }

}


