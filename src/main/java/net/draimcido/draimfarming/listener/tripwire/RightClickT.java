package net.draimcido.draimfarming.listener.tripwire;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.datamanager.CropManager;
import net.draimcido.draimfarming.datamanager.PotManager;
import net.draimcido.draimfarming.datamanager.SeasonManager;
import net.draimcido.draimfarming.datamanager.SprinklerManager;
import net.draimcido.draimfarming.integrations.protection.Integration;
import net.draimcido.draimfarming.limits.CropsPerChunk;
import net.draimcido.draimfarming.limits.SprinklersPerChunk;
import net.draimcido.draimfarming.listener.JoinAndQuit;
import net.draimcido.draimfarming.objects.Crop;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.draimcido.draimfarming.objects.Sprinkler;
import net.draimcido.draimfarming.objects.WateringCan;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;
import net.draimcido.draimfarming.objects.fertilizer.QualityCrop;
import net.draimcido.draimfarming.objects.fertilizer.YieldIncreasing;
import net.draimcido.draimfarming.requirements.PlantingCondition;
import net.draimcido.draimfarming.requirements.Requirement;
import net.draimcido.draimfarming.utils.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RightClickT implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        long time = System.currentTimeMillis();
        Player player = event.getPlayer();
        if (time - (JoinAndQuit.coolDown.getOrDefault(player, time - 200)) < 200) return;
        JoinAndQuit.coolDown.put(player, time);
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
            ItemStack itemStack = event.getItem();
            if (itemStack != null && itemStack.getType() != Material.AIR){
                NBTItem nbtItem = new NBTItem(itemStack);
                NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
                if (nbtCompound != null){
                    String id = nbtCompound.getString("id");
                    String namespace = nbtCompound.getString("namespace");
                    String itemNID = namespace + ":" + id;
                    if (id.endsWith("_seeds") && action == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP){
                        String cropName = StringUtils.remove(id, "_seeds");
                        Crop cropInstance = ConfigReader.CROPS.get(cropName);
                        if (cropInstance != null){
                            Block block = event.getClickedBlock();
                            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                            if (customBlock == null) return;
                            String namespacedID = customBlock.getNamespacedID();
                            if (namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                                Location location = block.getLocation().add(0,1,0); //已+1
                                for (Integration integration : ConfigReader.Config.integration)
                                    if(!integration.canPlace(location, player)) return;
                                if(FurnitureUtils.isSprinkler(location.clone().add(0.5, 0.5, 0.5))) return;
                                PlantingCondition plantingCondition = new PlantingCondition(player, location);
                                if (cropInstance.getRequirements() != null)
                                    for (Requirement requirement : cropInstance.getRequirements())
                                        if (!requirement.canPlant(plantingCondition)) return;
                                Label_out:
                                if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                                    if (!ConfigReader.Config.allWorld){
                                        for (String season : cropInstance.getSeasons())
                                            if (season.equals(SeasonManager.SEASON.get(location.getWorld().getName())))
                                                break Label_out;
                                    }else {
                                        for(String season : cropInstance.getSeasons())
                                            if (season.equals(SeasonManager.SEASON.get(ConfigReader.Config.referenceWorld)))
                                                break Label_out;
                                    }
                                    if(ConfigReader.Season.greenhouse){
                                        for(int i = 1; i <= ConfigReader.Season.range; i++){
                                            CustomBlock cb = CustomBlock.byAlreadyPlaced(location.clone().add(0,i,0).getBlock());
                                            if (cb != null)
                                                if(cb.getNamespacedID().equalsIgnoreCase(ConfigReader.Basic.glass))
                                                    break Label_out;
                                        }
                                    }
                                    if (ConfigReader.Config.nwSeason) AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.badSeason);
                                    if (ConfigReader.Config.pwSeason) return;
                                }
                                if (location.getBlock().getType() != Material.AIR) return;
                                if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                if (CropsPerChunk.isLimited(location)){
                                    AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.crop_limit.replace("{max}", String.valueOf(ConfigReader.Config.cropLimit)));
                                    return;
                                }
                                SimpleLocation simpleLocation = LocUtils.fromLocation(location);
                                CropManager.RemoveCache.remove(simpleLocation);
                                CropManager.Cache.put(simpleLocation, player.getName());
                                CustomBlock.place((namespace + ":" + cropName + "_stage_1"), location);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.plantSeedSource, ConfigReader.Sounds.plantSeedKey);
                            }
                        }else AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.not_configed);
                        return;
                    }
                    WateringCan wateringCan = ConfigReader.CANS.get(itemNID);
                    if (wateringCan != null){
                        int water = nbtItem.getInteger("WaterAmount");
                        List<Block> lineOfSight = player.getLineOfSight(null, 5);
                        for (Block block : lineOfSight) {
                            if (block.getType() == Material.WATER) {
                                if (wateringCan.getMax() > water){
                                    water += ConfigReader.Config.waterCanRefill;
                                    if (water > wateringCan.getMax()) water = wateringCan.getMax();
                                    nbtItem.setInteger("WaterAmount", water);
                                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL,1,1);
                                    if (ConfigReader.Message.hasWaterInfo)
                                        AdventureManager.playerActionbar(player,
                                                (ConfigReader.Message.waterLeft +
                                                        ConfigReader.Message.waterFull.repeat(water) +
                                                        ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                        ConfigReader.Message.waterRight)
                                                        .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                        .replace("{water}", String.valueOf(water)));
                                    if (ConfigReader.Basic.hasWaterLore){
                                        List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                        lores.clear();
                                        String string =
                                                (ConfigReader.Basic.waterLeft +
                                                        ConfigReader.Basic.waterFull.repeat(water) +
                                                        ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                        ConfigReader.Basic.waterRight)
                                                        .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                        .replace("{water}", String.valueOf(water));
                                        ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                    }
                                    if (ConfigReader.Config.hasParticle) player.getWorld().spawnParticle(Particle.WATER_SPLASH, block.getLocation().add(0.5,1, 0.5),15,0.1,0.1,0.1);
                                    itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                                }
                                return;
                            }
                        }
                        if(action == Action.RIGHT_CLICK_BLOCK){
                            Block block = event.getClickedBlock();
                            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                            if (customBlock == null) return;
                            for (Integration integration : ConfigReader.Config.integration)
                                if(!integration.canPlace(block.getLocation(), player)) return;
                            String namespacedID = customBlock.getNamespacedID();
                            if ((namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)) && event.getBlockFace() == BlockFace.UP){
                                if (water > 0){
                                    nbtItem.setInteger("WaterAmount", --water);
                                    AdventureManager.playerSound(player, ConfigReader.Sounds.waterPotSource, ConfigReader.Sounds.waterPotKey);
                                    PotUtils.waterPot(wateringCan.getWidth(), wateringCan.getLength(), block.getLocation(), player.getLocation().getYaw());
                                }
                                if (ConfigReader.Message.hasWaterInfo)
                                    AdventureManager.playerActionbar(player,
                                            (ConfigReader.Message.waterLeft +
                                                    ConfigReader.Message.waterFull.repeat(water) +
                                                    ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                    ConfigReader.Message.waterRight)
                                                    .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                    .replace("{water}", String.valueOf(water)));
                                if (ConfigReader.Basic.hasWaterLore){
                                    List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                    lores.clear();
                                    String string =
                                            (ConfigReader.Basic.waterLeft +
                                                    ConfigReader.Basic.waterFull.repeat(water) +
                                                    ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                    ConfigReader.Basic.waterRight)
                                                    .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                    .replace("{water}", String.valueOf(water));
                                    ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                }
                                itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                            }
                            else if (namespacedID.contains("_stage_")){
                                if (water > 0) {
                                    nbtItem.setInteger("WaterAmount", --water);
                                    AdventureManager.playerSound(player, ConfigReader.Sounds.waterPotSource, ConfigReader.Sounds.waterPotKey);
                                    PotUtils.waterPot(wateringCan.getWidth(), wateringCan.getLength(), block.getLocation().subtract(0, 1, 0), player.getLocation().getYaw());
                                }
                                if (ConfigReader.Message.hasWaterInfo)
                                    AdventureManager.playerActionbar(player,
                                            (ConfigReader.Message.waterLeft +
                                                    ConfigReader.Message.waterFull.repeat(water) +
                                                    ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                    ConfigReader.Message.waterRight)
                                                    .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                    .replace("{water}", String.valueOf(water)));
                                if (ConfigReader.Basic.hasWaterLore){
                                    List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                    lores.clear();
                                    String string =
                                            (ConfigReader.Basic.waterLeft +
                                                    ConfigReader.Basic.waterFull.repeat(water) +
                                                    ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                    ConfigReader.Basic.waterRight)
                                                    .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                    .replace("{water}", String.valueOf(water));
                                    ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                }
                                itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                            }
                        }
                        return;
                    }
                    Fertilizer fertilizerConfig = ConfigReader.FERTILIZERS.get(id);
                    if (fertilizerConfig != null && action == Action.RIGHT_CLICK_BLOCK){
                        Block block = event.getClickedBlock();
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                        if (customBlock == null) return;
                        for (Integration integration : ConfigReader.Config.integration)
                            if(!integration.canPlace(block.getLocation(), player)) return;
                        String namespacedID = customBlock.getNamespacedID();
                        if (namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                            CustomBlock customBlockUp = CustomBlock.byAlreadyPlaced(block.getLocation().clone().add(0,1,0).getBlock());
                            if (customBlockUp != null){
                                if (fertilizerConfig.isBefore() && customBlockUp.getNamespacedID().contains("_stage_")){
                                    AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.beforePlant);
                                    return;
                                }else {
                                    if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                    AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                                    PotUtils.addFertilizer(fertilizerConfig, block.getLocation());
                                }
                            }else {
                                if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                                PotUtils.addFertilizer(fertilizerConfig, block.getLocation());
                            }
                        }else if (namespacedID.contains("_stage_")){
                            if (!fertilizerConfig.isBefore()){
                                if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                PotUtils.addFertilizer(fertilizerConfig, block.getLocation().subtract(0,1,0));
                                AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                            }else {
                                AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.beforePlant);
                                return;
                            }
                        }
                        return;
                    }
                    Sprinkler sprinkler = ConfigReader.SPRINKLERS.get(itemNID);
                    if (sprinkler != null && action == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP){
                        Location location = event.getClickedBlock().getLocation();
                        for (Integration integration : ConfigReader.Config.integration)
                            if (!integration.canPlace(location, player)) return;
                        if (FurnitureUtils.isSprinkler(location.clone().add(0.5, 1.5, 0.5))) return;
                        if (SprinklersPerChunk.isLimited(location)){
                            AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.sprinkler_limit.replace("{max}", String.valueOf(ConfigReader.Config.sprinklerLimit)));
                            return;
                        }
                        Sprinkler sprinklerData = new Sprinkler(sprinkler.getRange(), 0);
                        sprinklerData.setPlayer(player.getName());
                        if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                        SimpleLocation simpleLocation = LocUtils.fromLocation(location.add(0,1,0));
                        SprinklerManager.Cache.put(simpleLocation, sprinklerData);
                        SprinklerManager.RemoveCache.remove(simpleLocation);
                        FurnitureUtils.placeFurniture(sprinkler.getNamespacedID_2(),location);
                        AdventureManager.playerSound(player, ConfigReader.Sounds.placeSprinklerSource, ConfigReader.Sounds.placeSprinklerKey);
                        return;
                    }
                    if (ConfigReader.Message.hasCropInfo && itemNID.equals(ConfigReader.Basic.soilDetector) && action == Action.RIGHT_CLICK_BLOCK){
                        Block block = event.getClickedBlock();
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                        if (customBlock == null) return;
                        for (Integration integration : ConfigReader.Config.integration) if(!integration.canPlace(block.getLocation(), player)) return;
                        String namespacedID = customBlock.getNamespacedID();
                        if (namespacedID.contains("_stage_")){
                            Location location = block.getLocation().subtract(0,1,0);
                            Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location));
                            if (fertilizer != null){
                                Fertilizer config = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                                if (config == null){
                                    PotManager.Cache.remove(LocUtils.fromLocation(location));
                                    return;
                                }
                                HoloUtils.showHolo(
                                        ConfigReader.Message.cropText
                                                .replace("{fertilizer}", config.getName())
                                                .replace("{times}", String.valueOf(fertilizer.getTimes()))
                                                .replace("{max_times}", String.valueOf(config.getTimes())),
                                        player,
                                        location.add(0.5, ConfigReader.Message.cropOffset, 0.5),
                                        ConfigReader.Message.cropTime);
                            }
                        }
                        else if(namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                            Location location = block.getLocation();
                            Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(block.getLocation()));
                            if (fertilizer != null){
                                Fertilizer config = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                                if (config == null){
                                    PotManager.Cache.remove(LocUtils.fromLocation(location));
                                    return;
                                }
                                HoloUtils.showHolo(
                                        ConfigReader.Message.cropText
                                                .replace("{fertilizer}", config.getName())
                                                .replace("{times}", String.valueOf(fertilizer.getTimes()))
                                                .replace("{max_times}", String.valueOf(config.getTimes())),
                                        player,
                                        location.add(0.5,ConfigReader.Message.cropOffset,0.5),
                                        ConfigReader.Message.cropTime);
                            }
                        }
                    }
                }
                else if (ConfigReader.Config.boneMeal && itemStack.getType() == Material.BONE_MEAL && action == Action.RIGHT_CLICK_BLOCK){
                    Block block = event.getClickedBlock();
                    CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                    if (customBlock == null) return;
                    for (Integration integration : ConfigReader.Config.integration)
                        if(!integration.canPlace(block.getLocation(), player)) return;
                    String namespacedID = customBlock.getNamespacedID();
                    if (namespacedID.contains("_stage_") && !namespacedID.equals(ConfigReader.Basic.dead)){
                        int nextStage = Integer.parseInt(namespacedID.substring(namespacedID.length()-1)) + 1;
                        String next = StringUtils.chop(namespacedID) + nextStage;
                        if (CustomBlock.getInstance(next) != null){
                            Location location = block.getLocation();
                            if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                            AdventureManager.playerSound(player, ConfigReader.Sounds.boneMealSource, ConfigReader.Sounds.boneMealKey);
                            if (Math.random() < ConfigReader.Config.boneMealChance){
                                CustomBlock.remove(location);
                                CustomBlock.place(next, location);
                                block.getWorld().spawnParticle(ConfigReader.Config.boneMealSuccess, location.add(0.5,0.3,0.5),5,0.2,0.2,0.2);
                            }
                        }
                    }
                }
                else if(ConfigReader.Config.rightClickHarvest && !ConfigReader.Config.needEmptyHand && action == Action.RIGHT_CLICK_BLOCK)
                    rightClickHarvest(event.getClickedBlock(), player);
            }
            else if (ConfigReader.Config.rightClickHarvest && action == Action.RIGHT_CLICK_BLOCK)
                rightClickHarvest(event.getClickedBlock(), player);
        }
    }

    /**
     * 右键收获判定
     * @param block 农作物方块
     * @param player 玩家
     */
    private void rightClickHarvest(Block block, Player player) {
        Location location = block.getLocation();
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) return;
        for (Integration integration : ConfigReader.Config.integration)
            if (!integration.canBreak(location, player)) return;
        String namespacedID = customBlock.getNamespacedID();
        if (namespacedID.contains("_stage_")){
            if(namespacedID.equals(ConfigReader.Basic.dead)) return;
            String[] cropNameList = StringUtils.split(customBlock.getId(), "_");
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) == null) {
                Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                if (ConfigReader.Config.quality){
                    ThreadLocalRandom current = ThreadLocalRandom.current();
                    int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                    World world = location.getWorld();
                    Location itemLoc = location.clone().add(0.5,0.2,0.5);
                    Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location.clone().subtract(0,1,0)));
                    List<String> commands = cropInstance.getCommands();
                    if (commands != null)
                        for (String command : commands)
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                    if (ConfigReader.Config.skillXP != null && cropInstance.getSkillXP() != 0) ConfigReader.Config.skillXP.addXp(player, cropInstance.getSkillXP());
                    if (cropInstance.doesDropIALoot()) customBlock.getLoot().forEach(itemStack -> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack));
                    if (cropInstance.getOtherLoots() != null) cropInstance.getOtherLoots().forEach(s -> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), CustomStack.getInstance(s).getItemStack()));
                    if (fertilizer != null){
                        Fertilizer fConfig = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                        if (fConfig == null) return;
                        if (fConfig instanceof QualityCrop qualityCrop){
                            int[] weights = qualityCrop.getChance();
                            double weightTotal = weights[0] + weights[1] + weights[2];
                            for (int i = 0; i < random; i++){
                                double ran = Math.random();
                                if (ran < weights[0]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                else if(ran > 1 - weights[1]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                else world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                            }
                        }else if (fConfig instanceof YieldIncreasing yieldIncreasing){
                            if (Math.random() < yieldIncreasing.getChance()){
                                random += yieldIncreasing.getBonus();
                            }
                            DropUtils.normalDrop(cropInstance, random, itemLoc, world);
                        }
                        else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
                    }
                    else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
                }
                else customBlock.getLoot().forEach(loot-> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), loot));
                CustomBlock.remove(location);
                AdventureManager.playerSound(player, ConfigReader.Sounds.harvestSource, ConfigReader.Sounds.harvestKey);
                if(cropInstance.getReturnStage() != null){
                    CustomBlock.place(cropInstance.getReturnStage(), location);
                    SimpleLocation simpleLocation = LocUtils.fromLocation(location);
                    CropManager.RemoveCache.remove(simpleLocation);
                    CropManager.Cache.put(simpleLocation, player.getName());
                }
            }
        }
    }
}
