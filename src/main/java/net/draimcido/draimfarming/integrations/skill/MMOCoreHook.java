package net.draimcido.draimfarming.integrations.skill;

import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import net.draimcido.draimfarming.integrations.SkillXP;
import org.bukkit.entity.Player;

public class MMOCoreHook implements SkillXP {
    @Override
    public void addXp(Player player, double amount) {
        Profession profession = net.Indyuce.mmocore.MMOCore.plugin.professionManager.get("farming");
        profession.giveExperience(net.Indyuce.mmocore.MMOCore.plugin.dataProvider.getDataManager().get(player), amount, null ,EXPSource.OTHER);
    }
}
