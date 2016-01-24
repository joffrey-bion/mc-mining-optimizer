package org.hildan.minecraft.mining.optimizer.blocks;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class SampleTest {

    private Sample sample;

    private Block block000;

    private Block block222;

    private Block block345;

    private Block block567;

    @Before
    public void initSample() {
        sample = new Sample(10, 10, 10);
        block000 = sample.getBlock(0, 0, 0);
        block222 = sample.getBlock(2, 2, 2);
        block345 = sample.getBlock(3, 4, 5);
        block567 = sample.getBlock(5, 6, 7);
    }

    @Test
    public void testGetBlockAbove() {
        Block block505 = sample.getBlock(5, 0, 5);
        Block block355 = sample.getBlock(3, 5, 5);
        Block block595 = sample.getBlock(5, 9, 5);
        Block relative355 = sample.getBlockAbove(block345, Wrapping.WRAP);
        Block relative355Cut = sample.getBlockAbove(block345, Wrapping.CUT);
        Block relative505 = sample.getBlockAbove(block595, Wrapping.WRAP);
        Block relative505Cut = sample.getBlockAbove(block595, Wrapping.CUT);
        assertEquals(block355, relative355);
        assertEquals(block355, relative355Cut);
        assertEquals(block505, relative505);
        assertNull(relative505Cut);
    }

    @Test
    public void testGetBlockRelative_CUT_standard() {
        Block relative000 = sample.getBlock(block345, -3, -4, -5, Wrapping.CUT);
        Block relative222 = sample.getBlock(block345, -1, -2, -3, Wrapping.CUT);
        Block relative345 = sample.getBlock(block000, 3, 4, 5, Wrapping.CUT);
        Block relative567 = sample.getBlock(block222, 3, 4, 5, Wrapping.CUT);
        assertEquals(block000, relative000);
        assertEquals(block222, relative222);
        assertEquals(block345, relative345);
        assertEquals(block567, relative567);
    }

    @Test
    public void testGetBlockRelative_WRAP_standard() {
        Block relative000 = sample.getBlock(block345, -3, -4, -5, Wrapping.WRAP);
        Block relative222 = sample.getBlock(block345, -1, -2, -3, Wrapping.WRAP);
        Block relative345 = sample.getBlock(block000, 3, 4, 5, Wrapping.WRAP);
        Block relative567 = sample.getBlock(block222, 3, 4, 5, Wrapping.WRAP);
        assertEquals(block000, relative000);
        assertEquals(block222, relative222);
        assertEquals(block345, relative345);
        assertEquals(block567, relative567);
    }

    @Test
    public void testGetBlockRelative_CUT_side() {
        Block relative000 = sample.getBlock(block000, 10, 10, 10, Wrapping.CUT);
        Block relative345 = sample.getBlock(block000, 13, 14, 5, Wrapping.CUT);
        Block relative567 = sample.getBlock(block222, 3, 14, 15, Wrapping.CUT);
        Block relative987 = sample.getBlock(block222, -3, -4, -5, Wrapping.CUT);
        assertNull(relative000);
        assertNull(relative345);
        assertNull(relative567);
        assertNull(relative987);
    }

    @Test
    public void testGetBlockRelative_WRAP_side() {
        Block relative000 = sample.getBlock(block000, 10, 10, 10, Wrapping.WRAP);
        Block relative345 = sample.getBlock(block000, 13, 14, 5, Wrapping.WRAP);
        Block relative567 = sample.getBlock(block222, 3, 14, 15, Wrapping.WRAP);
        assertEquals(block000, relative000);
        assertEquals(block345, relative345);
        assertEquals(block567, relative567);
    }

    @Test
    public void testGetAdjacentBlocks_CUT_standard() {
        Collection<Block> adjBlocks = sample.getAdjacentBlocks(sample.getBlock(2, 2, 2), Wrapping.CUT);
        assertTrue(adjBlocks.contains(sample.getBlock(1, 2, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(3, 2, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 1, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 3, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 2, 1)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 2, 3)));
    }

    @Test
    public void testGetAdjacentBlocks_WRAP_standard() {
        Collection<Block> adjBlocks = sample.getAdjacentBlocks(sample.getBlock(2, 2, 2), Wrapping.WRAP);
        assertTrue(adjBlocks.contains(sample.getBlock(1, 2, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(3, 2, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 1, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 3, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 2, 1)));
        assertTrue(adjBlocks.contains(sample.getBlock(2, 2, 3)));
    }

    @Test
    public void testGetAdjacentBlocks_CUT_side() {
        Collection<Block> adjBlocks = sample.getAdjacentBlocks(sample.getBlock(0, 2, 2), Wrapping.CUT);
        assertFalse(adjBlocks.contains(sample.getBlock(9, 2, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(1, 2, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 1, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 3, 2)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 2, 1)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 2, 3)));
    }

    @Test
    public void testGetAdjacentBlocks_WRAP_side() {
        Collection<Block> adjBlocks = sample.getAdjacentBlocks(sample.getBlock(0, 2, 0), Wrapping.WRAP);
        assertTrue(adjBlocks.contains(sample.getBlock(9, 2, 0)));
        assertTrue(adjBlocks.contains(sample.getBlock(1, 2, 0)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 1, 0)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 3, 0)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 2, 9)));
        assertTrue(adjBlocks.contains(sample.getBlock(0, 2, 1)));
    }

    @Test
    public void testDigBlock() {
        sample.digBlock(block000);
        sample.digBlock(block222);
        sample.digBlock(block345);
        sample.digBlock(1, 2, 3);
        sample.digBlock(0, 5, 4);
        sample.digBlock(7, 2, 6);
        assertTrue(block000.isDug());
        assertTrue(block222.isDug());
        assertTrue(block345.isDug());
        assertTrue(sample.getBlock(1, 2, 3).isDug());
        assertTrue(sample.getBlock(0, 5, 4).isDug());
        assertTrue(sample.getBlock(7, 2, 6).isDug());
    }

    @Test
    public void testGetDugBlocksCount() {
        sample.digBlock(0, 0, 0);
        sample.digBlock(1, 2, 3);
        sample.digBlock(0, 5, 4);
        long dugBlocksCount = sample.getNumberOfBlocksMatching(Block::isDug);
        assertEquals(3, dugBlocksCount);
    }

    @Test
    public void testGetOresCount() {
        sample.setBlock(0, 0, 0, BlockType.COAL_ORE);
        sample.setBlock(1, 2, 3, BlockType.IRON_ORE);
        sample.setBlock(2, 2, 3, BlockType.LAPIS_ORE);
        sample.setBlock(2, 3, 3, BlockType.DIAMOND_ORE);
        sample.setBlock(2, 3, 4, BlockType.GOLD_ORE);
        sample.setBlock(3, 3, 4, BlockType.REDSTONE_ORE);
        sample.setBlock(3, 4, 4, BlockType.STONE);
        sample.setBlock(3, 4, 5, BlockType.AIR);
        long oreBlocksCount = sample.getNumberOfBlocksMatching(Block::isOre);
        assertEquals(6, oreBlocksCount);
    }
}