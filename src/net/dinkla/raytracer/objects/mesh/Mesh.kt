package net.dinkla.raytracer.objects.mesh

import net.dinkla.raytracer.materials.Material
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.mesh.MeshTriangle

import java.util.ArrayList

class Mesh {

    var vertices: List<Point3D>
    //    public List<Integer> indices;
    var normals: ArrayList<Normal>
    //    public List<Float> us;
    //    public List<Float> vs;
    var vertexFaces: ArrayList<List<Int>>
    //    public int numVertices;
    //    public int numTriangles;

    var material: Material?

    init {
        vertices = ArrayList()
        //        indices = new ArrayList<Integer>();
        normals = ArrayList()
        //        us = new ArrayList<Float>();
        //        vs = new ArrayList<Float>();
        vertexFaces = ArrayList()
        //        numVertices = 0;
        //        numTriangles = 0;
        material = null
    }

    fun computeMeshNormals(objects: List<MeshTriangle>) {
        normals.ensureCapacity(vertices.size)

        for (index in vertices.indices) {
            var normal = Normal.ZERO

            //            for (int j = 0; j < vertexFaces.get(index).size(); j++) {
            //                normal = new Normal(normal.plus(objects.get(vertexFaces.get(index).get(j)).getNormal()));
            //            }

            for (i in vertexFaces[index]) {
                val n = objects[i].normal
                if (null != n) {
                    normal = Normal(normal.plus(n))
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

        // erase the vertex_faces arrays because we have now finished with them
        /*
        for (int index = 0; index < vertices.size(); index++)
            for (int j = 0; j < vertexFaces.get(index).size(); j++)
                mesh_ptr->vertex_faces[index].erase (mesh_ptr->vertex_faces[index].begin(), mesh_ptr->vertex_faces[index].end());

        mesh_ptr->vertex_faces.erase (mesh_ptr->vertex_faces.begin(), mesh_ptr->vertex_faces.end());
        cout << "finished constructing normals" << endl;
    */
    }

}
