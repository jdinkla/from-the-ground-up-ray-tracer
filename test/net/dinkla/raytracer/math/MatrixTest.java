package net.dinkla.raytracer.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 24.04.2010
 * Time: 18:24:16
 * To change this template use File | Settings | File Templates.
 */
public class MatrixTest {

    @Test
    public void testMatrixMult() {

        Matrix e = new Matrix();
        e.getM()[0][0] = 3;
        e.getM()[0][1] = 6;
        e.getM()[0][2] = 9;

        e.getM()[1][0] = 6;
        e.getM()[1][1] = 12;
        e.getM()[1][2] = 18;

        e.getM()[2][0] = 9;
        e.getM()[2][1] = 18;
        e.getM()[2][2] = 27;

        Matrix a = new Matrix();
        Matrix b = new Matrix();
        
        for (int j=0; j<3; j++) {
            for (int i=0; i<3; i++) {
                a.getM()[i][j] = i+1;
                b.getM()[i][j] = j+1;
            }
        }

        Matrix c = a.mult(b);
        assertEquals(e, c);
    }
}
