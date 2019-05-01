package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.*
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.objects.utilities.PlyReader

class ObjectsScope(private val materials: Map<String, IMaterial>, private val compound: Compound) {

    private val mutableObjects : MutableList<GeometricObject> = mutableListOf()

    val objects : List<GeometricObject>
        get() = mutableObjects.toList()

    fun GeometricObject.add() {
        mutableObjects.add(this)
        compound.add(this)
    }

    fun sphere(material: String, center: Point3D = Point3D.ORIGIN, radius: Double = 0.0) {
        Sphere(center, radius).apply {
            this.material = materials[material]
        }.add()
    }

    fun plane(material: String, point: Point3D = Point3D.ORIGIN, normal: Normal = Normal.UP) {
        Plane(point, normal).apply {
            this.material = materials[material]
        }.add()
    }

    fun triangle(material: String, a: Point3D, b: Point3D, c: Point3D) {
        Triangle(a, b, c).apply {
            this.material = materials[material]
        }.add()
    }

    fun smoothTriangle(material: String, a: Point3D, b: Point3D, c: Point3D) {
        SmoothTriangle(a, b, c).apply {
            this.material = materials[material]
        }.add()
    }

    fun grid(block: ObjectsScope.() -> Unit) {
        val grid = Grid()
        val scope = ObjectsScope(materials, grid)
        scope.block()
        grid.add()
    }

    fun ply(material: String, fileName: String, isSmooth: Boolean = false) {
        val mesh = Mesh()
        val grid = Grid(mesh).apply {
            this.material = materials[material]
        }
        val reverseNormal = false // TODO ? WIT?
        val isSmooth = false

        val plyReader = PlyReader(grid, isSmooth = isSmooth)
        plyReader.read(fileName)

        grid.add()
    }
}