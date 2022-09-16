package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan

class TimerTest : StringSpec({
    "get" {
        val t = Timer()

        t.start()
        Thread.sleep(5)
        t.stop()
        val x = t.duration
        x shouldBeGreaterThan 0

        t.start()
        Thread.sleep(50)
        t.stop()
        val y = t.duration
        y shouldBeGreaterThan 0
        y shouldBeGreaterThan x
    }
})

