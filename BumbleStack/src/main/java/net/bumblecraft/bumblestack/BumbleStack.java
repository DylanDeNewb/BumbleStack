package net.bumblecraft.bumblestack;

import lombok.Getter;
import net.bumblecraft.bumblestack.api.command.CmdLoader;
import net.bumblecraft.bumblestack.api.database.Database;
import net.bumblecraft.bumblestack.listeners.ListenerSpawners;
import net.bumblecraft.bumblestack.spawners.BumbleSpawner;
import net.bumblecraft.bumblestack.spawners.BumbleSpawnerManager;
import net.bumblecraft.bumblestack.utils.*;
import net.bumblecraft.bumblestack.utils.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class BumbleStack extends JavaPlugin {

    @Getter private BFile messages;
    @Getter private BFile configFile;

    private CmdLoader cmdLoader;
    @Getter private Database database;
    @Getter private BumbleSpawnerManager manager;

    @Getter private static BumbleStack instance;
    @Getter private BumbleStackSettings settings;

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.instance = this;

        this.cmdLoader = new CmdLoader(this);
        cmdLoader.load();

        this.database = new Database(this);

        this.settings = new BumbleStackSettings(this);
        settings.test();

        this.manager = new BumbleSpawnerManager(this);
        manager.loadSpawners(true, (bumbleSpawners, result) -> {

            if(result == Result.FAILED_AND_EMPTY) {
                ChatUtils.log("&7Spawner load failed, and was empty.");
                return;
            }

            for(BumbleSpawner spawner : bumbleSpawners){
                spawner.setItem(ItemUtils.fromBase64ToItemStack(spawner.getData().getItemBase64()));
                manager.getSpawners().add(spawner);
            }

            ChatUtils.log("&7Total of loaded spawners: &6" + manager.getSpawners().size());
        });

        events();

        MenuManager.setup(getServer(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for(BumbleSpawner spawners : this.getManager().getSpawners()){

            this.getManager().saveSpawnerOf(true, spawners, result -> {
                if(result == Result.FAILED) {
                    ChatUtils.log("&Failed to save spawner at: &6"
                            + spawners.getLoc().getWorld().getName() + ";"
                            + spawners.getLoc().getBlockX() + ";"
                            + spawners.getLoc().getBlockY() + ";"
                            + spawners.getLoc().getBlockZ()
                    );
                    return;
                }

                manager.getSpawners().remove(spawners);
            });
        }

        try {
            database.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        this.messages = new BFile(this, "messages.yml");
        this.configFile = new BFile(this, "config.yml");

        BMessages.update(this);
    }

    public void events(){
        Stream.of(
                new ListenerSpawners(this)
        ).forEach(event -> Bukkit.getPluginManager().registerEvents(event, this));
    }
}
