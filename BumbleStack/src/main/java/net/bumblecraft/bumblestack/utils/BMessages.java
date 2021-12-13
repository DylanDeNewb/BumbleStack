package net.bumblecraft.bumblestack.utils;

import lombok.Getter;
import lombok.Setter;
import net.bumblecraft.bumblestack.BumbleStack;
import org.bukkit.configuration.file.FileConfiguration;

public enum BMessages {

    PREFIX_SHORT("&5[&d&l✿&5] "),
    PREFIX_LONG("&6[&fBumbleStack&6] "),
    INSUFFICIENT_ENTITY("&fOnly players can run that command!"),
    INSUFFICIENT_PERMS("&fYou are &c&nnot&7 authorized to run this!"),
    SPAWNER_CREATE("&fSpawner successfully created!"),
    SPAWNER_ADDED("&fSuccessfully &a&nadded&f a spawner to the stack! &8(&7%total%&8)"),
    SPAWNER_DELETE("&fSpawner successfully removed!"),
    SPAWNER_REMOVED("&fSuccessfully &c&nremoved&f a spawner from stack! &8(&7%total%&8)"),
    SPAWNER_ERROR("&cEncountered an error, contact an &c&nadministrator&c!");

    @Getter @Setter
    private String message;

    BMessages(String message){
        this.message = ChatUtils.translate(message);
    }

    public static void update(BumbleStack core){
        FileConfiguration file = core.getMessages().getAsYaml();

        for(BMessages msg : BMessages.values()){
            String mesg = file.getString("messages." + msg.toString());
            if(mesg != null){
                msg.setMessage(ChatUtils.translate(mesg));
                ChatUtils.log("&7Loaded value&6: " + msg.getMessage());
            }
        }
    }
}
