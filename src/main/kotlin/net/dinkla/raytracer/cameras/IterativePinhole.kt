/*
package net.dinkla.raytracer.cameras;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.films.Film;
import net.dinkla.raytracer.tracers.Tracer;
import net.dinkla.raytracer.utilities.Timer;

*/
/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.04.2010
 * Time: 16:44:12
 * To change this template use File | Settings | File Templates.
 *//*

public class IterativePinhole extends Pinhole {

//    static final Logger LOGGER = Logger.getLogger(Pinhole.class);

//    int STEP_Y = 1;
//    int STEP_X = 1;

//    public double direction;
//    public double zoom;

    public IterativePinhole(ViewPlane viewPlane, Tracer tracer) {
        super(viewPlane, tracer);
//        this.direction = direction;
//        this.zoom = zoom;
        //viewPlane.size /= zoom;
    }

    @Override
    public void render(Film film, final int frame) {
        Timer t = new Timer();
        t.reset();
        int direction = 1024; // works fine
        int s = 0;
        while (direction > 0) {
            int k = (int) Math.pow(2, s-1) - 1;
//            System.out.println("direction=" + direction +", s=" + s + ", k=" + k);
            for (int r = 0; r < viewPlane.resolution.vres; r++) {
                if (-1 == k) {
                    int offset=0;
                    while (offset < viewPlane.resolution.hres) {
                        Color color = render(r, offset);
                        film.setBlock(0, offset, r, Math.min(direction, viewPlane.resolution.hres - 1 - offset), 1, color);
                        offset += direction;
                    }
//                    film.setBlock(0, 0, r, Math.min(direction, 32), 1, color);
                } else {
                    int i=0;
                    int offset=direction;

                    while (offset < viewPlane.resolution.hres) {
                        Color color = render(r, offset);
                        film.setBlock(0, offset, r, Math.min(direction, viewPlane.resolution.hres - 1 - offset), 1, color);
//                        film.setBlock(0, offset, r, Math.min(direction, 16), 1, color);
                        i++;
                        offset = 2*direction*i+direction;
                    }
                }
            }
            direction /= 2;
            s++;
        }
        LOGGER.info("rendering took " + t.get() + " ms");
    }

    */
/*
    8 Punkte

0 8   0
1 4           4                 8*i+4  i=0
2 2       2       6             4*i+2  i=0..1
3 1     1   3   5   7           2*i+1, i=0..3

    16 Punkte

        0 1 2 3 4 5 6 7 8 9 A B C D E F
0 16    0                                       32*i+16 i=-1
1  8                    8                       16*i+8  i=0
2  4            4               C               8*i+4  i=0..1    2^1-1
3  2        2       6       A       E           4*i+2  i=0..3    2^2-1
4  1      1   3   5   7   9   B   D   F         2*i+1, i=0..7    2^3-1

         *//*


}
*/
