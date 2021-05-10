package org.hildan.minecraft.mining.optimizer.statistics

import java.util.*

/**
 * Represents statistics about a digging pattern.
 */
data class Statistics(
    private val foundOres: Long,
    private val dugBlocks: Long,
    private val totalOres: Long,
) : Comparable<Statistics> {

    private val efficiency: Double = proportion(foundOres, dugBlocks)
    private val thoroughness: Double = proportion(foundOres, totalOres)

    private fun proportion(qty: Long, total: Long): Double = if (total == 0L) 100.0 else qty.toDouble() * 100.0 / total

    override fun compareTo(other: Statistics) = when {
        efficiency > other.efficiency -> if (thoroughness >= other.thoroughness) 1 else 0
        efficiency < other.efficiency -> if (thoroughness <= other.thoroughness) -1 else 0
        else -> thoroughness.compareTo(other.thoroughness)
    }

    override fun toString(): String {
        return String.format(Locale.US, "e=%.2f%% t=%.2f%%", efficiency, thoroughness)
    }

    fun toFullString(nbSamples: Int): String {
        val sb = StringBuilder()

        val avgTotalOres = totalOres.toDouble() / nbSamples
        val avgFoundOres = foundOres.toDouble() / nbSamples
        val avgDugBlocks = dugBlocks.toDouble() / nbSamples

        sb.append(String.format("            %10s  %12s%n", "Avg/sample", "Total"))
        sb.append(String.format("Total ores: %10.2f  %,12d%n", avgTotalOres, totalOres))
        sb.append(String.format("Found ores: %10.2f  %,12d%n", avgFoundOres, foundOres))
        sb.append(String.format("Dug Blocks: %10.2f  %,12d%n", avgDugBlocks, dugBlocks))
        sb.append(String.format("%n"))
        if (dugBlocks == 0L) {
            sb.append(String.format("/!\\ The pattern didn't dig anything!%n"))
        } else {
            sb.append(String.format("Efficiency:    %6.2f%%%n", efficiency))
            sb.append(String.format("Thoroughness:  %6.2f%%%n", thoroughness))
        }
        return sb.toString()
    }
}
