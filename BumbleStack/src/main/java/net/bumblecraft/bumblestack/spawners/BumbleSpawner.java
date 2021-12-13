package net.bumblecraft.bumblestack.spawners;

import lombok.Getter;
import lombok.Setter;
import net.bumblecraft.bumblestack.BumbleStack;
import net.bumblecraft.bumblestack.BumbleStackSettings;
import net.bumblecraft.bumblestack.utils.BMessages;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import net.bumblecraft.bumblestack.utils.Result;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BumbleSpawner implements Comparable<BumbleSpawner> {

    @NotNull @Getter private Location loc;
    @Getter @Setter private BumbleSpawnerData data;
    @Getter @Setter private ItemStack item;

    public BumbleSpawner(Block block){
        this.loc = block.getLocation();
        this.data = new BumbleSpawnerData();
    }

    public BumbleSpawner(Location loc){
        this.loc = loc;
        this.data = new BumbleSpawnerData();
    }

    public CreatureSpawner getSpawner(){
        if(!(loc.getBlock().getState() instanceof CreatureSpawner)){
            return null;
        }
        return (CreatureSpawner) loc.getBlock().getState();
    }

    @Override
    public int compareTo(@NotNull BumbleSpawner o) {
        return this.getData().getAmount().compareTo(o.getData().getAmount());
    }
}
