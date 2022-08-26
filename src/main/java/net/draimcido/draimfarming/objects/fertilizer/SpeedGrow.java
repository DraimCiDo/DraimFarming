package net.draimcido.draimfarming.objects.fertilizer;

public class SpeedGrow extends Fertilizer {

    private double chance;

    public SpeedGrow(String key, int times){
        super(key, times);
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}
