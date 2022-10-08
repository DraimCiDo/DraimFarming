package net.draimcido.draimfarming.integrations.season;

import me.casperge.realisticseasons.api.SeasonsAPI;
import net.draimcido.draimfarming.Function;
import net.draimcido.draimfarming.config.MainConfig;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RealisticSeasonsHook extends Function implements SeasonInterface {

    private SeasonsAPI api;

    @Override
    public void load() {
        super.load();
        this.api = SeasonsAPI.getInstance();
    }

    @Override
    public void unload() {
        super.unload();
    }

    @Override
    public boolean isWrongSeason(World world, @Nullable DFSeason[] seasonList) {
        if (seasonList == null) return false;
        for (DFSeason season : seasonList) {
            if (season == getSeason(world)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void unloadWorld(World world) {
    }

    @Override
    @NotNull
    public DFSeason getSeason(World world){
        if (!MainConfig.syncSeason) {
            switch (api.getSeason(world)){
                case SPRING -> {return DFSeason.SPRING;}
                case SUMMER -> {return DFSeason.SUMMER;}
                case WINTER -> {return DFSeason.WINTER;}
                case FALL -> {return DFSeason.AUTUMN;}
            }
        }
        else {
            switch (api.getSeason(MainConfig.syncWorld)){
                case SPRING -> {return DFSeason.SPRING;}
                case SUMMER -> {return DFSeason.SUMMER;}
                case WINTER -> {return DFSeason.WINTER;}
                case FALL -> {return DFSeason.AUTUMN;}
            }
        }
        return DFSeason.UNKNOWN;
    }

    @Override
    public void setSeason(DFSeason season, World world) {
    }
}
