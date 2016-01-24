package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.blocks.Sample;
import org.hildan.minecraft.mining.optimizer.blocks.Wrapping;
import org.hildan.minecraft.mining.optimizer.geometry.Position;
import org.hildan.minecraft.mining.optimizer.patterns.AbstractDiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.Access;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A pattern that can be programmatically generated.
 */
class GeneratedPattern extends AbstractDiggingPattern {

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
    GeneratedPattern(Map<Access, List<Action>> actionsPerAccess, int width, int height, int length) {
        this.actionsPerAccess = new HashMap<>(actionsPerAccess);
        this.width = width;
        this.height = height;
        this.length = length;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public Set<Access> getAccesses(int x, int y) {
        return actionsPerAccess.keySet()
                               .stream()
                               .map(a -> new Access(a.getX() + x, a.getY() + y))
                               .collect(Collectors.toSet());
    }

    @Override
    protected void digInto(Sample sample, int originX, int originY, int originZ) {
        for (Access access : getAccesses(originX, originY)) {
            Position feetBlock = sample.getBlock(access);
            sample.digBlock(feetBlock);
            Position headBlock = sample.getBlockAbove(feetBlock, Wrapping.WRAP);
            sample.digBlock(headBlock);
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
