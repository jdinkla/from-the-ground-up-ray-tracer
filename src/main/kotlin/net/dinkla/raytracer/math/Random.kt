package net.dinkla.raytracer.math

import java.util.Random

object Random {

    private val r = Random()

    fun int(high: Int): Int = r.nextInt(high)

    fun int(low: Int, high: Int): Int = r.nextInt(high - low) + low

    fun double(): Double = r.nextDouble()

    fun double(low: Double, high: Double): Double = r.nextDouble() * (high - low) + low

    fun setRandSeed(seed: Long) {
        r.setSeed(seed)
    }

    fun randomShuffle(ls: MutableList<Int>) {
        val n = ls.size
        for (i in 1 until n) {
            val i2 = int(n)
            val tmp = ls[i]
            ls[i] = ls[i2]
            ls[i2] = tmp
        }
    }
}
