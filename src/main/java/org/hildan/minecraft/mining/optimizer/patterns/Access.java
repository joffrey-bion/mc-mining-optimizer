package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.geometry.Position;

/**
 * Defines an entry point to start digging a pattern. In the code, any block can be dug at any time. In the real
 * Minecraft world, one needs to actually start somewhere that's reachable, and that's what defines an access.
 * <p>
 * An access consists of 2 blocks, because the player has to fit inside. This class only defines the position of the
 * feet of the player, the above block is obviously part of the access too, for the head to fit.
 * <p>
 * By convention, the accesses are on the Z=0 plane. This means every pattern must be oriented so that the access is
 * actually on this side.
 */
public class Access extends Position {

    public Access(int x, int y) {
        super(x, y, 0);
    }

    @Override
    public String toString() {
        return String.format("Access(%d,%d)", getX(), getY());
    }
}
