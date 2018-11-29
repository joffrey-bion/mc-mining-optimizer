package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * A pattern along with its statistics.
 */
class EvaluatedPattern(
    val pattern: DiggingPattern,
    val statistics: Statistics
) : Comparable<EvaluatedPattern> {

    override fun compareTo(other: EvaluatedPattern): Int = statistics.compareTo(other.statistics)

    override fun toString(): String = statistics.toString()
}
