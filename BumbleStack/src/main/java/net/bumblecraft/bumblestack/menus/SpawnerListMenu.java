package net.bumblecraft.bumblestack.menus;

import net.bumblecraft.bumblestack.BumbleStack;
import net.bumblecraft.bumblestack.spawners.BumbleSpawner;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import net.bumblecraft.bumblestack.utils.menu.MenuManagerException;
import net.bumblecraft.bumblestack.utils.menu.MenuManagerNotSetupException;
import net.bumblecraft.bumblestack.utils.menu.PaginatedMenu;
import net.bumblecraft.bumblestack.utils.menu.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SpawnerListMenu extends PaginatedMenu {

    private List<BumbleSpawner> data;

    public SpawnerListMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        data = BumbleStack.getInstance().getManager().getSpawners();
        data.sort(Collections.reverseOrder());
    }

    @Override
    public String getMenuName() {
        return ChatUtils.translate("&6Spawner List");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {
        switch(e.getCurrentItem().getType()){
            case SPAWNER:

                if(data == null || data.isEmpty()) { return; }

                int index = e.getSlot();
                BumbleSpawner spawner = this.data.get(index);

                playerMenuUtility.getOwner().teleport(spawner.getLoc().clone().add(0,1,0));
                break;
            case BARRIER:
                playerMenuUtility.getOwner().closeInventory();
        }
    }

    @Override
    public List<?> getData() {
        return data;
    }

    @Override
    public void loopCode(Object object) {
        BumbleSpawner spawner = (BumbleSpawner) object;

        ItemStack spawnerItem = spawner.getItem().clone();
        ItemMeta spawnerMeta = spawnerItem.getItemMeta();
        spawnerMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        spawnerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spawnerMeta.setLore(Arrays.asList(
                ChatUtils.translate("&fLocation&6: &c"
                        + spawner.getLoc().getWorld().getName() + ";"
                        + spawner.getLoc().getBlockX() + ";"
                        + spawner.getLoc().getBlockY() + ";"
                        + spawner.getLoc().getBlockZ()),
                ChatUtils.translate("&fAmount&6: &c" + spawner.getData().getAmount()),
                ChatUtils.translate(""),
                ChatUtils.translate("&e&lCLICK TO TELEPORT")
        ));

        spawnerItem.setItemMeta(spawnerMeta);

        inventory.setItem(index, spawnerItem);
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }
}
