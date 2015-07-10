package net.dinkla.raytracer.math;

import static net.dinkla.raytracer.math.MathUtils.PI_ON_180;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 28.04.2010
 * Time: 22:32:53
 * To change this template use File | Settings | File Templates.
 */
public class AffineTransformation {

    public Matrix forwardMatrix;
    public Matrix invMatrix;

    public AffineTransformation() {
        forwardMatrix = new Matrix();
        invMatrix = new Matrix();
    }
    
    public void translate(Vector3DF v) {
        translate(v.x(), v.y(), v.z());
    }

    public void translate(final float x, final float y, final float z) {
        Matrix invTranslationMatrix = new Matrix();
        invTranslationMatrix.m[0][3] = - x;
        invTranslationMatrix.m[1][3] = - y;
        invTranslationMatrix.m[2][3] = - z;
        invMatrix = invMatrix.mult(invTranslationMatrix);

        Matrix translationMatrix = new Matrix();
        translationMatrix.m[0][3] = x;
        translationMatrix.m[1][3] = y;
        translationMatrix.m[2][3] = z;
        forwardMatrix = translationMatrix.mult(forwardMatrix);
    }

    public void scale(Vector3DF v) {
        scale(v.x(), v.y(), v.z());
    }

    public void scale(final float x, final float y, final float z) {
        Matrix invScalingMatrix = new Matrix();
        invScalingMatrix.m[0][0] = 1.0f / x;
        invScalingMatrix.m[1][1] = 1.0f / y;
        invScalingMatrix.m[2][2] = 1.0f / z;
        invMatrix = invMatrix.mult(invScalingMatrix);

        Matrix scalingMatrix = new Matrix();
        scalingMatrix.m[0][0] = x;
        scalingMatrix.m[1][1] = y;
        scalingMatrix.m[2][2] = z;
        forwardMatrix = scalingMatrix.mult(forwardMatrix);        
    }

    public void rotateX(final float phi) {
        final float cosPhi = (float) Math.cos(phi * PI_ON_180);
        final float sinPhi = (float) Math.sin(phi * PI_ON_180);

        Matrix invRotationMatrix = new Matrix();
        invRotationMatrix.m[1][1] = cosPhi;
        invRotationMatrix.m[1][2] = sinPhi;
        invRotationMatrix.m[2][1] = - sinPhi;
        invRotationMatrix.m[2][2] = cosPhi;
        invMatrix = invMatrix.mult(invRotationMatrix);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.m[1][1] = cosPhi;
        rotationMatrix.m[1][2] = - sinPhi;
        rotationMatrix.m[2][1] = sinPhi;
        rotationMatrix.m[2][2] = cosPhi;
        forwardMatrix = rotationMatrix.mult(forwardMatrix);
    }

    public void rotateY(final float phi) {
        final float cosPhi = (float) Math.cos(phi * PI_ON_180);
        final float sinPhi = (float) Math.sin(phi * PI_ON_180);

        Matrix invRotationMatrix = new Matrix();
        invRotationMatrix.m[2][2] = cosPhi;
        invRotationMatrix.m[0][2] = - sinPhi;
        invRotationMatrix.m[2][0] = sinPhi;
        invRotationMatrix.m[0][0] = cosPhi;
        invMatrix = invMatrix.mult(invRotationMatrix);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.m[2][2] = cosPhi;
        rotationMatrix.m[0][2] = sinPhi;
        rotationMatrix.m[2][0] = - sinPhi;
        rotationMatrix.m[0][0] = cosPhi;
        forwardMatrix = rotationMatrix.mult(forwardMatrix);
    }

    public void rotateZ(final float phi) {
        final float cosPhi = (float) Math.cos(phi * PI_ON_180);
        final float sinPhi = (float) Math.sin(phi * PI_ON_180);

        Matrix invRotationMatrix = new Matrix();
        invRotationMatrix.m[0][0] = cosPhi;
        invRotationMatrix.m[0][1] = sinPhi;
        invRotationMatrix.m[1][0] = - sinPhi;
        invRotationMatrix.m[1][1] = cosPhi;
        invMatrix = invMatrix.mult(invRotationMatrix);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.m[0][0] = cosPhi;
        rotationMatrix.m[0][1] = - sinPhi;
        rotationMatrix.m[1][0] = sinPhi;
        rotationMatrix.m[1][1] = cosPhi;
        forwardMatrix = rotationMatrix.mult(forwardMatrix);
    }

    public void shear(Matrix s) {
        Matrix invShearingMatrix = new Matrix();
        
        // discriminant
    	float d = 1.0f - s.m[1][0] * s.m[0][1]
                       - s.m[2][0] * s.m[0][2]
                       - s.m[2][1] * s.m[1][2]
					   + s.m[1][0] * s.m[2][1] * s.m[0][2]
                       + s.m[2][0] * s.m[0][1] * s.m[2][1];

	    // diagonals
        invShearingMatrix.m[0][0] = 1.0f - s.m[2][1] * s.m[1][2];
        invShearingMatrix.m[1][1] = 1.0f - s.m[2][0] * s.m[0][2];
        invShearingMatrix.m[2][2] = 1.0f - s.m[1][0] * s.m[0][1];
        invShearingMatrix.m[3][3] = d;

	    // first row
	    invShearingMatrix.m[0][1] = -s.m[1][0] + s.m[2][0] * s.m[1][2];
	    invShearingMatrix.m[0][2] = -s.m[2][0] + s.m[1][0] * s.m[2][1];

	    // second row
	    invShearingMatrix.m[1][0] = -s.m[0][1] + s.m[2][1] * s.m[0][2];
	    invShearingMatrix.m[1][2] = -s.m[2][1] + s.m[2][0] * s.m[0][1];

	    // third row
	    invShearingMatrix.m[2][0] = -s.m[0][2] + s.m[0][1] * s.m[1][2];
	    invShearingMatrix.m[2][1] = -s.m[1][2] + s.m[1][0] * s.m[0][2];

	    // divide by discriminant
	    invShearingMatrix = invShearingMatrix.div(d);

    	invMatrix = invMatrix.mult(invShearingMatrix);
	    forwardMatrix = s.mult(forwardMatrix); 
    }
    
}
