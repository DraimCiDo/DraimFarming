package net.draimcido.draimfarming.objects.actions;

import org.bukkit.entity.Player;

public class ActionXP implements ActionInterface {

    private final int amount;

    public ActionXP(int amount) {
        this.amount = amount;
    }

    @Override
    public void performOn(Player player) {
        player.giveExp(amount);
    }
}
