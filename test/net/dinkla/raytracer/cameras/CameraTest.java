package net.dinkla.raytracer.cameras;

import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.math.Point3D$;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Vector3D$;
import net.dinkla.raytracer.math.Vector3DF;

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

        c.eye = Point3D$.MODULE$.ORIGIN();
        c.lookAt = Point3D$.MODULE$.ORIGIN();
        c.up = Vector3D$.MODULE$.UP();

//        System.out.println(c.uvw.u);
//        System.out.println(c.uvw.v);
//        System.out.println(c.uvw.w);
    }
}
