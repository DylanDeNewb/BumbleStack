package net.bumblecraft.bumblestack.api.command;

import net.bumblecraft.bumblestack.BumbleStack;
import net.bumblecraft.bumblestack.utils.BMessages;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Cmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args){

        if(sender instanceof Player){

            Player p = (Player) sender;

            if(p.hasPermission(getPermission())){
                command(p, args);
                return true;
            } else{
                ChatUtils.send(p, BMessages.INSUFFICIENT_PERMS, true, false);
                return false;
            }
        }else if(sender instanceof ConsoleCommandSender){

            ConsoleCommandSender cp = (ConsoleCommandSender) sender;

            command(cp, args);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
        return tabcomplete(sender, cmd, alias,args);
    }

    public abstract void command(Player player, String[] args);

    public abstract void command(ConsoleCommandSender console, String[] args);

    public abstract List<String> tabcomplete(CommandSender sender, Command cmd, String alias, String[] args);

    public abstract BumbleStack getCore();

    public abstract String getCommand();

    public abstract String getPermission();

    public abstract List<String> getAliases();

}
