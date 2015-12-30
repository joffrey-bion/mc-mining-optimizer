package org.hildan.minecraft.mining.optimizer.chunks;

public enum Block {
    AIR(false),
    STONE(false),
    COAL_ORE(true),
    IRON_ORE(true),
    GOLD_ORE(true),
    DIAMOND_ORE(true),
    REDSTONE_ORE(true),
    LAPIS_ORE(true);

    private final boolean isOre;

    Block(boolean isOre) {
        this.isOre = isOre;
    }

    public boolean isOre() {
        return isOre;
    }
}
