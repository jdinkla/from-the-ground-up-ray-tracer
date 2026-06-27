package net.dinkla.raytracer.utilities

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import korlibs.time.DateFormat.Companion.FORMAT2
import korlibs.time.DateTime
import co.touchlab.kermit.Logger as Kermit

/**
 * Logging facade over [Kermit] (co.touchlab:kermit), a Kotlin-native multiplatform logger. Kept as a
 * thin object so call sites stay `Logger.info("...")`: Kermit does the severity gating and dispatch,
 * while [TimestampLogWriter] reproduces the project's `TIMESTAMP LEVEL message` console format.
 *
 * The default [threshold] is [LogLevel.INFO], so DEBUG diagnostics (e.g. the kd-tree build trace) are
 * hidden unless the level is lowered.
 */
object Logger {
    enum class LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR,
    }

    /**
     * Minimum level that is actually printed; messages below it are dropped. Delegated to Kermit's
     * minimum severity. Lower it to [LogLevel.DEBUG] to recover the verbose output.
     */
    var threshold: LogLevel = LogLevel.INFO
        set(value) {
            field = value
            Kermit.setMinSeverity(value.toSeverity())
        }

    init {
        Kermit.setLogWriters(TimestampLogWriter())
        Kermit.setMinSeverity(threshold.toSeverity())
    }

    fun debug(s: String) = Kermit.d { s }

    fun info(s: String) = Kermit.i { s }

    fun warn(s: String) = Kermit.w { s }

    fun error(s: String) = Kermit.e { s }

    private fun LogLevel.toSeverity(): Severity =
        when (this) {
            LogLevel.DEBUG -> Severity.Debug
            LogLevel.INFO -> Severity.Info
            LogLevel.WARN -> Severity.Warn
            LogLevel.ERROR -> Severity.Error
        }
}

/**
 * A [LogWriter] that prints `TIMESTAMP LEVEL message` to stdout, matching the format the project used
 * before adopting Kermit. The level label uses the project's own names (DEBUG/INFO/WARN/ERROR) rather
 * than Kermit's single-letter prefixes; a [Throwable], if present, is appended on following lines.
 */
private class TimestampLogWriter : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        println("${DateTime.now().format(FORMAT2)} ${label(severity)} $message")
        throwable?.let { println(it.stackTraceToString()) }
    }

    private fun label(severity: Severity): String =
        when (severity) {
            Severity.Verbose -> "VERBOSE"
            Severity.Debug -> "DEBUG"
            Severity.Info -> "INFO"
            Severity.Warn -> "WARN"
            Severity.Error -> "ERROR"
            Severity.Assert -> "ASSERT"
        }
}
