package net.dinkla.raytracer.world

/**
 * A self-registering scene definition. Each scene under `src/examples/.../examples` is a Kotlin
 * `object` implementing this interface; at startup `Worlds.kt` scans the package with classgraph and
 * keys the discovered definitions by their [id]. To add a scene, create the object — there is no
 * central list to edit.
 */
interface WorldDefinition {
    /** The scene's unique key, conventionally its file name (e.g. `"World48.kt"`). */
    val id: String

    /** Builds the [World] for this scene, typically via the `Builder.build { ... }` DSL. */
    fun world(): World
}
