package org.hildan.minecraft.mining.optimizer.statistics

import java.util.Locale

/**
 * Represents statistics about a digging pattern.
 */
class Statistics(private val nbSamples: Int) {

    internal var totalOres: Long = 0

    internal var foundOres: Long = 0

    internal var dugBlocks: Long = 0

    private val efficiency: Double
        get() = proportion(foundOres, dugBlocks)

    private val thoroughness: Double
        get() = proportion(foundOres, totalOres)

    private fun proportion(qty: Long, total: Long): Double = if (total == 0L) 100.0 else qty.toDouble() * 100 / total

    internal fun isBetterThan(stats: Statistics): Boolean {
        val eff = efficiency
        val tho = thoroughness
        val effOther = stats.efficiency
        val thoOther = stats.thoroughness

        return eff > effOther && tho > thoOther
    }

    override fun toString(): String {
        return String.format(Locale.US, "e=%.2f%% t=%.2f%%", efficiency, thoroughness)
    }

    fun toFullString(): String {
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
