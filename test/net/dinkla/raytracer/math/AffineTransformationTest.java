package net.dinkla.raytracer.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 29.04.2010
 * Time: 20:56:03
 * To change this template use File | Settings | File Templates.
 */
public class AffineTransformationTest {

    private static final double DELTA = 0.001;

    //AffineTransformation t;
    //Vector3D v;
    Point3D p = new Point3D(1, 2, 3);

    @Test
    public void testTranslate() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.translate(new Vector3D(2, 3, 4));
        testT(t, p, new Point3D(-1, -1, -1), new Point3D(3, 5, 7));
    }

    @Test
    public void testScale() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.scale(1, 2, 3);
        testT(t, p, new Point3D(1, 1, 1), new Point3D(1, 4, 9));
    }

    @Test
    public void testRotateX() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.rotateX(90);
        testT(t, new Point3D(1, 1, 1), new Point3D(1, 1, -1), new Point3D(1, -1, 1));
    }

    @Test
    public void testRotateY() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.rotateY(90);
        testT(t, new Point3D(1, 1, 1), new Point3D(-1, 1, 1), new Point3D(1, 1, -1));
    }

    @Test
    public void testRotateZ() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.rotateZ(90);
        testT(t, new Point3D(1, 1, 1), new Point3D(1, -1, 1), new Point3D(-1, 1, 1));
    }

//    @Test
    public void testShear() throws Exception {
        AffineTransformation t = new AffineTransformation();
        Matrix m = new Matrix();
        m.getM()[1][1] = 2.34;
        t.shear(m);

        // TODO: shear funktioniert nicht wie erwartet
        assert(false);
        Point3D q = t.getInvMatrix().mult(p);
        System.out.println("q=" + q);
        System.out.println("inv=" + t.getInvMatrix());

        q = t.getForwardMatrix().mult(p);
        System.out.println("q=" + q);
        System.out.println("for=" + t.getForwardMatrix());

        //testT(t, new Point3D(1, 1, 1), new Point3D(1, -1, 1), new Point3D(-1, 1, 1));
    }

    private void assertEq(Point3D p, Point3D q) {
        assertEquals(p.getX(), q.getX(), DELTA); //, "x differs in " + p + " and " + q);
        assertEquals(p.getY(), q.getY(), DELTA); //, "y differs in " + p + " and " + q);
        assertEquals(p.getZ(), q.getZ(), DELTA); //, "z differs in " + p + " and " + q);
    }

    private void testT(AffineTransformation t, final Point3D p, final Point3D pInv, final Point3D pFor) {
        // inverse
        assertEq(pInv, t.getInvMatrix().mult(p));

        // forward
        assertEq(pFor, t.getForwardMatrix().mult(p));

        // composition yields identity
        assertEq(p, t.getInvMatrix().mult(t.getForwardMatrix().mult(p)));
        assertEq(p, t.getForwardMatrix().mult(t.getInvMatrix().mult(p)));
    }

}
