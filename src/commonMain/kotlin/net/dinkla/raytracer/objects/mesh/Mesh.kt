package net.dinkla.raytracer.objects.mesh

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D

class Mesh {

    var vertices: ArrayList<Point3D> = ArrayList()

    //    public List<Integer> indices;
    var normals: ArrayList<Normal> = ArrayList()

    //    public List<Float> us;
    //    public List<Float> vs;
    var vertexFaces: ArrayList<MutableList<Int>?> = ArrayList()
    //    public int numVerticesLeft;
    //    public int numTriangles;

    var material: IMaterial? = null

    fun computeMeshNormals(objects: ArrayList<MeshTriangle>) {
        normals.ensureCapacity(vertices.size)

        for (index in vertices.indices) {
            var normal = Normal.ZERO

            //            for (int j = 0; j < vertexFaces.get(index).size(); j++) {
            //                normal = new Normal(normal.plus(objects.get(vertexFaces.get(index).get(j)).getNormal()));
            //            }

            for (i in vertexFaces[index]!!) {
                val n = objects[i].normal
                if (null != n) {
                    normal = Normal.create(normal.plus(n))
                }
            }

            // The following code attempts to avoid (nan, nan, nan) normalised normals when all components = 0

            if (normal.x == 0.0 && normal.y == 0.0 && normal.z == 0.0) {
                normal = Normal(normal.x, 1.0, normal.z)
            } else {
                normal = normal.normalize()
            }

            normals.add(index, normal)
        }
    }
}
