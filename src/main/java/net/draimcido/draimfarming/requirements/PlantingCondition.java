package net.draimcido.draimfarming.requirements;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public record PlantingCondition (Player player, Location location){
    public Location getLocation() { return location; }
    public Player getPlayer() {
        return player;
    }
}
