package net.draimcido.draimfarming.objects;

import org.jetbrains.annotations.Nullable;

public class Sprinkler {

    private int range;
    private int water;
    private final String key;
    private String twoD;
    private String threeD;

    public Sprinkler(String key, int range, int water) {
        this.range = range;
        this.water = water;
        this.key = key;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public String getTwoD() {
        return twoD;
    }

    public void setTwoD(String twoD) {
        this.twoD = twoD;
    }


    @Nullable
    public String getThreeD() {
        return threeD;
    }

    public void setThreeD(String threeD) {
        this.threeD = threeD;
    }
}