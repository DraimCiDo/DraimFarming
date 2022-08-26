package net.draimcido.draimfarming.timer;

import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeCheck extends BukkitRunnable {

    @Override
    public void run() {
        ConfigReader.Config.worlds.forEach(world ->{
            long time = world.getTime();
            ConfigReader.Config.cropGrowTimeList.forEach(cropGrowTime -> {
                if(time == 0)
                    if(ConfigReader.Season.enable && ConfigReader.Season.seasonChange)
                        Main.plugin.getSeasonManager().getSeason(world);
                if(time == cropGrowTime){
                    if (ConfigReader.Config.allWorld){
                        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
                            Main.plugin.getCropManager().cropGrowAll();
                        });
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, ()->{
                            Main.plugin.getSprinklerManager().sprinklerWorkAll();
                        }, ConfigReader.Config.timeToGrow);
                    }else {
                        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
                            switch (ConfigReader.Config.growMode){
                                case 1 -> Main.plugin.getCropManager().growModeOne(world.getName());
                                case 2 -> Main.plugin.getCropManager().growModeTwo(world.getName());
                                case 3 -> Main.plugin.getCropManager().growModeThree(world.getName());
                                case 4 -> Main.plugin.getCropManager().growModeFour(world.getName());
                            }
                        });
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, ()->{
                            switch (ConfigReader.Config.growMode){
                                case 1 -> Main.plugin.getSprinklerManager().workModeOne(world.getName());
                                case 2 -> Main.plugin.getSprinklerManager().workModeTwo(world.getName());
                                case 3 -> Main.plugin.getSprinklerManager().workModeThree(world.getName());
                                case 4 -> Main.plugin.getSprinklerManager().workModeFour(world.getName());
                            }
                        }, ConfigReader.Config.timeToGrow);
                    }
                }
            });
        });
    }
}
