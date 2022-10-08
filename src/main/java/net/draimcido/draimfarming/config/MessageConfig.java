package net.draimcido.draimfarming.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageConfig {

    public static String spring;
    public static String summer;
    public static String autumn;
    public static String winter;
    public static String seasonDisabled;
    public static String autoSeasonDisabled;
    public static String prefix;
    public static String wrongArgs;
    public static String unavailableArgs;
    public static String lackArgs;
    public static String reload;
    public static String noPerm;
    public static String limitWire;
    public static String limitFrame;
    public static String growSimulation;
    public static String backUp;
    public static String setSeason;
    public static String beforePlant;
    public static String noSeason;
    public static String worldNotExists;
    public static String seasonNotExists;
    public static String wrongSeason;
    public static String nonArgs;

    public static void load() {
        YamlConfiguration config = ConfigUtil.getConfig("messages" + File.separator + "messages_" + MainConfig.lang +".yml");
        prefix = config.getString("messages.prefix");
        reload = config.getString("messages.reload");
        noPerm = config.getString("messages.no-perm");
        lackArgs = config.getString("messages.lack-args");
        unavailableArgs = config.getString("messages.invalid-args");
        wrongArgs = config.getString("messages.wrong-args");
        nonArgs = config.getString("messages.none-args");
        spring = config.getString("messages.spring");
        summer = config.getString("messages.summer");
        autumn = config.getString("messages.autumn");
        winter = config.getString("messages.winter");
        limitWire = config.getString("messages.limitation-tripwire");
        limitFrame = config.getString("messages.limitation-itemframe");
        growSimulation = config.getString("messages.grow-simulation");
        backUp = config.getString("messages.back-up");
        setSeason = config.getString("messages.set-season");
        beforePlant = config.getString("messages.before-plant");
        seasonDisabled = config.getString("messages.season-disabled");
        autoSeasonDisabled = config.getString("messages.auto-season-disabled");
        noSeason = config.getString("messages.no-season");
        worldNotExists = config.getString("messages.world-not-exist");
        seasonNotExists = config.getString("messages.season-not-exist");
        wrongSeason = config.getString("messages.wrong-season");
    }
}
