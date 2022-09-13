package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MatrixTest : AnnotationSpec()  {

    private val m1 = Matrix(listOf(1.0))
    private val m2 = Matrix(listOf(2.0))
    private val m3 = Matrix(listOf(3.0))

    @Test
    fun `construct from list`() {
        val value = 1.23
        val m = Matrix(listOf(value))
        m[0, 0] shouldBe value
        m[3, 3] shouldBe value
    }

    @Test
    fun `adding two matrices`() {
        m1 + m2 shouldBe m3
        m1 + m1 shouldNotBe m1
    }

    @Test
    fun `multiplication of two matrices`() {
        val e = Matrix.identity()
        e[0, 0] = 3.0
        e[0, 1] = 6.0
        e[0, 2] = 9.0

        e[1, 0] = 6.0
        e[1, 1] = 12.0
        e[1, 2] = 18.0

        e[2, 0] = 9.0
        e[2, 1] = 18.0
        e[2, 2] = 27.0

        val a = Matrix.identity()
        val b = Matrix.identity()

        for (j in 0..2) {
            for (i in 0..2) {
                a[i, j] = (i + 1).toDouble()
                b[i, j] = (j + 1).toDouble()
            }
        }
        a * b shouldBe e
    }

    @Test
    fun `multiplication with a Point3D`() {
        val r = 2.0 * 2 + 2 * 3 + 2 * 5 + 2
        val p = Point3D(2.0, 3.0, 5.0)
        m2 * p shouldBe Point3D(r, r, r)
    }

    @Test
    fun `multiplication with a Vector3D`() {
        val r = 2.0 * 2 + 2 * 3 + 2 * 5
        val p = Vector3D(2.0, 3.0, 5.0)
        m2 * p shouldBe Vector3D(r, r, r)
    }

    @Test
    fun `multiplication with a Normal`() {
        val r = 2.0 * 2 + 2 * 3 + 2 * 5
        val p = Normal(2.0, 3.0, 5.0)
        m2 * p shouldBe Normal(r, r, r)
    }

    @Test
    fun `indices`() {
        val indices = Matrix.indices()
        indices.size shouldBe Matrix.n * Matrix.n
        indices[0] shouldBe Pair(0,0)
        indices[Matrix.n * Matrix.n - 1] shouldBe Pair(Matrix.n - 1, Matrix.n - 1)
    }

    @Test
    fun `division with double value`() {
        val value = 1.5
        val m = m2 / value
        Matrix.indices().forEach { it ->
            m[it.first, it.second] shouldBe m2[it.first, it.second] / value
        }
    }

    @Test
    fun `create a matrix filled with zeros`() {
        val m = Matrix.zero()
        Matrix.indices().forEach { it ->
            m[it.first, it.second] shouldBe 0.0
        }
    }

}
