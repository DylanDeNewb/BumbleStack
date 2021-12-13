package net.bumblecraft.bumblestack;

import net.bumblecraft.bumblestack.api.command.Cmd;
import net.bumblecraft.bumblestack.spawners.BumbleSpawner;
import net.bumblecraft.bumblestack.spawners.BumbleSpawnerData;
import net.bumblecraft.bumblestack.utils.BMessages;
import net.bumblecraft.bumblestack.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandStack extends Cmd {

    private BumbleStack core;

    public CommandStack(BumbleStack core){
        this.core = core;
    }

    @Override
    public void command(Player player, String[] args) {
        if(args.length == 0){ help(player); }
        else{
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("check")){ check(player); }
                if(args[0].equalsIgnoreCase("reload")) { reload(player); }
            }
        }
    }

    public void help(Player player){

    }

    public void reload(Player player){
        core.getConfigFile().reload(false);
        core.getMessages().reload(false);

        core.getSettings().test();
        BMessages.update(core);

        ChatUtils.send(player, "&fReloaded configuration",true, false);
    }

    public void check(Player player){
        player.getLineOfSight(null, 5).stream()
                .filter(block -> block.getType() != Material.AIR)
                .forEach(spawner -> {
                    if(!core.getManager().isSpawner(spawner.getLocation())){ return; }

                    BumbleSpawner bSpawner = core.getManager().getSpawner(spawner.getLocation());
                    BumbleSpawnerData data = bSpawner.getData();

                    ChatUtils.send(player, "", true, false);
                    ChatUtils.send(player, "&fSpawner Information:", true, false);
                    ChatUtils.send(player, "&fAmount: &6" + data.getAmount(), true, false);
                    ChatUtils.send(player, "&fDelay: &6" + data.getDelay(), true, false);
                    ChatUtils.send(player, "&fMobs: &6" + data.getMob_count(), true, false);
                    ChatUtils.send(player, "", false, false);
                });
    }

    @Override
    public void command(ConsoleCommandSender console, String[] args) {
        ChatUtils.log(BMessages.INSUFFICIENT_ENTITY);
    }

    @Override
    public List<String> tabcomplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return new ArrayList<>(0);
    }

    @Override
    public BumbleStack getCore() {
        return core;
    }

    @Override
    public String getCommand() {
        return "stack";
    }

    @Override
    public String getPermission() {
        return "bumble.stacker";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(0);
    }
}
