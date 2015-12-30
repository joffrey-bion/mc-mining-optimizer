package org.hildan.minecraft.mining.optimizer.patterns.tunnels;

public class TunnelPattern {

    private final TunnelShape shape;

    private final int hSpacing;

    private final int vSpacing;

    public TunnelPattern(TunnelShape shape, int hSpacing, int vSpacing) {
        this.shape = shape;
        this.hSpacing = hSpacing;
        this.vSpacing = vSpacing;
    }

    public TunnelShape getShape() {
        return shape;
    }

    public int getHSpacing() {
        return hSpacing;
    }

    public int getVSpacing() {
        return vSpacing;
    }
}
