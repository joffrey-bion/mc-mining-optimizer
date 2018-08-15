package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

import java.util.ArrayList

/**
 * Stores patterns and their stats, keeping only the best ones.
 */
class PatternStore(private val margin: Double) : Iterable<EvaluatedPattern> {

    private val patterns = ArrayList<EvaluatedPattern>(20)

    fun add(pattern: DiggingPattern, stats: Statistics): Boolean {
        if (patterns.any { it.statistics.isBetterThan(stats, margin) }) {
            // new pattern not worth adding
            return false
        }
        // remove inferior patterns
        patterns.removeIf { stats.isBetterThan(it.statistics, margin) }
        patterns.add(EvaluatedPattern(pattern, stats))
        return true
    }

    override fun iterator() = patterns.iterator()

    override fun toString(): String {
        val s = if (patterns.size > 1) "s" else ""
        return "${patterns.size} pattern$s: $patterns"
    }
}
