package net.dinkla.raytracer.hits

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.IGeometricObject

interface IHit {
    var normal: Normal
    var t: Double
    var geometricObject: IGeometricObject?
}
