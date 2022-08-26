package net.draimcido.draimfarming.listener.itemframe;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.datamanager.CropManager;
import net.draimcido.draimfarming.datamanager.PotManager;
import net.draimcido.draimfarming.datamanager.SprinklerManager;
import net.draimcido.draimfarming.integrations.protection.Integration;
import net.draimcido.draimfarming.objects.Crop;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.draimcido.draimfarming.objects.Sprinkler;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;
import net.draimcido.draimfarming.objects.fertilizer.QualityCrop;
import net.draimcido.draimfarming.objects.fertilizer.YieldIncreasing;
import net.draimcido.draimfarming.utils.DropUtils;
import net.draimcido.draimfarming.utils.LocUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BreakFurnitureI implements Listener {

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event){
        String namespacedID = event.getNamespacedID();
        Sprinkler config = ConfigReader.SPRINKLERS.get(namespacedID);
        if (config != null){
            SimpleLocation simpleLocation = LocUtils.fromLocation(event.getBukkitEntity().getLocation());
            if (SprinklerManager.Cache.remove(simpleLocation) == null){
                SprinklerManager.RemoveCache.add(simpleLocation);
            }
            return;
        }
        if (namespacedID.contains("_stage_")){
            Player player = event.getPlayer();
            Location location = event.getBukkitEntity().getLocation();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, player)) return;
            SimpleLocation simpleLocation = LocUtils.fromLocation(location);
            if (CropManager.Cache.remove(simpleLocation) == null){
                CropManager.RemoveCache.add(simpleLocation);
            }
            if (!ConfigReader.Config.quality || namespacedID.equals(ConfigReader.Basic.dead)) return;
            String[] cropNameList = StringUtils.split(StringUtils.split(namespacedID, ":")[1], "_");
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomFurniture.getInstance(StringUtils.chop(namespacedID) + nextStage) == null) {
                Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                ThreadLocalRandom current = ThreadLocalRandom.current();
                int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                Location itemLoc = location.clone().add(0,0.2,0);
                World world = location.getWorld();
                List<String> commands = cropInstance.getCommands();
                Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location.clone().subtract(0,1,0)));
                if (commands != null)
                    for (String command : commands)
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                if (ConfigReader.Config.skillXP != null && cropInstance.getSkillXP() != 0) ConfigReader.Config.skillXP.addXp(player, cropInstance.getSkillXP());
                if (fertilizer != null){
                    if (fertilizer instanceof QualityCrop qualityCrop){
                        int[] weights = qualityCrop.getChance();
                        double weightTotal = weights[0] + weights[1] + weights[2];
                        for (int i = 0; i < random; i++){
                            double ran = Math.random();
                            if (ran < weights[0]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                            else if(ran > 1 - weights[1]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                            else world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                        }
                    }else if (fertilizer instanceof YieldIncreasing yieldIncreasing){
                        if (Math.random() < yieldIncreasing.getChance()){
                            random += yieldIncreasing.getBonus();
                        }
                        DropUtils.normalDrop(cropInstance, random , itemLoc, world);
                    }
                    else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
                }
                else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
            }
        }
    }
}
