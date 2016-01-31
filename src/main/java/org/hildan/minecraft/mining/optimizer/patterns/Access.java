package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.geometry.Position;

import java.util.Objects;

/**
 * Defines an entry point to start digging a pattern. In the code, any block can be dug at any time. In the real
 * Minecraft world, one needs to actually start somewhere that's reachable, and that's what defines an access.
 * <p>
 * An access consists of 2 blocks, because the player has to fit inside.
 * <p>
 * By convention, the accesses are on the Z=0 plane. This means every pattern must be oriented so that the access is
 * actually on this side.
 */
public class Access {

    private final Position feet;

    private final Position head;

    /**
     * Creates an access at the given feet position.
     *
     * @param x
     *         the X position of the feet of the player
     * @param y
     *         the Y position of the feet of the player
     */
    public Access(int x, int y) {
        this.feet = new Position(x, y, 0);
        this.head = new Position(x, y + 1, 0);
    }

    public Position feet() {
        return feet;
    }

    public Position head() {
        return head;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Access access = (Access) o;
        return Objects.equals(feet, access.feet) && Objects.equals(head, access.head);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feet, head);
    }

    @Override
    public String toString() {
        return String.format("Access(%d,%d)", feet.getX(), feet.getY());
    }
}
