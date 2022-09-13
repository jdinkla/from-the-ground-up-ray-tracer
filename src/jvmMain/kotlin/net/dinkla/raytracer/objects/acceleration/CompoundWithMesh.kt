package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.Mesh

abstract class CompoundWithMesh : Compound() {
    val mesh = Mesh()
}

