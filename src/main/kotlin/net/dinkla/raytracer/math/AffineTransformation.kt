package net.dinkla.raytracer.math

import net.dinkla.raytracer.math.MathUtils.PI_ON_180

class AffineTransformation {

    var forwardMatrix: Matrix
    var invMatrix: Matrix

    init {
        forwardMatrix = Matrix()
        invMatrix = Matrix()
    }

    fun translate(v: Vector3D) {
        translate(v.x, v.y, v.z)
    }

    fun translate(x: Double, y: Double, z: Double) {
        val invTranslationMatrix = Matrix()
        invTranslationMatrix.m[0][3] = -x
        invTranslationMatrix.m[1][3] = -y
        invTranslationMatrix.m[2][3] = -z
        invMatrix = invMatrix.mult(invTranslationMatrix)

        val translationMatrix = Matrix()
        translationMatrix.m[0][3] = x
        translationMatrix.m[1][3] = y
        translationMatrix.m[2][3] = z
        forwardMatrix = translationMatrix.mult(forwardMatrix)
    }

    fun scale(v: Vector3D) {
        scale(v.x, v.y, v.z)
    }

    fun scale(x: Double, y: Double, z: Double) {
        val invScalingMatrix = Matrix()
        invScalingMatrix.m[0][0] = 1.0 / x
        invScalingMatrix.m[1][1] = 1.0 / y
        invScalingMatrix.m[2][2] = 1.0 / z
        invMatrix = invMatrix.mult(invScalingMatrix)

        val scalingMatrix = Matrix()
        scalingMatrix.m[0][0] = x
        scalingMatrix.m[1][1] = y
        scalingMatrix.m[2][2] = z
        forwardMatrix = scalingMatrix.mult(forwardMatrix)
    }

    fun rotateX(phi: Double) {
        val cosPhi = Math.cos(phi * PI_ON_180)
        val sinPhi = Math.sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix()
        invRotationMatrix.m[1][1] = cosPhi
        invRotationMatrix.m[1][2] = sinPhi
        invRotationMatrix.m[2][1] = -sinPhi
        invRotationMatrix.m[2][2] = cosPhi
        invMatrix = invMatrix.mult(invRotationMatrix)

        val rotationMatrix = Matrix()
        rotationMatrix.m[1][1] = cosPhi
        rotationMatrix.m[1][2] = -sinPhi
        rotationMatrix.m[2][1] = sinPhi
        rotationMatrix.m[2][2] = cosPhi
        forwardMatrix = rotationMatrix.mult(forwardMatrix)
    }

    fun rotateY(phi: Double) {
        val cosPhi = Math.cos(phi * PI_ON_180)
        val sinPhi = Math.sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix()
        invRotationMatrix.m[2][2] = cosPhi
        invRotationMatrix.m[0][2] = -sinPhi
        invRotationMatrix.m[2][0] = sinPhi
        invRotationMatrix.m[0][0] = cosPhi
        invMatrix = invMatrix.mult(invRotationMatrix)

        val rotationMatrix = Matrix()
        rotationMatrix.m[2][2] = cosPhi
        rotationMatrix.m[0][2] = sinPhi
        rotationMatrix.m[2][0] = -sinPhi
        rotationMatrix.m[0][0] = cosPhi
        forwardMatrix = rotationMatrix.mult(forwardMatrix)
    }

    fun rotateZ(phi: Double) {
        val cosPhi = Math.cos(phi * PI_ON_180)
        val sinPhi = Math.sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix()
        invRotationMatrix.m[0][0] = cosPhi
        invRotationMatrix.m[0][1] = sinPhi
        invRotationMatrix.m[1][0] = -sinPhi
        invRotationMatrix.m[1][1] = cosPhi
        invMatrix = invMatrix.mult(invRotationMatrix)

        val rotationMatrix = Matrix()
        rotationMatrix.m[0][0] = cosPhi
        rotationMatrix.m[0][1] = -sinPhi
        rotationMatrix.m[1][0] = sinPhi
        rotationMatrix.m[1][1] = cosPhi
        forwardMatrix = rotationMatrix.mult(forwardMatrix)
    }

    fun shear(s: Matrix) {
        var invShearingMatrix = Matrix()

        // discriminant
        val d = ((1.0 - s.m[1][0] * s.m[0][1]
                - s.m[2][0] * s.m[0][2]
                - s.m[2][1] * s.m[1][2])
                + s.m[1][0] * s.m[2][1] * s.m[0][2]
                + s.m[2][0] * s.m[0][1] * s.m[2][1])

        // diagonals
        invShearingMatrix.m[0][0] = 1.0 - s.m[2][1] * s.m[1][2]
        invShearingMatrix.m[1][1] = 1.0 - s.m[2][0] * s.m[0][2]
        invShearingMatrix.m[2][2] = 1.0 - s.m[1][0] * s.m[0][1]
        invShearingMatrix.m[3][3] = d

        // first row
        invShearingMatrix.m[0][1] = -s.m[1][0] + s.m[2][0] * s.m[1][2]
        invShearingMatrix.m[0][2] = -s.m[2][0] + s.m[1][0] * s.m[2][1]

        // second row
        invShearingMatrix.m[1][0] = -s.m[0][1] + s.m[2][1] * s.m[0][2]
        invShearingMatrix.m[1][2] = -s.m[2][1] + s.m[2][0] * s.m[0][1]

        // third row
        invShearingMatrix.m[2][0] = -s.m[0][2] + s.m[0][1] * s.m[1][2]
        invShearingMatrix.m[2][1] = -s.m[1][2] + s.m[1][0] * s.m[0][2]

        // divide by discriminant
        invShearingMatrix = invShearingMatrix.div(d)

        invMatrix = invMatrix.mult(invShearingMatrix)
        forwardMatrix = s.mult(forwardMatrix)
    }

}
