package net.dinkla.raytracer.worlds.dsl

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.Plane
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.compound.Compound

class ObjectsScope(private val materials: Map<String, IMaterial>, private val compound: Compound) {

    private val mutableObjects : MutableList<GeometricObject> = mutableListOf()

    val objects : List<GeometricObject>
        get() = mutableObjects.toList()

    fun sphere(material: String, center: Point3D = Point3D.ORIGIN, radius: Double = 0.0) {
        val s = Sphere(center, radius)
        s.material = materials[material]
        mutableObjects.add(s)
        compound.add(s)
    }

    fun plane(material: String, point: Point3D = Point3D.ORIGIN, normal: Normal = Normal.UP) {
        val p = Plane(point, normal)
        p.material = materials[material]
        mutableObjects.add(p)
        compound.add(p)
    }

}