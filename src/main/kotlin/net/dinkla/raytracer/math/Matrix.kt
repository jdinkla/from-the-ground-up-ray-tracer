package net.dinkla.raytracer.math

class Matrix private constructor() {

    var m: Array<DoubleArray> = Array(4) { DoubleArray(4) }

    constructor(ls: List<Double>) : this() {
        val n = ls.size
        for (j in 0..3) {
            for (i in 0..3) {
                val index = (j * 4 + i) % n
                m[i][j] = ls[index]
            }
        }
    }

    operator fun get(i: Int, j: Int) = m[i][j]

    operator fun set(i: Int, j: Int, value: Double) {
        m[i][j] = value
    }

    operator fun plus(matrix: Matrix): Matrix {
        val result = Matrix()
        for (j in 0..3) {
            for (i in 0..3) {
                result.m[i][j] = m[i][j] + matrix.m[i][j]
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
                    sum += m[i][k] * matrix.m[k][j]
                }
                result.m[i][j] = sum
            }
        }
        return result
    }

    operator fun times(p: Point3D): Point3D {
        val x = m[0][0] * p.x + m[0][1] * p.y + m[0][2] * p.z + m[0][3]
        val y = m[1][0] * p.x + m[1][1] * p.y + m[1][2] * p.z + m[1][3]
        val z = m[2][0] * p.x + m[2][1] * p.y + m[2][2] * p.z + m[2][3]
        return Point3D(x, y, z)
    }

    operator fun times(v: Vector3D): Vector3D {
        val x = m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z
        val y = m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z
        val z = m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z
        return Vector3D(x, y, z)
    }

    // transformed m^t * n
    operator fun times(n: Normal): Normal {
        val x = m[0][0] * n.x + m[1][0] * n.y + m[2][0] * n.z
        val y = m[0][1] * n.x + m[1][1] * n.y + m[2][1] * n.z
        val z = m[0][2] * n.x + m[1][2] * n.y + m[2][2] * n.z
        return Normal(x, y, z)
    }

    operator fun div(value: Double): Matrix {
        val result = Matrix()
        for (j in 0..3) {
            for (i in 0..3) {
                result.m[i][j] = m[i][j] / value
            }
        }
        return result
    }

    fun setIdentity() {
        for (j in 0..3) {
            for (i in 0..3) {
                m[i][j] = if (i == j) 1.0 else 0.0
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

    override fun toString() = buildString {
        fun line(i: Int) = "${m[i][0]}, ${m[i][1]}, ${m[i][2]}, ${m[i][3]}   "
        append(line(0))
        append(line(1))
        append(line(2))
        append(line(3))
    }

    companion object {

        fun identity(): Matrix = Matrix().apply { setIdentity() }

        fun zero(): Matrix = Matrix()

    }
}
