package net.draimcido.draimfarming.objects.actions;

import net.draimcido.draimfarming.config.MainConfig;
import org.bukkit.entity.Player;

public class ActionSkillXP implements ActionInterface {

    private final double xp;

    public ActionSkillXP(double xp) {
        this.xp = xp;
    }

    @Override
    public void performOn(Player player) {
        if (MainConfig.skillXP != null) {
            MainConfig.skillXP.addXp(player, xp);
        }
    }
}
