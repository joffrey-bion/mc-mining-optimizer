package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.geometry.Position;

import java.util.Collections;
import java.util.List;

/**
 * A pattern where every single block is dug. Not a real-life example, but a good test pattern.
 */
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

    @Override
    public List<Position> getAccesses() {
        return Collections.singletonList(new Position(0, 0, 0));
    }

    @Override
    public void digInto(Chunk chunk, int originX, int originY, int originZ) {
        chunk.dig(originX, originY, originZ);
        chunk.dig(originX, originY + 1, originZ);
    }

}
