package net.draimcido.draimfarming.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.listener.JoinAndQuit;
import net.draimcido.draimfarming.objects.Crop;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.draimcido.draimfarming.objects.fertilizer.*;
import net.draimcido.draimfarming.utils.AdventureManager;
import net.draimcido.draimfarming.utils.FurnitureUtils;
import net.draimcido.draimfarming.utils.JedisUtils;
import net.draimcido.draimfarming.utils.LocUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CropManager{

    private YamlConfiguration data;
    public static ConcurrentHashMap<SimpleLocation, String> Cache = new ConcurrentHashMap<>();
    public static HashSet<SimpleLocation> RemoveCache = new HashSet<>();
    private final BukkitScheduler bukkitScheduler;
    private final boolean isEntity;

    public CropManager(boolean isEntity){
        this.bukkitScheduler = Bukkit.getScheduler();
        this.isEntity = isEntity;
    }

    public void loadData() {
        File file = new File(Main.plugin.getDataFolder(), "data" + File.separator + "crop.yml");
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureManager.consoleMessage("<gradient:#0070B3:#A0EACF>[DraimFarming] </gradient> <color:#E1FFFF>Не удалось создать файл данных обрезки!");
            }
        }
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public void saveData() {
        File file = new File(Main.plugin.getDataFolder(), "data" + File.separator + "crop.yml");
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<gradient:#0070B3:#A0EACF>[DraimFarming] </gradient> <color:#E1FFFF> crop.yml не удалось сохранить!");
        }
    }

    public void updateData(){
        Cache.forEach((location, String) -> {
            int x = location.getX();
            int z = location.getZ();
            data.set(location.getWorldName() + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getY() + "," + z, String);
        });
        Cache.clear();
        HashSet<SimpleLocation> set = new HashSet<>(RemoveCache);
        for (SimpleLocation location : set) {
            int x = location.getX();
            int z = location.getZ();
            data.set(location.getWorldName() + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getY() + "," + z, null);
        }
        RemoveCache.clear();
    }

    public void cleanData(){
        data.getKeys(false).forEach(world -> {
            data.getConfigurationSection(world).getKeys(false).forEach(chunk ->{
                if (data.getConfigurationSection(world + "." + chunk).getKeys(false).size() == 0){
                    data.set(world + "." + chunk, null);
                }
            });
        });
    }

    public void growModeOne(String worldName){
        if(!ConfigReader.Config.allWorld) updateData();
        if(!ConfigReader.Config.allWorld) saveData();
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            if (!isEntity){
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    String[] split = StringUtils.split(chunk,",");
                    if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                        data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                            String[] coordinate = StringUtils.split(key, ",");
                            Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                            bukkitScheduler.runTaskLaterAsynchronously(Main.plugin, ()-> {
                                if (growJudge(worldName, seedLocation)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                }
                            }, random);
                        });
                    }
                });
            }
            else {
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    String[] split = StringUtils.split(chunk,",");
                    if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                        data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                            String[] coordinate = StringUtils.split(key, ",");
                            Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                            bukkitScheduler.runTaskLater(Main.plugin, ()-> {
                                growJudgeEntity(worldName, seedLocation, worldName + "." + chunk + "." + key);
                            }, random);
                        });
                    }
                });
            }
        }
    }


    public void growModeTwo(String worldName){
        if(!ConfigReader.Config.allWorld) updateData();
        if(!ConfigReader.Config.allWorld) saveData();
        HashSet<String> players = getPlayers();
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            if (!isEntity){
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        if (!players.contains(value)) return;
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                        bukkitScheduler.runTaskLaterAsynchronously(Main.plugin, ()-> {
                            if (growJudge(worldName, seedLocation)){
                                data.set(worldName + "." + chunk + "." + key, null);
                            }
                        }, random);
                    });
                });
            }
            else {
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        if (!players.contains(value)) return;
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                        bukkitScheduler.runTaskLater(Main.plugin, ()-> {
                            growJudgeEntity(worldName, seedLocation, worldName + "." + chunk + "." + key);
                        }, random);
                    });
                });
            }
        }
    }

    public void growModeThree(String worldName){
        if(!ConfigReader.Config.allWorld) updateData();
        if(!ConfigReader.Config.allWorld) saveData();
        HashSet<String> players = getPlayers();
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            if (!isEntity){
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    String[] split = StringUtils.split(chunk,",");
                    if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                        data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                            String[] coordinate = StringUtils.split(key, ",");
                            Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                            bukkitScheduler.runTaskLaterAsynchronously(Main.plugin, ()-> {
                                if (growJudge(worldName, seedLocation)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                }
                            }, random);
                        });
                    }
                    else{
                        data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                            if (!players.contains(value)) return;
                            String[] coordinate = StringUtils.split(key, ",");
                            Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                            bukkitScheduler.runTaskLaterAsynchronously(Main.plugin, ()-> {
                                if (growJudge(worldName, seedLocation)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                }
                            }, random);
                        });
                    }
                });
            }
            else {
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    String[] split = StringUtils.split(chunk,",");
                    if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                        data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                            String[] coordinate = StringUtils.split(key, ",");
                            Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                            bukkitScheduler.runTaskLater(Main.plugin, ()-> {
                                growJudgeEntity(worldName, seedLocation, worldName + "." + chunk + "." + key);
                            }, random);
                        });
                    }
                    else{
                        data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                            if (!players.contains(value)) return;
                            String[] coordinate = StringUtils.split(key, ",");
                            Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                            bukkitScheduler.runTaskLater(Main.plugin, ()-> {
                                growJudgeEntity(worldName, seedLocation, worldName + "." + chunk + "." + key);
                            }, random);
                        });
                    }
                });
            }
        }
    }

    public void growModeFour(String worldName){
        if(!ConfigReader.Config.allWorld){updateData();}
        if(!ConfigReader.Config.allWorld) saveData();
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            if (!isEntity){
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                        bukkitScheduler.runTaskLaterAsynchronously(Main.plugin, ()-> {
                            if (growJudge(worldName, seedLocation)){
                                data.set(worldName + "." + chunk + "." + key, null);
                            }
                        }, random);
                    });
                });
            }
            else {
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                        bukkitScheduler.runTaskLater(Main.plugin, ()-> {
                            growJudgeEntity(worldName, seedLocation, worldName + "." + chunk + "." + key);
                        }, random);
                    });
                });
            }
        }
    }

    public void cropGrowAll(){
        updateData();
        List<World> worlds = Bukkit.getWorlds();
        for (int i = 0; i < worlds.size(); i++){
            String worldName = worlds.get(i).getName();
            bukkitScheduler.runTaskLaterAsynchronously(Main.plugin, () -> {
                switch (ConfigReader.Config.growMode){
                    case 1 -> growModeOne(worldName);
                    case 2 -> growModeTwo(worldName);
                    case 3 -> growModeThree(worldName);
                    case 4 -> growModeFour(worldName);
                }
            }, i * 40L);
        }
        saveData();
    }

    private boolean growJudge(String worldName, Location seedLocation) {
        CustomBlock seedBlock = CustomBlock.byAlreadyPlaced(seedLocation.getBlock());
        if(seedBlock == null) {
            return true;
        }
        String namespacedID = seedBlock.getNamespacedID();
        String id = seedBlock.getId();
        if(namespacedID.equals(ConfigReader.Basic.dead) || !namespacedID.contains("_stage_")) {
            return true;
        }
        Location potLocation = seedLocation.clone().subtract(0,1,0);
        CustomBlock pot = CustomBlock.byAlreadyPlaced(potLocation.getBlock());
        if (pot == null){
            return true;
        }
        String[] cropNameList = StringUtils.split(id,"_");
        Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
        if (cropInstance == null){
            return true;
        }
        String potNamespacedID = pot.getNamespacedID();
        if (potNamespacedID.equals(ConfigReader.Basic.watered_pot)){

            if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                if (isWrongSeason(seedLocation, cropInstance.getSeasons(), worldName)){
                    bukkitScheduler.runTask(Main.plugin, () -> {
                        CustomBlock.remove(seedLocation);
                        CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                    });
                    return true;
                }
            }
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) != null) {
                Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(potLocation));
                if (fertilizer != null){
                    int times = fertilizer.getTimes();
                    if (times > 0){
                        fertilizer.setTimes(times - 1);

                        Fertilizer fertilizerConfig = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                        if (fertilizerConfig instanceof SpeedGrow speedGrow){
                            if (cropInstance.getGrowChance() > Math.random()){
                                if (Math.random() < speedGrow.getChance() && CustomBlock.getInstance(StringUtils.chop(namespacedID) + (nextStage + 1)) != null){
                                    addStage(potLocation, seedLocation, namespacedID, nextStage + 1);
                                }else {
                                    addStage(potLocation, seedLocation, namespacedID, nextStage);
                                }
                            }else {
                                notAddStage(potLocation);
                            }
                        }
                        else if(fertilizerConfig instanceof RetainingSoil retainingSoil){
                            if (Math.random() < retainingSoil.getChance()){
                                if (cropInstance.getGrowChance() > Math.random()){
                                    addStage(seedLocation, namespacedID, nextStage);
                                }
                            }else {
                                if (cropInstance.getGrowChance() > Math.random()){
                                    addStage(potLocation, seedLocation, namespacedID, nextStage);
                                }else {
                                    notAddStage(potLocation);
                                }
                            }
                        }
                        else if(fertilizerConfig instanceof QualityCrop || fertilizerConfig instanceof YieldIncreasing){
                            if (cropInstance.getGrowChance() > Math.random()){
                                addStage(potLocation, seedLocation, namespacedID, nextStage);
                            }else {
                                notAddStage(potLocation);
                            }
                        }else {
                            AdventureManager.consoleMessage("<gradient:#0070B3:#A0EACF>[DraimFarming] </gradient> <color:#E1FFFF>Неизвестное удобрение, автоматически удаленно!");
                            PotManager.Cache.remove(LocUtils.fromLocation(potLocation));

                            if (cropInstance.getGrowChance() > Math.random()){
                                addStage(potLocation, seedLocation, namespacedID, nextStage);
                            }else {
                                notAddStage(potLocation);
                            }
                        }
                        if (times == 1){
                            PotManager.Cache.remove(LocUtils.fromLocation(potLocation));
                        }
                    }
                    else {
                        PotManager.Cache.remove(LocUtils.fromLocation(potLocation));
                        if (cropInstance.getGrowChance() > Math.random()){
                            addStage(potLocation, seedLocation, namespacedID, nextStage);
                        }else {
                            notAddStage(potLocation);
                        }
                    }
                }
                else {
                    if (cropInstance.getGrowChance() > Math.random()){
                        addStage(potLocation, seedLocation, namespacedID, nextStage);
                    }else {
                        notAddStage(potLocation);
                    }
                }
            }
            else if(cropInstance.getGiant() != null){
                if (cropInstance.getGiantChance() > Math.random()){

                    if (cropInstance.isBlock()){
                        bukkitScheduler.runTask(Main.plugin, () ->{
                            CustomBlock.remove(seedLocation);
                            CustomBlock.place(cropInstance.getGiant(), seedLocation);
                            CustomBlock.remove(potLocation);
                            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                        });
                    }else {
                        bukkitScheduler.runTask(Main.plugin, () ->{
                            CustomBlock.remove(seedLocation);
                            CustomFurniture.spawn(cropInstance.getGiant(), seedLocation.getBlock());
                            CustomBlock.remove(potLocation);
                            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                        });
                    }
                    return true;
                }else {
                    bukkitScheduler.runTask(Main.plugin, () ->{
                        CustomBlock.remove(potLocation);
                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                    });
                    return ConfigReader.Config.oneTry || ConfigReader.Config.growMode == 4;
                }
            }else {
                if (!ConfigReader.Season.enable) return true;
                bukkitScheduler.runTask(Main.plugin, () -> {
                    CustomBlock.remove(potLocation);
                    CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                });
            }
        }
        else if(potNamespacedID.equals(ConfigReader.Basic.pot)){
            if(!ConfigReader.Season.enable) return false;
            List<String> seasons = cropInstance.getSeasons();
            if(seasons == null) return false;
            if(isWrongSeason(seedLocation, seasons, worldName)){
                bukkitScheduler.runTask(Main.plugin, () -> {
                    CustomBlock.remove(seedLocation);
                    CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                });
                return true;
            }
        }
        else {
            return true;
        }
        return false;
    }

    private boolean isWrongSeason(Location seedLocation, List<String> seasons, String worldName){
        if(ConfigReader.Season.greenhouse){
            for(int i = 1; i <= ConfigReader.Season.range; i++){
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(seedLocation.clone().add(0,i,0).getBlock());
                if (customBlock != null){
                    if(customBlock.getNamespacedID().equals(ConfigReader.Basic.glass)){
                        return false;
                    }
                }
            }
        }
        if (!ConfigReader.Config.allWorld){
            for(String season : seasons){
                if (season.equals(SeasonManager.SEASON.get(worldName))) {
                    return false;
                }
            }
        }else {
            for(String season : seasons){
                if (season.equals(SeasonManager.SEASON.get(ConfigReader.Config.referenceWorld))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void growJudgeEntity(String worldName, Location seedLocation, String path) {
        Chunk chunk = seedLocation.getChunk();
        chunk.load();
        bukkitScheduler.runTaskLater(Main.plugin, ()-> {
            if (chunk.isEntitiesLoaded()){
                CustomFurniture crop = FurnitureUtils.getFurniture(seedLocation.clone().add(0.5,0.1,0.5));
                if(crop == null) {
                    data.set(path, null);
                    return;
                }
                String namespacedID = crop.getNamespacedID();
                if(namespacedID.equals(ConfigReader.Basic.dead) || !namespacedID.contains("_stage_")) {
                    data.set(path, null);
                    return;
                }
                Location potLocation = seedLocation.clone().subtract(0,1,0);
                CustomBlock pot = CustomBlock.byAlreadyPlaced(potLocation.getBlock());
                if (pot == null){
                    data.set(path, null);
                    return;
                }
                String id = crop.getId();
                String[] cropNameList = StringUtils.split(id,"_");
                Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                if (cropInstance == null){
                    data.set(path, null);
                    return;
                }
                String potNamespacedID = pot.getNamespacedID();
                if (potNamespacedID.equals(ConfigReader.Basic.watered_pot)){
                    if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                        if (isWrongSeason(seedLocation, cropInstance.getSeasons(), worldName)){
                            seedLocation.getChunk().load();
                            CustomFurniture.remove(crop.getArmorstand(), false);
                            FurnitureUtils.placeCrop(ConfigReader.Basic.dead, seedLocation);
                            data.set(path, null);
                            return;
                        }
                    }
                    int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                    if (CustomFurniture.getInstance(StringUtils.chop(namespacedID) + nextStage) != null) {
                        Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(potLocation));
                        if (fertilizer != null){
                            int times = fertilizer.getTimes();
                            if (times > 0){

                                fertilizer.setTimes(times - 1);

                                Fertilizer fertilizerConfig = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                                if (fertilizerConfig instanceof SpeedGrow speedGrow){
                                    if (cropInstance.getGrowChance() > Math.random()){
                                        if (Math.random() < speedGrow.getChance() && CustomBlock.getInstance(StringUtils.chop(namespacedID) + (nextStage + 1)) != null){
                                            addStageEntity(potLocation, seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + (nextStage + 1));
                                        }else {
                                            addStageEntity(potLocation, seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + nextStage);
                                        }
                                    }
                                    else {
                                        CustomBlock.remove(potLocation);
                                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                    }
                                }
                                else if(fertilizerConfig instanceof RetainingSoil retainingSoil){
                                    if (Math.random() < retainingSoil.getChance()){
                                        if (cropInstance.getGrowChance() > Math.random()){
                                            addStageEntity(seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + nextStage);
                                        }
                                    }else {
                                        if (cropInstance.getGrowChance() > Math.random()){
                                            addStageEntity(potLocation, seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + nextStage);
                                        }else {
                                            CustomBlock.remove(potLocation);
                                            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                        }
                                    }
                                }
                                else if(fertilizerConfig instanceof QualityCrop || fertilizerConfig instanceof YieldIncreasing){
                                    if (cropInstance.getGrowChance() > Math.random()){
                                        addStageEntity(potLocation, seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + nextStage);
                                    }else {
                                        CustomBlock.remove(potLocation);
                                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                    }
                                }else {
                                    AdventureManager.consoleMessage("<gradient:#0070B3:#A0EACF>[DraimFarming] </gradient> <color:#E1FFFF>Неизвестное удобрение, автоматически удаленно!");
                                    PotManager.Cache.remove(LocUtils.fromLocation(potLocation));
                                    if (cropInstance.getGrowChance() > Math.random()){
                                        addStageEntity(potLocation, seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + nextStage);
                                    }else {
                                        CustomBlock.remove(potLocation);
                                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                    }
                                }
                                if (times == 1){
                                    PotManager.Cache.remove(LocUtils.fromLocation(potLocation));
                                }
                            }
                            else {
                                PotManager.Cache.remove(LocUtils.fromLocation(potLocation));
                                if (cropInstance.getGrowChance() > Math.random()){
                                    addStageEntity(potLocation, seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + nextStage);
                                }else {
                                    CustomBlock.remove(potLocation);
                                    CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                }
                            }
                        }
                        else {
                            if (cropInstance.getGrowChance() > Math.random()){
                                addStageEntity(potLocation, seedLocation, crop.getArmorstand(), StringUtils.chop(namespacedID) + nextStage);
                            }else {
                                CustomBlock.remove(potLocation);
                                CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                            }
                        }
                    }
                    else if(cropInstance.getGiant() != null){
                        if (cropInstance.getGiantChance() > Math.random()){
                            if (cropInstance.isBlock()){
                                CustomBlock.remove(potLocation);
                                CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                CustomFurniture.remove(crop.getArmorstand(), false);
                                CustomBlock.place(cropInstance.getGiant(), seedLocation);
                            }else {
                                CustomBlock.remove(potLocation);
                                CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                CustomFurniture.remove(crop.getArmorstand(), false);
                                CustomFurniture.spawn(cropInstance.getGiant(), seedLocation.getBlock());
                            }
                            data.set(path, null);
                        }else {
                            CustomBlock.remove(potLocation);
                            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                            if (ConfigReader.Config.oneTry || ConfigReader.Config.growMode == 4){
                                data.set(path, null);
                            }
                        }
                    }else {
                        if (!ConfigReader.Season.enable){
                            data.set(path, null);
                            return;
                        }
                        CustomBlock.remove(potLocation);
                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                    }
                }
                else if(potNamespacedID.equals(ConfigReader.Basic.pot)){
                    if(!ConfigReader.Season.enable) return;
                    List<String> seasons = cropInstance.getSeasons();
                    if(seasons == null) return;
                    if(isWrongSeason(seedLocation, seasons, worldName)){
                        CustomBlock.remove(seedLocation);
                        CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                        data.set(path, null);
                    }
                }
            }
        },4);
    }


    private void addStage(Location potLocation, Location seedLocation, String namespacedID, int nextStage){
        String stage = StringUtils.chop(namespacedID) + nextStage;
        bukkitScheduler.runTask(Main.plugin, () ->{
            CustomBlock.remove(potLocation);
            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
            CustomBlock.remove(seedLocation);
            CustomBlock.place(stage, seedLocation);
        });
    }

    private void addStage(Location seedLocation, String namespacedID, int nextStage){
        String stage = StringUtils.chop(namespacedID) + nextStage;
        bukkitScheduler.runTask(Main.plugin, () ->{
            CustomBlock.remove(seedLocation);
            CustomBlock.place(stage, seedLocation);
        });
    }


    private void addStageEntity(Location potLocation, Location seedLocation, Entity entity, String nextStage){
        CustomBlock.remove(potLocation);
        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
        CustomFurniture.remove(entity,false);
        if (FurnitureUtils.getFurniture(seedLocation.add(0.5,0.1,0.5)) == null){
            FurnitureUtils.placeCrop(nextStage, seedLocation);
        }
    }

    private void addStageEntity(Location seedLocation, Entity entity, String nextStage){
        CustomFurniture.remove(entity,false);
        if (FurnitureUtils.getFurniture(seedLocation.add(0.5,0.1,0.5)) == null){
            FurnitureUtils.placeCrop(nextStage, seedLocation);
        }
    }

    private void notAddStage(Location potLocation){
        bukkitScheduler.runTask(Main.plugin, () ->{
            CustomBlock.remove(potLocation);
            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
        });
    }

    private HashSet<String> getPlayers(){
        if (JedisUtils.useRedis){
            return JedisUtils.getPlayers();
        }else {
            return new HashSet<>(JoinAndQuit.onlinePlayers);
        }
    }
}
