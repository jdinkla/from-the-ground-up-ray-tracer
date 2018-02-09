package net.dinkla.raytracer.math

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 23:22:46
 * To change this template use File | Settings | File Templates.
 */
class WrappedFloat : Comparable<WrappedFloat> {

    public var value: Double? = null

    constructor() {
        this.value = null
    }

    constructor(value: Double) {
        this.value = value
        //        this.value =  value;
    }

    fun setMaxValue() {
        value = java.lang.Double.MAX_VALUE
        //value = Double.MAX_VALUE;
    }

    fun setValue(value: Double) {
        //        this.value =  value;
        this.value = value
    }

    /**
     * null is treated as the smallest element
     *
     * null null    0
     * null Y       -1
     * X    null    1
     * X    Y       X.compareTo(Y)
     *
     * @param o
     * @return
     */
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
                value!!.compareTo(o.value!!)
            }
        }
    }

    override fun toString(): String {
        return if (null != value) value!!.toString() else "null"
    }

    fun isLessThan(tmin: WrappedFloat): Boolean {
        return value!!.compareTo(tmin.value!!) == -1
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is WrappedFloat) {
            val wf = obj as WrappedFloat?
            val f = wf!!.value
            return if (value == null) {
                f == null
            } else {
                this.value == f
            }
        } else {
            return false
        }
    }

    companion object {

        fun createMax(): WrappedFloat {
            val f = WrappedFloat()
            f.setMaxValue()
            return f
        }
    }
}
