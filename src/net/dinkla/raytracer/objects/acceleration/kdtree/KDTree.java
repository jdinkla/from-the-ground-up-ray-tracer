package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.compound.Compound;
import net.dinkla.raytracer.objects.mesh.Mesh;
import net.dinkla.raytracer.utilities.Counter;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 09.06.2010
 * Time: 23:14:43
 * To change this template use File | Settings | File Templates.
 */
public class KDTree extends Compound {

    static final Logger LOGGER = Logger.getLogger(KDTree.class);

    static int maxDepth = 15;

    public IKDTreeBuilder builder;
    
    protected AbstractNode root;

    public Mesh mesh;

    public KDTree() {
        super();
        mesh = null;
        root = null;
        builder = new SpatialMedianBuilder();
    }

    public KDTree(Mesh mesh) {
        super();
        this.mesh = mesh;
        root = null;
        builder = new SpatialMedianBuilder();
    }

    public void initialize() {
        super.initialize();
        int n = 8 + (int) (1.3 * (Math.log(getObjects().size()) / Math.log(2)));
        if (n != builder.getMaxDepth()) {
            LOGGER.warn("Ideal maxDepth = " + n + ", but set to " + builder.getMaxDepth());
        }
//        builder.setMaxDepth(n);
        root = builder.build(this, getBoundingBox());
        Statistics.statistics(this);
        LOGGER.info(root.printBBoxes(0));
    }

    @Override
    public boolean hit(Ray ray, Hit sr) {
        Counter.Companion.count("KDTree.hit");
        return root.hit(ray, sr);
    }

    @Override
    public boolean shadowHit(Ray ray, ShadowHit tmin) {
        Counter.Companion.count("KDTree.shadowHit");
        Hit h = new Hit();
        h.setT(tmin.getT());
        boolean b = hit(ray, h);
        tmin.setT(h.getT());
        return b;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }


}
