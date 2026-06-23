package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.shouldBeApprox

class RampTest :
    StringSpec({
        val ramp = Ramp(color1 = Color.BLACK, color2 = Color.WHITE)

        "scalar 0 returns the start colour" {
            ramp.colorAt(0.0) shouldBeApprox Color.BLACK
        }

        "scalar 1 returns the end colour" {
            ramp.colorAt(1.0) shouldBeApprox Color.WHITE
        }

        "scalar 0.5 returns the midpoint of the two colours" {
            ramp.colorAt(0.5) shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "the interpolation is linear between two arbitrary colours" {
            val coloured = Ramp(color1 = Color(0.2, 0.4, 0.6), color2 = Color(0.8, 0.0, 1.0))

            // 0.25 of the way: 0.75*c1 + 0.25*c2
            coloured.colorAt(0.25) shouldBeApprox Color(0.35, 0.3, 0.7)
        }

        "scalars outside [0,1] clamp to the endpoint colours" {
            ramp.colorAt(-1.0) shouldBeApprox Color.BLACK
            ramp.colorAt(2.0) shouldBeApprox Color.WHITE
        }

        "the ramp reads the chosen axis of the local hit point, wrapped into [0,1)" {
            // axis Y, frequency 1.0, y = 0.5 -> scalar 0.5 -> midpoint
            ramp.getColor(testShade(Point3D(0.0, 0.5, 0.0))) shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "frequency repeats the gradient periodically along the axis" {
            val repeating = Ramp(color1 = Color.BLACK, color2 = Color.WHITE, frequency = 2.0)

            // y = 0.75, frequency 2 -> coordinate 1.5 -> fractional 0.5 -> midpoint
            repeating.getColor(testShade(Point3D(0.0, 0.75, 0.0))) shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "the X-axis ramp reads the x coordinate of the hit point" {
            val rampX = Ramp(color1 = Color.BLACK, color2 = Color.WHITE, axis = Ramp.Axis.X)

            // axis X: x = 0.5 -> scalar 0.5 -> midpoint; y and z are ignored.
            rampX.getColor(testShade(Point3D(0.5, 9.0, 9.0))) shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "the Z-axis ramp reads the z coordinate of the hit point" {
            val rampZ = Ramp(color1 = Color.BLACK, color2 = Color.WHITE, axis = Ramp.Axis.Z)

            // axis Z: z = 0.5 -> scalar 0.5 -> midpoint; x and y are ignored.
            rampZ.getColor(testShade(Point3D(9.0, 9.0, 0.5))) shouldBeApprox Color(0.5, 0.5, 0.5)
        }
    })
