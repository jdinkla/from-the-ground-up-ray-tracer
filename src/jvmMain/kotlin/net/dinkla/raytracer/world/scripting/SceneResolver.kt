package net.dinkla.raytracer.world.scripting

import net.dinkla.raytracer.examples.worldMap
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.requireWorldDef
import java.io.File

/**
 * Resolves a `--world` value to a [WorldDefinition], widening the domain to external files (TASK-17)
 * while preserving the existing classgraph behavior for built-in scenes (AC#3).
 *
 * Resolution order:
 *  1. If [id] is the path of an existing regular file, load it via the scripting host
 *     ([FileWorldDefinition]).
 *  2. Otherwise fall back to the classgraph [available] scene map — and, for an unknown non-file id,
 *     [requireWorldDef] still fails fast with the same clear, actionable message as before (TASK-15).
 *
 * The filesystem check and the map fallback are kept separate from [requireWorldDef] so that the pure
 * id-validation logic stays unit-testable without touching the filesystem.
 */
object SceneResolver {
    fun resolveWorld(
        id: String,
        available: Map<String, WorldDefinition> = worldMap,
    ): WorldDefinition =
        if (isExistingFile(id)) {
            FileWorldDefinition(id)
        } else {
            requireWorldDef(id, available)
        }

    /** True when [id] denotes an existing regular file on disk (the trigger for script loading). */
    fun isExistingFile(id: String): Boolean = File(id).isFile
}
