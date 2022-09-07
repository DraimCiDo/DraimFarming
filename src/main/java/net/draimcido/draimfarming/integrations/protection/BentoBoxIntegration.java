package net.draimcido.draimfarming.integrations.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

import java.util.Optional;

public class BentoBoxIntegration implements Integration{

    @Override
    public boolean canBreak(Location location, Player player) {
        User user = User.getInstance(player);
        Optional<Island> islandOptional = BentoBox.getInstance().getIslands().getIslandAt(location);
        return islandOptional.map(island -> island.isAllowed(user, Flags.BREAK_BLOCKS)).orElse(true);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        User user = User.getInstance(player);
        Optional<Island> islandOptional = BentoBox.getInstance().getIslands().getIslandAt(location);
        return islandOptional.map(island -> island.isAllowed(user, Flags.PLACE_BLOCKS)).orElse(true);
    }
}
