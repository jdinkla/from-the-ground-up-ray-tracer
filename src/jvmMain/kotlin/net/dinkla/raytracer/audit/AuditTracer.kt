package net.dinkla.raytracer.audit

import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.SvEmissive
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.World

/**
 * The tracer the audit's heuristic picks when a scene declares none: [AREA] handles area lights,
 * emissive surfaces and ambient occlusion (which the [WHITTED] tracer leaves black or unlit),
 * [WHITTED] handles everything else (direct lighting plus reflection/refraction). Choosing the right
 * one avoids false "black image" flags for area-lit scenes.
 */
enum class AuditTracer { WHITTED, AREA }

fun chooseTracer(world: World): AuditTracer {
    val needsAreaLighting =
        world.lights.any { it is AreaLight } ||
            world.ambientLight is AmbientOccluder ||
            world.materials.values.any { it is Emissive || it is SvEmissive }
    return if (needsAreaLighting) AuditTracer.AREA else AuditTracer.WHITTED
}

/**
 * The full [Tracers] value the audit should render [world] with. A scene's declared
 * [preferredTracer][net.dinkla.raytracer.world.Metadata.preferredTracer] wins when present — this is
 * how scenes designed for a specific tracer (e.g. `MultipleObjects.kt` → [MULTIPLE_OBJECTS][Tracers],
 * `CornellBox.kt` → [PATH_TRACE][Tracers]) are no longer flagged as false near-black SUSPECTs. When a
 * scene declares nothing, this falls back to the [chooseTracer] heuristic (AREA vs. WHITTED).
 */
fun auditTracer(world: World): Tracers =
    world.metadata.preferredTracer
        ?: when (chooseTracer(world)) {
            AuditTracer.AREA -> Tracers.AREA
            AuditTracer.WHITTED -> Tracers.WHITTED
        }
