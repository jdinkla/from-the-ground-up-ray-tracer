package net.dinkla.raytracer.math

class Matrix {

    var m: Array<DoubleArray>

    init {
        m = Array(4) { DoubleArray(4) }
        setIdentity()
    }

    fun mult(matrix: Matrix): Matrix {
        val product = Matrix()
        for (j in 0..3) {
            for (i in 0..3) {
                var sum = 0.0
                for (k in 0..3) {
                    sum += m[i][k] * matrix.m[k][j]
                }
                product.m[i][j] = sum
            }
        }
        return product
    }

    fun mult(p: Point3D): Point3D {
        val x = m[0][0] * p.x + m[0][1] * p.y + m[0][2] * p.z + m[0][3]
        val y = m[1][0] * p.x + m[1][1] * p.y + m[1][2] * p.z + m[1][3]
        val z = m[2][0] * p.x + m[2][1] * p.y + m[2][2] * p.z + m[2][3]
        return Point3D(x, y, z)
    }

    fun mult(v: Vector3D): Vector3D {
        val x = m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z
        val y = m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z
        val z = m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z
        return Vector3D(x, y, z)
    }

    // transformed m^t * n
    fun mult(n: Normal): Normal {
        val x = m[0][0] * n.x + m[1][0] * n.y + m[2][0] * n.z
        val y = m[0][1] * n.x + m[1][1] * n.y + m[2][1] * n.z
        val z = m[0][2] * n.x + m[1][2] * n.y + m[2][2] * n.z
        return Normal(x, y, z)
    }

    operator fun div(f: Double): Matrix {
        val result = Matrix()
        for (j in 0..3) {
            for (i in 0..3) {
                result.m[i][j] = m[i][j] / f
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
        if (other is Matrix) {
            val mat = other as Matrix?
            for (y in 0..3) {
                for (x in 0..3) {
                    if (m[x][y] != mat!!.m[x][y]) {
                        return false
                    }
                }
            }
            return true
        } else {
            return false
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(m[0][0].toString() + ", " + m[0][1] + ", " + m[0][2] + ", " + m[0][3] + "   ")
        sb.append(m[1][0].toString() + ", " + m[1][1] + ", " + m[1][2] + ", " + m[1][3] + "   ")
        sb.append(m[2][0].toString() + ", " + m[2][1] + ", " + m[2][2] + ", " + m[2][3] + "   ")
        sb.append(m[3][0].toString() + ", " + m[3][1] + ", " + m[3][2] + ", " + m[3][3])
        return sb.toString()
    }
}
