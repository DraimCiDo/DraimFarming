package net.draimcido.draimfarming.config;

import net.draimcido.draimfarming.objects.Sprinkler;
import net.draimcido.draimfarming.utils.AdventureUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class SprinklerConfig {

    public static HashMap<String, Sprinkler> SPRINKLERS_CONFIG;
    public static HashMap<String, Sprinkler> SPRINKLERS_2D;
    public static HashMap<String, Sprinkler> SPRINKLERS_3D;

    public static void load() {
        SPRINKLERS_3D = new HashMap<>(8);
        SPRINKLERS_2D = new HashMap<>(8);
        SPRINKLERS_CONFIG = new HashMap<>(8);
        YamlConfiguration config = ConfigUtil.getConfig("sprinklers_" + MainConfig.customPlugin + ".yml");

        int amount = 0;
        for (String key : config.getKeys(false)) {

            Sprinkler sprinkler = new Sprinkler(
                    key,
                    config.getInt(key + ".range", 1),
                    config.getInt(key + ".max-water-storage", 5)
            );
            String twoD = config.getString(key + ".2Ditem");
            String threeD = config.getString(key + ".3Ditem");
            sprinkler.setTwoD(twoD);
            sprinkler.setThreeD(threeD);
            SPRINKLERS_CONFIG.put(key + "CONFIG", sprinkler);
            SPRINKLERS_2D.put(twoD, sprinkler);
            SPRINKLERS_3D.put(threeD, sprinkler);
            amount++;
        }

        AdventureUtil.consoleMessage("[DraimFarming] Загружено <green>" + amount + "<gray> разбрызгивателей");
    }
}
