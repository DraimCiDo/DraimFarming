package net.draimcido.draimfarming.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.datamanager.SeasonManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Placeholders extends PlaceholderExpansion{

    @Override
    public @NotNull String getIdentifier() {
        return "draimfarming";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DraimGooSe";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("season")){
            if (!ConfigReader.Season.enable) return "null";
            return Optional.ofNullable(SeasonManager.SEASON.get(player.getPlayer().getWorld().getName())).orElse(ConfigReader.Message.noSeason)
                    .replace("spring", ConfigReader.Message.spring)
                    .replace("summer", ConfigReader.Message.summer)
                    .replace("autumn", ConfigReader.Message.autumn)
                    .replace("winter", ConfigReader.Message.winter);
        }
        if (params.startsWith("season_")){
            if (!ConfigReader.Season.enable) return "null";
            return SeasonManager.SEASON.get(params.substring(7))
                    .replace("spring", ConfigReader.Message.spring)
                    .replace("summer", ConfigReader.Message.summer)
                    .replace("autumn", ConfigReader.Message.autumn)
                    .replace("winter", ConfigReader.Message.winter);
        }
        if (params.equalsIgnoreCase("nextseason")){
            if (!ConfigReader.Season.enable) return "null";
            if (!ConfigReader.Config.worlds.contains(player.getPlayer().getWorld())) return ConfigReader.Message.noSeason;
            return String.valueOf(ConfigReader.Season.duration - ((int) ((player.getPlayer().getWorld().getFullTime() / 24000L) % (ConfigReader.Season.duration * 4)) % ConfigReader.Season.duration));
        }
        if (params.startsWith("nextseason_")){
            if (!ConfigReader.Season.enable) return "null";
            return String.valueOf(ConfigReader.Season.duration - ((int) ((Bukkit.getWorld(params.substring(11)).getFullTime() / 24000L) % (ConfigReader.Season.duration * 4)) % ConfigReader.Season.duration));
        }
        if (params.equalsIgnoreCase("current")){
            if (!ConfigReader.Season.enable) return "null";
            if (!ConfigReader.Config.worlds.contains(player.getPlayer().getWorld())) return ConfigReader.Message.noSeason;
            return String.valueOf((int) ((player.getPlayer().getWorld().getFullTime() / 24000L) % (ConfigReader.Season.duration * 4)) % ConfigReader.Season.duration + 1);
        }
        if (params.startsWith("current_")){
            if (!ConfigReader.Season.enable) return "null";
            return String.valueOf(((int) (Bukkit.getWorld(params.substring(8)).getFullTime() / 24000L) % (ConfigReader.Season.duration * 4)) % ConfigReader.Season.duration+ 1);
        }
        return null;
    }
}
