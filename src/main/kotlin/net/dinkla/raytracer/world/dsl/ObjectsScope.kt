package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.*
import net.dinkla.raytracer.objects.compound.Compound

class ObjectsScope(private val materials: Map<String, IMaterial>, private val compound: Compound) {

    private val mutableObjects : MutableList<GeometricObject> = mutableListOf()

    val objects : List<GeometricObject>
        get() = mutableObjects.toList()

    fun sphere(material: String, center: Point3D = Point3D.ORIGIN, radius: Double = 0.0) {
        val obj = Sphere(center, radius).apply {
            this.material = materials[material]
        }
        mutableObjects.add(obj)
        compound.add(obj)
    }

    fun plane(material: String, point: Point3D = Point3D.ORIGIN, normal: Normal = Normal.UP) {
        val obj = Plane(point, normal).apply {
            this.material = materials[material]
        }
        mutableObjects.add(obj)
        compound.add(obj)
    }

    fun triangle(material: String, a: Point3D, b: Point3D, c: Point3D) {
        val obj = Triangle(a, b, c).apply {
            this.material = materials[material]
        }
        mutableObjects.add(obj)
        compound.add(obj)
    }

    fun smoothTriangle(material: String, a: Point3D, b: Point3D, c: Point3D) {
        val obj = SmoothTriangle(a, b, c).apply {
            this.material = materials[material]
        }
        mutableObjects.add(obj)
        compound.add(obj)
    }
}