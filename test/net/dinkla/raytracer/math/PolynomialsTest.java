package net.dinkla.raytracer.math;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 27.05.2010
 * Time: 21:14:03
 * To change this template use File | Settings | File Templates.
 */
public class PolynomialsTest {


    @Test
    public void testSolveQuadric() throws Exception {
        // A*x^2 + B*x + C = 0
        double[] s1 = { -16.0, 0.0, 1.0 };
        double[] sol = { 0.0, 0.0 };

        int num = Polynomials.INSTANCE.solveQuadric(s1, sol);

        assertEquals(2, num);
        assertEquals(4.0, sol[0]);
        assertEquals(-4.0, sol[1]);
    }

    @Test
    public void testSolveQuartic() throws Exception {

        // A*x^4 + B*x^3 + C*x^2+ D*x + E = 0
        double[] s1 = { 1.2, -3.2, 1.7, 2.5, -1.02};
        double[] sol = { 0.0, 0.0, 0.0, 0.0 };

        int num = Polynomials.INSTANCE.solveQuartic(s1, sol);

        System.out.println("num=" + num);
        for (double f : sol) {
            System.out.println("f=" + f);
        }
    }

    @Test
    public void testSolveCubic() throws Exception {

        // A*x^3 + B*x^2 + C*x + D = 0
        double[] s1 = { 0.0, 0.0, 0.0, 1.0 };
        double[] sol = { 0.0, 0.0, 0.0 };

        int num = Polynomials.INSTANCE.solveCubic(s1, sol);
        assertEquals(1, num);
        assertEquals(0.0, sol[0]);

        double[] s2 = { 8.0, 0.0, 0.0, 1.0 };
        num = Polynomials.INSTANCE.solveCubic(s2, sol);
        assertEquals(1, num);
        assertEquals(-2.0, sol[0]);

        double[] s3 = { -8.0, 0.0, 0.0, 1.0 };
        num = Polynomials.INSTANCE.solveCubic(s3, sol);
        assertEquals(1, num);
        assertEquals(2.0, sol[0]);

        double[] s4 = { 1.2, -3.2, 1.7, 2.5 };
        num = Polynomials.INSTANCE.solveCubic(s4, sol);
        assertEquals(1, num);
        assertEquals(-1.63938, sol[0], 0.00001);

        /*
        System.out.println("num=" + num);
        for (double f : sol) {
            System.out.println("f=" + f);
        }
        */
        
    }
}
