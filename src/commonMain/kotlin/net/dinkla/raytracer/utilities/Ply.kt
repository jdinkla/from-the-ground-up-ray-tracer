package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.objects.acceleration.Acceleration
import net.dinkla.raytracer.objects.acceleration.CompoundWithMesh

class Ply(
    val numVertices: Int,
    val numFaces: Int,
    val compound: CompoundWithMesh,
) {
    companion object {
        fun fromFile(
            fileName: String,
            material: IMaterial,
            isSmooth: Boolean = false,
            reverseNormal: Boolean = false,
            type: Acceleration = Acceleration.GRID,
        ): Ply {
            val compound = type.build()
            val plyReader =
                PlyReader(
                    material,
                    isSmooth = isSmooth,
                    compound = compound,
                    reverseNormal = reverseNormal,
                )
            return plyReader.read(read(fileName))
        }
    }
}
