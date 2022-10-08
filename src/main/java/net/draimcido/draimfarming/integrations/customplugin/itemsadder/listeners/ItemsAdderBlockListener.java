package net.draimcido.draimfarming.integrations.customplugin.itemsadder.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import net.draimcido.draimfarming.integrations.customplugin.itemsadder.ItemsAdderHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderBlockListener implements Listener {

    private final ItemsAdderHandler handler;

    public ItemsAdderBlockListener(ItemsAdderHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onBreakBlock(CustomBlockBreakEvent event) {
        handler.onBreakBlock(event);
    }

    @EventHandler
    public void onInteractBlock(CustomBlockInteractEvent event) {
        handler.onInteractBlock(event);
    }
}
