package net.dinkla.raytracer.world.scripting

import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.dsl.WorldScope
import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

/**
 * A [WorldDefinition] backed by an external Kotlin DSL file (`*.scene.kts`) evaluated at runtime via
 * the embedded Kotlin scripting host (TASK-17). The file body is the bare scene DSL — identical to
 * the contents of `Builder.build { ... }` — with a fresh [WorldScope] as its implicit receiver.
 *
 * [id] is the file name (so it reads naturally in logs and output file names), while [world]
 * compiles and evaluates the file against a fresh [WorldScope] and returns the assembled [World].
 *
 * Compilation or evaluation failures are surfaced as a [SceneScriptException] carrying the file path
 * and the compiler diagnostics (message + line where available) — never a silent null or a bare
 * stack trace (AC#4).
 */
class FileWorldDefinition(
    private val file: File,
) : WorldDefinition {
    constructor(path: String) : this(File(path))

    override val id: String = file.name

    override fun world(): World {
        val scope = WorldScope()

        val compilationConfiguration =
            createJvmCompilationConfigurationFromTemplate<SceneScript>()

        val evaluationConfiguration =
            ScriptEvaluationConfiguration {
                implicitReceivers(scope)
                constructorArgs()
            }

        val result =
            BasicJvmScriptingHost().eval(
                file.toScriptSource(),
                compilationConfiguration,
                evaluationConfiguration,
            )

        when (result) {
            is ResultWithDiagnostics.Success -> Unit
            is ResultWithDiagnostics.Failure -> throw SceneScriptException(file, result.reports)
        }

        return scope.world
    }
}

/**
 * Thrown when an external scene file fails to compile or evaluate. The message names the [file] and
 * lists each error/fatal diagnostic with its message and source line (when known), so the failure is
 * actionable rather than a bare stack trace (AC#4).
 */
class SceneScriptException(
    val file: File,
    val diagnostics: List<ScriptDiagnostic>,
) : RuntimeException(buildMessage(file, diagnostics)) {
    companion object {
        private fun buildMessage(
            file: File,
            diagnostics: List<ScriptDiagnostic>,
        ): String {
            val relevant =
                diagnostics.filter {
                    it.severity == ScriptDiagnostic.Severity.ERROR ||
                        it.severity == ScriptDiagnostic.Severity.FATAL
                }
            val shown = relevant.ifEmpty { diagnostics }
            val details =
                shown.joinToString(separator = "\n") { diagnostic ->
                    val line = diagnostic.location?.start?.line
                    val where = if (line != null) ":$line" else ""
                    "  [${diagnostic.severity}] ${file.name}$where: ${diagnostic.message}" +
                        (diagnostic.exception?.let { "\n    cause: $it" } ?: "")
                }
            return "Failed to load scene script '${file.path}':\n$details"
        }
    }
}
