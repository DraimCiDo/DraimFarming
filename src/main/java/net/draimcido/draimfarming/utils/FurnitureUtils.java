package net.draimcido.draimfarming.utils;

import dev.lone.itemsadder.api.CustomFurniture;
import net.draimcido.draimfarming.ConfigReader;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Random;

public class FurnitureUtils {

    static Rotation[] rotations4 = {Rotation.NONE, Rotation.FLIPPED, Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE};
    static Rotation[] rotations8 = {Rotation.NONE, Rotation.FLIPPED, Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE,
            Rotation.CLOCKWISE_45, Rotation.CLOCKWISE_135, Rotation.FLIPPED_45, Rotation.COUNTER_CLOCKWISE_45};

    public static void placeFurniture(String name, Location location){
        CustomFurniture.spawn(name, location.getBlock());
    }

    public static void placeCrop(String name, Location location){
        CustomFurniture customFurniture = CustomFurniture.spawn(name, location.getBlock());
        Entity entity = customFurniture.getArmorstand();
        if (ConfigReader.Config.rotation && entity instanceof ItemFrame itemFrame){
            if (ConfigReader.Config.variant4) itemFrame.setRotation(rotations4[new Random().nextInt(rotations4.length-1)]);
            else itemFrame.setRotation(rotations8[new Random().nextInt(rotations8.length-1)]);
        }
    }

    public static String getNamespacedID(Location location){
        CustomFurniture furniture = getFurniture(location);
        if (furniture != null){
            return furniture.getNamespacedID();
        }else {
            return null;
        }
    }

    public static CustomFurniture getFurniture(Location location){
        for(Entity entity : location.getWorld().getNearbyEntities(location,0,0,0)){
            CustomFurniture furniture = CustomFurniture.byAlreadySpawned(entity);
            if(furniture != null) return furniture;
        }
        return null;
    }

    public static boolean isSprinkler(Location location){
        String furniture = getNamespacedID(location);
        if (furniture != null) return ConfigReader.SPRINKLERS.get(furniture) != null;
        else return false;
    }
}
