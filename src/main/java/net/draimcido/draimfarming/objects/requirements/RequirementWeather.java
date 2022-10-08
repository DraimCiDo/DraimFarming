package net.draimcido.draimfarming.objects.requirements;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequirementWeather extends Requirement implements RequirementInterface {

    public RequirementWeather(@NotNull String[] values, boolean mode, @Nullable String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlantingCondition plantingCondition) {
        World world = plantingCondition.getLocation().getWorld();
        String weather;
        if (world.isThundering()) weather = "thunder";
        else if (world.isClearWeather()) weather = "clear";
        else weather = "rain";
        if (mode) {
            for (String value : values) {
                if (!value.equalsIgnoreCase(weather)) {
                    notMetMessage(plantingCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                if (value.equalsIgnoreCase(weather)) {
                    return true;
                }
            }
            notMetMessage(plantingCondition.getPlayer());
            return false;
        }
    }
}
