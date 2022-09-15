package net.dinkla.raytracer.interfaces

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import net.dinkla.raytracer.utilities.Timer

class TimerTest : AnnotationSpec() {

    @Test
    @Throws(Exception::class)
    fun get() {
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

}
