package net.draimcido.draimfarming.integrations.skill;

import com.gmail.nossr50.api.ExperienceAPI;
import org.bukkit.entity.Player;

public class mcMMO implements SkillXP {

    @Override
    public void addXp(Player player, double amount) {
        ExperienceAPI.addRawXP(player, "Herbalism", (float) amount, "UNKNOWN");
    }
}
