package net.draimcido.draimfarming.integrations.customplugin.oraxen.listeners;

import io.th0rgal.oraxen.events.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.events.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.events.OraxenFurniturePlaceEvent;
import net.draimcido.draimfarming.integrations.customplugin.oraxen.OraxenHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OraxenFurnitureListener implements Listener {

    private final OraxenHandler handler;

    public OraxenFurnitureListener(OraxenHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        handler.onInteractFurniture(event);
    }

    @EventHandler
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        handler.onBreakFurniture(event);
    }

    @EventHandler
    public void onPlaceFurniture(OraxenFurniturePlaceEvent event) {
        handler.placeScarecrow(event);
    }
}
