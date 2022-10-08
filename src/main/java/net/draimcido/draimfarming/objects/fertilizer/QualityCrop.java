package net.draimcido.draimfarming.objects.fertilizer;

import net.draimcido.draimfarming.objects.QualityRatio;

public class QualityCrop extends Fertilizer {

    private final QualityRatio qualityRatio;

    public QualityCrop(String key, int times, double chance, QualityRatio qualityRatio, boolean before, String name) {
        super(key, times, chance, before, name);
        this.qualityRatio = qualityRatio;
    }

    public QualityRatio getQualityRatio() {
        return qualityRatio;
    }

    @Override
    public Fertilizer getWithTimes(int times) {
        return new QualityCrop(this.key, times, this.chance, this.qualityRatio, this.before, this.name);
    }
}
