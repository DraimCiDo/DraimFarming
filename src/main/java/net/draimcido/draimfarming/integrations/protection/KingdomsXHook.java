package net.draimcido.draimfarming.integrations.protection;

import net.draimcido.draimfarming.integrations.AntiGrief;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.player.KingdomPlayer;

public class KingdomsXHook implements AntiGrief {

    @Override
    public boolean canBreak(Location location, Player player) {
        return kingdomsCheck(location, player);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        return kingdomsCheck(location, player);
    }

    private boolean kingdomsCheck(Location location, Player player) {
        Land land = Land.getLand(location);
        if (land == null) return true;
        if (land.isClaimed()) {
            KingdomPlayer kp = KingdomPlayer.getKingdomPlayer(player);
            Kingdom cropKingdom = land.getKingdom();
            if (kp.getKingdom() != null) {
                Kingdom kingdom = kp.getKingdom();
                return kingdom != cropKingdom;
            }
            else return false;
        }
        else return true;
    }
}
