package net.draimcido.draimfarming.integrations.skill;

import com.willfp.ecoskills.api.EcoSkillsAPI;
import com.willfp.ecoskills.skills.Skills;
import org.bukkit.entity.Player;

public class EcoSkillIntegration implements SkillXP{

    @Override
    public void addXp(Player player, double amount) {
        EcoSkillsAPI.getInstance().giveSkillExperience(player, Skills.FARMING, amount);
    }
}
