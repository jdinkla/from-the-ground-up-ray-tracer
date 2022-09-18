package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThan

class TimerTest : StringSpec({
    "duration should be greater than 0" {
        val t = Timer()
        t.start()
        doSomething() shouldBeGreaterThan 0
        t.stop()
        t.duration shouldBeGreaterThan 0
    }
})

private fun doSomething() = (1..100).toList().map { it + 1 }.reduce { a, b -> a + b }
