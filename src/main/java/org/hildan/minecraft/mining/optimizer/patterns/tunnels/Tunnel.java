package org.hildan.minecraft.mining.optimizer.patterns.tunnels;

import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelShape;

public class Tunnel {

    private TunnelShape shape;

    private int length;

    public Tunnel(int width, int height, int length) {
        this(new TunnelShape(width, height), length);
    }

    public Tunnel(TunnelShape shape, int length) {
        this.shape = shape;
        this.length = length;
    }

    public TunnelShape getShape() {
        return shape;
    }

    public int getLength() {
        return length;
    }
}
