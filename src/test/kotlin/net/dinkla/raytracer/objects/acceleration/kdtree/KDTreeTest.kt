package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.Sphere

import org.junit.jupiter.api.Test

class KDTreeTest {

    @Test
    @Throws(Exception::class)
    fun testBuild() {

        val s1 = Sphere(Point3D(1.0, 0.0, 0.0), 0.25)
        val s2 = Sphere(Point3D(2.0, 0.0, 0.0), 0.25)
        val s3 = Sphere(Point3D(1.0, 0.0, 1.0), 0.25)
        val s4 = Sphere(Point3D(2.0, 0.0, 1.0), 0.25)

        val ls = ArrayList<GeometricObject>()
        ls.add(s1)
        ls.add(s2)
        ls.add(s3)
        ls.add(s4)

        val bbox = BBox.create(ls)

        // TODO MOVE        AbstractNode tree = KDTree.build(ls, bbox, 0);

        //System.out.println("tree=" + tree);
    }


}
