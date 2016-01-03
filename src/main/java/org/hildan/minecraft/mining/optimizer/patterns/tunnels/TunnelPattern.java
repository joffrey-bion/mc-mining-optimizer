package org.hildan.minecraft.mining.optimizer.patterns.tunnels;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.geometry.Axis;

/**
 * Defines how to dig a tunnel. It is based on a {@link TunnelShape} repeated horizontally and vertically.
 */
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

  public void digInto(Chunk chunk, int originX, int originY, int originZ, int length, Axis lengthAxis,
      Axis heightAxis) {
    int xMax = originX + getSizeOnAxis(Axis.X, lengthAxis, heightAxis, length);
    int yMax = originY + getSizeOnAxis(Axis.Y, lengthAxis, heightAxis, length);
    int zMax = originZ + getSizeOnAxis(Axis.Z, lengthAxis, heightAxis, length);
    for (int x = originX; x < Math.min(xMax, chunk.getWidth()); x++) {
      for (int y = originY; y < Math.min(yMax, chunk.getHeight()); y++) {
        for (int z = originZ; z < Math.min(zMax, chunk.getLength()); z++) {
          chunk.dig(x, y, z);
        }
      }
    }
  }

  private int getSizeOnAxis(Axis axis, Axis lengthAxis, Axis heightAxis, int length) {
    if (axis == lengthAxis) {
      return length;
    }
    else if (axis == heightAxis) {
      return shape.getHeight();
    }
    else {
      return shape.getWidth();
    }
  }
}
