package net.draimcido.draimfarming.utils;

import net.draimcido.draimfarming.objects.SimpleLocation;
import org.bukkit.Location;

public class LocUtils {
    public static SimpleLocation fromLocation(Location location){
        return new SimpleLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
