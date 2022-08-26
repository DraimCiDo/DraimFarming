package net.draimcido.draimfarming.helper;

import net.draimcido.draimfarming.Main;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Utility for quickly accessing a logger instance without using {@link Bukkit#getLogger()}
 */
public final class Log {

    public static void info(@NotNull String s) {
        Main.plugin.getLogger().info(s);
    }

    public static void warn(@NotNull String s) {
        Main.plugin.getLogger().warning(s);
    }

    public static void severe(@NotNull String s) {
        Main.plugin.getLogger().severe(s);
    }

    public static void warn(@NotNull String s, Throwable t) {
        Main.plugin.getLogger().log(Level.WARNING, s, t);
    }

    public static void severe(@NotNull String s, Throwable t) {
        Main.plugin.getLogger().log(Level.SEVERE, s, t);
    }

    private Log() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
