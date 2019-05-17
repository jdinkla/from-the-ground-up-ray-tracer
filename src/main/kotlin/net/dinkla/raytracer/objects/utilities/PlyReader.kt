package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.acceleration.CompoundWithMesh
import net.dinkla.raytracer.objects.acceleration.Grid.Companion.logInterval
import net.dinkla.raytracer.objects.mesh.FlatMeshTriangle
import net.dinkla.raytracer.objects.mesh.MeshTriangle
import net.dinkla.raytracer.objects.mesh.SmoothMeshTriangle
import org.slf4j.LoggerFactory
import java.io.File

class PlyReader(val material: IMaterial,
                val reverseNormal: Boolean = false,
                val isSmooth: Boolean = false,
                val compound: CompoundWithMesh) {

    private val mesh = compound.mesh

    init {
        compound.material = material
    }

    var isInHeader = true
    var numLine = 0

    private var numVerticesLeft = -1
    private var numFacesLeft = -1

    var numVerticesOrig = -1
    var numFacesOrig = -1
    var countFaces = 0

    fun add(i: Int, countFaces: Int) {
        if (null == mesh.vertexFaces[i]) {
            mesh.vertexFaces[i] = ArrayList()
        }
        mesh.vertexFaces[i]?.add(countFaces)
    }

    fun read(fileName: String) = read(File(fileName).readLines())

    fun read(lines: List<String>): Ply {
        lines.forEach { line ->
            handleLine(line)
        }
        if (isSmooth) {
            mesh.computeMeshNormals(compound.objects as java.util.ArrayList<MeshTriangle>)
        }
        return Ply(numVerticesOrig, numFacesOrig, compound)
    }

    fun handleLine(line: String) {
        numLine++
        if (isInHeader) {
            when {
                isEndOfHeader(line) -> {
                    isInHeader = false
                }
                isElementVertex(line) -> {
                    numVerticesLeft = parseNumVertices(line)
                    numVerticesOrig = numVerticesLeft
                    mesh.vertices.ensureCapacity(numVerticesLeft)
                    if (isSmooth) {
                        mesh.vertexFaces.ensureCapacity(numVerticesLeft)
                        for (i in 0..numVerticesLeft) {
                            mesh.vertexFaces.add(i, ArrayList())
                        }
                    }
                }
                isElementFace(line) -> {
                    numFacesLeft = parseNumFaces(line)
                    numFacesOrig = numFacesLeft
                    compound.objects.ensureCapacity(numFacesLeft)
                }
            }
        } else {
            when {
                numVerticesLeft > 0 -> {
                    val cs = line.split(" ")
                    if (cs.size < 3) {
                        throw RuntimeException("Not enough elements in line $numLine")
                    }
                    val x = cs[0].toDouble()
                    val y = cs[1].toDouble()
                    val z = cs[2].toDouble()
                    val p = Point3D(x, y, z)
                    mesh.vertices.add(p)
                    numVerticesLeft--
                    if (numLine % logInterval == 0) {
                        LOG.debug("PLY: ${numVerticesLeft} vertices to read")
                    }
                }
                numFacesLeft > 0 -> {
                    val cs = line.split(" ")
                    val size = cs[0].toInt()
                    if (cs.size < size + 1) {
                        throw RuntimeException("Not enough elements in line $numLine")
                    }
                    val i0 = cs[1].toInt()
                    val i1 = cs[2].toInt()
                    val i2 = cs[3].toInt()
                    val triangle: MeshTriangle
                    if (isSmooth) {
                        triangle = SmoothMeshTriangle(mesh, i0, i1, i2)
                        add(i0, countFaces)
                        add(i1, countFaces)
                        add(i2, countFaces)
                    } else {
                        triangle = FlatMeshTriangle(mesh, i0, i1, i2)
                    }
                    triangle.computeNormal(reverseNormal)
                    triangle.material = this.material
                    compound.add(triangle)
                    numFacesLeft--
                    countFaces++
                    if (numLine % logInterval == 0) {
                        LOG.debug("PLY: ${numFacesLeft} faces to read")
                    }
                }
                isWhiteSpace(line) -> {
                }
                else -> {
                    throw RuntimeException("Unknown file format in line $numLine: `$line`")
                }
            }
        }
    }

    companion object {

        internal val LOG = LoggerFactory.getLogger(this::class.java)

        fun isEndOfHeader(line: String) = line == "end_header"

        val elemVertex = "element\\W+vertex".toRegex()
        fun isElementVertex(line: String): Boolean = elemVertex.containsMatchIn(line)

        internal fun parseNumVertices(line: String): Int {
            val rest = line.replace(elemVertex, "").trim()
            return Integer.parseInt(rest)
        }

        val elemFace = "element\\W+face".toRegex()
        fun isElementFace(line: String): Boolean = elemFace.containsMatchIn(line)

        fun parseNumFaces(line: String): Int {
            val rest = line.replace(elemFace, "").trim()
            return Integer.parseInt(rest)
        }

        fun isWhiteSpace(line: String) = line.trim().length == 0

    }

}