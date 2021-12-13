package net.bumblecraft.bumblestack.api.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.bumblecraft.bumblestack.BumbleStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {

    private BumbleStack core;
    private FileConfiguration config;
    private HikariDataSource data;

    @Getter private Connection connection;
    @Getter private ExecutorService executorService;

    public Database(BumbleStack core){
        this.core = core;
        this.config = core.getConfigFile().getAsYaml();

        this.data = new HikariDataSource();
        data.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        data.addDataSourceProperty("serverName", config.getString("database.host"));
        data.addDataSourceProperty("port", config.getInt("database.port"));
        data.addDataSourceProperty("databaseName", config.getString("database.name"));
        data.addDataSourceProperty("user", config.getString("database.username"));
        data.addDataSourceProperty("password", config.getString("database.password"));

        this.connection = createConnection();
        this.executorService = Executors.newCachedThreadPool();

        createSpawnerTable();
    }

    private Connection createConnection(){
        try{
            return data.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getPluginManager().disablePlugin(core);
        return null;
    }

    private void createSpawnerTable(){
        try{
            String sql = "CREATE TABLE IF NOT EXISTS `spawners` (id INT NOT NULL AUTO_INCREMENT, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, world VARCHAR(36) NOT NULL, data MEDIUMTEXT, PRIMARY KEY(`id`))";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
