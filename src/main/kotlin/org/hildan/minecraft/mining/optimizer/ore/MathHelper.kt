package org.hildan.minecraft.mining.optimizer.ore

internal object MathHelper {

    private const val N_STEPS = 65536

    private val SIN_CACHE = (0 until N_STEPS).map { Math.sin(it * Math.PI * 2 / N_STEPS) }

    fun sin(f: Double) = SIN_CACHE[(f * 10430.378).toInt() and 0xffff]

    fun cos(f: Double) = SIN_CACHE[(f * 10430.378 + 16384.0).toInt() and 0xffff]

    fun floor(d: Double): Int {
        val i = d.toInt()
        return if (d < i) i - 1 else i
    }
}
