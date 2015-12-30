package org.hildan.minecraft.mining.optimizer.patterns.tunnels;

public class TunnelShape {

    public static final TunnelShape SIMPLE_HOLE = new TunnelShape(1, 1);

    public static final TunnelShape MAN_SIZED = new TunnelShape(1, 2);

    public static final TunnelShape DOUBLE_MAN_SIZED = new TunnelShape(2, 2);

    public static final TunnelShape CORRIDOR = new TunnelShape(2, 3);

    private int width;

    private int height;

    public TunnelShape(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
