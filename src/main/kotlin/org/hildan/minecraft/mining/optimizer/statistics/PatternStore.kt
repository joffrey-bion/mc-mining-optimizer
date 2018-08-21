package org.hildan.minecraft.mining.optimizer.statistics

/**
 * Stores patterns and their stats, keeping only the best ones.
 */
class PatternStore : Iterable<EvaluatedPattern> {

    private val patterns = mutableListOf<EvaluatedPattern>()

    fun add(pattern: EvaluatedPattern): Boolean {
        if (containsBetterThan(pattern)) {
            return false
        }
        patterns.add(pattern)
        removeStrictlyWorseThan(pattern)
        return true
    }

    private fun containsBetterThan(pattern: EvaluatedPattern) =
        patterns.any { it > pattern }

    private fun removeStrictlyWorseThan(pattern: EvaluatedPattern) = patterns.removeAll { it < pattern }

    override fun iterator() = patterns.iterator()

    override fun toString(): String {
        val s = if (patterns.size > 1) "s" else ""
        return "${patterns.size} best pattern$s: $patterns"
    }
}
