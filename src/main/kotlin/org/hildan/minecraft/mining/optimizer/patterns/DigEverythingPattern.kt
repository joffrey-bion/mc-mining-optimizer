package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Player

/**
 * A pattern where every single block is dug. Not a real-life example, but a good test pattern.
 */
class DigEverythingPattern : AbstractDiggingPattern() {

    override val width = Player.WIDTH
    override val height = Player.HEIGHT
    override val length = Player.LENGTH

    override fun getAccesses(originX: Int, originY: Int) = setOf(Access(originX, originY))

    override fun digInto(sample: Sample, originX: Int, originY: Int, originZ: Int) {
        for (x in originX until originX + Player.WIDTH) {
            for (y in originY until originY + Player.HEIGHT) {
                for (z in originZ until originZ + Player.LENGTH) {
                    sample.digBlock(x, y, z)
                }
            }
        }
    }
}
