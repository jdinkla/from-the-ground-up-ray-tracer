package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.Test

class SamplerTest {

    internal val NUM = 1000

    internal var s = Sampler(PureRandom, 100, 10)

    @Test
    @Throws(Exception::class)
    fun testSampleUnitSquare() {
    }

    @Test
    @Throws(Exception::class)
    fun testSampleUnitDisk() {
        //        s.mapSamplesToUnitDisk();
        //        for (int i=0; i<NUM; i++) {
        //            Point2D p = s.sampleUnitDisk();
        //            assertTrue(0 <= p.x);
        //            assertTrue(p.x < 1);
        //            assertTrue(0 <= p.y);
        //            assertTrue(p.y < 1);
        //            assertTrue(p.x + p.y < 1.42);
        //        }
    }

    @Test
    @Throws(Exception::class)
    fun testSampleHemisphere() {
    }

    @Test
    @Throws(Exception::class)
    fun testSampleSphere() {
        //        s.mapSamplesToSphere();
        //        for (int i=0; i<NUM; i++) {
        //            Point3D p = s.sampleSphere();
        //            assertTrue(-1 < p.x);
        //            assertTrue(p.x < 1);
        //            assertTrue(-1 < p.y);
        //            assertTrue(p.y < 1);
        //            assertTrue(0 <= p.z);
        //            assertTrue(p.z < 1);
        //        }
    }

    @Test
    @Throws(Exception::class)
    fun testSampleOneSet() {
    }
}
