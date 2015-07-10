package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.math.Basis;
import net.dinkla.raytracer.math.Point3DF;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 07.06.2010
 * Time: 19:55:41
 * To change this template use File | Settings | File Templates.
 */
abstract public class AbstractLens implements ILens {

    protected ViewPlane viewPlane;
    protected Basis uvw;
    protected Point3DF eye;

    public AbstractLens(ViewPlane viewPlane) {
        assert(null != viewPlane);
        this.viewPlane = viewPlane;
        this.eye = null;
        this.uvw = null;
    }

    public Point3DF getEye() {
        return eye;
    }

    public void setEye(Point3DF eye) {
        this.eye = eye;
    }

    public Basis getUvw() {
        return uvw;
    }

    public void setUvw(Basis uvw) {
        this.uvw = uvw;
    }

    public ViewPlane getViewPlane() {
        return viewPlane;
    }

    public void setViewPlane(ViewPlane viewPlane) {
        this.viewPlane = viewPlane;
    }
    
}
