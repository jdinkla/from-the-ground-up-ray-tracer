package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.objects.Sphere;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.06.2010
 * Time: 09:24:07
 * To change this template use File | Settings | File Templates.
 */
public class KDTreeTest {

    @Test
    public void testBuild() throws Exception {

        Sphere s1 = new Sphere(new Point3D(1.0f, 0.0f, 0.0f), 0.25f);
        Sphere s2 = new Sphere(new Point3D(2.0f, 0.0f, 0.0f), 0.25f);
        Sphere s3 = new Sphere(new Point3D(1.0f, 0.0f, 1.0f), 0.25f);
        Sphere s4 = new Sphere(new Point3D(2.0f, 0.0f, 1.0f), 0.25f);

        List<GeometricObject> ls = new ArrayList<GeometricObject>();
        ls.add(s1);
        ls.add(s2);
        ls.add(s3);
        ls.add(s4);

        BBox bbox = BBox.Companion.create(ls);
        
// TODO MOVE        AbstractNode tree = KDTree.build(ls, bbox, 0);

        //System.out.println("tree=" + tree);
    }


}
