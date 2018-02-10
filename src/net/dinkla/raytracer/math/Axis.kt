package net.dinkla.raytracer.math

enum class Axis private constructor(internal val asInt: Int) {

    X(0), Y(1), Z(2);

    operator fun next(): Axis {
        when (this) {
            X -> return Y
            Y -> return Z
            Z -> return X
            else -> return X
        }
    }

    companion object {
        @JvmStatic
        fun fromInt(i: Int): Axis {
            when (i) {
                0 -> return X
                1 -> return Y
                2 -> return Z
            }
            return X
        }
    }

}
