package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree

enum class Acceleration(
    val build: () -> CompoundWithMesh,
) {
    GRID({ Grid() }),
    KDTREE({ KDTree() }),
}
