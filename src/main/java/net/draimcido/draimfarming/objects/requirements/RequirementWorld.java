package net.draimcido.draimfarming.objects.requirements;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequirementWorld extends Requirement implements RequirementInterface {

    public RequirementWorld(@NotNull String[] values, boolean mode, @Nullable String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlantingCondition plantingCondition) {
        String worldName = plantingCondition.getLocation().getWorld().getName();
        if (mode) {
            for (String value : values) {
                if (!value.equals(worldName)) {
                    notMetMessage(plantingCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                if (value.equals(worldName)) {
                    return true;
                }
            }
            notMetMessage(plantingCondition.getPlayer());
            return false;
        }
    }
}
