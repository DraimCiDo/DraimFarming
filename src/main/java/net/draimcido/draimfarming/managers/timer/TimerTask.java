package net.draimcido.draimfarming.managers.timer;

import net.draimcido.draimfarming.config.MainConfig;
import net.draimcido.draimfarming.managers.CropManager;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {

    private final CropManager cropManager;

    public TimerTask(CropManager cropManager) {
        this.cropManager = cropManager;
    }

    @Override
    public void run() {
        if (!MainConfig.autoGrow) return;
        for (World world : MainConfig.getWorldsList()) {
            long time = world.getTime();
            if (time > 950 && time < 1051) {
                cropManager.grow(world, MainConfig.timeToGrow, MainConfig.timeToWork, MainConfig.timeToDry, false);
            }
        }
    }
}
