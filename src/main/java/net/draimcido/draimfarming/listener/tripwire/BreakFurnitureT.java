package net.draimcido.draimfarming.listener.tripwire;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.datamanager.SprinklerManager;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.draimcido.draimfarming.objects.Sprinkler;
import net.draimcido.draimfarming.utils.LocUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BreakFurnitureT implements Listener {

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event){
        Sprinkler config = ConfigReader.SPRINKLERS.get(event.getNamespacedID());
        if (config != null){
            SimpleLocation simpleLocation = LocUtils.fromLocation(event.getBukkitEntity().getLocation());
            if(SprinklerManager.Cache.remove(simpleLocation) == null){
                SprinklerManager.RemoveCache.add(simpleLocation);
            }
        }
    }
}
