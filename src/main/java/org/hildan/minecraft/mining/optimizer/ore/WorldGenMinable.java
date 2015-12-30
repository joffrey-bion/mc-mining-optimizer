package org.hildan.minecraft.mining.optimizer.ore;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Chunk;

import java.util.Random;

public class WorldGenMinable {

    public static final WorldGenMinable coalGen = new WorldGenMinable(Block.COAL_ORE, 16);
    public static final WorldGenMinable ironGen = new WorldGenMinable(Block.IRON_ORE, 8);
    public static final WorldGenMinable goldGen = new WorldGenMinable(Block.GOLD_ORE, 8);
    public static final WorldGenMinable redstoneGen = new WorldGenMinable(Block.REDSTONE_ORE, 7);
    public static final WorldGenMinable diamondGen = new WorldGenMinable(Block.DIAMOND_ORE, 7);
    public static final WorldGenMinable lapisGen = new WorldGenMinable(Block.LAPIS_ORE, 6);

    private int maxVeinSize;

    private Block blockToGenerate;

    private Block blockToReplace;

    public WorldGenMinable(Block blockToGenerate, int maxVeinSize) {
        this.maxVeinSize = maxVeinSize;
        this.blockToGenerate = blockToGenerate;
        this.blockToReplace = Block.STONE;
    }

    public boolean generateInto(Chunk chunk, Random random, int i, int j, int k) {
        /*
        Implementation taken from Bukkit's github, here be dragons
         */
        float f = random.nextFloat() * 3.1415927F;
        double d0 = (double) ((float) (i + 8) + MathHelper.sin(f) * (float) this.maxVeinSize / 8.0F);
        double d1 = (double) ((float) (i + 8) - MathHelper.sin(f) * (float) this.maxVeinSize / 8.0F);
        double d2 = (double) ((float) (k + 8) + MathHelper.cos(f) * (float) this.maxVeinSize / 8.0F);
        double d3 = (double) ((float) (k + 8) - MathHelper.cos(f) * (float) this.maxVeinSize / 8.0F);
        double d4 = (double) (j + random.nextInt(3) - 2);
        double d5 = (double) (j + random.nextInt(3) - 2);

        for (int l = 0; l <= this.maxVeinSize; ++l) {
            double d6 = d0 + (d1 - d0) * (double) l / (double) this.maxVeinSize;
            double d7 = d4 + (d5 - d4) * (double) l / (double) this.maxVeinSize;
            double d8 = d2 + (d3 - d2) * (double) l / (double) this.maxVeinSize;
            double d9 = random.nextDouble() * (double) this.maxVeinSize / 16.0D;
            double d10 = (double) (MathHelper.sin((float) l * 3.1415927F / (float) this.maxVeinSize) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) l * 3.1415927F / (float) this.maxVeinSize) + 1.0F) * d9 + 1.0D;
            int i1 = MathHelper.floor(d6 - d10 / 2.0D);
            int j1 = MathHelper.floor(d7 - d11 / 2.0D);
            int k1 = MathHelper.floor(d8 - d10 / 2.0D);
            int l1 = MathHelper.floor(d6 + d10 / 2.0D);
            int i2 = MathHelper.floor(d7 + d11 / 2.0D);
            int j2 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int k2 = i1; k2 <= l1; ++k2) {
                double d12 = ((double) k2 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int l2 = j1; l2 <= i2; ++l2) {
                        double d13 = ((double) l2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int i3 = k1; i3 <= j2; ++i3) {
                                double d14 = ((double) i3 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && chunk.getBlock(k2, l2, i3) == blockToReplace) {
                                    chunk.setBlock(k2, l2, i3, blockToGenerate);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
