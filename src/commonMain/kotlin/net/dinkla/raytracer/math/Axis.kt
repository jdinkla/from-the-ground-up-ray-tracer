package net.dinkla.raytracer.math

enum class Axis(internal val value: Int) {

    X(0), Y(1), Z(2);

    operator fun next(): Axis = when (this) {
        X -> Y
        Y -> Z
        Z -> X
    }

    companion object {
        fun fromInt(i: Int): Axis = when (i) {
            0 -> X
            1 -> Y
            2 -> Z
            else -> Z
        }
    }
}
