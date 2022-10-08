package net.draimcido.draimfarming.objects.requirements;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequirementYPos extends Requirement implements RequirementInterface {

    public RequirementYPos(@NotNull String[] values, boolean mode, @Nullable String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlantingCondition plantingCondition) {
        int y = plantingCondition.getLocation().getBlockY();
        if (mode) {
            for (String value : values) {
                String[] yMinMax = StringUtils.split(value, "~");
                if (!(y > Long.parseLong(yMinMax[0]) && y < Long.parseLong(yMinMax[1]))) {
                    notMetMessage(plantingCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                String[] yMinMax = StringUtils.split(value, "~");
                if (y > Long.parseLong(yMinMax[0]) && y < Long.parseLong(yMinMax[1])) {
                    return true;
                }
            }
            notMetMessage(plantingCondition.getPlayer());
            return false;
        }
    }
}
