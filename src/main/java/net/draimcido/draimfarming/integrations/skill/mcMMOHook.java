package net.draimcido.draimfarming.integrations.skill;

import com.gmail.nossr50.api.ExperienceAPI;
import net.draimcido.draimfarming.integrations.SkillXP;
import org.bukkit.entity.Player;

public class mcMMOHook implements SkillXP {

    @Override
    public void addXp(Player player, double amount) {
        ExperienceAPI.addRawXP(player, "Herbalism", (float) amount, "UNKNOWN");
    }
}
