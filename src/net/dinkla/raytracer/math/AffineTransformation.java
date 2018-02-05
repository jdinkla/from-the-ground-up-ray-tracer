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
    
    public void translate(Vector3D v) {
        translate(v.getX(), v.getY(), v.getZ());
    }

    public void translate(final float x, final float y, final float z) {
        Matrix invTranslationMatrix = new Matrix();
        invTranslationMatrix.getM()[0][3] = - x;
        invTranslationMatrix.getM()[1][3] = - y;
        invTranslationMatrix.getM()[2][3] = - z;
        invMatrix = invMatrix.mult(invTranslationMatrix);

        Matrix translationMatrix = new Matrix();
        translationMatrix.getM()[0][3] = x;
        translationMatrix.getM()[1][3] = y;
        translationMatrix.getM()[2][3] = z;
        forwardMatrix = translationMatrix.mult(forwardMatrix);
    }

    public void scale(Vector3D v) {
        scale(v.getX(), v.getY(), v.getZ());
    }

    public void scale(final float x, final float y, final float z) {
        Matrix invScalingMatrix = new Matrix();
        invScalingMatrix.getM()[0][0] = 1.0f / x;
        invScalingMatrix.getM()[1][1] = 1.0f / y;
        invScalingMatrix.getM()[2][2] = 1.0f / z;
        invMatrix = invMatrix.mult(invScalingMatrix);

        Matrix scalingMatrix = new Matrix();
        scalingMatrix.getM()[0][0] = x;
        scalingMatrix.getM()[1][1] = y;
        scalingMatrix.getM()[2][2] = z;
        forwardMatrix = scalingMatrix.mult(forwardMatrix);        
    }

    public void rotateX(final float phi) {
        final float cosPhi = (float) Math.cos(phi * PI_ON_180);
        final float sinPhi = (float) Math.sin(phi * PI_ON_180);

        Matrix invRotationMatrix = new Matrix();
        invRotationMatrix.getM()[1][1] = cosPhi;
        invRotationMatrix.getM()[1][2] = sinPhi;
        invRotationMatrix.getM()[2][1] = - sinPhi;
        invRotationMatrix.getM()[2][2] = cosPhi;
        invMatrix = invMatrix.mult(invRotationMatrix);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.getM()[1][1] = cosPhi;
        rotationMatrix.getM()[1][2] = - sinPhi;
        rotationMatrix.getM()[2][1] = sinPhi;
        rotationMatrix.getM()[2][2] = cosPhi;
        forwardMatrix = rotationMatrix.mult(forwardMatrix);
    }

    public void rotateY(final float phi) {
        final float cosPhi = (float) Math.cos(phi * PI_ON_180);
        final float sinPhi = (float) Math.sin(phi * PI_ON_180);

        Matrix invRotationMatrix = new Matrix();
        invRotationMatrix.getM()[2][2] = cosPhi;
        invRotationMatrix.getM()[0][2] = - sinPhi;
        invRotationMatrix.getM()[2][0] = sinPhi;
        invRotationMatrix.getM()[0][0] = cosPhi;
        invMatrix = invMatrix.mult(invRotationMatrix);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.getM()[2][2] = cosPhi;
        rotationMatrix.getM()[0][2] = sinPhi;
        rotationMatrix.getM()[2][0] = - sinPhi;
        rotationMatrix.getM()[0][0] = cosPhi;
        forwardMatrix = rotationMatrix.mult(forwardMatrix);
    }

    public void rotateZ(final float phi) {
        final float cosPhi = (float) Math.cos(phi * PI_ON_180);
        final float sinPhi = (float) Math.sin(phi * PI_ON_180);

        Matrix invRotationMatrix = new Matrix();
        invRotationMatrix.getM()[0][0] = cosPhi;
        invRotationMatrix.getM()[0][1] = sinPhi;
        invRotationMatrix.getM()[1][0] = - sinPhi;
        invRotationMatrix.getM()[1][1] = cosPhi;
        invMatrix = invMatrix.mult(invRotationMatrix);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.getM()[0][0] = cosPhi;
        rotationMatrix.getM()[0][1] = - sinPhi;
        rotationMatrix.getM()[1][0] = sinPhi;
        rotationMatrix.getM()[1][1] = cosPhi;
        forwardMatrix = rotationMatrix.mult(forwardMatrix);
    }

    public void shear(Matrix s) {
        Matrix invShearingMatrix = new Matrix();
        
        // discriminant
    	float d = 1.0f - s.getM()[1][0] * s.getM()[0][1]
                       - s.getM()[2][0] * s.getM()[0][2]
                       - s.getM()[2][1] * s.getM()[1][2]
					   + s.getM()[1][0] * s.getM()[2][1] * s.getM()[0][2]
                       + s.getM()[2][0] * s.getM()[0][1] * s.getM()[2][1];

	    // diagonals
        invShearingMatrix.getM()[0][0] = 1.0f - s.getM()[2][1] * s.getM()[1][2];
        invShearingMatrix.getM()[1][1] = 1.0f - s.getM()[2][0] * s.getM()[0][2];
        invShearingMatrix.getM()[2][2] = 1.0f - s.getM()[1][0] * s.getM()[0][1];
        invShearingMatrix.getM()[3][3] = d;

	    // first row
	    invShearingMatrix.getM()[0][1] = -s.getM()[1][0] + s.getM()[2][0] * s.getM()[1][2];
	    invShearingMatrix.getM()[0][2] = -s.getM()[2][0] + s.getM()[1][0] * s.getM()[2][1];

	    // second row
	    invShearingMatrix.getM()[1][0] = -s.getM()[0][1] + s.getM()[2][1] * s.getM()[0][2];
	    invShearingMatrix.getM()[1][2] = -s.getM()[2][1] + s.getM()[2][0] * s.getM()[0][1];

	    // third row
	    invShearingMatrix.getM()[2][0] = -s.getM()[0][2] + s.getM()[0][1] * s.getM()[1][2];
	    invShearingMatrix.getM()[2][1] = -s.getM()[1][2] + s.getM()[1][0] * s.getM()[0][2];

	    // divide by discriminant
	    invShearingMatrix = invShearingMatrix.div(d);

    	invMatrix = invMatrix.mult(invShearingMatrix);
	    forwardMatrix = s.mult(forwardMatrix); 
    }
    
}
