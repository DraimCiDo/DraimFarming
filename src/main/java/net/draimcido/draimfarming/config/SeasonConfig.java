package net.draimcido.draimfarming.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class SeasonConfig {

    public static boolean enable;
    public static boolean auto;
    public static int duration;
    public static boolean greenhouse;
    public static int effectiveRange;

    public static void load() {

        YamlConfiguration config = ConfigUtil.getConfig("config.yml");
        enable = config.getBoolean("mechanics.season.enable", true);
        auto = config.getBoolean("mechanics.season.auto-season-change.enable", true);
        duration = config.getInt("mechanics.season.auto-season-change.duration", 28);
        greenhouse = config.getBoolean("mechanics.season.greenhouse.enable", true);
        effectiveRange = config.getInt("mechanics.season.greenhouse.range", 5);
    }
}
