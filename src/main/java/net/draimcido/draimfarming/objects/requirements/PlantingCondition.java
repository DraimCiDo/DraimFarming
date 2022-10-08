package net.draimcido.draimfarming.objects.requirements;

import net.draimcido.draimfarming.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PlantingCondition {

    private final Location location;
    private final Player player;
    private HashMap<String, String> papiMap;

    public PlantingCondition(Location location, Player player) {
        this.location = location;
        this.player = player;
        if (Main.plugin.hasPapi()){
            this.papiMap = new HashMap<>();
            CustomPapi.allPapi.forEach(papi -> this.papiMap.put(papi, Main.plugin.getPlaceholderManager().parse(player, papi)));
        }
    }

    @Nullable
    public HashMap<String, String> getPapiMap() {
        return papiMap;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }
}
