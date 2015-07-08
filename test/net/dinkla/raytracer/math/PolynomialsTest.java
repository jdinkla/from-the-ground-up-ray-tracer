package net.dinkla.raytracer.math;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

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
        float[] s1 = { -16.0f, 0.0f, 1.0f };
        float[] sol = { 0.0f, 0.0f };

        int num = Polynomials.solveQuadric(s1, sol);

        assertEquals(2, num);
        assertEquals(4.0f, sol[0]);
        assertEquals(-4.0f, sol[1]);
    }

    @Test
    public void testSolveQuartic() throws Exception {

        // A*x^4 + B*x^3 + C*x^2+ D*x + E = 0
        float[] s1 = { 1.2f, -3.2f, 1.7f, 2.5f, -1.02f};
        float[] sol = { 0.0f, 0.0f, 0.0f, 0.0f };

        int num = Polynomials.solveQuartic(s1, sol);

        System.out.println("num=" + num);
        for (float f : sol) {
            System.out.println("f=" + f);
        }
    }

    @Test
    public void testSolveCubic() throws Exception {

        // A*x^3 + B*x^2 + C*x + D = 0
        float[] s1 = { 0.0f, 0.0f, 0.0f, 1.0f };
        float[] sol = { 0.0f, 0.0f, 0.0f };

        int num = Polynomials.solveCubic(s1, sol);
        assertEquals(1, num);
        assertEquals(0.0f, sol[0]);

        float[] s2 = { 8.0f, 0.0f, 0.0f, 1.0f };
        num = Polynomials.solveCubic(s2, sol);
        assertEquals(1, num);
        assertEquals(-2.0f, sol[0]);

        float[] s3 = { -8.0f, 0.0f, 0.0f, 1.0f };
        num = Polynomials.solveCubic(s3, sol);
        assertEquals(1, num);
        assertEquals(2.0f, sol[0]);

        float[] s4 = { 1.2f, -3.2f, 1.7f, 2.5f };
        num = Polynomials.solveCubic(s4, sol);
        assertEquals(1, num);
        assertEquals(-1.63938f, sol[0], 0.00001f);

        /*
        System.out.println("num=" + num);
        for (float f : sol) {
            System.out.println("f=" + f);
        }
        */
        
    }
}
