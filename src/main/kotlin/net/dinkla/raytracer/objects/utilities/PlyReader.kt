package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.acceleration.Grid.Companion.logInterval
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.FlatMeshTriangle
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.objects.mesh.MeshTriangle
import net.dinkla.raytracer.objects.mesh.SmoothMeshTriangle
import java.io.File

class PlyReader(val grid: Grid, val reverseNormal: Boolean = false, val isSmooth: Boolean = false) {

    val mesh = grid.mesh

    val materialOfGrid = grid.material!!

    var isInHeader = true
    var numLine = 0
    var numVertices = -1
    var numVerticesOrig = -1
    var numFaces = -1
    var numFacesOrig = -1
    var countFaces = 0

    fun add(mesh: Mesh, i: Int, countFaces: Int) {
        if (null == mesh.vertexFaces[i]) {
            mesh.vertexFaces[i] = ArrayList()
        }
        mesh.vertexFaces[i]?.add(countFaces)
    }

    fun read(fileName: String) = read(File(fileName).readLines())

    fun read(lines: List<String>): Ply {
        assert(materialOfGrid != null)
        lines.forEach { line ->
            handleLine(line)
        }
        if (isSmooth) {
            mesh.computeMeshNormals(grid.objects as java.util.ArrayList<MeshTriangle>)
        }
        return Ply(numVerticesOrig, numFacesOrig)
    }

    fun handleLine(line: String) {
        numLine++
        if (isInHeader) {
            when {
                isEndOfHeader(line) -> {
                    isInHeader = false
                }
                isElementVertex(line) -> {
                    numVertices = parseNumVertices(line)
                    numVerticesOrig = numVertices
                    mesh.vertices.ensureCapacity(numVertices)
                    if (isSmooth) {
                        mesh.vertexFaces.ensureCapacity(numVertices)
                        for (i in 0..numVertices) {
                            mesh.vertexFaces.add(i, ArrayList())
                        }
                    }
                }
                isElementFace(line) -> {
                    numFaces = parseNumFaces(line)
                    numFacesOrig = numFaces
                    grid.objects.ensureCapacity(numFaces)
                }
            }
        } else {
            when {
                numVertices > 0 -> {
                    val cs = line.split(" ")
                    if (cs.size < 3) {
                        throw RuntimeException("Not enough elements in line $numLine")
                    }
                    val x = cs[0].toDouble()
                    val y = cs[1].toDouble()
                    val z = cs[2].toDouble()
                    val p = Point3D(x, y, z)
                    mesh.vertices.add(p)
                    numVertices--
                    if (numLine % logInterval == 0) {
                        println("PLY: ${numVertices} vertices to read")
                    }
                }
                numFaces > 0 -> {
                    val cs = line.split(" ")
                    val size = cs[0].toInt()
                    if (cs.size < size + 1) {
                        throw RuntimeException("Not enough elements in line $numLine")
                    }
                    val i0 = cs[1].toInt()
                    val i1 = cs[2].toInt()
                    val i2 = cs[3].toInt()
                    var triangle: MeshTriangle
                    if (isSmooth) {
                        triangle = SmoothMeshTriangle(mesh, i0, i1, i2)
                        add(mesh, i0, countFaces)
                        add(mesh, i1, countFaces)
                        add(mesh, i2, countFaces)
                    } else {
                        triangle = FlatMeshTriangle(mesh, i0, i1, i2)
                    }
                    triangle.computeNormal(reverseNormal)
                    triangle.apply {
                        this.material = materialOfGrid
                    }
                    grid.add(triangle)
                    numFaces--
                    countFaces++
                    if (numLine % logInterval == 0) {
                        println("PLY: ${numFaces} faces to read")
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
        fun isEndOfHeader(line: String) = line == "end_header"

        val elemVertex = "element\\W+vertex".toRegex()
        fun isElementVertex(line: String): Boolean = elemVertex.containsMatchIn(line)

        fun parseNumVertices(line: String): Int {
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