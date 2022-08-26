package net.draimcido.draimfarming.utils;

import dev.lone.itemsadder.api.CustomStack;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.objects.Crop;
import org.bukkit.Location;
import org.bukkit.World;

public class DropUtils {

    public static void normalDrop(Crop cropInstance, int random, Location itemLoc, World world) {
        for (int i = 0; i < random; i++){
            double ran = Math.random();
            if (ran < ConfigReader.Config.quality_1){
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
            }else if(ran > ConfigReader.Config.quality_2){
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
            }else {
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
            }
        }
    }

}
