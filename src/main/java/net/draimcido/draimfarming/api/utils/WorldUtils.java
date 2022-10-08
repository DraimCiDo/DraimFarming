package net.draimcido.draimfarming.api.utils;

import net.draimcido.draimfarming.Main;

import org.bukkit.World;

public class WorldUtils {

    public static void loadCropWorld(World world) {
        Main.plugin.getCropManager().onWorldLoad(world);
    }

    public static void unloadCropWorld(World world, boolean disable) {
        Main.plugin.getCropManager().onWorldUnload(world, disable);
    }
}
