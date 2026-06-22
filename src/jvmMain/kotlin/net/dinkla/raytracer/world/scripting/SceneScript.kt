package net.dinkla.raytracer.world.scripting

import net.dinkla.raytracer.world.dsl.WorldScope
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

/**
 * Script definition for external scene files (`*.scene.kts`).
 *
 * The body of such a file is *identical* to what goes inside `Builder.build { ... }` today: the
 * bare DSL with a [WorldScope] receiver in scope (`camera(...)`, `lights { }`, `materials { }`,
 * `objects { }`). This is achieved by declaring [WorldScope] as the script's implicit receiver, so
 * no wrapper and no imports are needed in the scene file. The common DSL value types are added as
 * default imports for convenience (`Color`, `Point3D`, `Normal`, `Vector3D`).
 *
 * See [FileWorldDefinition] for evaluation and `scenes/Sample.scene.kts` for an example file.
 */
@KotlinScript(
    displayName = "Ray tracer scene DSL",
    fileExtension = "scene.kts",
    compilationConfiguration = SceneScriptCompilationConfiguration::class,
)
abstract class SceneScript

object SceneScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        "net.dinkla.raytracer.colors.Color",
        "net.dinkla.raytracer.math.Point3D",
        "net.dinkla.raytracer.math.Normal",
        "net.dinkla.raytracer.math.Vector3D",
    )
    implicitReceivers(WorldScope::class)
    jvm {
        // Compile the script against the ray tracer's own classpath so the DSL (WorldScope and the
        // domain types it exposes) is available without the caller supplying any dependency config.
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
})
