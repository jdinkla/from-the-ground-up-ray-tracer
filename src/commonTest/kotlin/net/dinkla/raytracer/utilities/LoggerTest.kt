package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.io.ByteArrayOutputStream
import java.io.PrintStream

// Logger writes through println(stdout) and exposes no buffer, so capturing System.out is the only
// way to observe what a level actually emits. Redirect for the duration of the call and always
// restore it in a finally (mirrors CounterTest).
private fun captureStdout(block: () -> Unit): String {
    val original = System.out
    val buffer = ByteArrayOutputStream()
    System.setOut(PrintStream(buffer, true))
    try {
        block()
    } finally {
        System.setOut(original)
    }
    return buffer.toString()
}

class LoggerTest :
    StringSpec({

        // Logger.threshold is process-wide mutable state; save it before and restore it after each
        // scenario so a changed level cannot leak into other specs (e.g. CounterTest relies on INFO
        // printing). See specs/testing.md §9.
        var savedThreshold = Logger.LogLevel.INFO
        beforeTest { savedThreshold = Logger.threshold }
        afterTest { Logger.threshold = savedThreshold }

        "the default threshold suppresses DEBUG but prints INFO" {
            Logger.threshold shouldBe Logger.LogLevel.INFO

            captureStdout { Logger.debug("dbg-msg") } shouldNotContain "dbg-msg"
            captureStdout { Logger.info("info-msg") } shouldContain "info-msg"
        }

        "a message at the threshold level is printed" {
            Logger.threshold = Logger.LogLevel.WARN

            captureStdout { Logger.warn("warn-msg") } shouldContain "warn-msg"
        }

        "a message below the threshold is suppressed" {
            Logger.threshold = Logger.LogLevel.WARN

            captureStdout { Logger.info("info-msg") } shouldNotContain "info-msg"
        }

        "a message above the threshold is printed" {
            Logger.threshold = Logger.LogLevel.WARN

            captureStdout { Logger.error("err-msg") } shouldContain "err-msg"
        }

        "lowering the threshold to DEBUG recovers the verbose output, tagged with its level" {
            Logger.threshold = Logger.LogLevel.DEBUG

            captureStdout { Logger.debug("verbose") } shouldContain "DEBUG verbose"
        }
    })
