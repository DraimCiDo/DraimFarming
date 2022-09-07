package net.draimcido.draimfarming.listener.itemframe;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.datamanager.CropManager;
import net.draimcido.draimfarming.datamanager.PotManager;
import net.draimcido.draimfarming.datamanager.SprinklerManager;
import net.draimcido.draimfarming.integrations.protection.Integration;
import net.draimcido.draimfarming.listener.JoinAndQuit;
import net.draimcido.draimfarming.objects.Crop;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.draimcido.draimfarming.objects.Sprinkler;
import net.draimcido.draimfarming.objects.WateringCan;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;
import net.draimcido.draimfarming.objects.fertilizer.QualityCrop;
import net.draimcido.draimfarming.objects.fertilizer.YieldIncreasing;
import net.draimcido.draimfarming.utils.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class InteractFurnitureI implements Listener {

    @EventHandler
    public void onEntityInteract(FurnitureInteractEvent event){
        long time = System.currentTimeMillis();
        Player player = event.getPlayer();
        if (time - (JoinAndQuit.coolDown.getOrDefault(player, time - 200)) < 200) return;
        JoinAndQuit.coolDown.put(player, time);
        String namespacedID = event.getNamespacedID();
        Sprinkler config = ConfigReader.SPRINKLERS.get(namespacedID);
        if(config != null){
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Location location = event.getBukkitEntity().getLocation();
            String world = location.getWorld().getName();
            int x = location.getBlockX();
            int z = location.getBlockZ();
            int maxWater = config.getWater();
            int currentWater = 0;
            Location loc = location.clone().subtract(0,1,0).getBlock().getLocation().add(0,1,0);
            Sprinkler sprinkler = SprinklerManager.Cache.get(LocUtils.fromLocation(loc));
            if (itemStack.getType() == Material.WATER_BUCKET){
                itemStack.setType(Material.BUCKET);
                if (sprinkler != null){
                    currentWater = sprinkler.getWater();
                    currentWater += ConfigReader.Config.sprinklerRefill;
                    if (currentWater > maxWater) currentWater = maxWater;
                    sprinkler.setWater(currentWater);
                }else {
                    String path = world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z;
                    currentWater = SprinklerManager.data.getInt(path+ ".water");
                    currentWater += ConfigReader.Config.sprinklerRefill;
                    if (currentWater > maxWater) currentWater = maxWater;
                    SprinklerManager.data.set(path + ".water", currentWater);
                    SprinklerManager.data.set(path + ".range", config.getRange());
                }
                AdventureManager.playerSound(player, ConfigReader.Sounds.addWaterToSprinklerSource, ConfigReader.Sounds.addWaterToSprinklerKey);
            }
            else {
                if (ConfigReader.Config.canAddWater && itemStack.getType() != Material.AIR){
                    NBTItem nbtItem = new NBTItem(itemStack);
                    NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
                    if (nbtCompound != null) {
                        String id = nbtCompound.getString("id");
                        String namespace = nbtCompound.getString("namespace");
                        WateringCan wateringCan = ConfigReader.CANS.get(namespace + ":" + id);
                        if (wateringCan != null) {
                            int water = nbtItem.getInteger("WaterAmount");
                            if (water > 0){
                                nbtItem.setInteger("WaterAmount", --water);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.addWaterToSprinklerSource, ConfigReader.Sounds.addWaterToSprinklerKey);
                                if (nbtCompound.hasKey("custom_durability")){
                                    int dur = nbtCompound.getInteger("custom_durability");
                                    int max_dur = nbtCompound.getInteger("max_custom_durability");
                                    if (dur > 0){
                                        nbtCompound.setInteger("custom_durability", dur - 1);
                                        nbtCompound.setDouble("fake_durability", (int) itemStack.getType().getMaxDurability() * (double) (dur/max_dur));
                                        nbtItem.setInteger("Damage", (int) (itemStack.getType().getMaxDurability() * (1 - (double) dur/max_dur)));
                                    } else {
                                        AdventureManager.playerSound(player, net.kyori.adventure.sound.Sound.Source.PLAYER, Key.key("minecraft:item.shield.break"));
                                        itemStack.setAmount(itemStack.getAmount() - 1);
                                    }
                                }
                                if (sprinkler != null){
                                    currentWater = sprinkler.getWater();
                                    currentWater++;
                                    if (currentWater > maxWater) currentWater = maxWater;
                                    sprinkler.setWater(currentWater);
                                }else {
                                    String path = world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z;
                                    currentWater = SprinklerManager.data.getInt(path + ".water");
                                    currentWater++;
                                    if (currentWater > maxWater) currentWater = maxWater;
                                    SprinklerManager.data.set(path + ".water", currentWater);
                                    SprinklerManager.data.set(path + ".range", config.getRange());
                                }
                            }
                            else {
                                currentWater = SprinklerManager.getCurrentWater(location, world, x, z, sprinkler);
                            }
                            if (ConfigReader.Message.hasWaterInfo){
                                AdventureManager.playerActionbar(player,
                                        (ConfigReader.Message.waterLeft +
                                                ConfigReader.Message.waterFull.repeat(water) +
                                                ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                ConfigReader.Message.waterRight)
                                                .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                .replace("{water}", String.valueOf(water)));
                            }
                            if (ConfigReader.Basic.hasWaterLore){
                                String string =
                                        (ConfigReader.Basic.waterLeft +
                                                ConfigReader.Basic.waterFull.repeat(water) +
                                                ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                ConfigReader.Basic.waterRight)
                                                .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                .replace("{water}", String.valueOf(water));
                                List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                lores.clear();
                                ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                            }
                            itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                        }
                    }
                    else currentWater = SprinklerManager.getCurrentWater(location, world, x, z, sprinkler);
                }
                else currentWater = SprinklerManager.getCurrentWater(location, world, x, z, sprinkler);
            }
            if (ConfigReader.Message.hasSprinklerInfo)
                HoloUtils.showHolo(
                        (ConfigReader.Message.sprinklerLeft +
                                ConfigReader.Message.sprinklerFull.repeat(currentWater) +
                                ConfigReader.Message.sprinklerEmpty.repeat(maxWater - currentWater) +
                                ConfigReader.Message.sprinklerRight)
                                .replace("{max_water}", String.valueOf(maxWater))
                                .replace("{water}", String.valueOf(currentWater)),
                        player,
                        location.add(0, ConfigReader.Message.sprinklerOffset,0),
                        ConfigReader.Message.sprinklerTime);
        }
        if (namespacedID.contains("_stage_")){
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.AIR){
                NBTItem nbtItem = new NBTItem(itemStack);
                NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
                if (nbtCompound != null){
                    Location location = event.getBukkitEntity().getLocation();
                    for (Integration integration : ConfigReader.Config.integration)
                        if(!integration.canPlace(location, player)) return;
                    String id = nbtCompound.getString("id");
                    String namespace = nbtCompound.getString("namespace");
                    String nsID = namespace + ":" +id;
                    WateringCan wateringCan = ConfigReader.CANS.get(nsID);
                    if (wateringCan != null){
                        int water = nbtItem.getInteger("WaterAmount");
                        if (water > 0){
                            nbtItem.setInteger("WaterAmount", --water);
                            if (nbtCompound.hasKey("custom_durability")){
                                int dur = nbtCompound.getInteger("custom_durability");
                                int max_dur = nbtCompound.getInteger("max_custom_durability");
                                if (dur > 0){
                                    nbtCompound.setInteger("custom_durability", dur - 1);
                                    nbtCompound.setDouble("fake_durability", (int) itemStack.getType().getMaxDurability() * (double) (dur/max_dur));
                                    nbtItem.setInteger("Damage", (int) (itemStack.getType().getMaxDurability() * (1 - (double) dur/max_dur)));
                                } else {
                                    AdventureManager.playerSound(player, net.kyori.adventure.sound.Sound.Source.PLAYER, Key.key("minecraft:item.shield.break"));
                                    itemStack.setAmount(itemStack.getAmount() - 1);
                                }
                            }
                            AdventureManager.playerSound(player, ConfigReader.Sounds.waterPotSource, ConfigReader.Sounds.waterPotKey);
                            PotUtils.waterPot(wateringCan.getWidth(), wateringCan.getLength(), location.subtract(0.5,1,0.5), player.getLocation().getYaw());
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
                        return;
                    }
                    Fertilizer fertilizerConfig = ConfigReader.FERTILIZERS.get(id);
                    if (fertilizerConfig != null){
                        if (!fertilizerConfig.isBefore()){
                            if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                            PotUtils.addFertilizer(fertilizerConfig, event.getBukkitEntity().getLocation().subtract(0,1,0));
                            AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                        }else {
                            AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.beforePlant);
                        }
                        return;
                    }
                    if (ConfigReader.Message.hasCropInfo && nsID.equals(ConfigReader.Basic.soilDetector)){
                        Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location.subtract(0,1,0)));
                        if (fertilizer != null){
                            Fertilizer fConfig = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                            if (fConfig == null) {
                                PotManager.Cache.remove(LocUtils.fromLocation(location));
                                return;
                            }
                            HoloUtils.showHolo(
                                    ConfigReader.Message.cropText
                                            .replace("{fertilizer}", fConfig.getName())
                                            .replace("{times}", String.valueOf(fertilizer.getTimes()))
                                            .replace("{max_times}", String.valueOf(fConfig.getTimes())),
                                    player,
                                    location.add(0, ConfigReader.Message.cropOffset, 0),
                                    ConfigReader.Message.cropTime);
                        }
                    }
                }
                else if (ConfigReader.Config.boneMeal && itemStack.getType() == Material.BONE_MEAL){
                    Entity entity = event.getBukkitEntity();
                    Location location = entity.getLocation();
                    for (Integration integration : ConfigReader.Config.integration)
                        if(!integration.canPlace(location, player)) return;
                    if (!namespacedID.equals(ConfigReader.Basic.dead)){
                        int nextStage = Integer.parseInt(namespacedID.substring(namespacedID.length()-1)) + 1;
                        String next = StringUtils.chop(namespacedID) + nextStage;
                        if (CustomFurniture.getInstance(next) != null){
                            if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                            AdventureManager.playerSound(player, ConfigReader.Sounds.boneMealSource, ConfigReader.Sounds.boneMealKey);
                            if (Math.random() < ConfigReader.Config.boneMealChance){
                                CustomFurniture.remove(entity, false);
                                FurnitureUtils.placeCrop(next, location);
                                location.getWorld().spawnParticle(ConfigReader.Config.boneMealSuccess, location.add(0,0.3,0),5,0.2,0.2,0.2);
                            }
                        }
                    }
                }
                else if(ConfigReader.Config.rightClickHarvest && !ConfigReader.Config.needEmptyHand){
                    rightClickHarvest(event.getFurniture(), player);
                }
            }
            else if(ConfigReader.Config.rightClickHarvest && !Objects.equals(ConfigReader.Basic.dead, namespacedID)){
                rightClickHarvest(event.getFurniture(), player);
            }
        }
    }

    /**
     * 右键收获判定
     * @param crop 农作物实体
     * @param player 玩家
     */
    private void rightClickHarvest(CustomFurniture crop, Player player) {
        Entity entity = crop.getArmorstand();
        Location location = entity.getLocation();
        for (Integration integration : ConfigReader.Config.integration)
            if(!integration.canBreak(location, player)) return;
        String namespacedID = crop.getNamespacedID();
        String[] cropNameList = StringUtils.split(namespacedID, "_");
        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
        if (CustomFurniture.getInstance(StringUtils.chop(namespacedID) + nextStage) == null) {
            CustomFurniture.remove(entity, false);
            Crop cropInstance = ConfigReader.CROPS.get(StringUtils.split(cropNameList[0], ":")[1]);
            if (ConfigReader.Config.quality){
                ThreadLocalRandom current = ThreadLocalRandom.current();
                int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                World world = location.getWorld();
                Location itemLoc = location.clone().add(0,0.2,0);
                Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location.clone().subtract(0,1,0)));
                List<String> commands = cropInstance.getCommands();
                if (commands != null)
                    for (String command : commands)
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                if (ConfigReader.Config.skillXP != null && cropInstance.getSkillXP() != 0) ConfigReader.Config.skillXP.addXp(player, cropInstance.getSkillXP());
                if (cropInstance.getOtherLoots() != null) cropInstance.getOtherLoots().forEach(s -> location.getWorld().dropItem(itemLoc, CustomStack.getInstance(s).getItemStack()));
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
                        DropUtils.normalDrop(cropInstance, random , itemLoc, world);
                    }
                    else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
                }
                else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
            }
            AdventureManager.playerSound(player, ConfigReader.Sounds.harvestSource, ConfigReader.Sounds.harvestKey);
            if(cropInstance.getReturnStage() != null){
                FurnitureUtils.placeCrop(cropInstance.getReturnStage(), location);
                SimpleLocation simpleLocation = LocUtils.fromLocation(location);
                CropManager.RemoveCache.remove(simpleLocation);
                CropManager.Cache.put(simpleLocation, player.getName());
            }
        }
    }
}
