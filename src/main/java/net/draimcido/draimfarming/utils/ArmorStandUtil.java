package net.draimcido.draimfarming.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.config.BasicItemConfig;
import net.draimcido.draimfarming.config.MainConfig;
import net.draimcido.draimfarming.managers.CropManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ArmorStandUtil {

    private final CropManager cropManager;

    public ArmorStandUtil(CropManager cropManager) {
        this.cropManager = cropManager;
    }

    public CropManager getCropManager() {
        return cropManager;
    }

    public void playWaterAnimation(Player player, Location location) {
        int id = new Random().nextInt(1000000000);
        try {
            Main.protocolManager.sendServerPacket(player, getSpawnPacket(id, location));
            Main.protocolManager.sendServerPacket(player, getMetaPacket(id));
            Main.protocolManager.sendServerPacket(player, getEquipPacket(id, cropManager.getCustomInterface().getItemStack(BasicItemConfig.waterEffect)));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, () -> {
            try {
                Main.protocolManager.sendServerPacket(player, getDestroyPacket(id));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }, MainConfig.timeToWork/2);
    }

    public WrappedDataWatcher createDataWatcher() {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), false);
        byte flag = 0x20;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), flag);
        return wrappedDataWatcher;
    }

    public PacketContainer getDestroyPacket(int id) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(id));
        return destroyPacket;
    }

    public PacketContainer getSpawnPacket(int id, Location location) {
        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, id);
        entityPacket.getModifier().write(1, UUID.randomUUID());
        entityPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY());
        entityPacket.getDoubles().write(2, location.getZ());
        return entityPacket;
    }

    public PacketContainer getMetaPacket(int id) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, id);
        metaPacket.getWatchableCollectionModifier().write(0, createDataWatcher().getWatchableObjects());
        return metaPacket;
    }

    public PacketContainer getEquipPacket(int id, ItemStack itemStack) {
        PacketContainer equipPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipPacket.getIntegers().write(0, id);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, itemStack));
        equipPacket.getSlotStackPairLists().write(0, pairs);
        return equipPacket;
    }

    public PacketContainer getTeleportPacket(int id, Location location, float yaw) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, id);
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        packet.getBytes().write(0, (byte) (yaw * (128.0 / 180)));
        return packet;
    }

    public PacketContainer getRotationPacket(int id, float yaw) {
        PacketContainer rotationPacket = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        rotationPacket.getIntegers().write(0, id);
        return rotationPacket;
    }
}
