package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * A pattern along with its statistics.
 */
class EvaluatedPattern(val pattern: DiggingPattern, val statistics: Statistics) {

    override fun toString(): String = statistics.toString()
}
