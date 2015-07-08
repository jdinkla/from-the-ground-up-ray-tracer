package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.mesh.FlatMeshTriangle
import net.dinkla.raytracer.objects.acceleration.Grid
import org.apache.log4j.Logger
import net.dinkla.raytracer.math.PointUtilities
import net.dinkla.raytracer.objects.mesh.MeshTriangle
import net.dinkla.raytracer.objects.mesh.SmoothMeshTriangle
import net.dinkla.raytracer.objects.acceleration.SparseGrid
import net.dinkla.raytracer.objects.compound.Compound

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 17.05.2010
 * Time: 08:37:38
 * To change this template use File | Settings | File Templates.
 */
class PlyReader {

    static final Logger LOGGER = Logger.getLogger(PlyReader.class);

    static boolean logProgress = true

    static int logInterval = 10000 

    static void read(Compound compound, String filename, boolean reverseNormal, boolean isSmooth) {
        // TODO duck typing ist ein wenig unsauber
        Mesh mesh = compound.mesh

        LOGGER.info("PLY: reading file '${filename}'")        
        boolean isInHeader = true
        int numLine = 0
        int numVertices = 0
        int numFaces = 0
        int countFaces = 0
        new File(filename).eachLine { String line ->
            numLine++
            if (isInHeader) {
                if (line =~ /end_header/) {
                    isInHeader = false
                    return
                }
                if (line =~ /element\W+vertex/) {
                    line = line.replaceFirst(/element\W+vertex\W+/, '')
                    numVertices = Integer.valueOf(line)
                    mesh.vertices.ensureCapacity(numVertices)
                    if (isSmooth) {
                        mesh.vertexFaces.ensureCapacity(numVertices)
                        for (int i=0; i<numVertices; i++) {
                            mesh.vertexFaces.add(i, new ArrayList())
                        }
                    }
                    LOGGER.info("PLY: ${numVertices} vertices")
                    return
                }
                if (line =~ /element\W+face/) {
                    line = line.replaceFirst(/element\W+face\W+/, '')
                    numFaces = Integer.valueOf(line)
                    compound.objects.ensureCapacity(numFaces)
                    LOGGER.info("PLY: ${numFaces} faces")
                    return
                }
            } else {
                if (numVertices > 0) {
                    def cs = line.split(/[ ]+/)
//                    println "line=$line"
//                    println "cs=$cs"
                    if (cs.size() < 3) {
                        throw new RuntimeException("Not enough elements in line $numLine")
                    }
                    float x = Float.valueOf(cs[0])
                    float y = Float.valueOf(cs[1])
                    float z = Float.valueOf(cs[2])
                    Point3D p = new Point3D(x, y, z)
                    mesh.vertices.add(p)
                    numVertices--
                    if (numLine % logInterval == 0) {
                        LOGGER.info("PLY: ${numVertices} vertices to read")
                    }
                } else if (numFaces > 0) {
                    def cs = line.split(/[ ]+/)
//                    println "line=$line"
//                    println "cs=$cs"
                    int size = Integer.valueOf(cs[0])
                    if (cs.size() < size + 1) {
                        throw new RuntimeException("Not enough elements in line $numLine")
                    }
                    int i0 = Integer.valueOf(cs[1])
                    int i1 = Integer.valueOf(cs[2])
                    int i2 = Integer.valueOf(cs[3])
                    MeshTriangle triangle;
                    if (isSmooth) {
                        triangle = new SmoothMeshTriangle(mesh, i0, i1, i2);
                        add(mesh, i0, countFaces)
                        add(mesh, i1, countFaces)
                        add(mesh, i2, countFaces)
                    } else {
                        triangle = new FlatMeshTriangle(mesh, i0, i1, i2);
                    }
                    triangle.computeNormal(reverseNormal)
                    compound.add(triangle)

                    numFaces--
                    countFaces++
                    if (numLine % logInterval == 0) {
                        LOGGER.info("PLY: ${numFaces} faces to read")
                    }
                } else if (!(line=~/^\W*$/)) {
                    throw new RuntimeException("Unknown file format in line $numLine")
                }
            }
            //println "$isInHeader $numVertices $numFaces $line"
        }
        if (isSmooth) {
            mesh.computeMeshNormals(compound.objects)
        }
    }

    private static def add(Mesh mesh, int i, int countFaces) {
        if (null == mesh.vertexFaces.get(i)) {
            mesh.vertexFaces.add(i, new ArrayList())
        }
        mesh.vertexFaces.get(i).add(countFaces)
    }

}
