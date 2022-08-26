package net.draimcido.draimfarming.timer;

import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class CropTimer {

    private final int taskID;

    public CropTimer() {
        TimeCheck tc = new TimeCheck();
        BukkitTask task;
        if (ConfigReader.Config.asyncCheck) task = tc.runTaskTimerAsynchronously(Main.plugin, 1,1);
        else task = tc.runTaskTimer(Main.plugin, 1,1);
        this.taskID = task.getTaskId();
    }

    public void stopTimer(int ID) {
        Bukkit.getScheduler().cancelTask(ID);
    }

    public int getTaskID() {
        return this.taskID;
    }
}
