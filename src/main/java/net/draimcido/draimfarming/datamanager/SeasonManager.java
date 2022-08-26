package net.draimcido.draimfarming.datamanager;

import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.hook.RealisticSeason;
import net.draimcido.draimfarming.utils.AdventureManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class SeasonManager{

    public static HashMap<String, String> SEASON = new HashMap<>();

    /**
     * 读取文件中的季节
     * @param file 季节数据文件
     */
    private YamlConfiguration readData(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomCrops] 季节数据文件生成失败!</red>");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 载入数据
     */
    public void loadData() {
        SEASON.clear();
        YamlConfiguration data = readData(new File(Main.plugin.getDataFolder(), "data" + File.separator + "season.yml"));
        if (ConfigReader.Season.seasonChange) {
            autoSeason();
        } else {
            Set<String> set = data.getKeys(false);
            ConfigReader.Config.worldNames.forEach(worldName -> {
                if (set.contains(worldName)) {
                    SEASON.put(worldName, data.getString(worldName));
                } else {
                    getSeason(Bukkit.getWorld(worldName));
                }
            });
        }
    }

    public void autoSeason() {
        ConfigReader.Config.worlds.forEach(this::getSeason);
    }

    /**
     * 计算某个世界的季节
     * @param world 世界
     */
    public void getSeason(World world) {
        if (ConfigReader.Config.realisticSeason){
            Bukkit.getScheduler().runTaskLater(Main.plugin, ()->{
                SEASON.put(world.getName(), RealisticSeason.getSeason(world));
            },60);
        }
        else {
            int season = (int) ((world.getFullTime() / 24000L) % (ConfigReader.Season.duration * 4)) / ConfigReader.Season.duration;
            switch (season) {
                case 0 -> SEASON.put(world.getName(), "spring");
                case 1 -> SEASON.put(world.getName(), "summer");
                case 2 -> SEASON.put(world.getName(), "autumn");
                case 3 -> SEASON.put(world.getName(), "winter");
                default -> AdventureManager.consoleMessage("<red>[CustomCrops] 自动季节计算错误!</red>");
            }
        }
    }

    /**
     * 保存数据
     */
    public void saveData() {
        SEASON.forEach((key, value) -> {
            File file = new File(Main.plugin.getDataFolder(), "data" + File.separator + "season.yml");
            YamlConfiguration data = readData(file);
            data.set(key, value);
            try {
                data.save(file);
            } catch (IOException e) {
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomCrops] season.yml保存出错!</red>");
            }
        });
    }

    /**
     * 设置季节
     * @param worldName 世界名
     * @param season 季节
     */
    public boolean setSeason(String worldName, String season){
        if (!ConfigReader.Config.worldNames.contains(worldName)){
            return false;
        }
        if (!Arrays.asList("spring","summer","autumn","winter").contains(season)){
            return false;
        }
        SEASON.put(worldName, season);
        return true;
    }
}
