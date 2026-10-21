package net.astropvp.core.guis;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.astropvp.core.items.RevokeScratchCard;
import net.astropvp.core.items.RogueScratchCard;
import net.astropvp.core.items.SlotbotScratchCard;
import net.astropvp.core.utils.GUIBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MerchantGUI extends GUIBuilder {
    public MerchantGUI(Player player) {
        super(player);
    }

    @Override
    protected String getName() {
        return CC.translate("&8Scratch Card Merchant");
    }

    @Override
    protected int getSlots() {
        return 9;
    }

    @Override
    protected void setItems() {
        this.inventory.setItem(0, getItems(0));
        this.inventory.setItem(1, getItems(1));
        this.inventory.setItem(2, getItems(2));
    }

    @Override
    protected ItemStack getItems(int slot) {
        if (slot == 0) return withPrice(RogueScratchCard.getRogueSratchCard(), "&c&l* PRICE: &f$1,000,000,000");
        if (slot == 1) return withPrice(RevokeScratchCard.getRogueSratchCard(), "&c&l* PRICE: &f$100,000,000");
        if (slot == 2) return withPrice(SlotbotScratchCard.getRogueSratchCard(), "&c&l* PRICE: &f$150,000,000");
        return null;
    }

    private ItemStack withPrice(ItemStack item, String price) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add("");
        lore.add(CC.translate(price));
        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void click(InventoryClickEvent event) throws UserDoesNotExistException, NoLoanPermittedException {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        switch (event.getSlot()) {
            case 0:
                buyItem("rogue_scratchcard", 1000000000, this.player);
                setItems();
                break;
            case 1:
                buyItem("revoke_scratchcard", 100000000, this.player);
                setItems();
                break;
            case 2:
                buyItem("slotbot_scratchcard", 150000000, this.player);
                setItems();
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void buyItem(String item, int price, Player player) throws UserDoesNotExistException, NoLoanPermittedException {
        String name = player.getName();
        double money = Economy.getMoney(name);
        if (money < price) {
            player.sendMessage(CC.translate("&c&l(!)&c You don't have enough money to purchase this Scratch Card."));
            player.sendMessage(CC.translate("&c&l* &cRequired: &f" + Economy.format(price - money)));
            player.closeInventory();
            return;
        }
        Economy.subtract(name, price);
        player.sendMessage(CC.translate("&a&l(!)&a You have purchased an item from the Scratch Card Merchant."));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "customitems give " + player.getName() + " " + item + " 1");
    }
}
