package net.bumblecraft.bumblestack.api.command;

import net.bumblecraft.bumblestack.BumbleStack;
import net.bumblecraft.bumblestack.CommandStack;

import java.util.stream.Stream;

public class CmdLoader {

    private final BumbleStack core;
    private CmdWrapper map;

    public CmdLoader(BumbleStack core){
        this.core = core;
    }

    public void load(){
        try {
            map = new CmdWrapper();
        } catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }

        Stream.of(
                new CommandStack(core)
        ).forEach(command -> map.load(command));

    }

}
