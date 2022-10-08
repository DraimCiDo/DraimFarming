package net.draimcido.draimfarming.managers.listener;

import net.draimcido.draimfarming.integrations.customplugin.HandlerP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InteractListener implements Listener {

    private final HandlerP handlerP;

    public InteractListener(HandlerP handlerP) {
        this.handlerP = handlerP;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        handlerP.onPlayerInteract(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handlerP.onQuit(event.getPlayer());
    }
}
