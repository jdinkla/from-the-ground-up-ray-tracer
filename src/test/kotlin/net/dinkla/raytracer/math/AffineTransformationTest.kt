package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

class AffineTransformationTest {

    //AffineTransformation t;
    //Vector3D v;
    internal var p = Point3D(1.0, 2.0, 3.0)

    @Test
    @Throws(Exception::class)
    fun testTranslate() {
        val t = AffineTransformation()
        t.translate(Vector3D(2.0, 3.0, 4.0))
        testT(t, p, Point3D(-1.0, -1.0, -1.0), Point3D(3.0, 5.0, 7.0))
    }

    @Test
    @Throws(Exception::class)
    fun testScale() {
        val t = AffineTransformation()
        t.scale(1.0, 2.0, 3.0)
        testT(t, p, Point3D(1.0, 1.0, 1.0), Point3D(1.0, 4.0, 9.0))
    }

    @Test
    @Throws(Exception::class)
    fun testRotateX() {
        val t = AffineTransformation()
        t.rotateX(90.0)
        testT(t, Point3D(1.0, 1.0, 1.0), Point3D(1.0, 1.0, -1.0), Point3D(1.0, -1.0, 1.0))
    }

    @Test
    @Throws(Exception::class)
    fun testRotateY() {
        val t = AffineTransformation()
        t.rotateY(90.0)
        testT(t, Point3D(1.0, 1.0, 1.0), Point3D(-1.0, 1.0, 1.0), Point3D(1.0, 1.0, -1.0))
    }

    @Test
    @Throws(Exception::class)
    fun testRotateZ() {
        val t = AffineTransformation()
        t.rotateZ(90.0)
        testT(t, Point3D(1.0, 1.0, 1.0), Point3D(1.0, -1.0, 1.0), Point3D(-1.0, 1.0, 1.0))
    }

    //    @Test
    @Throws(Exception::class)
    fun testShear() {
        val t = AffineTransformation()
        val m = Matrix.identity()
        m.m[1][1] = 2.34
        t.shear(m)

        // TODO: shear funktioniert nicht wie erwartet
        assert(false)
        var q = t.invMatrix.times(p)
        println("q=" + q)
        println("inv=" + t.invMatrix)

        q = t.forwardMatrix.times(p)
        println("q=" + q)
        println("for=" + t.forwardMatrix)

        //testT(t, new Point3D(1, 1, 1), new Point3D(1, -1, 1), new Point3D(-1, 1, 1));
    }

    private fun assertEq(p: Point3D, q: Point3D) {
        assertEquals(p.x, q.x, DELTA) //, "x differs in " + p + " and " + q);
        assertEquals(p.y, q.y, DELTA) //, "y differs in " + p + " and " + q);
        assertEquals(p.z, q.z, DELTA) //, "z differs in " + p + " and " + q);
    }

    private fun testT(t: AffineTransformation, p: Point3D, pInv: Point3D, pFor: Point3D) {
        // inverse
        assertEq(pInv, t.invMatrix.times(p))

        // forward
        assertEq(pFor, t.forwardMatrix.times(p))

        // composition yields identity
        assertEq(p, t.invMatrix.times(t.forwardMatrix.times(p)))
        assertEq(p, t.forwardMatrix.times(t.invMatrix.times(p)))
    }

    companion object {

        private val DELTA = 0.001
    }

}
