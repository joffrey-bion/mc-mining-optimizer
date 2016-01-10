package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;
import org.hildan.minecraft.mining.optimizer.patterns.AbstractDiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.Access;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A pattern that can be programmatically generated.
 */
public class GeneratedPattern extends AbstractDiggingPattern {

    private final Map<Access, List<Action>> actionsPerAccess;

    private final int width;

    private final int height;

    private final int length;

    /**
     * Creates a GeneratedPattern with the given list of actions for each given access.
     *
     * @param actionsPerAccess
     *         defines for each access, the corresponding list of actions
     */
    public GeneratedPattern(Map<Access, List<Action>> actionsPerAccess, int width, int height, int length) {
        this.actionsPerAccess = new HashMap<>(actionsPerAccess);
        this.width = width;
        this.height = height;
        this.length = length;
    }

    @Override
    public int getWidth() {
        // TODO calculate pattern's width based on actions, and add pattern spacing
        return width;
    }

    @Override
    public int getHeight() {
        // TODO calculate pattern's height based on actions, and add pattern spacing
        return height;
    }

    @Override
    public int getLength() {
        // TODO calculate pattern's length based on actions, and add pattern spacing
        return length;
    }

    @Override
    public Set<Access> getAccesses(int x, int y) {
        return actionsPerAccess.keySet();
    }

    @Override
    protected void digInto(Sample sample, int originX, int originY, int originZ) {
        for (Access access : getAccesses(originX, originY)) {
            Block feetBlock = sample.getBlock(access.getX(), access.getY(), 0);
            sample.dig(feetBlock);
            Block headBlock = sample.getBlockAbove(feetBlock, Wrapping.WRAP);
            sample.dig(headBlock);
            for (Action action : actionsPerAccess.get(access)) {
                headBlock = action.executeOn(sample, headBlock);
            }
        }
    }

    @Override
    public String toString() {
        Set<Access> accesses = getAccesses(0, 0);
        StringBuilder sb = new StringBuilder();
        final String indent = "   ";
        for (Access access : accesses) {
            sb.append(access).append(String.format("%n"));
            for (Action action : actionsPerAccess.get(access)) {
                sb.append(indent).append(action).append(String.format("%n"));
            }
        }
        return sb.toString();
    }
}