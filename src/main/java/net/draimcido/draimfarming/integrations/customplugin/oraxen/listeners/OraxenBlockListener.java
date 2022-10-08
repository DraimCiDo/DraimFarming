package net.draimcido.draimfarming.integrations.customplugin.oraxen.listeners;

import io.th0rgal.oraxen.events.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.events.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.events.OraxenStringBlockBreakEvent;
import io.th0rgal.oraxen.events.OraxenStringBlockInteractEvent;
import net.draimcido.draimfarming.integrations.customplugin.oraxen.OraxenHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OraxenBlockListener implements Listener {

    private final OraxenHandler handler;

    public OraxenBlockListener(OraxenHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onBreakNote(OraxenNoteBlockBreakEvent event) {
        handler.onBreakNoteBlock(event);
    }

    @EventHandler
    public void onInteractNote(OraxenNoteBlockInteractEvent event) {
        handler.onInteractNoteBlock(event);
    }

    @EventHandler
    public void onBreakString(OraxenStringBlockBreakEvent event) {
        handler.onBreakStringBlock(event);
    }

    @EventHandler
    public void onInteractString(OraxenStringBlockInteractEvent event) {
        handler.onInteractStringBlock(event);
    }
}
