package net.bumblecraft.bumblestack.listeners;

import net.bumblecraft.bumblestack.BumbleStack;
import net.bumblecraft.bumblestack.menus.SpawnerMenu;
import net.bumblecraft.bumblestack.spawners.BumbleSpawner;
import net.bumblecraft.bumblestack.spawners.BumbleSpawnerData;
import net.bumblecraft.bumblestack.spawners.BumbleSpawnerManager;
import net.bumblecraft.bumblestack.utils.BMessages;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import net.bumblecraft.bumblestack.utils.ItemUtils;
import net.bumblecraft.bumblestack.utils.Result;
import net.bumblecraft.bumblestack.utils.menu.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ListenerSpawners implements Listener {

    private BumbleStack core;
    private BumbleSpawnerManager manager;
    private List<BlockFace> faces;

    public ListenerSpawners(BumbleStack core){
        this.core = core;
        this.manager = core.getManager();
        this.faces = new ArrayList<>();

        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
        this.faces.addAll(Arrays.asList(faces));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws MenuManagerNotSetupException, MenuManagerException {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        Action action = event.getAction();

        if(block == null || block.getType() != Material.SPAWNER){
            return;
        }

        if(action != Action.RIGHT_CLICK_BLOCK){ return; }
        if(!player.isSneaking()){ return; }

        if(!core.getManager().isSpawner(block.getLocation())) { return; }
        BumbleSpawner spawner = core.getManager().getSpawner(block.getLocation());

        MenuManager.getPlayerMenuUtility(player).setData(MenuData.SPAWNER, spawner);
        MenuManager.openMenu(SpawnerMenu.class, player);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if(!(block.getType() == Material.SPAWNER)){
            return;
        }

        if(!core.getManager().isSpawner(block.getLocation())) { return; }
        BumbleSpawner spawner = core.getManager().getSpawner(block.getLocation());

        ItemStack main = player.getInventory().getItemInMainHand();

        if(spawner.getItem() != null && main.getEnchantments().containsKey(Enchantment.SILK_TOUCH) && player.hasPermission("bumble.stack.silk")){
            block.getWorld().dropItemNaturally(block.getLocation(), spawner.getItem());
        }

        if(spawner.getData().getAmount() != 1){
            //Send removed one message
            spawner.getData().setAmount(spawner.getData().getAmount()-1);
            ChatUtils.send(player, BMessages.SPAWNER_REMOVED.getMessage().replace("%total%", String.valueOf(spawner.getData().getAmount())), true, true);
            event.setCancelled(true);
            return;
        }

        //Send spawner removed message
        //Delete spawner stuff from database
        core.getManager().deleteSpawnerOf(true, spawner, result -> {
            if(result == Result.FAILED){
                ChatUtils.log("&Failed to delete spawner at: &6"
                        + spawner.getLoc().getWorld().getName() + ";"
                        + spawner.getLoc().getBlockX() + ";"
                        + spawner.getLoc().getBlockY() + ";"
                        + spawner.getLoc().getBlockZ()
                );
                ChatUtils.send(player, BMessages.SPAWNER_ERROR, true, false);
                return;
            }
            ChatUtils.send(player, BMessages.SPAWNER_DELETE, true, true);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            core.getManager().getSpawners().remove(spawner);
        });
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(!(block.getType() == Material.SPAWNER)) {
            return;
        }

        CreatureSpawner bspawner = (CreatureSpawner) block.getState();
        AtomicBoolean complete = new AtomicBoolean(false);

        faces.forEach(face -> {

            if(complete.get()){ return; }

            Block brel = block.getRelative(face);

            if(brel.getType() == Material.SPAWNER){
                if(core.getManager().isSpawner(brel.getLocation())){

                    BumbleSpawner spawner = manager.getSpawner(brel.getLocation());
                    BumbleSpawnerData data = spawner.getData();

                    if(!item.isSimilar(spawner.getItem())){
                        complete.set(true);
                        event.setCancelled(true);
                        return;
                    }

                    if(!(bspawner.getSpawnedType() == spawner.getSpawner().getSpawnedType())){
                        complete.set(true);
                        event.setCancelled(true);
                        return;
                    }

                    if(data.getAmount() >= core.getSettings().getSPAWNER_LIMIT()){
                        complete.set(true);
                        event.setCancelled(true);
                        return;
                    }

                    item.setAmount(item.getAmount()-1);
                    data.setAmount(data.getAmount() + 1);

                    if(data.getAmount() >= core.getSettings().getSPAWNER_LIMIT()/2){
                        spawner.getSpawner().setSpawnCount(core.getSettings().getSPAWNER_AMOUNT_MAX());
                        spawner.getSpawner().update();
                    }

                    ChatUtils.send(player, BMessages.SPAWNER_ADDED.getMessage().replace("%total%", String.valueOf(spawner.getData().getAmount())),true, true);
                    complete.set(true);
                    event.setCancelled(true);
                }
            }
        });

        if(!complete.get()){
            BumbleSpawner spawner = create(player, block, item.clone());
        }
    }

    private BumbleSpawner create(Player player, Block block, ItemStack item){
        BumbleSpawner spawner = manager.create(block, (bumbleSpawner, result) -> {
            if(result == Result.SUCCESS){

                item.setAmount(1);

                bumbleSpawner.setItem(item);
                bumbleSpawner.getData().setItemBase64(ItemUtils.toBase64(item));

                Location sBlock = bumbleSpawner.getLoc();
                manager.getSpawners().add(bumbleSpawner);
                ChatUtils.log("&7New spawner at: &6"
                        + sBlock.getWorld().getName() + ";"
                        + sBlock.getBlockX() + ";"
                        + sBlock.getBlockY() + ";"
                        + sBlock.getBlockZ()
                );
                ChatUtils.send(player, BMessages.SPAWNER_CREATE, true, true);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }else if(result == Result.FAILED){
                ChatUtils.send(player, BMessages.SPAWNER_ERROR, true, false);
            }
        });
        return spawner;
    }
}
