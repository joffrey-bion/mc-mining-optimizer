package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Player

/**
 * A pattern where every single block is dug. Not a real-life example, but a good test pattern.
 */
class DigEverythingPattern : RepeatedDiggingPattern {

    override val width = Player.WIDTH
    override val height = Player.HEIGHT
    override val length = Player.LENGTH

    override fun getAccesses(offsetX: Int, offsetY: Int) = setOf(Access(offsetX, offsetY))

    override fun digInto(sample: Sample, offsetX: Int, offsetY: Int, offsetZ: Int) {
        for (x in offsetX until offsetX + Player.WIDTH) {
            for (y in offsetY until offsetY + Player.HEIGHT) {
                for (z in offsetZ until offsetZ + Player.LENGTH) {
                    sample.digBlock(x, y, z)
                }
            }
        }
    }
}
