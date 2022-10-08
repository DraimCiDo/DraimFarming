package net.draimcido.draimfarming.objects.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionCommand implements ActionInterface {

    private final String[] commands;

    public ActionCommand(String[] commands) {
        this.commands = commands;
    }

    @Override
    public void performOn(Player player) {
        for (String command : commands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
        }
    }
}
