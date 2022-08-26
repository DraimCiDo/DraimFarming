package net.draimcido.draimfarming.utils;

import dev.lone.itemsadder.api.CustomBlock;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.datamanager.PotManager;
import net.draimcido.draimfarming.objects.fertilizer.*;
import org.bukkit.Location;
import org.bukkit.Particle;

public class PotUtils {

    public static void waterPot(int width, int length, Location location, float yaw){
        if (ConfigReader.Config.hasParticle)
            location.getWorld().spawnParticle(Particle.WATER_SPLASH, location.clone().add(0.5,1.2,0.5),15,0.1,0.1, 0.1);
        int extend = width / 2;
        if (yaw < 45 && yaw > -135) {
            if (yaw > -45) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(i, 0, -1);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(0,0,1);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
            else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(-1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(1,0,0);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
        }
        else {
            if (yaw > 45 && yaw < 135) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(1,0,0);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
            else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(i, 0, 1);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(0,0,1);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void addFertilizer(Fertilizer fertilizerConfig, Location location) {
        if (fertilizerConfig instanceof QualityCrop config){
            QualityCrop qualityCrop = new QualityCrop(config.getKey(), config.getTimes());
            PotManager.Cache.put(LocUtils.fromLocation(location), qualityCrop);
        }else if (fertilizerConfig instanceof SpeedGrow config){
            SpeedGrow speedGrow = new SpeedGrow(config.getKey(), config.getTimes());
            PotManager.Cache.put(LocUtils.fromLocation(location), speedGrow);
        }else if (fertilizerConfig instanceof RetainingSoil config){
            RetainingSoil retainingSoil = new RetainingSoil(config.getKey(), config.getTimes());
            PotManager.Cache.put(LocUtils.fromLocation(location), retainingSoil);
        }else if (fertilizerConfig instanceof YieldIncreasing config){
            YieldIncreasing yieldIncreasing = new YieldIncreasing(config.getKey(), config.getTimes());
            PotManager.Cache.put(LocUtils.fromLocation(location), yieldIncreasing);
        }
        if (fertilizerConfig.getParticle() != null) location.getWorld().spawnParticle(fertilizerConfig.getParticle(), location.add(0.5,1.3,0.5), 5,0.2,0.2,0.2);
    }
}
