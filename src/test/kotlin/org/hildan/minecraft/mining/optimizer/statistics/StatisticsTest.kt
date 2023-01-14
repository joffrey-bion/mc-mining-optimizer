package org.hildan.minecraft.mining.optimizer.statistics

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class StatisticsTest {

    private val eff50tho50 = Statistics(5, 10, 10)
    private val eff50tho100 = Statistics(5, 10, 5)
    private val eff100tho50 = Statistics(5, 5, 10)
    private val eff100tho100 = Statistics(5, 5, 5)

    @Test
    fun testComparison_basicEquality() {
        assertTrue(eff50tho50 >= eff50tho50)
        assertTrue(eff50tho50 <= eff50tho50)

        assertTrue(eff50tho100 >= eff50tho100)
        assertTrue(eff50tho100 <= eff50tho100)

        assertTrue(eff100tho50 >= eff100tho50)
        assertTrue(eff100tho50 <= eff100tho50)

        assertTrue(eff100tho100 >= eff100tho100)
        assertTrue(eff100tho100 <= eff100tho100)
    }

    @Test
    fun testComparison_basicSuperiority() {
        assertTrue(eff50tho50 < eff100tho100)
        assertTrue(eff100tho100 > eff50tho50)
    }

    @Test
    fun testComparison_superiorIfBetterEfficiencyAndSameThoroughness() {
        assertTrue(eff50tho50 < eff100tho50)
        assertTrue(eff50tho100 < eff100tho100)
        assertTrue(eff100tho50 > eff50tho50)
        assertTrue(eff100tho100 > eff50tho100)
    }

    @Test
    fun testComparison_superiorIfBetterThoroughnessAndSameEfficiency() {
        assertTrue(eff50tho50 < eff50tho100)
        assertTrue(eff50tho100 > eff50tho50)
        assertTrue(eff100tho50 < eff100tho100)
        assertTrue(eff100tho100 > eff100tho50)
    }

    @Test
    fun testComparison_equalIfOneBetterAndOneWorse() {
        assertTrue(eff50tho100 <= eff100tho50)
        assertTrue(eff50tho100 >= eff100tho50)
        assertTrue(eff100tho50 <= eff50tho100)
        assertTrue(eff100tho50 >= eff50tho100)
    }
}
