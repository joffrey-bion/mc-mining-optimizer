package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SampleTest {

    private lateinit var sample: Sample

    private lateinit var block000: Block

    private lateinit var block222: Block

    private lateinit var block345: Block

    private lateinit var block567: Block

    @BeforeEach
    fun initSample() {
        sample = Sample(Dimensions(10, 10, 10), BlockType.STONE)
        block000 = sample.getBlock(0, 0, 0)
        block222 = sample.getBlock(2, 2, 2)
        block345 = sample.getBlock(3, 4, 5)
        block567 = sample.getBlock(5, 6, 7)
    }

    @Test
    fun testDigBlock() {
        with(sample.dimensions) {
            sample.digBlock(block000.index)
            sample.digBlock(block222.index)
            sample.digBlock(block345.index)
            sample.digBlock(1, 2, 3)
            sample.digBlock(0, 5, 4)
            sample.digBlock(7, 2, 6)
            assertTrue(block000.isDug)
            assertTrue(block222.isDug)
            assertTrue(block345.isDug)
            assertTrue(sample.getBlock(1, 2, 3).isDug)
            assertTrue(sample.getBlock(0, 5, 4).isDug)
            assertTrue(sample.getBlock(7, 2, 6).isDug)
        }
    }

    @Test
    fun testGetDugBlocksCount() {
        sample.digBlock(0, 0, 0)
        sample.digBlock(1, 2, 3)
        sample.digBlock(0, 5, 4)
        val dugBlocksCount = sample.dugBlocksCount.toLong()
        assertEquals(3, dugBlocksCount)
    }

    @Test
    fun testGetOresCount() {
        sample.setBlock(0, 0, 0, BlockType.COAL_ORE)
        sample.setBlock(1, 2, 3, BlockType.IRON_ORE)
        sample.setBlock(2, 2, 3, BlockType.LAPIS_ORE)
        sample.setBlock(2, 3, 3, BlockType.DIAMOND_ORE)
        sample.setBlock(2, 3, 4, BlockType.GOLD_ORE)
        sample.setBlock(3, 3, 4, BlockType.REDSTONE_ORE)
        sample.setBlock(3, 4, 4, BlockType.STONE)
        sample.setBlock(3, 4, 5, BlockType.AIR)
        val oreBlocksCount = sample.oreBlocksCount.toLong()
        assertEquals(6, oreBlocksCount)
    }
}
