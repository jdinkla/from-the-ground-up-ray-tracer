package net.dinkla.raytracer.math

class WrappedFloat(var value: Double) : Comparable<WrappedFloat> {

    override fun compareTo(o: WrappedFloat): Int {
        return if (null == value) {
            if (null == o.value) {
                0
            } else {
                -1
            }
        } else {
            if (null == o.value) {
                1
            } else {
                value!!.compareTo(o.value)
            }
        }
    }

    override fun toString(): String {
        return if (null != value) value.toString() else "null"
    }

    fun isLessThan(tmin: WrappedFloat): Boolean {
        return value.compareTo(tmin.value) == -1
    }

    override fun equals(other: Any?): Boolean {
        return if (null == other || other !is WrappedFloat) {
            false
        } else {
            this.value == other.value
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {

        fun createMax() = WrappedFloat(java.lang.Double.MAX_VALUE)

    }
}
