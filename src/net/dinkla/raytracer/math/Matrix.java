package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:18:05
 * To change this template use File | Settings | File Templates.
 */
public class Matrix {

    public float[][] m;

    public Matrix() {
        m = new float[4][4];
        setIdentity();
    }

    public Matrix mult(Matrix matrix) {
        Matrix product = new Matrix();
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                float sum = 0.0f;
                for (int k = 0; k < 4; k++) {
                    sum += m[i][k] * matrix.m[k][j];
                }
                product.m[i][j] = sum;
            }
        }
        return product;
    }

    public Point3D mult(Point3D p) {
        final float x = m[0][0] * p.x + m[0][1] * p.y + m[0][2] * p.z + m[0][3];
        final float y = m[1][0] * p.x + m[1][1] * p.y + m[1][2] * p.z + m[1][3];
        final float z = m[2][0] * p.x + m[2][1] * p.y + m[2][2] * p.z + m[2][3];
        return new Point3D(x, y, z);
    }

    public Vector3D mult(Vector3D v) {
        final float x = m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z;
        final float y = m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z;
        final float z = m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z;
        return new Vector3D(x, y, z);
    }

    // transformed m^t * n 
    public Normal mult(Normal n) {
        final float x = m[0][0] * n.x + m[1][0] * n.y + m[2][0] * n.z;
        final float y = m[0][1] * n.x + m[1][1] * n.y + m[2][1] * n.z;
        final float z = m[0][2] * n.x + m[1][2] * n.y + m[2][2] * n.z;
        return new Normal(x, y, z);
    }

    public Matrix div(final float f) {
        Matrix result = new Matrix();
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                result.m[i][j] = m[i][j] / f;
            }
        }
        return result;
    }

    public void setIdentity() {
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                m[i][j] = i == j ? 1.0f : 0.0f;
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Matrix) {
            Matrix mat = (Matrix) obj;
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    if (m[x][y] != mat.m[x][y]) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(m[0][0] + ", " + m[0][1] + ", " + m[0][2] + ", " + m[0][3] + "   ");
        sb.append(m[1][0] + ", " + m[1][1] + ", " + m[1][2] + ", " + m[1][3] + "   ");
        sb.append(m[2][0] + ", " + m[2][1] + ", " + m[2][2] + ", " + m[2][3] + "   ");
        sb.append(m[3][0] + ", " + m[3][1] + ", " + m[3][2] + ", " + m[3][3]);
        return sb.toString();
    }
}
