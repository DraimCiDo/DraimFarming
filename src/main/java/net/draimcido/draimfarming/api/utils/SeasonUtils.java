package net.draimcido.draimfarming.api.utils;

import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.integrations.season.DFSeason;

import org.bukkit.World;

import org.jetbrains.annotations.NotNull;

public class SeasonUtils {

    public static void setSeason(World world, DFSeason season) {
        Main.plugin.getCropManager().getSeasonAPI().setSeason(season, world);
    }

    @NotNull
    public static DFSeason getSeason(World world) {
        return Main.plugin.getCropManager().getSeasonAPI().getSeason(world);
    }

    public static void unloadSeason(World world) {
        Main.plugin.getCropManager().getSeasonAPI().unloadWorld(world);
    }
}
