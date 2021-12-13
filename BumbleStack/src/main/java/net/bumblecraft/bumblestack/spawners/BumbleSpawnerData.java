package net.bumblecraft.bumblestack.spawners;

import lombok.Data;

@Data
public class BumbleSpawnerData {

    private Integer amount = 1;
    private int delay = 20;
    private int mob_count = 4;
    private String itemBase64 = "";

}
