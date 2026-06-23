package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.io.ByteArrayOutputStream
import java.io.PrintStream

// Counter.stats and printStats both report via Logger -> println(stdout). Capturing stdout is the
// only way to observe the accumulated counts through the public API (there is no getter), so these
// tests redirect System.out for the duration of the call and always restore it in a finally.
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

// Returns the count printed for [key] on its "key:<padding><count>" line. Reading the count off the
// key's own line (rather than a substring search on the whole capture) avoids false matches against
// the timestamp digits Logger prepends to every line.
private fun countOf(
    output: String,
    key: String,
): Int =
    output
        .lineSequence()
        .first { it.contains("$key:") }
        .substringAfterLast("$key:")
        .trim()
        .toInt()

class CounterTest :
    StringSpec({

        // Counter is a process-wide singleton; clear it before each scenario so counts from other
        // tests cannot leak in and make the assertions order-dependent.
        beforeTest {
            Counter.reset()
        }

        "the first count of a key records a tally of one" {
            Counter.count("alpha")

            val out = captureStdout { Counter.stats(20) }

            countOf(out, "alpha") shouldBe 1
        }

        "counting the same key repeatedly accumulates on the existing map" {
            Counter.count("beta")
            Counter.count("beta")
            Counter.count("beta")

            val out = captureStdout { Counter.stats(20) }

            countOf(out, "beta") shouldBe 3
        }

        "counting distinct keys keeps independent tallies" {
            Counter.count("aa")
            Counter.count("bb")
            Counter.count("bb")

            val out = captureStdout { Counter.stats(20) }

            countOf(out, "aa") shouldBe 1
            countOf(out, "bb") shouldBe 2
        }

        "reset clears all recorded counts" {
            Counter.count("gamma")
            Counter.reset()

            val out = captureStdout { Counter.stats(20) }

            out shouldNotContain "gamma"
        }

        "stats on an empty counter reports the header but no counted keys" {
            Counter.count("temporary")
            Counter.reset()

            val out = captureStdout { Counter.stats(20) }

            // The header is always printed; no previously counted key survives the reset.
            out shouldContain "Counter.stats"
            out shouldNotContain "temporary"
        }

        // printStats is the top-level function in Counter.kt (CounterKt). The padding branch pads the
        // key to `columns`; a key longer than columns must clamp the padding to zero (max(..., 0)).
        "printStats pads each key out to the requested column width" {
            val out = captureStdout { printStats(mapOf("k" to 7), 10) }

            // 10 columns, key length 1 -> max(10 - 1 - 1, 0) = 8 spaces of padding before the count.
            out shouldContain "k:" + " ".repeat(8) + "7"
            countOf(out, "k") shouldBe 7
        }

        "printStats does not pad below zero for a key wider than the column width" {
            // Key length (8) exceeds columns (3): the max(columns - len - 1, 0) guard yields 0 spaces,
            // so the count sits directly after the colon.
            val out = captureStdout { printStats(mapOf("verylong" to 4), 3) }

            out shouldContain "verylong:4"
            countOf(out, "verylong") shouldBe 4
        }

        "printStats lists keys in sorted order" {
            val out = captureStdout { printStats(mapOf("zeta" to 1, "alpha" to 2), 12) }

            // keys are emitted sorted, so "alpha" must appear before "zeta" in the output.
            (out.indexOf("alpha") < out.indexOf("zeta")) shouldBe true
        }
    })
