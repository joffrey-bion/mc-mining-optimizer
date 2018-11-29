package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.geometry.Player
import org.hildan.minecraft.mining.optimizer.geometry.Position

/**
 * Defines an entry point to start digging a pattern. In the code, any block can be dug at any time. In the real
 * Minecraft world, one needs to actually start somewhere that's reachable, and that's what defines an access.
 *
 * An access consists of 2 blocks, because the player has to fit inside.
 *
 * By convention, the accesses are on the Z=0 plane. This means every pattern must be oriented so that the access is
 * actually on this side.
 */
data class Access(val feet: Position, val head: Position) {
    /**
     * Creates an access at the given feet position.
     *
     * @param x the X position of the feet of the player
     * @param y the Y position of the feet of the player
     */
    constructor(x: Int, y: Int) : this(Position.of(x, y, 0), Position.of(x, y + Player.HEIGHT - 1, 0))

    override fun toString(): String = "Access(${feet.x},${feet.y})"
}
