package net.bumblecraft.bumblestack.menus;

import net.bumblecraft.bumblestack.spawners.BumbleSpawner;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import net.bumblecraft.bumblestack.utils.menu.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SpawnerMenu extends Menu {

    private BumbleSpawner spawner;

    public SpawnerMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.spawner = (BumbleSpawner) playerMenuUtility.getData(MenuData.SPAWNER);
    }

    @Override
    public String getMenuName() {
        return ChatUtils.translate("&6Spawner Interaction");
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {
        switch(e.getCurrentItem().getType()){
            case RED_STAINED_GLASS_PANE:
                MenuManager.openMenu(SpawnerListMenu.class, playerMenuUtility.getOwner());
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {

        int[] slots = {0,1,2,3,4,5,6,7,8,9,17,18,19,20,21,22,23,24,25,26};

        for(int slot : slots) {
            inventory.setItem(slot, super.FILLER_GLASS);
        }

        ItemStack backItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(ChatUtils.translate("&cBack"));
        backMeta.setLore(Arrays.asList(
                ChatUtils.translate("&fReturn to spawner list...")
        ));

        backItem.setItemMeta(backMeta);

        if(playerMenuUtility.getOwner().hasPermission("bumble.stacker")) { inventory.setItem(0, backItem); }


        ItemStack spawnerItem = spawner.getItem().clone();
        ItemMeta spawnerMeta = spawnerItem.getItemMeta();
        spawnerMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        spawnerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spawnerMeta.setLore(Arrays.asList(
                ChatUtils.translate("&fAmount&6: &c" + spawner.getData().getAmount())
        ));

        spawnerItem.setItemMeta(spawnerMeta);

        inventory.setItem(13, spawnerItem);
    }
}
