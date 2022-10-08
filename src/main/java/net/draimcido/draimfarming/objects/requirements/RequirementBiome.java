package net.draimcido.draimfarming.objects.requirements;

public class RequirementBiome extends Requirement implements RequirementInterface {

    public RequirementBiome(String[] values, boolean mode, String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlantingCondition plantingCondition) {
        String currentBiome = plantingCondition.getLocation().getBlock().getBiome().getKey().toString();
        if (mode) {
            for (String value : values) {
                if (!(currentBiome.equalsIgnoreCase(value))) {
                    notMetMessage(plantingCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                if (currentBiome.equalsIgnoreCase(value)) {
                    return true;
                }
            }
            notMetMessage(plantingCondition.getPlayer());
            return false;
        }
    }
}
