package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 05.05.2010
 * Time: 21:20:07
 * To change this template use File | Settings | File Templates.
 */
public class Polynomials {
    
    public static int solveQuadric(float[] c, float[] s) {
        assert(c.length == 3);
        assert(s.length == 2);

        /* normal form: x^2 + px + q = 0 */
        final float p = c[1] / (2 * c[2]);
        final float q = c[0] / c[2];
        final float D = p * p - q;

        if (MathUtils.isZero(D)) {
            s[0] = - p;
            return 1;
        } else if (D > 0) {
            float sqrtD = (float) Math.sqrt(D);
            s[0] =   sqrtD - p;
            s[1] = - sqrtD - p;
            return 2;
        } else {
            return 0;
        }
    }

    public static int solveQuartic(float[] c, float[] s) {
        assert (c.length == 5);
        assert (s.length == 4);

        float coeffs4[] = new float[4];
        float coeffs3[] = new float[3];
        float z, u, v, sub;
        float A, B, C, D;
        float sq_A, p, q, r;
        int i, num;

        /* normal form: x^4 + Ax^3 + Bx^2 + Cx + D = 0 */
        A = c[3] / c[4];
        B = c[2] / c[4];
        C = c[1] / c[4];
        D = c[0] / c[4];

        /*  substitute x = y - A/4 to eliminate cubic term:
            x^4 + px^2 + qx + r = 0 */
        sq_A = A * A;
        p = -3.0f / 8 * sq_A + B;
        q = 1.0f / 8 * sq_A * A - 1.0f / 2 * A * B + C;
        r = -3.0f / 256 * sq_A * sq_A + 1.0f / 16 * sq_A * B - 1.0f / 4 * A * C + D;

        if (MathUtils.isZero(r)) {
            /* no absolute term: y(y^3 + py + q) = 0 */
            coeffs4[0] = q;
            coeffs4[1] = p;
            coeffs4[2] = 0;
            coeffs4[3] = 1;
            float[] ss0 = { s[0], s[1], s[2] };
            num = solveCubic(coeffs4, ss0);
            s[0] = ss0[0];
            s[1] = ss0[1];
            s[2] = ss0[2];
            s[num++] = 0;
        } else {
            /* solve the resolvent cubic ... */
            coeffs4[0] = 1.0f / 2 * r * p - 1.0f / 8 * q * q;
            coeffs4[1] = -r;
            coeffs4[2] = -1.0f / 2 * p;
            coeffs4[3] = 1;

            float[] ss1 = { s[0], s[1], s[2] };
            solveCubic(coeffs4, ss1);
            s[0] = ss1[0];
            s[1] = ss1[1];
            s[2] = ss1[2];

            /* ... and take the one real solution ... */
            z = s[0];

            /* ... to build two quadric equations */
            u = z * z - r;
            v = 2 * z - p;

            if (MathUtils.isZero(u)) {
                u = 0;
            } else if (u > 0) {
                u = (float) Math.sqrt(u);
            } else {
                return 0;
            }
            if (MathUtils.isZero(v)) {
                v = 0;
            } else if (v > 0) {
                v = (float) Math.sqrt(v);
            } else {
                return 0;
            }
            coeffs3[0] = z - u;
            coeffs3[1] = q < 0 ? -v : v;
            coeffs3[2] = 1;

            float[] ss2 = { s[0], s[1] };
            num = solveQuadric(coeffs3, ss2);
            s[0] = ss2[0];
            s[1] = ss2[1];

            coeffs3[0] = z + u;
            coeffs3[1] = q < 0 ? v : -v;
            coeffs3[2] = 1;

            float[] ss3 = { s[0 + num], s[1 + num] };
            num += Polynomials.solveQuadric(coeffs3, ss3);
            s[0] = ss3[0];
            s[1] = ss3[1];

        }

        /* resubstitute */
        sub = 1.0f / 4 * A;

        for (i = 0; i < num; ++i) {
            s[i] -= sub;
        }
        return num;
    }

