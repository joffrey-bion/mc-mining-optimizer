package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.blocks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Player;

import java.util.Collections;
import java.util.Set;

/**
 * A pattern where every single block is dug. Not a real-life example, but a good test pattern.
 */
public class DigEverythingPattern extends AbstractDiggingPattern {

    @Override
    public int getWidth() {
        return Player.WIDTH;
    }

    @Override
    public int getHeight() {
        return Player.HEIGHT;
    }

    @Override
    public int getLength() {
        return Player.LENGTH;
    }

    @Override
    public Set<Access> getAccesses(int x, int y) {
        return Collections.singleton(new Access(x, y));
    }

    @Override
    public void digInto(Sample sample, int originX, int originY, int originZ) {
        for (int x = originX; x < originX + Player.WIDTH; x++) {
            for (int y = originY; y < originY + Player.HEIGHT; y++) {
                for (int z = originZ; z < originZ + Player.LENGTH; z++) {
                    sample.digBlock(x, y, z);
                }
            }
        }
    }
}
