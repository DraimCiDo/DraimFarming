package net.draimcido.draimfarming.objects.fertilizer;

public class YieldIncreasing extends Fertilizer {

    private final int bonus;

    public YieldIncreasing(String key, int times, double chance ,int bonus, boolean before, String name) {
        super(key, times, chance, before, name);
        this.bonus = bonus;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public Fertilizer getWithTimes(int times) {
        return new YieldIncreasing(this.key, times, this.chance, this.bonus, this.before, this.name);
    }
}
