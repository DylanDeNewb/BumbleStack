package net.bumblecraft.bumblestack.spawners;

import lombok.Getter;
import net.bumblecraft.bumblestack.BumbleStack;
import net.bumblecraft.bumblestack.api.database.Serialize;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import net.bumblecraft.bumblestack.utils.MultiConsumer;
import net.bumblecraft.bumblestack.utils.Result;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BumbleSpawnerManager {

    private BumbleStack core;
    @Getter private List<BumbleSpawner> spawners;

    public BumbleSpawnerManager(BumbleStack core){
        this.core = core;
        this.spawners = new ArrayList<>();
    }

    public BumbleSpawner create(Block block, MultiConsumer<BumbleSpawner, Result> onComplete){
        if(!(block.getType() == Material.SPAWNER)){
            onComplete.accept(null, Result.INVALID);
            return null;
        }

        BumbleSpawner spawner = new BumbleSpawner(block);
        createSpawnerOf(true, block, spawner, (result -> {
            if(result == Result.FAILED){
                ChatUtils.log("&cCreation of Spawner Failed.");
                onComplete.accept(null, Result.FAILED);
            }else{
                onComplete.accept(spawner, Result.SUCCESS);
            }
        }));

        spawner.getSpawner().setSpawnCount(core.getSettings().getSPAWNER_AMOUNT_MIN());
        spawner.getSpawner().update();

        spawner.getData().setMob_count(spawner.getSpawner().getSpawnCount());
        spawner.getData().setDelay(spawner.getSpawner().getDelay());

        return spawner;
    }

    public void createSpawnerOf(boolean useExecutors, Block block, BumbleSpawner spawner, Consumer<Result> onComplete){
        if (useExecutors) core.getDatabase().getExecutorService().execute(() -> executeSpawnerCreate(block, spawner, onComplete));
        else executeSpawnerCreate(block, spawner, onComplete);
    }

    public void executeSpawnerCreate(Block block, BumbleSpawner spawner, Consumer<Result> onComplete){
        Connection connection = core.getDatabase().getConnection();
        PreparedStatement statement = null;
        Location loc = block.getLocation();

        try {

            String sql = "INSERT INTO `spawners` (x,y,z,world,data) VALUES (?,?,?,?,?)";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, loc.getBlockX());
            statement.setInt(2, loc.getBlockY());
            statement.setInt(3, loc.getBlockZ());
            statement.setString(4, loc.getWorld().getName());
            statement.setString(5, Serialize.GSON.toJson(spawner.getData()));
            statement.executeUpdate();

            onComplete.accept(Result.SUCCESS);

        } catch (SQLException e) {
            e.printStackTrace();
            ChatUtils.log("&cThere was an error with creating a spawner here.");
            onComplete.accept(Result.FAILED);
        }
    }

    public void deleteSpawnerOf(boolean useExecutors, BumbleSpawner spawner, Consumer<Result> onComplete){
        if (useExecutors) core.getDatabase().getExecutorService().execute(() -> executeSpawnerDelete(spawner, onComplete));
        else executeSpawnerDelete(spawner, onComplete);
    }

    public void executeSpawnerDelete(BumbleSpawner spawner, Consumer<Result> onComplete){
        Connection connection = core.getDatabase().getConnection();
        PreparedStatement statement = null;

        try {

            String sql = "DELETE FROM `spawners` WHERE x = ? AND y = ? AND z = ? AND world = ?";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, spawner.getLoc().getBlockX());
            statement.setInt(2, spawner.getLoc().getBlockY());
            statement.setInt(3, spawner.getLoc().getBlockZ());
            statement.setString(4, spawner.getLoc().getWorld().getName());
            statement.executeUpdate();

            onComplete.accept(Result.SUCCESS);

        } catch (SQLException e) {
            e.printStackTrace();
            ChatUtils.log("&cThere was an error with deleting a spawner here.");
            onComplete.accept(Result.FAILED);
        }
    }

    public void saveSpawnerOf(boolean useExecutors, BumbleSpawner spawner, Consumer<Result> onComplete){
        if (useExecutors) core.getDatabase().getExecutorService().execute(() -> executeSpawnerSave(spawner, onComplete));
        else executeSpawnerSave(spawner, onComplete);
    }

    public void executeSpawnerSave(BumbleSpawner spawner, Consumer<Result> onComplete){
        Connection connection = core.getDatabase().getConnection();
        PreparedStatement statement = null;

        try {

            String sql = "UPDATE `spawners` SET data = ? WHERE x = ? AND y = ? AND z = ? AND world = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Serialize.GSON.toJson(spawner.getData()));
            statement.setInt(2, spawner.getLoc().getBlockX());
            statement.setInt(3, spawner.getLoc().getBlockY());
            statement.setInt(4, spawner.getLoc().getBlockZ());
            statement.setString(5, spawner.getLoc().getWorld().getName());
            statement.executeUpdate();

            onComplete.accept(Result.SUCCESS);

        } catch (SQLException e) {
            e.printStackTrace();
            ChatUtils.log("&cThere was an error with saving a spawner here.");
            onComplete.accept(Result.FAILED);
        }
    }

    public void loadSpawners(boolean useExecutors, MultiConsumer<List<BumbleSpawner>, Result> onComplete){
        if (useExecutors) core.getDatabase().getExecutorService().execute(() -> executeSpawnersLoad(onComplete));
        else executeSpawnersLoad(onComplete);
    }

    public void executeSpawnersLoad(MultiConsumer<List<BumbleSpawner>, Result> onComplete){
        Connection connection = core.getDatabase().getConnection();
        PreparedStatement statement = null;

        List<BumbleSpawner> spawners = new ArrayList<>();

        try {

            String sql = "SELECT * FROM `spawners` ORDER BY id";

            statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                Location loc = new Location(Bukkit.getWorld(rs.getString(5)),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4));

                BumbleSpawner spawner = new BumbleSpawner(loc.getBlock());
                spawner.setData(Serialize.GSON.fromJson(rs.getString(6), Serialize.spawnerDataToken.getType()));

                spawners.add(spawner);

                ChatUtils.log("&fLoaded spawner at: &6"
                        + loc.getWorld().getName() + ";"
                        + loc.getBlockX() + ";"
                        + loc.getBlockY() + ";"
                        + loc.getBlockZ()
                );
            }

            onComplete.accept(spawners, Result.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            if(spawners.isEmpty()){
                onComplete.accept(spawners, Result.FAILED_AND_EMPTY);
            }else{
                onComplete.accept(spawners, Result.FAILED);
            }
        }
    }

    public BumbleSpawner getSpawner(Location loc){
        if(loc == null){ return null; }
        for(BumbleSpawner spawner : spawners){
            if(loc.equals(spawner.getLoc())) { return spawner; }
        }

        return null;
    }

    public boolean isSpawner(Location loc){
        if(loc == null){ return false; }
        for(BumbleSpawner spawner : spawners){
            if(loc.equals(spawner.getLoc())) { return true; }
        }

        return false;
    }
}
