package net.dinkla.raytracer.math

class WrappedDouble(
    var value: Double,
) : Comparable<WrappedDouble> {
    override fun compareTo(other: WrappedDouble): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()

    fun isLessThan(tmin: WrappedDouble): Boolean = value.compareTo(tmin.value) == -1

    override fun equals(other: Any?): Boolean =
        if (null == other || other !is WrappedDouble) {
            false
        } else {
            this.value == other.value
        }

    override fun hashCode(): Int = value.hashCode()

    companion object {
        fun createMax() = WrappedDouble(Double.MAX_VALUE)
    }
}
