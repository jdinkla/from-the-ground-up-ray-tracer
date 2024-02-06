package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan

class TimerTest : StringSpec({
    "duration should be greater than 0" {
        val t = Timer()
        t.start()
        doSomething()
        t.stop()
        t.duration shouldBeGreaterThan 0
    }
})

private fun doSomething() = Thread.sleep(1L)
