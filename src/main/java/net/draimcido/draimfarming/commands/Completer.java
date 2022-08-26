package net.draimcido.draimfarming.commands;

import net.draimcido.draimfarming.ConfigReader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Completer implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender.isOp() || sender.hasPermission("draimfarming.admin"))){
            return null;
        }
        if (args.length == 1) {
            List<String> arrayList = new ArrayList<>();
            for (String cmd : Arrays.asList("backup", "forcegrow", "forcesave", "forcewater", "reload", "setseason")) {
                if (cmd.startsWith(args[0]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("setseason") && args.length == 2){
            List<String> arrayList = new ArrayList<>();
            for (String cmd : ConfigReader.Config.worldNames) {
                if (cmd.startsWith(args[1]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("forcesave") && args.length == 2){
            List<String> arrayList = new ArrayList<>();
            if (ConfigReader.Season.enable){
                if (ConfigReader.Season.seasonChange){
                    for (String cmd : Arrays.asList("all","crop","pot","sprinkler")) {
                        if (cmd.startsWith(args[1]))
                            arrayList.add(cmd);
                    }
                }else{
                    for (String cmd : Arrays.asList("all","crop","pot","season","sprinkler")) {
                        if (cmd.startsWith(args[1]))
                            arrayList.add(cmd);
                    }
                }
            }else {
                for (String cmd : Arrays.asList("all","crop","pot","sprinkler")) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("setseason") && args.length == 3){
            List<String> arrayList = new ArrayList<>();
            for (String cmd : Arrays.asList("spring","summer","autumn","winter")) {
                if (cmd.startsWith(args[2]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("forcegrow") || args[0].equalsIgnoreCase("forcewater")){
            List<String> arrayList = new ArrayList<>();
            for (String cmd : ConfigReader.Config.worldNames) {
                if (cmd.startsWith(args[1]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        return null;
    }
}