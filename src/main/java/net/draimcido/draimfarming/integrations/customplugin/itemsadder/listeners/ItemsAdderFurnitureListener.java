package net.draimcido.draimfarming.integrations.customplugin.itemsadder.listeners;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import net.draimcido.draimfarming.integrations.customplugin.itemsadder.ItemsAdderHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderFurnitureListener implements Listener {

    private final ItemsAdderHandler handler;

    public ItemsAdderFurnitureListener(ItemsAdderHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        handler.onInteractFurniture(event);
    }

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event) {
        handler.onBreakFurniture(event);
    }

    @EventHandler
    public void onPlaceFurniture(FurniturePlaceSuccessEvent event) {
        handler.placeScarecrow(event);
    }
}
