package net.dinkla.raytracer.math

import net.dinkla.raytracer.math.MathUtils.PI_ON_180
import net.dinkla.raytracer.utilities.equals
import kotlin.math.cos
import kotlin.math.sin

class AffineTransformation : Transformation {

    override var forwardMatrix: Matrix = Matrix.identity()
    override var invMatrix: Matrix = Matrix.identity()

    override fun translate(v: Vector3D) {
        translate(v.x, v.y, v.z)
    }

    private fun translate(x: Double, y: Double, z: Double) {
        val invTranslationMatrix = Matrix.identity()
        invTranslationMatrix[0, 3] = -x
        invTranslationMatrix[1, 3] = -y
        invTranslationMatrix[2, 3] = -z
        invMatrix *= invTranslationMatrix

        val translationMatrix = Matrix.identity()
        translationMatrix[0, 3] = x
        translationMatrix[1, 3] = y
        translationMatrix[2, 3] = z
        forwardMatrix = translationMatrix * forwardMatrix
    }

    override fun scale(v: Vector3D) {
        scale(v.x, v.y, v.z)
    }

    private fun scale(x: Double, y: Double, z: Double) {
        val invScalingMatrix = Matrix.identity()
        invScalingMatrix[0, 0] = 1.0 / x
        invScalingMatrix[1, 1] = 1.0 / y
        invScalingMatrix[2, 2] = 1.0 / z
        invMatrix *= invScalingMatrix

        val scalingMatrix = Matrix.identity()
        scalingMatrix[0, 0] = x
        scalingMatrix[1, 1] = y
        scalingMatrix[2, 2] = z
        forwardMatrix = scalingMatrix * forwardMatrix
    }

    override fun rotate(axis: Axis, phi: Double) {
        when (axis) {
            Axis.X -> rotateX(phi)
            Axis.Y -> rotateY(phi)
            Axis.Z -> rotateZ(phi)
        }
    }

    private fun rotateX(phi: Double) {
        val cosPhi = cos(phi * PI_ON_180)
        val sinPhi = sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix.identity()
        invRotationMatrix[1, 1] = cosPhi
        invRotationMatrix[1, 2] = sinPhi
        invRotationMatrix[2, 1] = -sinPhi
        invRotationMatrix[2, 2] = cosPhi
        invMatrix *= invRotationMatrix

        val rotationMatrix = Matrix.identity()
        rotationMatrix[1, 1] = cosPhi
        rotationMatrix[1, 2] = -sinPhi
        rotationMatrix[2, 1] = sinPhi
        rotationMatrix[2, 2] = cosPhi
        forwardMatrix = rotationMatrix * forwardMatrix
    }

    private fun rotateY(phi: Double) {
        val cosPhi = cos(phi * PI_ON_180)
        val sinPhi = sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix.identity()
        invRotationMatrix[2, 2] = cosPhi
        invRotationMatrix[0, 2] = -sinPhi
        invRotationMatrix[2, 0] = sinPhi
        invRotationMatrix[0, 0] = cosPhi
        invMatrix *= invRotationMatrix

        val rotationMatrix = Matrix.identity()
        rotationMatrix[2, 2] = cosPhi
        rotationMatrix[0, 2] = sinPhi
        rotationMatrix[2, 0] = -sinPhi
        rotationMatrix[0, 0] = cosPhi
        forwardMatrix = rotationMatrix * forwardMatrix
    }

    private fun rotateZ(phi: Double) {
        val cosPhi = cos(phi * PI_ON_180)
        val sinPhi = sin(phi * PI_ON_180)

        val invRotationMatrix = Matrix.identity()
        invRotationMatrix[0, 0] = cosPhi
        invRotationMatrix[0, 1] = sinPhi
        invRotationMatrix[1, 0] = -sinPhi
        invRotationMatrix[1, 1] = cosPhi
        invMatrix *= invRotationMatrix

        val rotationMatrix = Matrix.identity()
        rotationMatrix[0, 0] = cosPhi
        rotationMatrix[0, 1] = -sinPhi
        rotationMatrix[1, 0] = sinPhi
        rotationMatrix[1, 1] = cosPhi
        forwardMatrix = rotationMatrix * forwardMatrix
    }

    override fun ray(ray: Ray): Ray {
        val origin = invMatrix * ray.origin
        val direction = invMatrix * ray.direction
        return Ray(origin, direction)
    }

    override fun equals(other: Any?): Boolean = this.equals<AffineTransformation>(other) { a, b ->
        a.forwardMatrix == b.forwardMatrix && a.invMatrix == b.invMatrix
    }

    override fun hashCode(): Int = forwardMatrix.hashCode() + 7 * invMatrix.hashCode()

    override fun toString(): String = "AffineTransformation($forwardMatrix, $invMatrix)"
}
