package net.draimcido.draimfarming.objects.fertilizer;

public class RetainingSoil extends Fertilizer {

    public RetainingSoil(String key, int times, double chance, boolean before, String name){
        super(key, times, chance, before, name);
    }

    @Override
    public Fertilizer getWithTimes(int times) {
        return new RetainingSoil(this.key, times, this.chance, this.before, this.name);
    }
}
