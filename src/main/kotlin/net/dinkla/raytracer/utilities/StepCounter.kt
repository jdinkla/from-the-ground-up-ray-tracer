package net.dinkla.raytracer.utilities

class StepCounter(internal val start: Int, internal val end: Int, internal val step: Int, internal val cyclic: Boolean) {
    var current: Int = 0
        internal set

    init {
        assert(start < end)
        current = start
    }

    operator fun hasNext(): Boolean {
        return current < end
    }

    fun step() {
        current += step
        if (current > end && cyclic) {
            current = start
        }
    }

}
