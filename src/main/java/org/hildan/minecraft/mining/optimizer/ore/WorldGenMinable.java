package org.hildan.minecraft.mining.optimizer.ore;

import org.hildan.minecraft.mining.optimizer.blocks.BlockType;
import org.hildan.minecraft.mining.optimizer.blocks.Sample;

import java.util.Random;

@SuppressWarnings({"MagicNumber", "UnnecessaryExplicitNumericCast"})
class WorldGenMinable {

    private final int maxVeinSize;

    private final BlockType blockTypeToGenerate;

    private final BlockType blockTypeToReplace;

    public WorldGenMinable(BlockType blockTypeToGenerate) {
        this.maxVeinSize = blockTypeToGenerate.getMaxVeinSize();
        this.blockTypeToGenerate = blockTypeToGenerate;
        this.blockTypeToReplace = BlockType.STONE;
    }

    @SuppressWarnings({"OverlyLongMethod", "OverlyNestedMethod"}) // will be broken up
    public void generateInto(Sample sample, Random random, int centerX, int centerY, int centerZ) {
        /*
        Implementation taken from Bukkit's github, here be dragons
         */
        float f = random.nextFloat() * 3.1415927F;
        double d0 = (double) ((float) (centerX + 8) + MathHelper.sin(f) * (float) maxVeinSize / 8.0F);
        double d1 = (double) ((float) (centerX + 8) - MathHelper.sin(f) * (float) maxVeinSize / 8.0F);
        double d2 = (double) ((float) (centerZ + 8) + MathHelper.cos(f) * (float) maxVeinSize / 8.0F);
        double d3 = (double) ((float) (centerZ + 8) - MathHelper.cos(f) * (float) maxVeinSize / 8.0F);
        double d4 = (double) (centerY + random.nextInt(3) - 2);
        double d5 = (double) (centerY + random.nextInt(3) - 2);

        for (int l = 0; l <= maxVeinSize; ++l) {
            double d6 = d0 + (d1 - d0) * (double) l / (double) maxVeinSize;
            double d7 = d4 + (d5 - d4) * (double) l / (double) maxVeinSize;
            double d8 = d2 + (d3 - d2) * (double) l / (double) maxVeinSize;
            double d9 = random.nextDouble() * (double) maxVeinSize / 16.0D;
            double d10 =
                    (double) (MathHelper.sin((float) l * 3.1415927F / (float) maxVeinSize) + 1.0F) * d9 + 1.0D;
            double d11 =
                    (double) (MathHelper.sin((float) l * 3.1415927F / (float) maxVeinSize) + 1.0F) * d9 + 1.0D;
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

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && sample.hasBlock(k2, l2, i3)
                                        && sample.getBlock(k2, l2, i3).getType() == blockTypeToReplace) {
                                    sample.setBlock(k2, l2, i3, blockTypeToGenerate);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
