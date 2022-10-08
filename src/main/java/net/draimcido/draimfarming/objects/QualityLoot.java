package net.draimcido.draimfarming.objects;

public class QualityLoot {

    private final int min;
    private final int max;

    private final String quality_1;
    private final String quality_2;
    private final String quality_3;

    public QualityLoot(int min, int max, String quality_1, String quality_2, String quality_3) {
        this.quality_1 = quality_1;
        this.quality_2 = quality_2;
        this.quality_3 = quality_3;
        this.max = max;
        this.min = min;
    }

    public String getQuality_1() {
        return quality_1;
    }

    public String getQuality_2() {
        return quality_2;
    }

    public String getQuality_3() {
        return quality_3;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
