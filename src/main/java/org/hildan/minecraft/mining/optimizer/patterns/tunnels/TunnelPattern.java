package org.hildan.minecraft.mining.optimizer.patterns.tunnels;

public class TunnelPattern {

    public static final TunnelPattern STANDARD_SHAFT = new TunnelPattern(TunnelShape.DOUBLE_MAN_SIZED, 23, 2);

    public static final TunnelPattern BIG_SHAFT = new TunnelPattern(TunnelShape.BIG_CORRIDOR, 23, 1);

    public static final TunnelPattern STANDARD_BRANCH_2SPACED = new TunnelPattern(TunnelShape.MAN_SIZED, 2, -1);

    public static final TunnelPattern STANDARD_BRANCH_3SPACED = new TunnelPattern(TunnelShape.MAN_SIZED, 3, -1);

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
