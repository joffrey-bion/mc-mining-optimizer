package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * A pattern along with its statistics.
 */
class EvaluatedPattern(val pattern: DiggingPattern, val statistics: Statistics) {

    internal fun isBetterThan(pattern: EvaluatedPattern) = statistics.isBetterThan(pattern.statistics)

    override fun toString(): String = statistics.toString()
}
