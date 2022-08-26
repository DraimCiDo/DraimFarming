package net.draimcido.draimfarming.objects;

public record WateringCan(int max, int width, int length) {

    public int getMax() { return max; }
    public int getLength() { return length; }
    public int getWidth() { return width; }

}
