package net.draimcido.draimfarming.integrations.protection;

import net.draimcido.draimfarming.integrations.AntiGrief;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook implements AntiGrief {

    @Override
    public boolean canBreak(Location location, Player player) {
        return me.ryanhamshire.GriefPrevention.GriefPrevention.instance.allowBreak(player, location.getBlock(), location) == null;
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        return me.ryanhamshire.GriefPrevention.GriefPrevention.instance.allowBuild(player, location) == null;
    }
}
