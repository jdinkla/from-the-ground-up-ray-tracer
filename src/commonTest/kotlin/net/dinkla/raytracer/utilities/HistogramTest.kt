package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.io.ByteArrayOutputStream
import java.io.PrintStream

// println() writes to stdout; capture it so the min/max summary branch can be observed. Always
// restore the original stream in a finally.
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

internal class HistogramTest :
    StringSpec({

        "an empty histogram has no keys" {
            val h = Histogram()
            h.keys().size shouldBe 0
        }

        // get on a key that was never added returns 0 via the `?: 0` elvis fallback (the absent-key
        // branch of `counts[key] ?: 0`).
        "querying a never-added key returns zero" {
            val h = Histogram()
            h.add(3)

            h[99] shouldBe 0
        }

        "adding one element" {
            val h = Histogram()
            h.add(3)
            h.keys().size shouldBe 1
            h[3] shouldBe 1
        }

        "adding one element twice" {
            val h = Histogram()
            h.add(3)
            h.add(3)
            h.keys().size shouldBe 1
            h[3] shouldBe 2
        }

        "adding two different elements" {
            val h = Histogram()
            h.add(3)
            h.add(4)
            h.keys().size shouldBe 2
            h[3] shouldBe 1
            h[4] shouldBe 1
        }

        "adding multiple different elements" {
            val h = Histogram()
            h.add(3)
            h.add(4)
            h.add(3)
            h.add(4)
            h.add(3)
            h.keys().size shouldBe 2
            h[3] shouldBe 3
            h[4] shouldBe 2
        }

        "keys returns the keys" {
            val h = Histogram()
            h.add(3)
            h.keys() shouldContainExactly setOf(3)
        }

        // println() over a populated histogram updates min/max and prints the summary line: this is the
        // non-empty branch of the final `if (min != MAX_VALUE || max != MIN_VALUE)` guard.
        "println prints the min and max summary for a populated histogram" {
            val h = Histogram()
            h.add(3)
            h.add(3)
            h.add(4)

            val out = captureStdout { h.println() }

            out shouldContain "min="
            out shouldContain "max="
        }

        // An empty histogram leaves min == MAX_VALUE and max == MIN_VALUE, so the summary line is
        // skipped (the false branch of the same guard).
        "println omits the summary line for an empty histogram" {
            val h = Histogram()

            val out = captureStdout { h.println() }

            out shouldNotContain "min="
        }
    })
