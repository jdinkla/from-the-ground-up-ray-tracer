package net.dinkla.raytracer.samplers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 */
public class SamplerTest {

    final int NUM = 1000;

    Sampler s = new Sampler(new PureRandom(), 100, 10);
    
    @Test
    public void testSampleUnitSquare() throws Exception {
    }

    @Test
    public void testSampleUnitDisk() throws Exception {
//        s.mapSamplesToUnitDisk();
//        for (int i=0; i<NUM; i++) {
//            Point2DF p = s.sampleUnitDisk();
//            assertTrue(0 <= p.x);
//            assertTrue(p.x < 1);
//            assertTrue(0 <= p.y);
//            assertTrue(p.y < 1);
//            assertTrue(p.x + p.y < 1.42);
//        }
    }

    @Test
    public void testSampleHemisphere() throws Exception {
    }

    @Test
    public void testSampleSphere() throws Exception {
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
    public void testSampleOneSet() throws Exception {
    }
}
