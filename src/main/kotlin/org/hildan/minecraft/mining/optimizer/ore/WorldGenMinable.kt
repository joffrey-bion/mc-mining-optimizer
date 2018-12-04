package org.hildan.minecraft.mining.optimizer.ore

import org.hildan.minecraft.mining.optimizer.blocks.Sample

import java.util.Random

internal class WorldGenMinable(
    private val blockTypeToGenerate: BlockType
) {
    private val maxVeinSize: Int = blockTypeToGenerate.maxVeinSize
    private val maxVeinSizeDouble: Double = blockTypeToGenerate.maxVeinSize.toDouble()
    private val blockTypeToReplace: BlockType = BlockType.STONE

    /*
     Implementation retro-engineered from Bukkit's github, here be dragons.
     https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/WorldGenMinable.java
     */
    fun generateInto(sample: Sample, random: Random, centerX: Int, centerY: Int, centerZ: Int) {

        // looks like random orientation
        val a = random.nextDouble() * Math.PI
        val sinA = MathHelper.sin(a)
        val cosA = MathHelper.cos(a)

        // looks like bounds calculation based on orientation
        val shiftedCenterX = (centerX + 8).toDouble()
        val shiftedCenterZ = (centerZ + 8).toDouble()
        val highX = (shiftedCenterX + sinA * maxVeinSizeDouble / 8.0)
        val lowX = (shiftedCenterX - sinA * maxVeinSizeDouble / 8.0)
        val highZ = (shiftedCenterZ + cosA * maxVeinSizeDouble / 8.0)
        val lowZ = (shiftedCenterZ - cosA * maxVeinSizeDouble / 8.0)
        val highY = (centerY + random.nextInt(3) - 2).toDouble()
        val lowY = (centerY + random.nextInt(3) - 2).toDouble()

        for (size in 0..maxVeinSize) {
            val sizeD = size.toDouble()

            fun scale(d: Double) = d * sizeD / maxVeinSizeDouble

            val scaledX = highX + scale(lowX - highX)
            val scaledY = highY + scale(lowY - highY)
            val scaledZ = highZ + scale(lowZ - highZ)

            val randSize = random.nextDouble() * maxVeinSizeDouble / 16.0
            val sinScaledPi = MathHelper.sin(scale(Math.PI))

            val diameter = (sinScaledPi + 1.0) * randSize + 1.0
            val radius = diameter / 2.0
            val minX = MathHelper.floor(scaledX - radius)
            val minY = MathHelper.floor(scaledY - radius)
            val minZ = MathHelper.floor(scaledZ - radius)
            val maxX = MathHelper.floor(scaledX + radius)
            val maxY = MathHelper.floor(scaledY + radius)
            val maxZ = MathHelper.floor(scaledZ + radius)

            for (x in minX..maxX) {
                val d12 = (x.toDouble() + 0.5 - scaledX) / radius
                val sqD12 = d12 * d12

                if (sqD12 < 1.0) {
                    for (y in minY..maxY) {
                        val d13 = (y.toDouble() + 0.5 - scaledY) / radius
                        val sqD13 = d13 * d13

                        if (sqD12 + sqD13 < 1.0) {
                            for (z in minZ..maxZ) {
                                val d14 = (z.toDouble() + 0.5 - scaledZ) / radius
                                val sqD14 = d14 * d14

                                if (sqD12 + sqD13 + sqD14 < 1.0 && sample.contains(x, y, z)
                                    && sample.getBlock(x, y, z).type == blockTypeToReplace
                                ) {
                                    sample.setBlock(x, y, z, blockTypeToGenerate)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
