package net.draimcido.draimfarming.commands;

import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.utils.AdventureManager;
import net.draimcido.draimfarming.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class Executor implements CommandExecutor {

    private final Main plugin;

    public Executor(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender.hasPermission("draimfarming.admin") || sender.isOp())){
            AdventureManager.playerMessage((Player) sender, ConfigReader.Message.prefix + ConfigReader.Message.noPerm);
            return true;
        }

        if (args.length < 1) {
            lackArgs(sender);
            return true;
        }
        switch (args[0]){
            case "reload" -> {
                long time = System.currentTimeMillis();
                ConfigReader.reloadConfig();
                if(sender instanceof Player){
                    AdventureManager.playerMessage((Player) sender,ConfigReader.Message.prefix + ConfigReader.Message.reload.replace("{time}", String.valueOf(System.currentTimeMillis() - time)));
                }else {
                    AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.reload.replace("{time}", String.valueOf(System.currentTimeMillis() - time)));
                }
                return true;
            }
            case "forcegrow" -> {
                if (args.length < 2) {
                    lackArgs(sender);
                    return true;
                }
                Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, ()-> {
                    switch (ConfigReader.Config.growMode){
                        case 1 -> plugin.getCropManager().growModeOne(args[1]);
                        case 2 -> plugin.getCropManager().growModeTwo(args[1]);
                        case 3 -> plugin.getCropManager().growModeThree(args[1]);
                        case 4 -> plugin.getCropManager().growModeFour(args[1]);
                    }
                });
                if (sender instanceof Player player){
                    AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.forceGrow.replace("{world}",args[1]));
                }else {
                    AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.forceGrow.replace("{world}",args[1]));
                }
                return true;
            }
            case "forcewater" -> {
                if (args.length < 2) {
                    lackArgs(sender);
                    return true;
                }
                Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, ()-> {
                    switch (ConfigReader.Config.growMode){
                        case 1 -> plugin.getSprinklerManager().workModeOne(args[1]);
                        case 2 -> plugin.getSprinklerManager().workModeTwo(args[1]);
                        case 3 -> plugin.getSprinklerManager().workModeThree(args[1]);
                        case 4 -> plugin.getSprinklerManager().workModeFour(args[1]);
                    }
                });
                if (sender instanceof Player player){
                    AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.forceWater.replace("{world}",args[1]));
                }else {
                    AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.forceWater.replace("{world}",args[1]));
                }
                return true;
            }
            case "forcesave" -> {
                if (args.length < 2) {
                    lackArgs(sender);
                    return true;
                }
                Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, ()->{
                    switch (args[1]){
                        case "all" -> {
                            plugin.getSprinklerManager().updateData();
                            plugin.getSprinklerManager().saveData();
                            if (ConfigReader.Season.enable && !ConfigReader.Season.seasonChange){
                                plugin.getSeasonManager().saveData();
                            }
                            plugin.getCropManager().updateData();
                            plugin.getCropManager().saveData();
                            plugin.getPotManager().saveData();
                            forceSave(sender);
                        }
                        case "crop" -> {
                            plugin.getCropManager().updateData();
                            plugin.getCropManager().saveData();
                            forceSave(sender);
                        }
                        case "pot" -> {
                            plugin.getPotManager().saveData();
                            forceSave(sender);
                        }
                        case "season" -> {
                            plugin.getSeasonManager().saveData();
                            forceSave(sender);
                        }
                        case "sprinkler" -> {
                            plugin.getSprinklerManager().updateData();
                            plugin.getSprinklerManager().saveData();
                            forceSave(sender);
                        }
                    }
                });
            }
            case "backup" -> {
                FileUtils.backUpData();
                if (sender instanceof Player player){
                    AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.backUp);
                }else {
                    AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.backUp);
                }
                return true;
            }
            case "cleandata" -> {
                plugin.getCropManager().cleanData();
                plugin.getSprinklerManager().cleanData();
                return true;
            }
            case "setseason" -> {
                if (args.length < 3) {
                    lackArgs(sender);
                    return true;
                }
                if (plugin.getSeasonManager().setSeason(args[1], args[2])){
                    if (sender instanceof Player player){
                        AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.setSeason.replace("{world}",args[1]).replace("{season}",args[2]));
                    }else {
                        AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.setSeason.replace("{world}",args[1]).replace("{season}",args[2]));
                    }
                }else {
                    if (sender instanceof Player player){
                        AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.wrongArgs);
                    }else {
                        AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.wrongArgs);
                    }
                }
                return true;
            }
            default -> {
                if (sender instanceof Player player){
                    AdventureManager.playerMessage(player,"<color:#F5DEB3>/draimfarming reload -");
                    AdventureManager.playerMessage(player,"<color:#F5DEB3>/draimfarming setseason <world> <season> -");
                    AdventureManager.playerMessage(player,"<color:#F5DEB3>/draimfarming backup -");
                    AdventureManager.playerMessage(player,"<color:#F5DEB3>/draimfarming forcegrow <world> -");
                    AdventureManager.playerMessage(player,"<color:#F5DEB3>/draimfarming forcewater <world> -");
                    AdventureManager.playerMessage(player,"<color:#F5DEB3>/draimfarming forcesave <file> -");
                }else {
                    AdventureManager.consoleMessage("<color:#F5DEB3>/draimfarming reload -");
                    AdventureManager.consoleMessage("<color:#F5DEB3>/draimfarming setseason <world> <season> -");
                    AdventureManager.consoleMessage("<color:#F5DEB3>/draimfarming backup -");
                    AdventureManager.consoleMessage("<color:#F5DEB3>/draimfarming forcegrow <world> -");
                    AdventureManager.consoleMessage("<color:#F5DEB3>/draimfarming forcewater <world> -");
                    AdventureManager.consoleMessage("<color:#F5DEB3>/draimfarming forcesave <file> -");
                }
            }
        }
        return true;
    }

    private void lackArgs(CommandSender sender){
        if (sender instanceof Player){
            AdventureManager.playerMessage((Player) sender,ConfigReader.Message.prefix + ConfigReader.Message.lackArgs);
        }else {
            AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.lackArgs);
        }
    }

    private void forceSave(CommandSender sender){
        if (sender instanceof Player player){
            AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.forceSave);
        }else {
            AdventureManager.consoleMessage(ConfigReader.Message.prefix + ConfigReader.Message.forceSave);
        }
    }
}
