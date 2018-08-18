package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action

/**
 * A pattern that can be programmatically generated.
 *
 * @constructor creates a GeneratedPattern with the given list of actions for each given access
 * @param actionsByAccess defines for each access, the corresponding list of actions
 */
internal class GeneratedPattern(
    private val actionsByAccess: Map<Access, List<Action>>
) : DiggingPattern {

    override fun getAccesses(dimensions: Dimensions) = actionsByAccess.keys

    override fun digInto(sample: Sample) {
        for ((access, actions) in actionsByAccess) {
            sample.digBlock(access.feet)
            sample.digBlock(access.head)

            var pos = access.head
            for (action in actions) {
                pos = action.executeOn(sample, pos)
            }
        }
    }
}
