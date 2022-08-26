package net.draimcido.draimfarming.api;

import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.datamanager.SeasonManager;

public class DraimFarmingAPI {

    public static Main getPlugin(){
        return Main.plugin;
    }
    public static String getSeason(String worldName){
        return SeasonManager.SEASON.get(worldName);
    }
}

