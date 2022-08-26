package net.draimcido.draimfarming.integrations.skill;

import org.bukkit.entity.Player;

public interface SkillXP {
    void addXp(Player player, double amount);
}
