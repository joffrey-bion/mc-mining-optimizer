package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;
import org.hildan.minecraft.mining.optimizer.patterns.AbstractDiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.Access;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A pattern that can be programmatically generated.
 */
public class GeneratedPattern extends AbstractDiggingPattern {

    private final List<Action> actions;

    public GeneratedPattern(List<Action> actions) {
        this.actions = new ArrayList<>(actions);
    }

    @Override
    public int getWidth() {
        // TODO calculate pattern width based on actions, and add pattern spacing
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Set<Access> getAccesses(int x, int y) {
        return null;
    }

    @Override
    protected void digInto(Sample sample, int originX, int originY, int originZ) {
        for (Access access : getAccesses(originX, originY)) {
            Block feetBlock = sample.getBlock(access.getX(), access.getY(), 0);
            sample.dig(feetBlock);
            Block headBlock = sample.getBlockAbove(feetBlock, Wrapping.WRAP);
            sample.dig(headBlock);
            for (Action action : actions) {
                headBlock = action.applyTo(sample, headBlock);
            }
        }
    }
}
