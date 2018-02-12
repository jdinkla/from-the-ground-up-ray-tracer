package net.dinkla.raytracer.math

object Random {

    internal var r = java.util.Random()

    fun randInt(high: Int): Int = r.nextInt(high)

    fun randInt(low: Int, high: Int): Int = r.nextInt(high - low) + low

    // TODO rename randFloat
    fun randFloat(): Double = r.nextDouble()

    // TODO rename randFloat
    fun randFloat(low: Double, high: Double): Double = r.nextDouble() * (high - low) + low

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
