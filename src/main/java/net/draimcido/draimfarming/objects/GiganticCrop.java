package net.draimcido.draimfarming.objects;

public class GiganticCrop {

    private final double chance;
    private final boolean isBlock;
    private final String blockID;

    public GiganticCrop(double chance, boolean isBlock, String blockID) {
        this.chance = chance;
        this.isBlock = isBlock;
        this.blockID = blockID;
    }

    public double getChance() {
        return chance;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public String getBlockID() {
        return blockID;
    }
}
