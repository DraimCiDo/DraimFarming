package net.draimcido.draimfarming.integrations.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import net.draimcido.draimfarming.Function;
import net.draimcido.draimfarming.config.SeasonConfig;
import org.bukkit.entity.Player;

public class PlaceholderManager extends Function {

    private SeasonPapi seasonPapi;

    public PlaceholderManager() {
        load();
    }

    @Override
    public void load() {
        super.load();
        if (SeasonConfig.enable) {
            this.seasonPapi = new SeasonPapi();
            this.seasonPapi.register();
        }
    }

    @Override
    public void unload() {
        super.unload();
        if (this.seasonPapi != null) {
            this.seasonPapi.unregister();
        }
    }

    public String parse(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
