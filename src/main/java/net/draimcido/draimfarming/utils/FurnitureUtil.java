package net.draimcido.draimfarming.utils;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Random;


public class FurnitureUtil {

    private static final Rotation[] rotations4 = {Rotation.NONE, Rotation.FLIPPED, Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE};

    public static ItemFrame getItemFrame(Location location) {
        for(Entity entity : location.getWorld().getNearbyEntities(location,0,0,0)){
            if (entity instanceof ItemFrame itemFrame) {
                return itemFrame;
            }
        }
        return null;
    }

    public static boolean hasFurniture(Location location) {
        return getItemFrame(location) != null;
    }

    public static Rotation getRandomRotation() {
        return rotations4[new Random().nextInt(rotations4.length-1)];
    }
}
