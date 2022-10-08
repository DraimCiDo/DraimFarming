package net.draimcido.draimfarming.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.helper.Log;
import net.draimcido.draimfarming.utils.AdventureUtil;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static void update(String fileName){
        try {
            YamlDocument.create(new File(Main.plugin.getDataFolder(), fileName), Main.plugin.getResource(fileName), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException e){
            Log.warn(e.getMessage());
        }
    }

    public static YamlConfiguration readData(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureUtil.consoleMessage("<orange>[DraimFishing] Не удалось сгенерировать файлы данных!</orange>");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static YamlConfiguration getConfig(String configName) {
        File file = new File(Main.plugin.getDataFolder(), configName);
        if (!file.exists()) Main.plugin.saveResource(configName, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void reloadConfigs() {
        MainConfig.load();
        BasicItemConfig.load();
        CropConfig.load();
        FertilizerConfig.load();
        MessageConfig.load();
        SeasonConfig.load();
        SprinklerConfig.load();
        WaterCanConfig.load();
        SoundConfig.load();
        if (Main.plugin.getPlaceholderManager() != null) {
            Main.plugin.getPlaceholderManager().unload();
            Main.plugin.getPlaceholderManager().load();
        }
    }
}
