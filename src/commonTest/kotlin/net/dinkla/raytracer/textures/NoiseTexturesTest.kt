package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.noise.LinearNoise
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.sin

/**
 * Colour-mapping tests for the noise-driven textures. The noise field itself is exercised in
 * [net.dinkla.raytracer.noise.LatticeNoiseTest]; here the focus is the pure value→colour seam each
 * texture exposes (`colorFor` / `colorAt`), with expectations derived from the documented mapping, plus
 * a determinism check on the full `getColor` path through a [testShade].
 */
class NoiseTexturesTest :
    StringSpec({

        val noise = LinearNoise()

        // --- FBmTexture: signed fBm in [-1,1] remapped to [0,1] then windowed/lerped. ---

        "fBm texture maps signed 0 to the midpoint colour" {
            val tex = FBmTexture(noise, minColor = Color.BLACK, maxColor = Color.WHITE)

            // unit = (0+1)/2 = 0.5; defaults window [0,1] so t = 0.5.
            tex.colorFor(0.0) shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "fBm texture maps signed +1 to maxColor and signed -1 to minColor" {
            val tex = FBmTexture(noise, minColor = Color.BLACK, maxColor = Color.WHITE)

            tex.colorFor(1.0) shouldBeApprox Color.WHITE
            tex.colorFor(-1.0) shouldBeApprox Color.BLACK
        }

        "fBm texture windows the value to [minValue, maxValue] before normalising" {
            // window [0.5, 1.0]: signed 0 -> unit 0.5 -> windowed 0.5 -> t = 0 -> minColor.
            val tex = FBmTexture(noise, minColor = Color.BLACK, maxColor = Color.WHITE, minValue = 0.5, maxValue = 1.0)

            tex.colorFor(0.0) shouldBeApprox Color.BLACK
            tex.colorFor(1.0) shouldBeApprox Color.WHITE
        }

        // --- TurbulenceTexture: turbulence in [0,1], windowed, lerped. ---

        "turbulence texture maps 0 to minColor and 1 to maxColor" {
            val tex = TurbulenceTexture(noise, minColor = Color.BLACK, maxColor = Color.WHITE)

            tex.colorFor(0.0) shouldBeApprox Color.BLACK
            tex.colorFor(1.0) shouldBeApprox Color.WHITE
        }

        "turbulence texture maps 0.5 to the midpoint colour" {
            val tex = TurbulenceTexture(noise, minColor = Color.BLACK, maxColor = Color.WHITE)

            tex.colorFor(0.5) shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        // --- WrappedFBmTexture: expand by expansionNumber, keep fractional part. ---

        "wrapped fBm keeps the fractional part of the expanded value" {
            val tex = WrappedFBmTexture(noise, minColor = Color.BLACK, maxColor = Color.WHITE, expansionNumber = 2.0)

            // 2 * 0.25 = 0.5 -> wrapped 0.5 -> midpoint.
            tex.colorFor(0.25) shouldBeApprox Color(0.5, 0.5, 0.5)
            // 2 * 0.6 = 1.2 -> wrapped 0.2.
            tex.colorFor(0.6) shouldBeApprox Color(0.2, 0.2, 0.2)
        }

        // --- RampFBmTexture (marble): ramp indexed by sin of (coord + amplitude*fbm). ---

        "marble indexes the ramp by the normalised sine of the perturbed coordinate" {
            val ramp = Ramp(color1 = Color.BLACK, color2 = Color.WHITE)
            val tex = RampFBmTexture(noise, ramp = ramp, frequency = 1.0, amplitude = 2.0)

            // coord 0, noise 0 -> sin(0) = 0 -> index (1+0)/2 = 0.5 -> ramp midpoint.
            tex.colorFor(0.0, 0.0) shouldBeApprox Color(0.5, 0.5, 0.5)

            // coord pi/2, noise 0 -> sin(pi/2) = 1 -> index 1 -> ramp end (WHITE).
            val phase = kotlin.math.PI / 2.0
            val expectedIndex = (1.0 + sin(phase)) * 0.5
            tex.colorFor(phase, 0.0) shouldBeApprox ramp.colorAt(expectedIndex)
        }

        // --- Wood: concentric rings around the y axis warped by turbulence. ---

        "wood is the light colour at a ring edge and the dark colour at a ring centre" {
            val light = Color(0.7, 0.5, 0.3)
            val dark = Color(0.3, 0.2, 0.1)
            val tex = Wood(noise, lightColor = light, darkColor = dark, ringFrequency = 1.0, ringWarp = 0.4)

            // radius 0, turbulence 0 -> warped 0 -> ringPos 0 -> ring 0 -> light.
            tex.colorFor(0.0, 0.0) shouldBeApprox light
            // radius 0.5, turbulence 0 -> warped 0.5 -> ringPos 0.5 -> ring 1 -> dark.
            tex.colorFor(0.5, 0.0) shouldBeApprox dark
        }

        // --- Determinism through the full getColor path. ---

        "noise textures return the same colour for the same hit point" {
            val fbm = FBmTexture(noise)
            val turb = TurbulenceTexture(noise)
            val marble = RampFBmTexture(noise)
            val wood = Wood(noise)
            val p = Point3D(1.3, -2.7, 0.9)

            fbm.getColor(testShade(p)) shouldBe fbm.getColor(testShade(p))
            turb.getColor(testShade(p)) shouldBe turb.getColor(testShade(p))
            marble.getColor(testShade(p)) shouldBe marble.getColor(testShade(p))
            wood.getColor(testShade(p)) shouldBe wood.getColor(testShade(p))
        }
    })
