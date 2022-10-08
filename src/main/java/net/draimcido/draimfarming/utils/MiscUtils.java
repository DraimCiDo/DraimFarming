package net.draimcido.draimfarming.utils;

import net.draimcido.draimfarming.objects.SimpleLocation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class MiscUtils {

    public static SimpleLocation getSimpleLocation(Location location) {
        return new SimpleLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Nullable
    public static Location getLocation(SimpleLocation location) {
        World world = Bukkit.getWorld(location.getWorldName());
        if (world == null) return null;
        return new Location(world, location.getX(), location.getY(), location.getZ());
    }

    public static SimpleLocation getSimpleLocation(String location, String world) {
        String[] loc = StringUtils.split(location, ",");
        return new SimpleLocation(world, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
    }
}
