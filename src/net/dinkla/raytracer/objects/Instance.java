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

    public void translate(Vector3DF v) {
        trans.translate(v);
    }

    public void translate(final float x, final float y, final float z) {
        trans.translate(x, y, z);
    }

    public void scale(Vector3DF v) {
        trans.scale(v);
    }

    public void scale(final float x, final float y, final float z) {
        trans.scale(x, y, z);
    }

    public void rotateX(final float phi) {
        trans.rotateX(phi);
    }

    public void rotateY(final float phi) {
        trans.rotateY(phi);
    }

    public void rotateZ(final float phi) {
        trans.rotateZ(phi);
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        Point3DF ro = trans.invMatrix.mult(ray.o);
        Vector3DF rd = trans.invMatrix.mult(ray.d);
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
        Point3DF ro = trans.invMatrix.mult(ray.o);
        Vector3DF rd = trans.invMatrix.mult(ray.d);
        Ray invRay = new Ray(ro, rd);
        if (object.shadowHit(invRay, tmin)) {
            return true;
        }
        return false;
    }

    @Override
    public BBox getBoundingBox() {
        BBox objectBbox = object.getBoundingBox();

        Point3DF v[] = new Point3DF[8];

        float vx[] = new float[8];
        float vy[] = new float[8];
        float vz[] = new float[8];

        vx[0] = objectBbox.p.x; vy[0] = objectBbox.p.y; vz[0] = objectBbox.p.z;
        vx[1] = objectBbox.q.x; vy[1] = objectBbox.p.y; vz[1] = objectBbox.p.z;
        vx[2] = objectBbox.q.x; vy[2] = objectBbox.q.y; vz[2] = objectBbox.p.z;
        vx[3] = objectBbox.p.x; vy[3] = objectBbox.q.y; vz[3] = objectBbox.p.z;

        vx[4] = objectBbox.p.x; vy[4] = objectBbox.p.y; vz[4] = objectBbox.q.z;
        vx[5] = objectBbox.q.x; vy[5] = objectBbox.p.y; vz[5] = objectBbox.q.z;
        vx[6] = objectBbox.q.x; vy[6] = objectBbox.q.y; vz[6] = objectBbox.q.z;
        vx[7] = objectBbox.p.x; vy[7] = objectBbox.q.y; vz[7] = objectBbox.q.z;

        // Transform these using the forward matrix
        for (int i=0; i<8;i++) {
            v[i] = new Point3DF(vx[i], vy[i], vz[i]);
            v[i] = trans.forwardMatrix.mult(v[i]);

        }

        // Compute the minimum values
        float x0 = MathUtils.K_HUGEVALUE;
        float y0 = MathUtils.K_HUGEVALUE;
        float z0 = MathUtils.K_HUGEVALUE;

        for (int j = 0; j <= 7; j++)  {
            if (v[j].x < x0)
                x0 = v[j].x;
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].y < y0)
                y0 = v[j].y;
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].z < z0)
                z0 = v[j].z;
        }

        // Compute the minimum values

        float x1 = -MathUtils.K_HUGEVALUE;
        float y1 = -MathUtils.K_HUGEVALUE;
        float z1 = -MathUtils.K_HUGEVALUE;

        for (int j = 0; j <= 7; j++) {
            if (v[j].x > x1)
                x1 = v[j].x;
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].y > y1)
                y1 = v[j].y;
        }

        for (int j = 0; j <= 7; j++) {
            if (v[j].z > z1)
                z1 = v[j].z;
        }

        // Assign values to the bounding box
        BBox bbox = new BBox(new Point3DF(x0, y0, z0), new Point3DF(x1, y1, z1));
        return bbox;
    }

    public void initialize() {
        object.initialize();
    }

}
