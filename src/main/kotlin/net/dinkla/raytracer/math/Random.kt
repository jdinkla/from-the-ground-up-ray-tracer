package net.dinkla.raytracer.math

import java.util.ArrayList

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 18:14:31
 * To change this template use File | Settings | File Templates.
 */
object Random {

    internal var r = java.util.Random()

    fun randInt(high: Int): Int {
        return r.nextInt(high)
    }

    fun randInt(low: Int, high: Int): Int {
        return r.nextInt(high - low) + low
    }

    // TODO rename randFloat
    fun randFloat(): Double {
        return r.nextDouble()
    }

    // TODO rename randFloat
    fun randFloat(low: Double, high: Double): Double {
        return r.nextDouble() * (high - low) + low
    }

    fun setRandSeed(seed: Int) {
        r.setSeed(seed.toLong())
    }

    fun randomShuffle(ls: MutableList<Int>) {
        val n = ls.size
        for (i in 1 until n) {
            val i2 = randInt(n)
            val tmp = ls[i]
            ls[i] = ls[i2]
            ls[i2] = tmp
        }
    }
}
