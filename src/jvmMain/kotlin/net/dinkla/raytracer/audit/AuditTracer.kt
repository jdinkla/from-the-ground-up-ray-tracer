package net.dinkla.raytracer.audit

import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.SvEmissive
import net.dinkla.raytracer.world.World

/**
 * The tracer the audit renders a scene with. Scenes do not declare a tracer, so the audit picks one:
 * [AREA] handles area lights, emissive surfaces and ambient occlusion (which the [WHITTED] tracer
 * leaves black or unlit), [WHITTED] handles everything else (direct lighting plus
 * reflection/refraction). Choosing the right one avoids false "black image" flags for area-lit scenes.
 */
enum class AuditTracer { WHITTED, AREA }

fun chooseTracer(world: World): AuditTracer {
    val needsAreaLighting =
        world.lights.any { it is AreaLight } ||
            world.ambientLight is AmbientOccluder ||
            world.materials.values.any { it is Emissive || it is SvEmissive }
    return if (needsAreaLighting) AuditTracer.AREA else AuditTracer.WHITTED
}
