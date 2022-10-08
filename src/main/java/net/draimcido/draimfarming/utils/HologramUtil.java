package net.draimcido.draimfarming.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class HologramUtil {

    public static HashMap<SimpleLocation, Integer> cache = new HashMap<>();

    public static void showHolo(String text, Player player, Location location, int duration){

        Integer entityID = cache.remove(MiscUtils.getSimpleLocation(location));
        if (entityID != null) {
            removeHolo(player, entityID);
        }
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        int id = new Random().nextInt(1000000000);
        cache.put(MiscUtils.getSimpleLocation(location), id);
        spawnPacket.getModifier().write(0, id);
        spawnPacket.getModifier().write(1, UUID.randomUUID());
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY());
        spawnPacket.getDoubles().write(2, location.getZ());
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
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
        metaPacket.getModifier().write(0,id);
        metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
        try {
            Main.protocolManager.sendServerPacket(player, spawnPacket);
            Main.protocolManager.sendServerPacket(player, metaPacket);
        }
        catch (Exception e) {
            AdventureUtil.consoleMessage("<red>[DraimFarming] Не удалось отобразить голограмму для " + player.getName() + " !</red>");
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskLater(Main.plugin, ()->{
            removeHolo(player, id);
            cache.remove(MiscUtils.getSimpleLocation(location));
        }, duration * 20L);
    }

    public static void removeHolo(Player player, int entityId){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, List.of(entityId));
        try {
            Main.protocolManager.sendServerPacket(player, packet);
        }
        catch (Exception e) {
            AdventureUtil.consoleMessage("<red>[DraimFarming] Не удалось удалить голограмму для " + player.getName() + " !</red>");
            e.printStackTrace();
        }
    }
}
