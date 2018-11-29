package org.hildan.minecraft.mining.optimizer.geometry

val ONE_EAST: Distance3D = Distance3D.of(+1, 0, 0)
val ONE_WEST: Distance3D = Distance3D.of(-1, 0, 0)
val ONE_ABOVE: Distance3D = Distance3D.of(0, +1, 0)
val ONE_BELOW: Distance3D = Distance3D.of(0, -1, 0)
val ONE_SOUTH: Distance3D = Distance3D.of(0, 0, +1)
val ONE_NORTH: Distance3D = Distance3D.of(0, 0, -1)

/**
 * A generic 3D vector.
 */
interface Vector3D {
    val x: Int
    val y: Int
    val z: Int
    val sqNorm: Int
        get() = x * x + y * y + z * z
}

/**
 * An immutable 3D position.
 */
interface Position : Vector3D {
    companion object {
        fun of(x: Int, y: Int, z: Int): Position = BasicVector.of(x, y, z)
    }
}

/**
 * An immutable 3D distance.
 */
interface Distance3D : Vector3D {
    companion object {
        fun of(dx: Int, dy: Int, dz: Int): Distance3D = BasicVector.of(dx, dy, dz)
    }
}

private data class BasicVector(
    override val x: Int,
    override val y: Int,
    override val z: Int
) : Position, Distance3D {

    companion object {
        private val cache: MutableMap<Int, MutableMap<Int, MutableMap<Int, BasicVector>>> = HashMap()

        fun of(x: Int, y: Int, z: Int): BasicVector {
            return cache.getOrPut(x, ::HashMap).getOrPut(y, ::HashMap).getOrPut(z) { BasicVector(x, y, z) }
        }
    }
}
