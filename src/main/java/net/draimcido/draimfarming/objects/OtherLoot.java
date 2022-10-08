package net.draimcido.draimfarming.objects;

public class OtherLoot {

    private final int min;
    private final int max;
    private final String itemID;
    private final double chance;

    public OtherLoot(int min, int max, String itemID, double chance) {
        this.min = min;
        this.max = max;
        this.itemID = itemID;
        this.chance = chance;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getItemID() {
        return itemID;
    }

    public double getChance() {
        return chance;
    }
}
