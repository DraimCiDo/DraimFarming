package net.draimcido.draimfarming.managers.listener;

import net.draimcido.draimfarming.managers.CropManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {

    private final CropManager cropManager;

    public WorldListener(CropManager cropManager) {
        this.cropManager = cropManager;
    }

    @EventHandler
    public void onWorldUnload(WorldLoadEvent event) {
        cropManager.onWorldLoad(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        cropManager.onWorldUnload(event.getWorld(), false);
    }
}
