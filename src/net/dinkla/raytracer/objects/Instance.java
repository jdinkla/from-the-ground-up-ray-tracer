package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 17.04.2010
 * Time: 09:17:31
 * To change this template use File | Settings | File Templates.
 */
public class Instance extends GeometricObject {

    public final GeometricObject object;
    boolean transformTexture;
    AffineTransformation trans;

    public Instance(GeometricObject object) {
        this.object = object;
        this.trans = new AffineTransformation();
    }

    public void translate(Vector3D v) {
        trans.translate(v);
    }

    public void translate(final double x, final double y, final double z) {
        trans.translate(x, y, z);
    }

    public void scale(Vector3D v) {
        trans.scale(v);
    }

    public void scale(final double x, final double y, final double z) {
        trans.scale(x, y, z);
    }

    public void rotateX(final double phi) {
        trans.rotateX(phi);
    }

    public void rotateY(final double phi) {
        trans.rotateY(phi);
    }

    public void rotateZ(final double phi) {
        trans.rotateZ(phi);
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        Point3D ro = trans.invMatrix.mult(ray.getO());
        Vector3D rd = trans.invMatrix.mult(ray.getD());
        Ray invRay = new Ray(ro, rd);
        if (object.hit(invRay, sr)) {
            // TODO: Instance hit?
            Normal tmp = trans.invMatrix.mult(sr.getNormal());
            sr.setNormal(tmp.normalize());
            if (null != object.getMaterial()) {
                sr.setObject(object);
            }
//            if (!transformTexture) {
//            }
            return true;
        }
        return false;
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
        Point3D ro = trans.invMatrix.mult(ray.getO());
        Vector3D rd = trans.invMatrix.mult(ray.getD());
        Ray invRay = new Ray(ro, rd);
        if (object.shadowHit(invRay, tmin)) {
            return true;
        }
        return false;
    }

    @Override
    public BBox getBoundingBox() {
        BBox objectBbox = object.getBoundingBox();

        Point3D v[] = new Point3D[8];

        double vx[] = new double[8];
        double vy[] = new double[8];
        double vz[] = new double[8];

        vx[0] = objectBbox.getP().getX(); vy[0] = objectBbox.getP().getY(); vz[0] = objectBbox.getP().getZ();
        vx[1] = objectBbox.getQ().getX(); vy[1] = objectBbox.getP().getY(); vz[1] = objectBbox.getP().getZ();
        vx[2] = objectBbox.getQ().getX(); vy[2] = objectBbox.getQ().getY(); vz[2] = objectBbox.getP().getZ();
        vx[3] = objectBbox.getP().getX(); vy[3] = objectBbox.getQ().getY(); vz[3] = objectBbox.getP().getZ();

        vx[4] = objectBbox.getP().getX(); vy[4] = objectBbox.getP().getY(); vz[4] = objectBbox.getQ().getZ();
        vx[5] = objectBbox.getQ().getX(); vy[5] = objectBbox.getP().getY(); vz[5] = objectBbox.getQ().getZ();
        vx[6] = objectBbox.getQ().getX(); vy[6] = objectBbox.getQ().getY(); vz[6] = objectBbox.getQ().getZ();
        vx[7] = objectBbox.getP().getX(); vy[7] = objectBbox.getQ().getY(); vz[7] = objectBbox.getQ().getZ();

        // Transform these using the forward matrix
        for (int i=0; i<8;i++) {
            v[i] = new Point3D(vx[i], vy[i], vz[i]);
            v[i] = trans.forwardMatrix.mult(v[i]);

        }

        // Compute the minimum values
        double x0 = MathUtils.K_HUGEVALUE;
        double y0 = MathUtils.K_HUGEVALUE;
        double z0 = MathUtils.K_HUGEVALUE;

        for (int j = 0; j <= 7; j++)  {
            if (v[j].getX() < x0)
                x0 = v[j].getX();
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].getY() < y0)
                y0 = v[j].getY();
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].getZ() < z0)
                z0 = v[j].getZ();
        }

        // Compute the minimum values

        double x1 = -MathUtils.K_HUGEVALUE;
        double y1 = -MathUtils.K_HUGEVALUE;
        double z1 = -MathUtils.K_HUGEVALUE;

        for (int j = 0; j <= 7; j++) {
            if (v[j].getX() > x1)
                x1 = v[j].getX();
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].getY() > y1)
                y1 = v[j].getY();
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].getZ() > z1)
                z1 = v[j].getZ();
        }

        // Assign values to the bounding box
        BBox bbox = new BBox(new Point3D(x0, y0, z0), new Point3D(x1, y1, z1));
        return bbox;
    }

    public void initialize() {
        object.initialize();
    }

}