    public static int solveCubic(float[] c, float[] s) {
        assert(c.length == 4);
        assert(s.length == 3);

        int     i, num;
        float  sub;
        float  A, B, C;
        float  sq_A, p, q;
        float  cb_p, D;

        /* normal form: x^3 + Ax^2 + Bx + C = 0 */
        A = c[ 2 ] / c[ 3 ];
        B = c[ 1 ] / c[ 3 ];
        C = c[ 0 ] / c[ 3 ];

        /*  substitute x = y - A/3 to eliminate quadric term: x^3 +px + q = 0 */
        sq_A = A * A;
        p = 1.0f/3 * (- 1.0f/3 * sq_A + B);
        q = 1.0f/2 * (2.0f/27 * A * sq_A - 1.0f/3 * A * B + C);

        /* use Cardano's formula */
        cb_p = p * p * p;
        D = q * q + cb_p;

        if (MathUtils.isZero(D)) {
            if (MathUtils.isZero(q)) { /* one triple solution */
                s[ 0 ] = 0;
                num = 1;
            } else { /* one single and one double solution */
                float u = (float) Math.cbrt(-q);
                s[ 0 ] = 2 * u;
                s[ 1 ] = - u;
                num = 2;
            }
        } else if (D < 0) { /* Casus irreducibilis: three real solutions */
            float phi = 1.0f/3 * (float) Math.acos(-q / (float) Math.sqrt(-cb_p));
            float t = 2 * (float) Math.sqrt(-p);

            s[ 0 ] =   t * (float) Math.cos(phi);
            s[ 1 ] = - t * (float) Math.cos(phi + Math.PI / 3);
            s[ 2 ] = - t * (float) Math.cos(phi - Math.PI / 3);

            num = 3;
        } else { /* one real solution */
            float sqrt_D = (float) Math.sqrt(D);
            float u = (float) Math.cbrt(sqrt_D - q);
            float v = - (float) Math.cbrt(sqrt_D + q);
            s[ 0 ] = u + v;
            num = 1;
        }

        /* resubstitute */
        sub = 1.0f/3 * A;
        for (i = 0; i < num; ++i) {
            s[ i ] -= sub;
        }
        return num;
    }

    public static int solveQuadric(double[] c, double[] s) {
        assert(c.length == 3);
        assert(s.length == 2);

        /* normal form: x^2 + px + q = 0 */
        final double p = c[1] / (2 * c[2]);
        final double q = c[0] / c[2];
        final double D = p * p - q;

        if (MathUtils.isZero(D)) {
            s[0] = - p;
            return 1;
        } else if (D > 0) {
            double sqrtD = (float) Math.sqrt(D);
            s[0] =   sqrtD - p;
            s[1] = - sqrtD - p;
            return 2;
        } else {
            return 0;
        }
    }

