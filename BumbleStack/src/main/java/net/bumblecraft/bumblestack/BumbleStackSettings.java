package net.bumblecraft.bumblestack;

import lombok.Getter;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class BumbleStackSettings {

    private BumbleStack core;
    private FileConfiguration config;

    @Getter private int SPAWNER_LIMIT;
    @Getter private int SPAWNER_AMOUNT_MIN;
    @Getter private int SPAWNER_AMOUNT_MAX;

    public BumbleStackSettings(BumbleStack core){
        this.core = core;
        this.config = core.getConfigFile().getAsYaml();
    }

    public void test(){
        SPAWNER_LIMIT = config.getInt("spawners.limit");
        SPAWNER_AMOUNT_MIN = config.getInt("spawners.mob-amount.min");
        SPAWNER_AMOUNT_MAX = config.getInt("spawners.mob-amount.max");
    }
}
