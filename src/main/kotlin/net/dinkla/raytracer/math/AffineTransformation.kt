package net.dinkla.raytracer.math

import net.dinkla.raytracer.math.MathUtils.PI_ON_180

class AffineTransformation {

    var forwardMatrix: Matrix
    var invMatrix: Matrix

    init {
        forwardMatrix = Matrix.identity()
        invMatrix = Matrix.identity()
    }

    fun translate(v: Vector3D) {
        translate(v.x, v.y, v.z)
    }

    fun translate(x: Double, y: Double, z: Double) {
        val invTranslationMatrix = Matrix.identity()
        invTranslationMatrix[0, 3] = -x
        invTranslationMatrix[1, 3] = -y
        invTranslationMatrix[2, 3] = -z
        invMatrix = invMatrix.times(invTranslationMatrix)

        val translationMatrix = Matrix.identity()
        translationMatrix[0, 3] = x
        translationMatrix[1, 3] = y
        translationMatrix[2, 3] = z
        forwardMatrix = translationMatrix.times(forwardMatrix)
    }

    fun scale(v: Vector3D) {
        scale(v.x, v.y, v.z)
    }

    fun scale(x: Double, y: Double, z: Double) {
        val invScalingMatrix = Matrix.identity()
        invScalingMatrix[0, 0] = 1.0 / x
        invScalingMatrix[1, 1] = 1.0 / y
        invScalingMatrix[2, 2] = 1.0 / z
        invMatrix = invMatrix.times(invScalingMatrix)

        val scalingMatrix = Matrix.identity()
        scalingMatrix[0, 0] = x
        scalingMatrix[1, 1] = y
        scalingMatrix[2, 2] = z
        forwardMatrix = scalingMatrix.times(forwardMatrix)
    }

    fun rotateX(phi: Double) {
        val cosPhi = Math.cos(phi * PI_ON_180)
        val sinPhi = Math.sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix.identity()
        invRotationMatrix[1, 1] = cosPhi
        invRotationMatrix[1, 2] = sinPhi
        invRotationMatrix[2, 1] = -sinPhi
        invRotationMatrix[2, 2] = cosPhi
        invMatrix = invMatrix.times(invRotationMatrix)

        val rotationMatrix = Matrix.identity()
        rotationMatrix[1, 1] = cosPhi
        rotationMatrix[1, 2] = -sinPhi
        rotationMatrix[2, 1] = sinPhi
        rotationMatrix[2, 2] = cosPhi
        forwardMatrix = rotationMatrix.times(forwardMatrix)
    }

    fun rotateY(phi: Double) {
        val cosPhi = Math.cos(phi * PI_ON_180)
        val sinPhi = Math.sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix.identity()
        invRotationMatrix[2, 2] = cosPhi
        invRotationMatrix[0, 2] = -sinPhi
        invRotationMatrix[2, 0] = sinPhi
        invRotationMatrix[0, 0] = cosPhi
        invMatrix = invMatrix.times(invRotationMatrix)

        val rotationMatrix = Matrix.identity()
        rotationMatrix[2, 2] = cosPhi
        rotationMatrix[0, 2] = sinPhi
        rotationMatrix[2, 0] = -sinPhi
        rotationMatrix[0, 0] = cosPhi
        forwardMatrix = rotationMatrix.times(forwardMatrix)
    }

    fun rotateZ(phi: Double) {
        val cosPhi = Math.cos(phi * PI_ON_180)
        val sinPhi = Math.sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix.identity()
        invRotationMatrix[0, 0] = cosPhi
        invRotationMatrix[0, 1] = sinPhi
        invRotationMatrix[1, 0] = -sinPhi
        invRotationMatrix[1, 1] = cosPhi
        invMatrix = invMatrix.times(invRotationMatrix)

        val rotationMatrix = Matrix.identity()
        rotationMatrix[0, 0] = cosPhi
        rotationMatrix[0, 1] = -sinPhi
        rotationMatrix[1, 0] = sinPhi
        rotationMatrix[1, 1] = cosPhi
        forwardMatrix = rotationMatrix.times(forwardMatrix)
    }

    fun shear(s: Matrix) {
        var invShearingMatrix = Matrix.identity()

        // discriminant
        val d = ((1.0 - s[1, 0] * s[0, 1]
                - s[2, 0] * s[0, 2]
                - s[2, 1] * s[1, 2])
                + s[1, 0] * s[2, 1] * s[0, 2]
                + s[2, 0] * s[0, 1] * s[2, 1])

        // diagonals
        invShearingMatrix[0, 0] = 1.0 - s[2, 1] * s[1, 2]
        invShearingMatrix[1, 1] = 1.0 - s[2, 0] * s[0, 2]
        invShearingMatrix[2, 2] = 1.0 - s[1, 0] * s[0, 1]
        invShearingMatrix[3, 3] = d

        // first row
        invShearingMatrix[0, 1] = -s[1, 0] + s[2, 0] * s[1, 2]
        invShearingMatrix[0, 2] = -s[2, 0] + s[1, 0] * s[2, 1]

        // second row
        invShearingMatrix[1, 0] = -s[0, 1] + s[2, 1] * s[0, 2]
        invShearingMatrix[1, 2] = -s[2, 1] + s[2, 0] * s[0, 1]

        // third row
        invShearingMatrix[2, 0] = -s[0, 2] + s[0, 1] * s[1, 2]
        invShearingMatrix[2, 1] = -s[1, 2] + s[1, 0] * s[0, 2]

        // divide by discriminant
        invShearingMatrix = invShearingMatrix.div(d)

        invMatrix = invMatrix.times(invShearingMatrix)
        forwardMatrix = s.times(forwardMatrix)
    }

}
