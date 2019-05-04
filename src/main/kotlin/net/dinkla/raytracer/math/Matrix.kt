package net.dinkla.raytracer.math

import java.util.Arrays


class Matrix private constructor() {

    private var m: DoubleArray = DoubleArray(4 * 4)

    operator fun get(i: Int, j: Int) = m[index(i, j)]

    operator fun set(i: Int, j: Int, value: Double) {
        m[index(i, j)] = value
    }

    constructor(ls: List<Double>) : this() {
        val n = ls.size
        for (j in 0..3) {
            for (i in 0..3) {
                this[i, j] = ls[index(i, j) % n]
            }
        }
    }

    operator fun plus(matrix: Matrix): Matrix {
        val result = Matrix()
        for (j in 0..3) {
            for (i in 0..3) {
                val idx = index(i, j)
                result.m[idx] = m[idx] + matrix.m[idx]
            }
        }
        return result
    }

    operator fun times(matrix: Matrix): Matrix {
        val result = Matrix()
        for (j in 0..3) {
            for (i in 0..3) {
                var sum = 0.0
                for (k in 0..3) {
                    sum += m[i, k] * matrix.m[k, j]
                }
                result.m[i, j] = sum
            }
        }
        return result
    }

    operator fun times(p: Point3D): Point3D {
        fun add(i: Int) =m[i, 0] * p.x + m[i, 1] * p.y + m[i, 2] * p.z + m[i, 3]
        return Point3D(add(0), add(1), add(2))
    }

    operator fun times(v: Vector3D): Vector3D {
        fun add(i: Int) =m[i, 0] * v.x + m[i, 1] * v.y + m[i, 2] * v.z
        return Vector3D(add(0), add(1), add(2))
    }

    // transformed m^t * n
    operator fun times(n: Normal): Normal {
        fun add(i: Int) =m[i, 0] * n.x + m[i, 1] * n.y + m[i, 2] * n.z
        return Normal(add(0), add(1), add(2))
    }

    operator fun div(value: Double): Matrix {
        val result = Matrix()
        for (j in 0..3) {
            for (i in 0..3) {
                result[i, j] = m[i, j] / value
            }
        }
        return result
    }

    fun setIdentity() {
        for (j in 0..3) {
            for (i in 0..3) {
                this[i, j] = if (i == j) 1.0 else 0.0
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (null == other || other !is Matrix) {
            return false
        } else {
            for (y in 0..3) {
                for (x in 0..3) {
                    if (this[x, y] != other[x, y]) {
                        return false
                    }
                }
            }
            return true
        }
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(m)
    }

    override fun toString() = buildString {
        fun line(i: Int) = "${m[i, 0]}, ${m[i, 1]}, ${m[i, 2]}, ${m[i, 3]}   "
        append(line(0))
        append(line(1))
        append(line(2))
        append(line(3))
    }

    companion object {

        fun identity(): Matrix = Matrix().apply { setIdentity() }

        fun zero(): Matrix = Matrix()

        fun index(i: Int, j: Int) = 4 * i + j

        operator fun DoubleArray.get(i: Int, j: Int): Double = this[index(i, j)]

        operator fun DoubleArray.set(i: Int, j: Int, value: Double) {
            this[index(i, j)] = value
        }
    }
}