    public static int solveQuartic(double[] c, double[] s) {
        assert (c.length == 5);
        assert (s.length == 4);

        double coeffs4[] = new double[4];
        double coeffs3[] = new double[3];
        double z, u, v, sub;
        double A, B, C, D;
        double sq_A, p, q, r;
        int i, num;

        /* normal form: x^4 + Ax^3 + Bx^2 + Cx + D = 0 */
        A = c[3] / c[4];
        B = c[2] / c[4];
        C = c[1] / c[4];
        D = c[0] / c[4];

        /*  substitute x = y - A/4 to eliminate cubic term:
            x^4 + px^2 + qx + r = 0 */
        sq_A = A * A;
        p = -3.0f / 8 * sq_A + B;
        q = 1.0f / 8 * sq_A * A - 1.0f / 2 * A * B + C;
        r = -3.0f / 256 * sq_A * sq_A + 1.0f / 16 * sq_A * B - 1.0f / 4 * A * C + D;

        if (MathUtils.isZero(r)) {
            /* no absolute term: y(y^3 + py + q) = 0 */
            coeffs4[0] = q;
            coeffs4[1] = p;
            coeffs4[2] = 0;
            coeffs4[3] = 1;
            double[] ss = { s[0], s[1], s[2] };
            num = solveCubic(coeffs4, ss);
            s[0] = ss[0];
            s[1] = ss[1];
            s[2] = ss[2];
            s[num++] = 0;
        } else {
            /* solve the resolvent cubic ... */
            coeffs4[0] = 1.0f / 2 * r * p - 1.0f / 8 * q * q;
            coeffs4[1] = -r;
            coeffs4[2] = -1.0f / 2 * p;
            coeffs4[3] = 1;

            double[] ss = { s[0], s[1], s[2] };
            solveCubic(coeffs4, ss);
            s[0] = ss[0];
            s[1] = ss[1];
            s[2] = ss[2];

            /* ... and take the one real solution ... */
            z = s[0];

            /* ... to build two quadric equations */
            u = z * z - r;
            v = 2 * z - p;

            if (MathUtils.isZero(u)) {
                u = 0;
            } else if (u > 0) {
                u = (float) Math.sqrt(u);
            } else {
                return 0;
            }
            if (MathUtils.isZero(v)) {
                v = 0;
            } else if (v > 0) {
                v = (float) Math.sqrt(v);
            } else {
                return 0;
            }
            coeffs3[0] = z - u;
            coeffs3[1] = q < 0 ? -v : v;
            coeffs3[2] = 1;

            double[] ss2 = { s[0], s[1] };
            num = solveQuadric(coeffs3, ss2);
            s[0] = ss2[0];
            s[1] = ss2[1];

            coeffs3[0] = z + u;
            coeffs3[1] = q < 0 ? v : -v;
            coeffs3[2] = 1;

            // TODO: Was heißt s+ num
//            float[] ss3 = { s[0 + num], s[1 + num] };
            double[] ss3 = { s[0 + num], s[1 + num] };
            num += Polynomials.solveQuadric(coeffs3, ss3);
            s[0] = ss3[0];
            s[1] = ss3[1];

        }

        /* resubstitute */
        sub = 1.0f / 4 * A;

        for (i = 0; i < num; ++i) {
            s[i] -= sub;
        }
        return num;
    }

    public static int solveCubic(double[] c, double[] s) {
        assert(c.length == 4);
        assert(s.length == 3);

        int     i, num;
        double  sub;
        double  A, B, C;
        double  sq_A, p, q;
        double  cb_p, D;

        /* normal form: x^3 + Ax^2 + Bx + C = 0 */
        A = c[ 2 ] / c[ 3 ];
        B = c[ 1 ] / c[ 3 ];
        C = c[ 0 ] / c[ 3 ];

        /*  substitute x = y - A/3 to eliminate quadric term: x^3 +px + q = 0 */
        sq_A = A * A;
        p = 1.0f/3 * (- 1.0f/3 * sq_A + B);
        q = 1.0f/2 * (2.0f/27 * A * sq_A - 1.0f/3 * A * B + C);

        /* use Cardano's formula */
        cb_p = p * p * p;
        D = q * q + cb_p;

        if (MathUtils.isZero(D)) {
            if (MathUtils.isZero(q)) { /* one triple solution */
                s[ 0 ] = 0;
                num = 1;
            } else { /* one single and one double solution */
                double u = Math.cbrt(-q);
                s[ 0 ] = 2 * u;
                s[ 1 ] = - u;
                num = 2;
            }
        } else if (D < 0) { /* Casus irreducibilis: three real solutions */
            double phi = 1.0f/3 * Math.acos(-q / Math.sqrt(-cb_p));
            double t = 2 * Math.sqrt(-p);

            s[ 0 ] =   t * Math.cos(phi);
            s[ 1 ] = - t * Math.cos(phi + Math.PI / 3);
            s[ 2 ] = - t * Math.cos(phi - Math.PI / 3);

            num = 3;
        } else { /* one real solution */
            double sqrt_D = Math.sqrt(D);
            double u = Math.cbrt(sqrt_D - q);
            double v = - Math.cbrt(sqrt_D + q);
            s[ 0 ] = u + v;
            num = 1;
        }

        /* resubstitute */
        sub = 1.0f/3 * A;
        for (i = 0; i < num; ++i) {
            s[ i ] -= sub;
        }
        return num;
    }

}
