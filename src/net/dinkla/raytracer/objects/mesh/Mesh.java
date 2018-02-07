package net.dinkla.raytracer.objects.mesh;

import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.objects.mesh.MeshTriangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 28.04.2010
 * Time: 20:08:10
 * To change this template use File | Settings | File Templates.
 */
public class Mesh {

    public List<Point3D> vertices;
//    public List<Integer> indices;
    public ArrayList<Normal> normals;
//    public List<Float> us;
//    public List<Float> vs;
    public ArrayList<List<Integer>> vertexFaces;
//    public int numVertices;
//    public int numTriangles;

    protected Material material;

    public Mesh() {
        vertices = new ArrayList<Point3D>();
//        indices = new ArrayList<Integer>();
        normals = new ArrayList<Normal>();
//        us = new ArrayList<Float>();
//        vs = new ArrayList<Float>();
        vertexFaces = new ArrayList<List<Integer>>();
//        numVertices = 0;
//        numTriangles = 0;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void computeMeshNormals(List<MeshTriangle> objects) {
        normals.ensureCapacity(vertices.size());

        for (int index = 0; index < vertices.size(); index++) {
            Normal normal = Normal.Companion.getZERO();

//            for (int j = 0; j < vertexFaces.get(index).size(); j++) {
//                normal = new Normal(normal.plus(objects.get(vertexFaces.get(index).get(j)).getNormal()));
//            }

            for (Integer i : vertexFaces.get(index)) {
                normal = new Normal(normal.plus(objects.get(i).getNormal()));
            }

            // The following code attempts to avoid (nan, nan, nan) normalised normals when all components = 0

            if (normal.getX() == 0.0 && normal.getY() == 0.0 && normal.getZ() == 0.0) {
                normal = new Normal(normal.getX(), 1.0, normal.getZ());
            } else {
                normal = normal.normalize();
            }
            
            normals.add(index, normal);
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
