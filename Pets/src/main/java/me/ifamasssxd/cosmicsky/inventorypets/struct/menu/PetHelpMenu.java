package me.ifamasssxd.cosmicsky.inventorypets.struct.menu;

import com.cosmicpvp.cosmicutils.gui.CosmicGUI;
import com.cosmicpvp.cosmicutils.gui.InventoryItem;
import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.PlayerUtil;
import me.ifamasssxd.cosmicsky.inventorypets.CosmicSkyInventoryPets;
import me.ifamasssxd.cosmicsky.inventorypets.struct.InventoryPets;
import org.bukkit.entity.Player;

public class PetHelpMenu extends CosmicGUI {
   /** Lets staff take pets out of the menu; everyone else can only look. */
   public static final String ADMIN_PERMISSION = "cosmicpets.admin";

   public PetHelpMenu(Player player) {
      super(player, "Pet Help", 45);
   }

   public void redraw() {
      int index = 0;
      InventoryPets[] var2 = InventoryPets.values();
      InventoryPets[] array = var2;
      int length = var2.length;

      for(int i = 0; i < length; ++i) {
         InventoryPets pet = array[i];
         this.setItem(index++, new InventoryItem(CosmicSkyInventoryPets.get().getPetItem().createItem(null, pet)).click(() -> {
            if (!this.player.hasPermission(ADMIN_PERMISSION)) {
               return;
            }
            // a new pet item (own item ID), not the menu icon
            PlayerUtil.giveOrDropProtectedItem(this.player, CosmicSkyInventoryPets.get().getPetItem().createItem(null, pet), -1, null, CC.RedB + "(!) " + CC.Red + "Your pet has been dropped on the floor!");
            this.player.sendMessage(CC.GreenB + "(!)" + CC.Green + " You took a " + pet.getItemName() + CC.Green + ".");
         }));
      }

   }
}
