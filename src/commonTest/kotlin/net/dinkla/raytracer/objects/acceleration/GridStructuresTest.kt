package net.dinkla.raytracer.objects.acceleration

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Point3D.Companion.ORIGIN
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.NullObject
import net.dinkla.raytracer.objects.compound.Compound
import java.lang.reflect.Field

private class StubObject(
    private val box: BBox,
    private val shouldHit: Boolean = true,
    private val t: Double = 0.5,
) : IGeometricObject {
    override var isShadows: Boolean = false
    override var boundingBox: BBox = box
    override var material: IMaterial? = null

    override fun initialize() {
        // no-op for tests
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        if (!shouldHit) {
            return false
        }
        sr.t = t
        sr.normal = Normal.UP
        sr.geometricObject = this
        return true
    }

    override fun shadowHit(ray: Ray): Shadow = if (shouldHit) Shadow.Hit(t) else Shadow.None

    override fun shadowHit(
        ray: Ray,
        tmin: ShadowHit,
    ): Boolean =
        if (shouldHit) {
            tmin.t = t
            true
        } else {
            false
        }
}

private class TunableGrid : Grid() {
    fun tune(
        multiplierValue: Double,
    ) {
        multiplier = multiplierValue
    }

    fun cellsField(): Field = Grid::class.java.getDeclaredField("cells").apply { isAccessible = true }
}

class GridStructuresTest : StringSpec({
    "grid initializes cells and promotes to subgrid when threshold exceeded" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        grid.add(StubObject(bbox))
        grid.add(StubObject(bbox))

        grid.initialize()

        val cells = grid.cellsField().get(grid) as Array<*>
        cells.any { it is Compound } shouldBe true
    }

    "grid hit traverses and returns closest object" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val closer = StubObject(bbox, t = 0.25)
        val farther = StubObject(bbox, t = 0.75)
        grid.add(closer)
        grid.add(farther)
        grid.initialize()

        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        val hit = grid.hit(ray, sr)

        hit shouldBe true
        sr.geometricObject shouldBe closer
        sr.t shouldBe 0.25
    }

    "grid initialization is a no-op when already initialized" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        grid.add(StubObject(bbox))
        grid.initialize()

        val first = grid.cellsField().get(grid) as Array<*>
        grid.initialize()
        val second = grid.cellsField().get(grid) as Array<*>

        second shouldBe first
    }

    "grid initialize returns early when empty" {
        val grid = TunableGrid()

        grid.initialize()

        val cells = grid.cellsField().get(grid) as Array<*>
        cells.size shouldBe 0
        grid.isInitialized shouldBe true
        grid.boundingBox shouldBe BBox()
    }

    "grid leaves untouched cells as null objects" {
        val grid = TunableGrid()
        grid.add(StubObject(BBox(ORIGIN, Point3D(0.1, 0.1, 0.1))))
        grid.add(StubObject(BBox(Point3D(0.9, 0.9, 0.9), Point3D(1.0, 1.0, 1.0))))

        grid.initialize()

        val cells = grid.cellsField().get(grid) as Array<*>
        cells.any { it is NullObject } shouldBe true
        cells.any { it is Compound } shouldBe true
    }

    "grid promotes crowded cell into nested grid" {
        val grid = TunableGrid().apply { tune(multiplierValue = 0.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))

        withGridThresholds(factor = 0, depth = 1) {
            grid.add(StubObject(bbox))
            grid.add(StubObject(bbox))
            grid.initialize()
        }

        val cells = grid.cellsField().get(grid) as Array<*>
        cells.size shouldBe 1
        val nested = cells.first()
        (nested is Grid) shouldBe true

        val depthField = Grid::class.java.getDeclaredField("depth").apply { isAccessible = true }
        depthField.getInt(nested) shouldBe 1
    }

    "grid hit returns false when ray misses bounding box" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val target = StubObject(bbox)
        grid.add(target)
        grid.initialize()

        val ray = Ray(Point3D(-1.0, 2.0, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        val hit = grid.hit(ray, sr)

        hit shouldBe false
        sr.geometricObject shouldBe null
    }

    "grid hit traverses correctly for negative direction steps" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val target = StubObject(bbox, t = 0.4)
        grid.add(target)
        grid.initialize()

        val ray = Ray(Point3D(2.0, 0.5, 0.5), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        grid.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
        sr.t shouldBe 0.4
    }

    "grid shadowHit delegates to hit and updates tmin" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val target = StubObject(bbox, t = 0.3)
        grid.add(target)
        grid.initialize()

        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val tmin = ShadowHit(Double.MAX_VALUE)

        grid.shadowHit(ray, tmin) shouldBe true
        tmin.t shouldBe 0.3
    }

    "sparse grid inserts and traverses map-backed cells" {
        val sparse = SparseGrid().apply { multiplier = 1.0 }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val target = StubObject(bbox)
        sparse.add(target)
        sparse.initialize()

        val field = SparseGrid::class.java.getDeclaredField("cellsX").apply { isAccessible = true }
        val cells = field.get(sparse) as Map<Int, Any>
        cells.shouldContainKey(0)

        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        sparse.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
    }
})

private fun <T> withGridThresholds(
    factor: Int,
    depth: Int,
    block: () -> T,
): T {
    val gridClass = Grid::class.java
    val factorField = gridClass.getDeclaredField("factorSize").apply { isAccessible = true }
    val maxDepthField = gridClass.getDeclaredField("maxDepth").apply { isAccessible = true }
    val oldFactor = factorField.getInt(null)
    val oldDepth = maxDepthField.getInt(null)
    return try {
        factorField.setInt(null, factor)
        maxDepthField.setInt(null, depth)
        block()
    } finally {
        factorField.setInt(null, oldFactor)
        maxDepthField.setInt(null, oldDepth)
    }
}
