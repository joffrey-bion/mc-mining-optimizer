package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.ore.BlockType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleTest {

    private lateinit var sample: Sample

    private val block000 = Position.of(0, 0, 0)

    private val block222 = Position.of(2, 2, 2)

    private val block345 = Position.of(3, 4, 5)

    @BeforeTest
    fun initSample() {
        sample = Sample(Dimensions(10, 10, 10), BlockType.STONE)
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
            assertEquals(BlockType.AIR, sample.getBlockType(0, 0, 0))
            assertEquals(BlockType.AIR, sample.getBlockType(2, 2, 2))
            assertEquals(BlockType.AIR, sample.getBlockType(3, 4, 5))
            assertEquals(BlockType.AIR, sample.getBlockType(1, 2, 3))
            assertEquals(BlockType.AIR, sample.getBlockType(0, 5, 4))
            assertEquals(BlockType.AIR, sample.getBlockType(7, 2, 6))
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
        sample.setBlockType(0, 0, 0, BlockType.COAL_ORE)
        sample.setBlockType(1, 2, 3, BlockType.IRON_ORE)
        sample.setBlockType(2, 2, 3, BlockType.LAPIS_ORE)
        sample.setBlockType(2, 3, 3, BlockType.DIAMOND_ORE)
        sample.setBlockType(2, 3, 4, BlockType.GOLD_ORE)
        sample.setBlockType(3, 3, 4, BlockType.REDSTONE_ORE)
        sample.setBlockType(3, 4, 4, BlockType.STONE)
        sample.setBlockType(3, 4, 5, BlockType.AIR)
        val oreBlocksCount = sample.oreBlocksCount.toLong()
        assertEquals(6, oreBlocksCount)
    }

    @Test
    fun testFillAir() {
        sample.digBlock(2)
        sample.setBlockType(1, 0, 3, BlockType.COAL_ORE)
        sample.fill(BlockType.AIR)
        assertEquals(1000, sample.dugBlocksCount)
        assertEquals(0, sample.oreBlocksCount)
        sample.dimensions.positions.forEach {
            assertEquals(BlockType.AIR, sample.getBlockType(it.x, it.y, it.z))
        }
    }

    @Test
    fun testFillOre() {
        sample.digBlock(2)
        sample.setBlockType(1, 0, 3, BlockType.COAL_ORE)
        sample.fill(BlockType.GOLD_ORE)
        assertEquals(0, sample.dugBlocksCount)
        assertEquals(1000, sample.oreBlocksCount)
        sample.dimensions.positions.forEach {
            assertEquals(BlockType.GOLD_ORE, sample.getBlockType(it.x, it.y, it.z))
        }
    }

    @Test
    fun testFillStone() {
        sample.digBlock(2)
        sample.setBlockType(1, 0, 3, BlockType.COAL_ORE)
        sample.fill(BlockType.STONE)
        assertEquals(0, sample.dugBlocksCount)
        assertEquals(0, sample.oreBlocksCount)
        sample.dimensions.positions.forEach {
            assertEquals(BlockType.STONE, sample.getBlockType(it.x, it.y, it.z))
        }
    }
}
