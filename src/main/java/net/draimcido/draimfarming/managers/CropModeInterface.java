package net.draimcido.draimfarming.managers;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface CropModeInterface {

    boolean growJudge(Location location);

    default void loadChunk(Location location) {
        Chunk chunk = location.getChunk();
        chunk.load();
    }
}
