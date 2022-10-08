package net.draimcido.draimfarming.integrations.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.draimcido.draimfarming.api.utils.SeasonUtils;
import net.draimcido.draimfarming.config.MainConfig;
import net.draimcido.draimfarming.config.MessageConfig;
import net.draimcido.draimfarming.config.SeasonConfig;
import net.draimcido.draimfarming.integrations.season.DFSeason;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeasonPapi extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "dfarming";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DraimGooSe";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (!SeasonConfig.enable) return MessageConfig.seasonDisabled;
        switch (params) {
            case "current" -> {
                if (!MainConfig.getWorldsList().contains(player.getWorld())) return MessageConfig.noSeason;
                return getSeasonText(player.getWorld());
            }
            case "days_left" -> {
                if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                if (!MainConfig.getWorldsList().contains(player.getWorld())) return MessageConfig.noSeason;
                return String.valueOf(SeasonConfig.duration - ((int) ((player.getWorld().getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration));
            }
            case "days_gone" -> {
                if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                if (!MainConfig.getWorldsList().contains(player.getWorld())) return MessageConfig.noSeason;
                return String.valueOf((int) ((player.getWorld().getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration + 1);
            }
            default -> {
                if (params.startsWith("current_")) {
                    World world = Bukkit.getWorld(params.substring(8));
                    if (world == null) return MessageConfig.noSeason;
                    if (!MainConfig.getWorldsList().contains(world)) return MessageConfig.noSeason;
                    return getSeasonText(world);
                }
                if (params.startsWith("days_left_")) {
                    if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                    World world = Bukkit.getWorld(params.substring(10));
                    if (world == null) return MessageConfig.noSeason;
                    if (!MainConfig.getWorldsList().contains(world)) return MessageConfig.noSeason;
                    return String.valueOf(SeasonConfig.duration - ((int) ((world.getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration));
                }
                if (params.startsWith("days_gone_")) {
                    if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                    World world = Bukkit.getWorld(params.substring(10));
                    if (world == null) return MessageConfig.noSeason;
                    if (!MainConfig.getWorldsList().contains(world)) return MessageConfig.noSeason;
                    return String.valueOf((int) ((world.getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration + 1);
                }
            }
        }
        return "null";
    }

    private String getSeasonText(World world) {
        DFSeason season = SeasonUtils.getSeason(world);
        return switch (season) {
            case SPRING -> MessageConfig.spring;
            case SUMMER -> MessageConfig.summer;
            case AUTUMN -> MessageConfig.autumn;
            case WINTER -> MessageConfig.winter;
            default -> throw new IllegalStateException("Неожиданное значение: " + season);
        };
    }
}
