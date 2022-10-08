package net.draimcido.draimfarming.objects.actions;

import net.draimcido.draimfarming.utils.AdventureUtil;
import org.bukkit.entity.Player;

public class ActionMessage implements ActionInterface{

    private final String[] messages;

    public ActionMessage(String[] messages) {
        this.messages = messages;
    }

    @Override
    public void performOn(Player player) {
        for (String message : messages) {
            AdventureUtil.playerMessage(player, message.replace("{player}", player.getName()));
        }
    }
}
