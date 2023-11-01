package net.dinkla.raytracer.hits

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray

interface IShade : IHit {
    var ray: Ray
    val hitPoint: Point3D
    var depth: Int
    val material: IMaterial?
}
