package net.draimcido.draimfarming.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.draimcido.draimfarming.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class HoloUtils {

    public static HashMap<Location, Integer> cache = new HashMap<>();
    public static void showHolo(String text, Player player, Location location, int duration){

        if (cache.get(location) != null){
            removeHolo(player, cache.get(location));
        }

        PacketContainer packet1 = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

        int id = new Random().nextInt(1000000000);
        packet1.getModifier().write(0, id);
        packet1.getModifier().write(1, UUID.randomUUID());
        packet1.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packet1.getDoubles().write(0, location.getX());
        packet1.getDoubles().write(1, location.getY());
        packet1.getDoubles().write(2, location.getZ());

        PacketContainer packet2 = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        Component component = MiniMessage.miniMessage().deserialize(text);
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, serializer1), true);
        byte mask1 = 0x20;
        byte mask2 = 0x01;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), mask1);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), mask2);
        packet2.getModifier().write(0,id);
        packet2.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
        cache.put(location, id);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet1);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet2);
        }
        catch (Exception e) {
            AdventureManager.consoleMessage("<gradient:#0070B3:#A0EACF>[DraimFarming] </gradient> <color:#E1FFFF>Не удалось отобразить информацию об игроке "+ player.getName());
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskLater(Main.plugin, ()->{
            removeHolo(player, id);
            cache.remove(location);
        }, duration * 20L);
    }

    public static void removeHolo(Player player, int entityId){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, List.of(entityId));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
        catch (Exception e) {
            AdventureManager.consoleMessage("<gradient:#0070B3:#A0EACF>[DraimFarming] </gradient> <color:#E1FFFF>Не удалось удалить информацию об игроке "+ player.getName());
            e.printStackTrace();
        }
    }
}
