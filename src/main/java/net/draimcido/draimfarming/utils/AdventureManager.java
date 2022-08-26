package net.draimcido.draimfarming.utils;

import net.draimcido.draimfarming.Main;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

public class AdventureManager {

    /**
     * 发送控制台信息
     * @param s 文本
     */
    public static void consoleMessage(String s) {
        Audience au = Main.adventure.sender(Bukkit.getConsoleSender());
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(s);
        au.sendMessage(parsed);
    }

    /**
     * 发送玩家信息
     * @param player 玩家
     * @param s 文本
     */
    public static void playerMessage(Player player, String s) {
        Audience au = Main.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(s);
        au.sendMessage(parsed);
    }

    /**
     * 发送玩家标题
     * @param player 玩家
     * @param s1 主标题
     * @param s2 副标题
     * @param in 淡入时间
     * @param duration 停留时间
     * @param out 淡出时间
     */
    public static void playerTitle(Player player, String s1, String s2, int in, int duration, int out) {
        Audience au = Main.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        Title.Times times = Title.Times.times(Duration.ofMillis(in), Duration.ofMillis(duration), Duration.ofMillis(out));
        Title title = Title.title(mm.deserialize(s1), mm.deserialize(s2), times);
        au.showTitle(title);
    }

    /**
     * 发送动作消息
     * @param player 玩家
     * @param s 文本
     */
    public static void playerActionbar(Player player, String s) {
        Audience au = Main.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        au.sendActionBar(mm.deserialize(s));
    }

    /**
     * 发送声音
     * @param player 玩家
     * @param source 来源
     * @param key 键
     */
    public static void playerSound(Player player, Sound.Source source, Key key) {
        Sound sound = Sound.sound(key, source, 1, 1);
        Audience au = Main.adventure.player(player);
        au.playSound(sound);
    }
}
