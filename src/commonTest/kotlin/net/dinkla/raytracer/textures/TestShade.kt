package net.dinkla.raytracer.textures

import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject

/**
 * A minimal hand-built [IShade] for texture/mapping tests: it carries the [localHitPoint] (and
 * optional parametric [u]/[v]) a texture reads, with everything else inert. A fake over a mock,
 * per the testing guide.
 */
fun testShade(
    localHitPoint: Point3D = Point3D.ORIGIN,
    u: Double = 0.0,
    v: Double = 0.0,
    normal: Normal = Normal.UP,
): IShade =
    object : IShade {
        override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
        override val hitPoint: Point3D = localHitPoint
        override val localHitPoint: Point3D = localHitPoint
        override val u: Double = u
        override val v: Double = v
        override var depth: Int = 0
        override val material: IMaterial? = null
        override var normal: Normal = normal
        override var t: Double = 0.0
        override var geometricObject: IGeometricObject? = null
    }
