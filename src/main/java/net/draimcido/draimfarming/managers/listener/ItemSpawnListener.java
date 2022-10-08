package net.draimcido.draimfarming.managers.listener;

import net.draimcido.draimfarming.managers.CropManager;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ItemSpawnListener implements Listener {

    private final CropManager cropManager;

    public ItemSpawnListener(CropManager cropManager) {
        this.cropManager = cropManager;
    }

    @EventHandler
    public void onItemSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item item) {
            cropManager.onItemSpawn(item);
        }
    }
}
