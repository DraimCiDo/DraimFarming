package net.draimcido.draimfarming.objects.requirements;

public class RequirementPermission extends Requirement implements RequirementInterface {

    public RequirementPermission(String[] values, boolean mode, String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlantingCondition plantingCondition) {
        if (mode) {
            for (String value : values) {
                if (!(plantingCondition.getPlayer().hasPermission(value))) {
                    notMetMessage(plantingCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                if (plantingCondition.getPlayer().hasPermission(value)) {
                    return true;
                }
            }
            notMetMessage(plantingCondition.getPlayer());
            return false;
        }
    }
}
