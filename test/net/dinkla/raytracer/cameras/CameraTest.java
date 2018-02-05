package net.dinkla.raytracer.cameras;

import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 12:14:05
 * To change this template use File | Settings | File Templates.
 */
public class CameraTest {
//    @Test
    public void testComputeUVW() throws Exception {
        Camera c = new Camera(null, null) {
            @Override
            public void render(IFilm film, final int frame) {
            }
        };

        c.eye = Point3D.Companion.getORIGIN();
        c.lookAt = Point3D.Companion.getORIGIN();
        c.up = Vector3D.Companion.getUP();

        System.out.println(c.uvw.getU());
        System.out.println(c.uvw.getV());
        System.out.println(c.uvw.getW());
    }
}
