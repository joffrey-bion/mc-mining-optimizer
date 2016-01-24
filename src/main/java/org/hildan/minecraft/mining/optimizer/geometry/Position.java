package org.hildan.minecraft.mining.optimizer.geometry;

/**
 * Represents an immutable 3D position.
 */
public class Position {

    private final int x;

    private final int y;

    private final int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Position source) {
        this.x = source.x;
        this.y = source.y;
        this.z = source.z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Position above() {
        return new Position(x, y + 1, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Position position = (Position) obj;
        return x == position.x && y == position.y && z == position.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return String.format("Position(%d,%d,%d)", getX(), getY(), getZ());
    }
}
