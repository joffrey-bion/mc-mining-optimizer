package org.hildan.minecraft.mining.optimizer.geometry

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertNull

internal class DimensionsTest {

    private val pos000 = Position.of(0, 0, 0)
    private val pos100 = Position.of(1, 0, 0)
    private val pos010 = Position.of(0, 1, 0)
    private val pos110 = Position.of(1, 1, 0)
    private val pos020 = Position.of(0, 2, 0)
    private val pos120 = Position.of(1, 2, 0)
    private val pos001 = Position.of(0, 0, 1)
    private val pos101 = Position.of(1, 0, 1)
    private val pos011 = Position.of(0, 1, 1)
    private val pos111 = Position.of(1, 1, 1)
    private val pos021 = Position.of(0, 2, 1)
    private val pos121 = Position.of(1, 2, 1)
    private val pos211 = Position.of(2, 1, 1)
    private val pos112 = Position.of(1, 1, 2)
    private val pos003 = Position.of(0, 0, 3)

    @Test
    fun nbPositions() {
        assertEquals(0, Dimensions(0, 1, 1).nbPositions)
        assertEquals(0, Dimensions(1, 0, 1).nbPositions)
        assertEquals(0, Dimensions(1, 1, 0).nbPositions)
        assertEquals(1, Dimensions(1, 1, 1).nbPositions)
        assertEquals(2, Dimensions(2, 1, 1).nbPositions)
        assertEquals(2, Dimensions(1, 2, 1).nbPositions)
        assertEquals(2, Dimensions(1, 1, 2).nbPositions)
        assertEquals(24, Dimensions(2, 3, 4).nbPositions)
    }

    @Test
    fun positions() {
        val onlyZero = listOf(pos000)
        val firstLine = onlyZero + pos100
        val firstPlane = firstLine + pos010 + pos110
        val cube2x2 = firstPlane + pos001 + pos101 + pos011 + pos111

        assertEquals(onlyZero, Dimensions(1, 1, 1).positions)
        assertEquals(firstLine, Dimensions(2, 1, 1).positions)
        assertEquals(firstPlane, Dimensions(2, 2, 1).positions)
        assertEquals(cube2x2, Dimensions(2, 2, 2).positions)
    }

    @Test
    fun positionIndex() {
        with(Dimensions(2, 3, 4)) {
            assertEquals(0, pos000.index)
            assertEquals(1, pos100.index)
            assertEquals(2, pos010.index)
            assertEquals(3, pos110.index)
            assertEquals(6, pos001.index)
            assertEquals(7, pos101.index)
            assertEquals(8, pos011.index)
            assertEquals(9, pos111.index)
        }
    }

    @Test
    fun indexAbove() {
        with(Dimensions(2, 3, 4)) {
            assertNull(pos020.index.above)
            assertNull(pos120.index.above)
            assertNull(pos021.index.above)
            assertNull(pos121.index.above)
            assertEquals(pos010.index, pos000.index.above)
            assertEquals(pos110.index, pos100.index.above)
            assertEquals(pos011.index, pos001.index.above)
            assertEquals(pos111.index, pos101.index.above)
        }
    }

    @Test
    fun indexBelow() {
        with(Dimensions(2, 3, 4)) {
            assertNull(pos000.index.below)
            assertNull(pos100.index.below)
            assertNull(pos001.index.below)
            assertNull(pos101.index.below)
            assertEquals(pos000.index, pos010.index.below)
            assertEquals(pos100.index, pos110.index.below)
            assertEquals(pos001.index, pos011.index.below)
            assertEquals(pos101.index, pos111.index.below)
        }
    }

    @Test
    fun indexPlus() {
        with(Dimensions(2, 3, 4)) {
            assertNull(pos000.index + ONE_BELOW)
            assertNull(pos100.index + ONE_BELOW)
            assertNull(pos001.index + ONE_BELOW)
            assertNull(pos101.index + ONE_BELOW)
            assertEquals(pos000.index, pos010.index + ONE_BELOW)
            assertEquals(pos100.index, pos110.index + ONE_BELOW)
            assertEquals(pos001.index, pos011.index + ONE_BELOW)
            assertEquals(pos101.index, pos111.index + ONE_BELOW)

            assertEquals(pos100.index, pos000.index + ONE_EAST)
            assertEquals(pos000.index, pos100.index + ONE_EAST)
            assertEquals(pos110.index, pos010.index + ONE_EAST)
            assertEquals(pos010.index, pos110.index + ONE_EAST)
            assertEquals(pos101.index, pos001.index + ONE_EAST)
            assertEquals(pos001.index, pos101.index + ONE_EAST)
            assertEquals(pos111.index, pos011.index + ONE_EAST)
            assertEquals(pos011.index, pos111.index + ONE_EAST)

            assertEquals(pos000.index, pos000.index + Distance3D.of(2, 0, 0))
            assertEquals(pos020.index, pos000.index + Distance3D.of(0, 2, 0))
            assertEquals(pos003.index, pos000.index + Distance3D.of(0, 0, 3))
        }
    }

