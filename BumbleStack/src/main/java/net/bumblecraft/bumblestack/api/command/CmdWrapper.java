package net.bumblecraft.bumblestack.api.command;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class CmdWrapper {

    private final CommandMap map;

    public CmdWrapper() throws NoSuchFieldException, IllegalAccessException {
        Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        this.map = (CommandMap) field.get(Bukkit.getServer());
    }

    public void load(Cmd command){
        Validate.notNull(map, "commandMap null");
        map.register("newbs", new createCommand(command));
    }

    class createCommand extends Command {

        private final Cmd command;

        createCommand(Cmd command){
            super(command.getCommand(), "", "/" + command.getCommand(), command.getAliases());
            this.command = command;
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            return command.onCommand(sender, this, s, args);
        }

        @Override
        public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            return Objects.requireNonNull(command.onTabComplete(sender, this, alias, args));
        }
    }

}
