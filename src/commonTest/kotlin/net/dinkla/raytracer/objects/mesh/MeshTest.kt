package net.dinkla.raytracer.objects.mesh

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.shouldBeApprox

class MeshTest : StringSpec({
    "computeMeshNormals averages and normalizes the face normals around each vertex" {
        // One face (the right triangle in z = 0) whose face normal is +z; every vertex belongs only to it,
        // so each vertex normal comes out as the normalized +z.
        val mesh =
            Mesh().apply {
                vertices.add(Point3D(0.0, 0.0, 0.0))
                vertices.add(Point3D(1.0, 0.0, 0.0))
                vertices.add(Point3D(0.0, 1.0, 0.0))
                vertexFaces.add(mutableListOf(0))
                vertexFaces.add(mutableListOf(0))
                vertexFaces.add(mutableListOf(0))
            }
        val face = MeshTriangle(mesh, 0, 1, 2).apply { computeNormal(reverseNormal = false) }

        mesh.computeMeshNormals(arrayListOf(face))

        mesh.normals[0] shouldBeApprox Normal.FORWARD
        mesh.normals[1] shouldBeApprox Normal.FORWARD
        mesh.normals[2] shouldBeApprox Normal.FORWARD
    }

    "computeMeshNormals defaults a vertex with no contributing faces to the +y normal" {
        // A vertex whose face list is empty accumulates a zero normal; the all-zero guard substitutes
        // (0, 1, 0) to avoid a NaN normalized normal.
        val mesh =
            Mesh().apply {
                vertices.add(Point3D(0.0, 0.0, 0.0))
                vertexFaces.add(mutableListOf())
            }

        mesh.computeMeshNormals(arrayListOf())

        mesh.normals[0] shouldBe Normal.UP
    }

    "computeMeshNormals skips faces whose normal has not been computed (null)" {
        // The single face touching the vertex has a null (uncomputed) normal, so it contributes nothing
        // and the vertex falls back to the all-zero default normal.
        val mesh =
            Mesh().apply {
                vertices.add(Point3D(0.0, 0.0, 0.0))
                vertices.add(Point3D(1.0, 0.0, 0.0))
                vertices.add(Point3D(0.0, 1.0, 0.0))
                vertexFaces.add(mutableListOf(0))
                vertexFaces.add(mutableListOf(0))
                vertexFaces.add(mutableListOf(0))
            }
        val uncomputed = MeshTriangle(mesh) // single-arg constructor leaves normal == null

        mesh.computeMeshNormals(arrayListOf(uncomputed))

        mesh.normals[0] shouldBe Normal.UP
    }
})
