package net.draimcido.draimfarming.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class BasicItemConfig {

    public static String dryPot;
    public static String wetPot;
    public static String deadCrop;
    public static String soilSurveyor;
    public static String greenHouseGlass;
    public static String crowLand;
    public static String crowFly;
    public static String scarecrow;
    public static String waterEffect;

    public static void load() {
        YamlConfiguration config = ConfigUtil.getConfig("basic_" + MainConfig.customPlugin + ".yml");
        dryPot = config.getString("dry-pot");
        wetPot = config.getString("wet-pot");
        greenHouseGlass = config.getString("greenhouse-glass");
        soilSurveyor = config.getString("soil-surveyor");
        deadCrop = config.getString("dead-crop");
        crowLand = config.getString("crow-land");
        crowFly = config.getString("crow-fly");
        scarecrow = config.getString("scarecrow");
        waterEffect = config.getString("water-effect");
    }
}
