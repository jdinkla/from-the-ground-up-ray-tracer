package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.interfaces.Timer
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class TimerTest {

    @Test
    @Throws(Exception::class)
    fun get() {
        val t = Timer()

        t.start()
        Thread.sleep(5)
        t.stop()
        val x = t.duration
        assertTrue(x > 0)

        t.start()
        Thread.sleep(50)
        t.stop()
        val y = t.duration
        assertTrue(y > 0)

        assertTrue(y > x)
    }

}
