package net.draimcido.draimfarming.listener;

import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.utils.JedisUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;

public class JoinAndQuit implements Listener {

    public static HashSet<String> onlinePlayers = new HashSet<>();
    public static HashMap<Player, Long> coolDown = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (ConfigReader.useRedis) JedisUtils.addPlayer(event.getPlayer().getName());
        else onlinePlayers.add(event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if (ConfigReader.useRedis) JedisUtils.remPlayer(event.getPlayer().getName());
        else onlinePlayers.remove(event.getPlayer().getName());
        coolDown.remove(event.getPlayer());
    }
}
