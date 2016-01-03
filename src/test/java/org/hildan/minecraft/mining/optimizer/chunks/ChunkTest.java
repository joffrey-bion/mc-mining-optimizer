package org.hildan.minecraft.mining.optimizer.chunks;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ChunkTest {

    @Test
    public void testGetBlockAbsolute() throws Exception {

    }

    @Test
    public void testGetBlockRelative() throws Exception {

    }

    @Test
    public void testGetAdjacentBlocks_CUT_standard() throws Exception {
        Chunk chunk = new Chunk(5, 5, 5);
        List<Block> adjBlocks = chunk.getAdjacentBlocks(chunk.getBlock(2, 2, 2), Wrapping.CUT);
        assertTrue(adjBlocks.contains(chunk.getBlock(1, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(3, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 1, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 3, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 2, 1)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 2, 3)));
    }

    @Test
    public void testGetAdjacentBlocks_WRAP_standard() throws Exception {
        Chunk chunk = new Chunk(5, 5, 5);
        List<Block> adjBlocks = chunk.getAdjacentBlocks(chunk.getBlock(2, 2, 2), Wrapping.WRAP);
        assertTrue(adjBlocks.contains(chunk.getBlock(1, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(3, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 1, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 3, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 2, 1)));
        assertTrue(adjBlocks.contains(chunk.getBlock(2, 2, 3)));
    }

    @Test
    public void testGetAdjacentBlocks_CUT_side() throws Exception {
        Chunk chunk = new Chunk(5, 5, 5);
        List<Block> adjBlocks = chunk.getAdjacentBlocks(chunk.getBlock(0, 2, 2), Wrapping.CUT);
        assertFalse(adjBlocks.contains(chunk.getBlock(4, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(1, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 1, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 3, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 2, 1)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 2, 3)));
    }

    @Test
    public void testGetAdjacentBlocks_WRAP_side() throws Exception {
        Chunk chunk = new Chunk(5, 5, 5);
        List<Block> adjBlocks = chunk.getAdjacentBlocks(chunk.getBlock(0, 2, 2), Wrapping.WRAP);
        assertTrue(adjBlocks.contains(chunk.getBlock(4, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(1, 2, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 1, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 3, 2)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 2, 1)));
        assertTrue(adjBlocks.contains(chunk.getBlock(0, 2, 3)));
    }

    @Test
    public void testGetOresCount() throws Exception {

    }

    @Test
    public void testGetDugBlocksCount() throws Exception {

    }

    @Test
    public void testDig() throws Exception {

    }

    @Test
    public void testPutOre() throws Exception {

    }
}