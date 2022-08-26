package net.draimcido.draimfarming.integrations.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Integration {
    boolean canBreak(Location location, Player player);
    boolean canPlace(Location location, Player player);
}
