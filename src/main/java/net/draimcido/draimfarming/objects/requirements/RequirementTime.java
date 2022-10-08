package net.draimcido.draimfarming.objects.requirements;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequirementTime extends Requirement implements RequirementInterface {

    public RequirementTime(@NotNull String[] values, boolean mode, @Nullable String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlantingCondition plantingCondition) {
        long time = plantingCondition.getLocation().getWorld().getTime();
        if (mode) {
            for (String value : values) {
                String[] timeMinMax = StringUtils.split(value, "~");
                if (!(time > Long.parseLong(timeMinMax[0]) && time < Long.parseLong(timeMinMax[1]))) {
                    notMetMessage(plantingCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                String[] timeMinMax = StringUtils.split(value, "~");
                if (time > Long.parseLong(timeMinMax[0]) && time < Long.parseLong(timeMinMax[1])) {
                    return true;
                }
            }
            notMetMessage(plantingCondition.getPlayer());
            return false;
        }
    }
}
