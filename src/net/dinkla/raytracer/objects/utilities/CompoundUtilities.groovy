package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.math.PointUtilities
import net.dinkla.raytracer.objects.mesh.MeshTriangle
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.06.2010
 * Time: 18:53:47
 * To change this template use File | Settings | File Templates.
 */
class CompoundUtilities {

    static final Logger LOGGER = Logger.getLogger(CompoundUtilities.class);

    static Compound norm(Compound compound) {
        Mesh newMesh = new Mesh()
        Compound newCompound = compound.class.newInstance(newMesh)

        Point3DF min = PointUtilities.minPoints(compound.getMesh().vertices)
        LOGGER.info("PLY: minimum=" + min)

        for (Point3DF p: compound.getMesh().vertices) {
            newMesh.vertices.add(new Point3DF(p.minus(min)))
        }

        for (MeshTriangle tri: compound.objects) {
            tri.setMesh(newMesh)
            newCompound.add(tri)
        }

        newMesh.normals = compound.getMesh().normals
        return newCompound;
    }

    static Compound norm2(Compound compound) {
        Mesh newMesh = new Mesh()
        Compound newCompound = compound.class.newInstance(newMesh)

        Point3DF min = PointUtilities.minPoints(compound.getMesh().vertices)
        LOGGER.info("PLY: minimum=" + min)

        Point3DF max = PointUtilities.maxPoints(compound.getMesh().vertices)
        LOGGER.info("PLY: maximum=" + max)

        //Point3DF mid = min.plus(max.minus(min).mult(0.5f));
        Point3DF mid = new Point3DF(max.minus(min).mult(0.5f))

        Point3DF q = new Point3DF(-mid.x, min.y, min.z)
        for (Point3DF p: compound.getMesh().vertices) {
//             newMesh.vertices.add(new Point3DF(p.minus(min).plus(mid)))
//            newMesh.vertices.add(new Point3DF(p.plus(mid)))
             newMesh.vertices.add(new Point3DF(p.minus(q)))
        }

//         Point3DF m2 = PointUtilities.minPoints(newMesh.vertices)
//         LOGGER.info("PLY: new minimum=" + m2)

        for (MeshTriangle tri: compound.objects) {
            tri.setMesh(newMesh)
            newCompound.add(tri)
        }

        newMesh.normals = compound.getMesh().normals
        return newCompound;
    }

}
