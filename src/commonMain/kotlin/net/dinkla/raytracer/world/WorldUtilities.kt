package net.dinkla.raytracer.world

internal fun rand() = r.nextDouble()
internal fun randInt(n: Int) = r.nextInt(n)
private val r = java.util.Random()

internal fun repeat3(n: Int, closure: (Int, Int, Int) -> Unit): Unit {
    repeat(n) { i ->
        repeat(n) { j ->
            repeat(n) { k ->
                closure(i, j, k)
            }
        }
    }
}
