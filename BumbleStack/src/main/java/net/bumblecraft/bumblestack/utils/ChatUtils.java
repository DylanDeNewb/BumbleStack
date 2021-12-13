package net.bumblecraft.bumblestack.utils;

import net.bumblecraft.bumblestack.BumbleStack;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class ChatUtils {

    private static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void log(String message){
        console.sendMessage(BMessages.PREFIX_LONG.getMessage() + translate(message));
    }

    public static void log(BMessages message){
        console.sendMessage(BMessages.PREFIX_LONG.getMessage() + message.getMessage());
    }

    public static void send(Player player, String message, boolean prefix, boolean action){
        String content = "";
        if(prefix){ content = BMessages.PREFIX_SHORT.getMessage() + translate(message); }
        else{
            content = translate(message);
        }

        if(action){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(content));
        }else{
            player.sendMessage(content);
        }
    }

    public static void send(Player player, BMessages message, boolean prefix, boolean action){
        String content = "";
        if(prefix){ content = BMessages.PREFIX_SHORT.getMessage() + translate(message.getMessage()); }
        else{
            content = message.getMessage();
        }

        if(action){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(content));
        }else{
            player.sendMessage(content);
        }
    }

    public static String translate(String message){
        final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

}
