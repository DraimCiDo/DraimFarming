package net.draimcido.draimfarming.config;

import net.draimcido.draimfarming.objects.WaterCan;
import net.draimcido.draimfarming.utils.AdventureUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class WaterCanConfig {

    public static HashMap<String, WaterCan> CANS = new HashMap<>();

    public static void load() {
        CANS = new HashMap<>(8);
        YamlConfiguration config = ConfigUtil.getConfig("watercans_" + MainConfig.customPlugin + ".yml");
        for (String key : config.getKeys(false)) {
            WaterCan waterCan = new WaterCan(
                    config.getInt(key + ".max-water-storage"),
                    config.getInt(key + ".width"),
                    config.getInt(key + ".length")
            );
            CANS.put(config.getString(key + ".item"), waterCan);
        }
        AdventureUtil.consoleMessage("[DraimFarming] Загружено <green>" + CANS.size() + "<gray> леек");
    }
}
