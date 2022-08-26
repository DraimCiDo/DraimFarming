package net.draimcido.draimfarming.objects.fertilizer;

public class RetainingSoil extends Fertilizer {

    double chance;

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public RetainingSoil(String key, int times){
        super(key, times);
    }

}
