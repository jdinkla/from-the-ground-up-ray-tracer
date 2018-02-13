package net.dinkla.raytracer.worlds

import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.tracers.AreaLighting

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.Optional
import java.util.function.BiPredicate
import java.util.stream.Stream


import org.junit.jupiter.api.Test


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

class WorldBuilderTest {

    @Test
    fun testCreate1() {
        val f = findExample("World20.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertEquals(w.size(), 3)
        assertEquals(w.lights.size, 1)
    }

    @Test
    fun testCreate2() {
        val f = findExample("World7.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertEquals(w.size(), 6)
        assertEquals(w.lights.size, 3)
    }

    @Test
    fun testCreate3() {
        val f = findExample("World14.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertEquals(w.size(), 2)
        assertEquals(w.lights.size, 0)
        assertEquals(AmbientOccluder::class.java, w.ambientLight.javaClass)
    }

    @Test
    fun testCreate4() {
        val f = findExample("World17.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertEquals(w.size(), 10)
        assertEquals(w.lights.size, 2)
    }

    @Test
    fun testCreate5() {
        val f = findExample("World23.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertEquals(w.size(), 26)
        assertEquals(w.lights.size, 1)
        assertEquals(w.tracer.javaClass, AreaLighting::class.java)
    }

    @Test
    fun testCreate6() {
        val f = findExample("World26.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertEquals(w.size(), 2)
        assertEquals(w.lights.size, 1)
    }

    @Test
    fun testCreate7() {
        val f = findExample("World34.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertNotNull(w.viewPlane, "viewPlane == null")
        assertNotNull(w.camera, "camera == null")
        assertNotNull(w.tracer, "tracer == null")
        assertEquals(w.size(), 5)
        assertEquals(w.lights.size, 1)
    }

    @Test
    fun testCreate8() {
        val f = findExample("World38.groovy").get().toFile()
        val w = WorldBuilder.create(f)
        assertEquals(w.size(), 6)
    }

    companion object {

        private val DIR = "examples"

        private fun findExample(filename: String): Optional<Path> {
            val pr = BiPredicate<Path?, BasicFileAttributes> { p, a ->
                val fn = p?.getFileName().toString()
                filename == fn
            }
            try {
                val f = File(DIR)
                val ps = Files.find(f.toPath(), 99, pr)
                return ps.findFirst()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return Optional.empty()
        }
    }

}
