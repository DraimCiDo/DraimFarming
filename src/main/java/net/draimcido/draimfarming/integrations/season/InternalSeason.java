package net.draimcido.draimfarming.integrations.season;

import net.draimcido.draimfarming.Function;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.config.ConfigUtil;
import net.draimcido.draimfarming.config.MainConfig;
import net.draimcido.draimfarming.config.SeasonConfig;
import net.draimcido.draimfarming.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InternalSeason extends Function implements SeasonInterface {

    private ConcurrentHashMap<World, DFSeason> seasonHashMap;
    private BukkitTask task;

    public InternalSeason() {
        load();
    }

    @Override
    public void load() {
        super.load();
        this.seasonHashMap = new ConcurrentHashMap<>();
        startTimer();
    }

    @Override
    public void unload() {
        super.unload();
        this.seasonHashMap.clear();
        if (task != null) task.cancel();
    }

    @Override
    public boolean isWrongSeason(World world, @Nullable DFSeason[] seasonList) {
        if (seasonList == null) return false;
        for (DFSeason season : seasonList) {
            if (season == seasonHashMap.get(world)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void unloadWorld(World world) {
        seasonHashMap.remove(world);
    }

    @Override
    @NotNull
    public DFSeason getSeason(World world) {
        DFSeason season;
        if (MainConfig.syncSeason) season = seasonHashMap.get(MainConfig.syncWorld);
        else season = seasonHashMap.get(world);
        if (season == null) {
            season = countSeason(world);
            setSeason(season, world);
        }
        return season;
    }

    @Override
    public void setSeason(DFSeason season, World world) {
        if (season == DFSeason.UNKNOWN) {
            setSeason(countSeason(world), world);
        }
        else {
            seasonHashMap.put(world, season);
        }
    }

    private void startTimer() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!SeasonConfig.auto) return;
                for (World world : MainConfig.getWorldsArray()) {
                    if (world.getTime() < 100) {
                        setSeason(countSeason(world), world);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 0, 100);
    }

    private DFSeason countSeason(World world) {
        int season = (int) ((world.getFullTime() / 24000L) % (SeasonConfig.duration * 4)) / SeasonConfig.duration;
        return switch (season) {
            case 0 -> DFSeason.SPRING;
            case 1 -> DFSeason.SUMMER;
            case 2 -> DFSeason.AUTUMN;
            case 3 -> DFSeason.WINTER;
            default -> DFSeason.UNKNOWN;
        };
    }
}
