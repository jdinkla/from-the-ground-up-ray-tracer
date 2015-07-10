package net.dinkla.raytracer.math;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 29.04.2010
 * Time: 20:56:03
 * To change this template use File | Settings | File Templates.
 */
public class AffineTransformationTest {

    private static final float DELTA = 0.001f;

    //AffineTransformation t;
    //Vector3DF v;
    Point3DF p = new Point3DF(1, 2, 3);

    @Test
    public void testTranslate() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.translate(new Vector3DF(2, 3, 4));
        testT(t, p, new Point3DF(-1, -1, -1), new Point3DF(3, 5, 7));
    }

    @Test
    public void testScale() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.scale(1, 2, 3);
        testT(t, p, new Point3DF(1, 1, 1), new Point3DF(1, 4, 9));
    }

    @Test
    public void testRotateX() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.rotateX(90);
        testT(t, new Point3DF(1, 1, 1), new Point3DF(1, 1, -1), new Point3DF(1, -1, 1));
    }

    @Test
    public void testRotateY() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.rotateY(90);
        testT(t, new Point3DF(1, 1, 1), new Point3DF(-1, 1, 1), new Point3DF(1, 1, -1));
    }

    @Test
    public void testRotateZ() throws Exception {
        AffineTransformation t = new AffineTransformation();
        t.rotateZ(90);
        testT(t, new Point3DF(1, 1, 1), new Point3DF(1, -1, 1), new Point3DF(-1, 1, 1));
    }

    @Test
    public void testShear() throws Exception {
        AffineTransformation t = new AffineTransformation();
        Matrix m = new Matrix();
        m.m[1][1] = 2.34f;
        t.shear(m);

        // TODO: shear funktioniert nicht wie erwartet
        assert(false);
        Point3DF q = t.invMatrix.mult(p);
        System.out.println("q=" + q);
        System.out.println("inv=" + t.invMatrix);

        q = t.forwardMatrix.mult(p);
        System.out.println("q=" + q);
        System.out.println("for=" + t.forwardMatrix);

        //testT(t, new Point3DF(1, 1, 1), new Point3DF(1, -1, 1), new Point3DF(-1, 1, 1));
    }

    private void assertEq(Point3DF p, Point3DF q) {
        assertEquals(p.x, q.x, DELTA, "x differs in " + p + " and " + q);
        assertEquals(p.y, q.y, DELTA, "y differs in " + p + " and " + q);
        assertEquals(p.z, q.z, DELTA, "z differs in " + p + " and " + q);       
    }

    private void testT(AffineTransformation t, final Point3DF p, final Point3DF pInv, final Point3DF pFor) {
        // inverse
        assertEq(pInv, t.invMatrix.mult(p));

        // forward
        assertEq(pFor, t.forwardMatrix.mult(p));

        // composition yields identity
        assertEq(p, t.invMatrix.mult(t.forwardMatrix.mult(p)));
        assertEq(p, t.forwardMatrix.mult(t.invMatrix.mult(p)));
    }

}
