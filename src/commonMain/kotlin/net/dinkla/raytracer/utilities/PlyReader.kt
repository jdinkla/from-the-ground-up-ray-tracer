package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.acceleration.CompoundWithMesh
import net.dinkla.raytracer.objects.acceleration.Grid.Companion.logInterval
import net.dinkla.raytracer.objects.mesh.FlatMeshTriangle
import net.dinkla.raytracer.objects.mesh.MeshTriangle
import net.dinkla.raytracer.objects.mesh.SmoothMeshTriangle

/**
 * Raised when a PLY header declares more vertices or faces than the configured sanity bound allows.
 * Declaring (e.g.) billions of vertices would otherwise drive the up-front `ensureCapacity`
 * allocations and per-element reads into an [OutOfMemoryError]; this typed failure surfaces the
 * problem with a clear message instead.
 */
class PlyLimitExceededException(
    message: String,
) : IllegalArgumentException(message)

class PlyReader(
    val material: IMaterial,
    val reverseNormal: Boolean = false,
    val isSmooth: Boolean = false,
    val compound: CompoundWithMesh,
    private val maxVertices: Int = DEFAULT_MAX_VERTICES,
    private val maxFaces: Int = DEFAULT_MAX_FACES,
) {
    private val mesh = compound.mesh
    private var isInHeader = true
    private var numLine = 0
    private var numVerticesLeft = -1
    private var numFacesLeft = -1
    private var numVerticesOrig = -1
    private var numFacesOrig = -1
    private var countFaces = 0

    init {
        compound.material = material
    }

    fun read(lines: List<String>): Ply {
        lines.forEach { line ->
            handleLine(line)
        }
        if (isSmooth) {
            mesh.computeMeshNormals(compound.objects as ArrayList<MeshTriangle>)
        }
        return Ply(numVerticesOrig, numFacesOrig, compound)
    }

    private fun handleLine(line: String) {
        numLine++
        if (isInHeader) {
            handleHeader(line)
        } else {
            handleBody(line)
        }
    }

    private fun handleHeader(line: String) {
        when {
            isEndOfHeader(line) -> {
                isInHeader = false
            }

            isElementVertex(line) -> {
                numVerticesLeft = parseNumVertices(line)
                requireWithinLimit(numVerticesLeft, maxVertices, "vertices")
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
                requireWithinLimit(numFacesLeft, maxFaces, "faces")
                numFacesOrig = numFacesLeft
                compound.objects.ensureCapacity(numFacesLeft)
            }
        }
    }

    private fun handleBody(line: String) =
        when {
            numVerticesLeft > 0 -> handleVerticesLeft(line)
            numFacesLeft > 0 -> handleFacesLeft(line)
            isWhiteSpace(line) -> {
            }
            else -> {
                throw IllegalArgumentException("Unknown file format in line $numLine: `$line`")
            }
        }

    private fun handleFacesLeft(line: String) {
        val cs = line.split(" ")
        val size = cs[0].toInt()
        if (cs.size < size + 1) {
            throw IllegalArgumentException("Not enough elements in line $numLine")
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
            Logger.debug("PLY: $numFacesLeft faces to read")
        }
    }

    private fun handleVerticesLeft(line: String) {
        val cs = line.split(" ")
        if (cs.size < 3) {
            throw IllegalArgumentException("Not enough elements in line $numLine")
        }
        val x = cs[0].toDouble()
        val y = cs[1].toDouble()
        val z = cs[2].toDouble()
        val p = Point3D(x, y, z)
        mesh.vertices.add(p)
        numVerticesLeft--
        if (numLine % logInterval == 0) {
            Logger.debug("PLY: $numVerticesLeft vertices to read")
        }
    }

    private fun add(
        i: Int,
        countFaces: Int,
    ) {
        if (null == mesh.vertexFaces[i]) {
            mesh.vertexFaces[i] = ArrayList()
        }
        mesh.vertexFaces[i]?.add(countFaces)
    }

    private fun requireWithinLimit(
        declared: Int,
        limit: Int,
        kind: String,
    ) {
        if (declared > limit) {
            throw PlyLimitExceededException(
                "PLY model declares $declared $kind in line $numLine, exceeds limit $limit",
            )
        }
    }

    companion object {
        /**
         * Sanity bound on the declared vertex count of a PLY header. Set well above the largest
         * bundled/downloaded model (Isis ~47k vertices), so real models load unaffected; it only
         * trips on a malformed or hostile header that would otherwise force a huge allocation.
         */
        const val DEFAULT_MAX_VERTICES: Int = 50_000_000

        /** Sanity bound on the declared face count of a PLY header; see [DEFAULT_MAX_VERTICES]. */
        const val DEFAULT_MAX_FACES: Int = 100_000_000
    }
}

private fun isWhiteSpace(line: String) = line.trim().isEmpty()

private fun isEndOfHeader(line: String) = line == "end_header"

internal fun isElementVertex(line: String): Boolean = elemVertex.containsMatchIn(line)

internal fun isElementFace(line: String): Boolean = elemFace.containsMatchIn(line)

internal fun parseNumVertices(line: String): Int = line.replace(elemVertex, "").trim().toInt()

internal fun parseNumFaces(line: String): Int = line.replace(elemFace, "").trim().toInt()

private val elemFace = "element\\W+face".toRegex()
private val elemVertex = "element\\W+vertex".toRegex()
