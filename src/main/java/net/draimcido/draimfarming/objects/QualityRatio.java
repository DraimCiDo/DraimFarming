package net.draimcido.draimfarming.objects;

public class QualityRatio {

    private final double quality_1;
    private final double quality_2;

    public QualityRatio(double quality_1, double quality_2) {
        this.quality_1 = quality_1;
        this.quality_2 = quality_2;
    }

    public double getQuality_1() {
        return quality_1;
    }

    public double getQuality_2() {
        return quality_2;
    }
}
