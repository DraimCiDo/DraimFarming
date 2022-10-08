package net.draimcido.draimfarming.integrations;

import net.draimcido.draimfarming.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface AntiGrief {

    boolean canBreak(Location location, Player player);

    boolean canPlace(Location location, Player player);

    static boolean testBreak(Player player, Location location) {
        for (AntiGrief antiGrief : MainConfig.antiGriefs) {
            if(!antiGrief.canBreak(location, player)) {
                return false;
            }
        }
        return true;
    }

    static boolean testPlace(Player player, Location location) {
        for (AntiGrief antiGrief : MainConfig.antiGriefs) {
            if(!antiGrief.canPlace(location, player)) {
                return false;
            }
        }
        return true;
    }
}
