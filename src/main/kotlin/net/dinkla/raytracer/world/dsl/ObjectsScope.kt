package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.*
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.objects.utilities.PlyReader

class ObjectsScope(internal val materials: Map<String, IMaterial>, private val compound: Compound) {

    private val mutableObjects: MutableList<GeometricObject> = mutableListOf()

    val objects: List<GeometricObject>
        get() = mutableObjects.toList()

    private fun GeometricObject.add() {
        mutableObjects.add(this)
        compound.add(this)
    }

    fun alignedBox(material: String, p: Point3D = Point3D.ORIGIN, q: Point3D = Point3D.ORIGIN) {
        AlignedBox(p, q).apply {
            this.material = materials[material]
        }.add()
    }

    fun disk(material: String, center: Point3D = Point3D.ORIGIN, radius: Double = 0.0, normal: Normal = Normal.UP) {
        Disk(center, radius, normal).apply {
            this.material = materials[material]
        }.add()
    }

    fun grid(block: ObjectsScope.() -> Unit) {
        val grid = Grid()
        val scope = ObjectsScope(materials, grid)
        scope.block()
        grid.add()
    }

    fun instance(material: String, of: GeometricObject, block: InstanceScope.() -> Unit) {
        val instance = Instance(of)
        instance.apply {
            this.material = materials[material]
        }
        InstanceScope(instance).block()
        instance.add()
    }

    fun openCylinder(material: String, y0: Double = 0.0, y1: Double = 1.0, radius: Double = 0.0) {
        OpenCylinder(y0, y1, radius).apply {
            this.material = materials[material]
        }.add()
    }

    fun plane(material: String, point: Point3D = Point3D.ORIGIN, normal: Normal = Normal.UP) {
        Plane(point, normal).apply {
            this.material = materials[material]
        }.add()
    }

    fun ply(material: String, fileName: String, isSmooth: Boolean = false) {
        val reverseNormal = false // TODO ? WIT?
        val m = materials[material]!!
        val plyReader = PlyReader(m, isSmooth = isSmooth)
        val ply = plyReader.read(fileName)
        ply.grid.add()
    }

    fun rectangle(material: String, p0: Point3D = Point3D.ORIGIN, a: Vector3D = Vector3D.UP, b: Vector3D = Vector3D.RIGHT) {
        Rectangle(p0, a, b).apply {
            this.material = materials[material]
        }.add()
    }

    fun solidCylinder(material: String, y0: Double = 0.0, y1: Double = 1.0, radius: Double = 0.0) {
        SolidCylinder(y0, y1, radius).apply {
            this.material = materials[material]
        }.add()
    }

    fun sphere(material: String, center: Point3D = Point3D.ORIGIN, radius: Double = 0.0) {
        Sphere(center, radius).apply {
            this.material = materials[material]
        }.add()
    }

    fun triangle(material: String, a: Point3D, b: Point3D, c: Point3D, smooth: Boolean = false) {
        if (smooth) {
            SmoothTriangle(a, b, c).apply {
                this.material = materials[material]
            }.add()
        } else {
            Triangle(a, b, c).apply {
                this.material = materials[material]
            }.add()
        }
    }

}