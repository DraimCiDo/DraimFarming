package net.draimcido.draimfarming.api.event;

import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.jetbrains.annotations.NotNull;

public class FertilizerUseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Fertilizer fertilizer;
    private final Location potLoc;

    public FertilizerUseEvent(@NotNull Player who, Fertilizer fertilizer, Location potLoc) {
        super(who);
        this.cancelled = false;
        this.potLoc = potLoc;
        this.fertilizer = fertilizer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(Fertilizer fertilizer) {
        this.fertilizer = fertilizer;
    }

    public Location getPotLoc() {
        return potLoc;
    }
}
