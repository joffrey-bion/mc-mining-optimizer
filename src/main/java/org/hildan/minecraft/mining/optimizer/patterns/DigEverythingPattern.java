package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;

public class DigEverythingPattern extends AbstractDiggingPattern {

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 2;
    }

    @Override
    public int getLength() {
        return 1;
    }

    public int[][] getAccesses() {
        return new int[][] { { 0, 0, 0 } };
    }

    @Override
    public void digInto(Chunk chunk, int originX, int originY, int originZ) {
        chunk.dig(originX, originY, originZ);
        chunk.dig(originX, originY + 1, originZ);
    }

}
