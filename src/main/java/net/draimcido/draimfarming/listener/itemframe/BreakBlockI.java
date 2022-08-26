package net.draimcido.draimfarming.listener.itemframe;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.datamanager.CropManager;
import net.draimcido.draimfarming.datamanager.PotManager;
import net.draimcido.draimfarming.integrations.protection.Integration;
import net.draimcido.draimfarming.objects.Crop;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;
import net.draimcido.draimfarming.objects.fertilizer.QualityCrop;
import net.draimcido.draimfarming.utils.DropUtils;
import net.draimcido.draimfarming.utils.FurnitureUtils;
import net.draimcido.draimfarming.utils.LocUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.ThreadLocalRandom;

public class BreakBlockI implements Listener {

    @EventHandler
    public void onBreak(CustomBlockBreakEvent event){
        String namespacedId = event.getNamespacedID();
        if(namespacedId.equalsIgnoreCase(ConfigReader.Basic.watered_pot) || namespacedId.equalsIgnoreCase(ConfigReader.Basic.pot)){
            Location location = event.getBlock().getLocation();
            PotManager.Cache.remove(LocUtils.fromLocation(location));
            World world = location.getWorld();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, event.getPlayer())) return;
            CustomFurniture furniture = FurnitureUtils.getFurniture(location.add(0.5,1.1,0.5));
            if(furniture != null){
                String nsID = furniture.getNamespacedID();
                if(nsID.contains("_stage_")){
                    SimpleLocation simpleLocation = LocUtils.fromLocation(location);
                    if (CropManager.Cache.remove(simpleLocation) == null){
                        CropManager.RemoveCache.add(simpleLocation);
                    }
                    CustomFurniture.remove(furniture.getArmorstand(), false);
                    if (nsID.equals(ConfigReader.Basic.dead)) return;
                    if (ConfigReader.Config.quality){
                        String[] cropNameList = StringUtils.split(StringUtils.split(nsID, ":")[1], "_");
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomFurniture.getInstance(StringUtils.chop(nsID) + nextStage) == null) {
                            Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                            ThreadLocalRandom current = ThreadLocalRandom.current();
                            int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                            Location itemLoc = location.clone().add(0.5,0.2,0.5);
                            Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location.clone().subtract(0,1,0)));
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
                                }
                            }
                            else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
                        }
                    }
                }
            }
        }
    }
}
