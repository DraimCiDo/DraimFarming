package net.draimcido.draimfarming.objects.fertilizer;

public class QualityCrop extends Fertilizer {

    private int[] chance;

    public QualityCrop(String key, int times) {
        super(key, times);
    }

    public int[] getChance() {
        return chance;
    }

    public void setChance(int[] chance) {
        this.chance = chance;
    }
}