    @Test
    fun positionPlus() {
        with(Dimensions(2, 3, 4)) {
            assertNull(pos000 + ONE_BELOW)
            assertNull(pos100 + ONE_BELOW)
            assertNull(pos001 + ONE_BELOW)
            assertNull(pos101 + ONE_BELOW)
            assertEquals(pos000.index, pos010 + ONE_BELOW)
            assertEquals(pos100.index, pos110 + ONE_BELOW)
            assertEquals(pos001.index, pos011 + ONE_BELOW)
            assertEquals(pos101.index, pos111 + ONE_BELOW)

            assertEquals(pos100.index, pos000 + ONE_EAST)
            assertEquals(pos000.index, pos100 + ONE_EAST)
            assertEquals(pos110.index, pos010 + ONE_EAST)
            assertEquals(pos010.index, pos110 + ONE_EAST)
            assertEquals(pos101.index, pos001 + ONE_EAST)
            assertEquals(pos001.index, pos101 + ONE_EAST)
            assertEquals(pos111.index, pos011 + ONE_EAST)
            assertEquals(pos011.index, pos111 + ONE_EAST)

            assertEquals(pos000.index, pos000 + Distance3D.of(2, 0, 0))
            assertEquals(pos020.index, pos000 + Distance3D.of(0, 2, 0))
            assertEquals(pos003.index, pos000 + Distance3D.of(0, 0, 3))
        }
    }

    @Test
    fun `findAdjacentIndices in middle`() {
        with(Dimensions(3, 3, 3)) {
            val expected = listOf(pos011, pos101, pos110, pos211, pos121, pos112).map { it.index }.toSet()

            assertEquals(expected, getAdjacentIndices(pos111, Wrapping.CUT).toSet())
            assertEquals(expected, getAdjacentIndices(pos111, Wrapping.WRAP).toSet())
            assertEquals(expected, getAdjacentIndices(pos111, Wrapping.WRAP_XZ).toSet())
            assertEquals(expected, getAdjacentIndices(pos111).toSet())
        }
    }

    @Test
    fun `findAdjacentIndices on the side`() {
        with(Dimensions(2, 3, 4)) {
            val expectedCut = setOf(pos011, pos101, pos110, pos121, pos112).map { it.index }.toSet()
            val expectedWrap = setOf(pos011, pos101, pos110, pos121, pos112, pos011).map { it.index }.toSet()

            assertEquals(expectedCut, getAdjacentIndices(pos111, Wrapping.CUT).toSet())
            assertEquals(expectedWrap, getAdjacentIndices(pos111, Wrapping.WRAP).toSet())
            assertEquals(expectedWrap, getAdjacentIndices(pos111, Wrapping.WRAP_XZ).toSet())
            assertEquals(expectedWrap, getAdjacentIndices(pos111).toSet())
        }
    }

    @Test
    fun `findAdjacentIndices in the corner`() {
        with(Dimensions(2, 3, 4)) {
            val expectedCut = setOf(pos001, pos010, pos100).map { it.index }.toSet()
            val expectedWrap = setOf(pos001, pos010, pos100, pos003, pos020).map { it.index }.toSet()
            val expectedWrapXZ = setOf(pos001, pos010, pos100, pos003).map { it.index }.toSet()

            assertEquals(expectedCut, getAdjacentIndices(pos000, Wrapping.CUT).toSet())
            assertEquals(expectedWrap, getAdjacentIndices(pos000, Wrapping.WRAP).toSet())
            assertEquals(expectedWrapXZ, getAdjacentIndices(pos000, Wrapping.WRAP_XZ).toSet())
            assertEquals(expectedWrapXZ, getAdjacentIndices(pos000).toSet())
        }
    }
}
