package net.draimcido.draimfarming.integrations.season;

import org.bukkit.World;

public interface SeasonInterface {

    DFSeason getSeason(World world);

    void setSeason(DFSeason season, World world);

    void load();

    void unload();

    boolean isWrongSeason(World world, DFSeason[] seasonList);

    void unloadWorld(World world);

}
