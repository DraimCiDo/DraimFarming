package net.draimcido.draimfarming.integrations.protection;

import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.land.Area;
import net.draimcido.draimfarming.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsIntegration implements Integration{

    @Override
    public boolean canBreak(Location location, Player player) {
        Area area = new me.angeschossen.lands.api.integration.LandsIntegration(Main.plugin).getAreaByLoc(location);
        if (area != null) return area.hasFlag(player, Flags.BLOCK_BREAK, false);
        else return true;
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        Area area = new me.angeschossen.lands.api.integration.LandsIntegration(Main.plugin).getAreaByLoc(location);
        if (area != null) return area.hasFlag(player, Flags.BLOCK_PLACE, false);
        else return true;
    }
}
