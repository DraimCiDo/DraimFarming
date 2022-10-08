package net.draimcido.draimfarming.managers.timer;

import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.config.BasicItemConfig;
import net.draimcido.draimfarming.utils.ArmorStandUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class CrowTask extends BukkitRunnable {

    private int timer;
    private final int entityID;
    private final ArmorStandUtil armorStandUtil;
    private final Vector vectorDown;
    private final Vector vectorUp;
    private final Location from;
    private final Player player;
    private float yaw;


    public CrowTask(Player player, Location crop, ArmorStandUtil armorStandUtil) {
        this.timer = 0;
        this.player = player;
        this.armorStandUtil = armorStandUtil;
        this.entityID = new Random().nextInt(10000000);
        yaw = new Random().nextInt(361) - 180;
        this.from = crop.clone().add(10 * Math.sin((Math.PI * yaw)/180), 10, - 10 * Math.cos((Math.PI * yaw)/180));
        Location relative = crop.clone().subtract(from);
        this.vectorDown = new Vector(relative.getX() / 100, -0.1, relative.getZ() / 100);
        this.vectorUp = new Vector(relative.getX() / 100, 0.1, relative.getZ() / 100);
        try {
            Main.protocolManager.sendServerPacket(player, armorStandUtil.getSpawnPacket(entityID, from));
            Main.protocolManager.sendServerPacket(player, armorStandUtil.getMetaPacket(entityID));
            Main.protocolManager.sendServerPacket(player, armorStandUtil.getEquipPacket(entityID, armorStandUtil.getCropManager().getCustomInterface().getItemStack(BasicItemConfig.crowFly)));
        }
        catch (InvocationTargetException e) {
            //release
        }
    }

    @Override
    public void run() {
        timer++;
        if (timer < 100) {
            try {
                Main.protocolManager.sendServerPacket(player, armorStandUtil.getTeleportPacket(entityID, from.add(vectorDown), yaw));
            }
            catch (InvocationTargetException e) {
            }
        }
        else if (timer == 100){
            try {
                Main.protocolManager.sendServerPacket(player, armorStandUtil.getEquipPacket(entityID, armorStandUtil.getCropManager().getCustomInterface().getItemStack(BasicItemConfig.crowLand)));
            }
            catch (InvocationTargetException e) {
            }
        }
        else if (timer == 150) {
            try {
                Main.protocolManager.sendServerPacket(player, armorStandUtil.getEquipPacket(entityID, armorStandUtil.getCropManager().getCustomInterface().getItemStack(BasicItemConfig.crowFly)));
            }
            catch (InvocationTargetException e) {
            }
        }
        else if (timer > 150) {
            try {
                Main.protocolManager.sendServerPacket(player, armorStandUtil.getTeleportPacket(entityID, from.add(vectorUp), yaw));
            }
            catch (InvocationTargetException e) {
            }
        }
        if (timer > 300) {
            try {
                Main.protocolManager.sendServerPacket(player, armorStandUtil.getDestroyPacket(entityID));
            }
            catch (InvocationTargetException e) {
            }
            cancel();
        }
    }
}
