package net.draimcido.draimfarming.utils;

import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.helper.Log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JedisUtils {

    private static JedisPool jedisPool;

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    public static void initializeRedis(YamlConfiguration configuration){

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(configuration.getInt("redis.MinEvictableIdleTimeMillis",1800000));
        jedisPoolConfig.setMaxTotal(configuration.getInt("redis.MaxTotal",8));
        jedisPoolConfig.setMaxIdle(configuration.getInt("redis.MaxIdle",8));
        jedisPoolConfig.setMinIdle(configuration.getInt("redis.MinIdle",1));
        jedisPoolConfig.setMaxWaitMillis(configuration.getInt("redis.MaxWaitMillis",30000));

        jedisPool = new JedisPool(jedisPoolConfig, configuration.getString("redis.host","localhost"), configuration.getInt("redis.port",6379));

        AdventureManager.consoleMessage("<gradient:#0070B3:#A0EACF>[DraimFarming] </gradient> <color:#E1FFFF>Redis запущен!");

        List<Jedis> minIdleJedisList = new ArrayList<>(jedisPoolConfig.getMinIdle());
        for (int i = 0; i < jedisPoolConfig.getMinIdle(); i++) {
            Jedis jedis;
            try {
                jedis = jedisPool.getResource();
                minIdleJedisList.add(jedis);
                jedis.ping();
            } catch (Exception e) {
                Log.warn(e.getMessage());
            }
        }

        for (int i = 0; i < jedisPoolConfig.getMinIdle(); i++) {
            Jedis jedis;
            try {
                jedis = minIdleJedisList.get(i);
                jedis.close();
            } catch (Exception e) {
                Log.warn(e.getMessage());
            }
        }
    }

    public static void addPlayer(String player){
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, ()->{
            Jedis jedis = getJedis();
            jedis.sadd("df_players", player);
            jedis.close();
        }, 20);
    }

    public static void remPlayer(String player){
        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, ()->{
            Jedis jedis = getJedis();
            jedis.srem("df_players", player);
            jedis.close();
        });
    }

    public static HashSet<String> getPlayers(){
        Jedis jedis = getJedis();
        HashSet<String> players = (HashSet<String>) jedis.smembers("df_players");
        jedis.close();
        return players;
    }
}
