package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.patterns.AbstractDiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action
import java.util.HashMap

/**
 * A pattern that can be programmatically generated.
 *
 * @constructor creates a GeneratedPattern with the given list of actions for each given access
 * @param actionsPerAccess defines for each access, the corresponding list of actions
 */
internal class GeneratedPattern(
    actionsPerAccess: Map<Access, List<Action>>,
    override val width: Int,
    override val height: Int,
    override val length: Int
) : AbstractDiggingPattern() {

    private val actionsPerAccess: Map<Access, List<Action>> = HashMap(actionsPerAccess)

    override fun getAccesses(originX: Int, originY: Int) = if (originX == 0 && originY == 0) {
        actionsPerAccess.keys
    } else {
        actionsPerAccess.keys.map { a -> Access(a.feet.x + originX, a.feet.y + originY) }.toSet()
    }

    override fun digInto(sample: Sample, originX: Int, originY: Int, originZ: Int) {
        for (access in getAccesses(originX, originY)) {
            sample.digBlock(access.feet)
            sample.digBlock(access.head)

            var pos = access.head
            for (action in actionsPerAccess[access]!!) {
                pos = action.executeOn(sample, pos)
            }
        }
    }

    override fun toString(): String {
        val accesses = getAccesses(0, 0)
        val sb = StringBuilder()
        val indent = "   "
        for (access in accesses) {
            sb.append(access).append(String.format("%n"))
            for (action in actionsPerAccess[access]!!) {
                sb.append(indent).append(action).append(String.format("%n"))
            }
        }
        return sb.toString()
    }
}
