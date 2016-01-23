package org.hildan.minecraft.mining.optimizer.patterns.generated;

/**
 * Represents constraints to limit the number of generated patterns.
 */
public class GenerationConstraints {

    private final int maxActions;

    private final int maxDugBlocks;

    public GenerationConstraints(int maxActions, int maxDugBlocks) {
        this.maxActions = maxActions;
        this.maxDugBlocks = maxDugBlocks;
    }

    public int getMaxActions() {
        return maxActions;
    }

    public int getMaxDugBlocks() {
        return maxDugBlocks;
    }
}
