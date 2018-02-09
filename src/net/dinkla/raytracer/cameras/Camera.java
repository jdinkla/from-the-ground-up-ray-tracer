package net.dinkla.raytracer.cameras;

import net.dinkla.raytracer.cameras.lenses.ILens;
import net.dinkla.raytracer.cameras.render.IRenderer;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.utilities.Timer;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 12:11:32
 * To change this template use File | Settings | File Templates.
 *
 * TODO: Kamera konfigurierbarer und modularer
 * 
 * 1. Render: Single | Sample
 * 2. Sequential | Parallel
 * 3. Lens: Pinhole, FishEye, Spherical, ThinLens, Orthographic
 * 4. Iterative ?  
 */
public class Camera {

    static final Logger LOGGER = Logger.getLogger(Camera.class);

    public Point3D eye;
    public Point3D lookAt;
    public Vector3D up;
    public Basis uvw;
    
    protected ILens lens;
    public IRenderer render2;
    
    public Camera(final ILens lens, final IRenderer render2) {
        this.lens = lens;
        this.render2 = render2;
        setup(Point3D.Companion.getDEFAULT_CAMERA(), Point3D.Companion.getORIGIN(), Vector3D.Companion.getUP());
    }

    public final void setup(Point3D eye, Point3D lookAt, Vector3D up) {
        this.eye = eye;
        this.lookAt = lookAt;
        this.up = up;
        computeUVW();
    }

    public final void computeUVW() {
        uvw = new Basis(eye, lookAt, up);
        lens.setEye(eye);
        lens.setUvw(uvw);
    }

    public void render(IFilm film, final int frame) {
        LOGGER.info("rendering: eye=" + eye + ", lookAt=" + lookAt + ", up=" + up);
        Timer t = new Timer();
        t.start();
        render2.render(film, frame);
        t.stop();
        LOGGER.info("rendering took " + t.getDuration() + " ms");
    }

    public ILens getLens() {
        return lens;
    }

    public void setLens(ILens lens) {
        this.lens = lens;
        this.lens.setEye(eye);
        this.lens.setUvw(uvw);        
    }
    
}
