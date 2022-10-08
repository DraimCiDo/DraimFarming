package net.draimcido.draimfarming.api.event;

import net.draimcido.draimfarming.api.crop.Crop;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropHarvestEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final Crop crop;
    private final Fertilizer fertilizer;

    public CropHarvestEvent(@NotNull Player who, Crop crop, Location location, @Nullable Fertilizer fertilizer) {
        super(who);
        this.crop = crop;
        this.location = location;
        this.cancelled = false;
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

    public Crop getCrop() {
        return crop;
    }

    public Location getLocation() {
        return location;
    }

    @Nullable
    public Fertilizer getFertilizer() {
        return fertilizer;
    }
}
