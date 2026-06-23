package net.dinkla.raytracer.audit

/**
 * The user-facing kinds of production class whose *example coverage* the [scene audit][SceneAuditor]
 * measures — the things one authors when writing a scene. Deliberately excludes internal collaborators
 * (brdf/btdf/samplers/mappings/noise) and tracers: a tracer is a render-time choice
 * ([net.dinkla.raytracer.world.Context]), never declared in a scene, so per-scene tracer coverage
 * would only reflect the audit's own configuration.
 */
enum class Category(
    val display: String,
) {
    GEOMETRY("Geometric objects"),
    ACCELERATION("Acceleration structures"),
    MATERIALS("Materials"),
    LIGHTS("Lights"),
    CAMERAS("Cameras"),
    LENSES("Lenses"),
    TEXTURES("Textures"),
}

/** The leaf name of a fully-qualified class name, e.g. `…objects.Sphere` -> `Sphere`. */
fun simpleNameOf(fqn: String): String = fqn.substringAfterLast('.')
