package net.dinkla.raytracer.objects

import net.dinkla.raytracer.materials.IMaterial

interface IGeometricObject {
    var material: IMaterial?
}