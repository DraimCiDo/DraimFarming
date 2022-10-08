package net.draimcido.draimfarming.objects.fertilizer;

import org.bukkit.Particle;

public abstract class Fertilizer {

    String key;
    int times;
    boolean before;
    String name;
    Particle particle;
    double chance;

    protected Fertilizer(String key, int times, double chance, boolean before, String name) {
        this.key = key;
        this.times = times;
        this.chance = chance;
        this.before = before;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isBefore() {
        return before;
    }

    public String getName() {
        return name;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public Fertilizer getWithTimes(int times) {
        return null;
    }

    public double getChance() {
        return chance;
    }
}
