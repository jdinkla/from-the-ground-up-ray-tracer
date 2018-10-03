package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.math.Point3D
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

        Point3D min = PointUtilities.minPoints(compound.getMesh().vertices)
        LOGGER.info("PLY: minimum=" + min)

        for (Point3D p: compound.getMesh().vertices) {
            newMesh.vertices.add(new Point3D(p.minus(min)))
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

        Point3D min = PointUtilities.minPoints(compound.getMesh().vertices)
        LOGGER.info("PLY: minimum=" + min)

        Point3D max = PointUtilities.maxPoints(compound.getMesh().vertices)
        LOGGER.info("PLY: maximum=" + max)

        //Point3D mid = min.plus(max.minus(min).minus(0.5));
        Point3D mid = new Point3D(max.minus(min).times(0.5))

        Point3D q = new Point3D(-mid.x, min.y, min.z)
        for (Point3D p: compound.getMesh().vertices) {
//             newMesh.vertices.add(new Point3D(p.minus(min).plus(mid)))
//            newMesh.vertices.add(new Point3D(p.plus(mid)))
             newMesh.vertices.add(new Point3D(p.minus(q)))
        }

//         Point3D m2 = PointUtilities.minPoints(newMesh.vertices)
//         LOGGER.info("PLY: new minimum=" + m2)

        for (MeshTriangle tri: compound.objects) {
            tri.setMesh(newMesh)
            newCompound.add(tri)
        }

        newMesh.normals = compound.getMesh().normals
        return newCompound;
    }

}
