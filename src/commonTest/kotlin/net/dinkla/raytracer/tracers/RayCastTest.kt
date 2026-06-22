package net.dinkla.raytracer.tracers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.world.IWorld

private class FakeWorld : IWorld {
    override var tracer: Tracer? = null
    override val lights: List<Light> = emptyList()
    override val ambientLight: Ambient = Ambient()
    override var backgroundColor: Color = Color.BLACK

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean = false

    override fun inShadow(
        ray: Ray,
        sr: IShade,
        d: Double,
    ): Boolean = false

    override fun shouldStopRecursion(depth: Int): Boolean = true
}

/**
 * Pins that [RayCast] is a deprecated tracer that refuses construction: its init throws
 * [UnsupportedOperationException] (formerly a bare RuntimeException; the throw trigger is unchanged).
 */
internal class RayCastTest :
    StringSpec({

        "RayCast cannot be constructed" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    RayCast(FakeWorld())
                }
            ex.message shouldContain "RayCast"
        }
    })
