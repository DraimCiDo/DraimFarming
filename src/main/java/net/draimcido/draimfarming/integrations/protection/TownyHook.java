package net.draimcido.draimfarming.integrations.protection;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import net.draimcido.draimfarming.integrations.AntiGrief;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyHook implements AntiGrief {

    @Override
    public boolean canBreak(Location location, Player player) {
        return TownyPermission(player, location, TownyPermission.ActionType.DESTROY);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        return TownyPermission(player, location, TownyPermission.ActionType.BUILD);
    }

    private boolean TownyPermission(Player player, Location location, TownyPermission.ActionType actionType){
        return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), actionType);
    }
}
