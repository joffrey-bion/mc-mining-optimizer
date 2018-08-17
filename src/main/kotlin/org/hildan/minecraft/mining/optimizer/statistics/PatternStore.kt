package org.hildan.minecraft.mining.optimizer.statistics

import java.util.ArrayList

/**
 * Stores patterns and their stats, keeping only the best ones.
 */
class PatternStore : Iterable<EvaluatedPattern> {

    private val patterns = ArrayList<EvaluatedPattern>(20)

    fun add(pattern: EvaluatedPattern): Boolean {
        if (patterns.any { it.isBetterThan(pattern) }) {
            // new pattern not worth adding
            return false
        }
        // remove inferior patterns
        patterns.removeIf { pattern.isBetterThan(it) }
        patterns.add(pattern)
        return true
    }

    override fun iterator() = patterns.iterator()

    override fun toString(): String {
        val s = if (patterns.size > 1) "s" else ""
        return "${patterns.size} best pattern$s: $patterns"
    }
}
