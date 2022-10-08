package net.draimcido.draimfarming.objects.fertilizer;

public class Gigantic extends Fertilizer{

    public Gigantic(String key, int times, double chance, boolean before, String name) {
        super(key, times, chance, before, name);
    }

    @Override
    public Fertilizer getWithTimes(int times) {
        return new Gigantic(this.key, times, this.chance, this.before, this.name);
    }
}
