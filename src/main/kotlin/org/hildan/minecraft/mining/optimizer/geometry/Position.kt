package org.hildan.minecraft.mining.optimizer.geometry

/**
 * Represents an immutable 3D position.
 */
open class Position(
    val x: Int,
    val y: Int,
    val z: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    override fun toString(): String = "($x,$y,$z)"
}
