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

private class TunableGrid(
    factorSize: Int = 500,
    maxDepth: Int = 0,
) : Grid(factorSize, maxDepth) {
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
        val grid = TunableGrid(factorSize = 0, maxDepth = 1).apply { tune(multiplierValue = 0.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))

        grid.add(StubObject(bbox))
        grid.add(StubObject(bbox))
        grid.initialize()

        val cells = grid.cellsField().get(grid) as Array<*>
        cells.size shouldBe 1
        val nested = cells.first()
        (nested is Grid) shouldBe true

        val depthField = Grid::class.java.getDeclaredField("depth").apply { isAccessible = true }
        depthField.getInt(nested) shouldBe 1
    }

    "grid adds a third object into an already-promoted nested grid cell" {
        // First object wraps the cell in a Compound; the second promotes it to a nested Grid;
        // the third must take the `cells[index] is Grid` insertion branch and be added to it.
        val grid = TunableGrid(factorSize = 0, maxDepth = 2).apply { tune(multiplierValue = 0.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))

        grid.add(StubObject(bbox))
        grid.add(StubObject(bbox))
        grid.add(StubObject(bbox, t = 0.15))
        grid.initialize()

        val cells = grid.cellsField().get(grid) as Array<*>
        cells.size shouldBe 1
        val nested = cells.first()
        (nested is Grid) shouldBe true
        // The promoted grid holds the original cell's NullObject placeholder plus all three
        // StubObjects (the dense grid wraps the first occupant alongside the empty-cell marker).
        (nested as Grid).objects.size shouldBe 4

        // and the populated nested grid still resolves a ray to its closest occupant
        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        grid.hit(ray, sr) shouldBe true
        sr.t shouldBe 0.15
    }

    "grid hit steps across multiple cells along the y axis (positive y direction)" {
        // A spacer at low y stretches the grid across many y-cells; the target sits at high y, so a
        // +y ray must step up (nextAxis=Y, stepOut=Y) past the non-matching cells to reach it.
        val grid = TunableGrid().apply { tune(multiplierValue = 6.0) }
        val spacer = StubObject(BBox(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.1, 1.0)), shouldHit = false)
        val target = StubObject(BBox(Point3D(0.0, 1.9, 0.0), Point3D(1.0, 2.0, 1.0)), t = 2.95)
        grid.add(spacer)
        grid.add(target)
        grid.initialize()

        val ray = Ray(Point3D(0.5, -1.0, 0.5), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        grid.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
        sr.t shouldBe 2.95
    }

    "grid hit steps across multiple cells along the z axis (negative z direction)" {
        // A spacer at high z stretches the grid across many z-cells; the target sits at low z so a
        // -z ray must step down (nextAxis=Z, negative axisStep / stepOut-Z) to reach it. The spacer
        // never hits, so the empty/non-matching cells in between are skipped during the walk.
        val grid = TunableGrid().apply { tune(multiplierValue = 6.0) }
        val spacer = StubObject(BBox(Point3D(0.0, 0.0, 1.9), Point3D(1.0, 1.0, 2.0)), shouldHit = false)
        val target = StubObject(BBox(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 1.0, 0.1)), t = 1.95)
        grid.add(spacer)
        grid.add(target)
        grid.initialize()

        val ray = Ray(Point3D(0.5, 0.5, 3.0), Vector3D(0.0, 0.0, -1.0))
        val sr = Hit(Double.MAX_VALUE)

        grid.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
        sr.t shouldBe 1.95
    }

    "grid hit ignores an empty leading cell and keeps stepping" {
        // A ray entering through an empty cell must take the null-cell branch in the traversal
        // (acceptsHit returns false for a null cell) before reaching the occupied cell.
        val grid = TunableGrid().apply { tune(multiplierValue = 6.0) }
        val target = StubObject(BBox(Point3D(0.9, 0.0, 0.0), Point3D(1.0, 1.0, 1.0)), t = 1.95)
        grid.add(target)
        grid.initialize()

        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        grid.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
    }

    "grid toString reports the object count" {
        val grid = TunableGrid()
        grid.add(StubObject(BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))))
        grid.add(StubObject(BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))))

        grid.toString() shouldBe "Grid(#objs=2)"
    }

    "sparse grid wraps a second occupant of a cell in a compound" {
        // Two objects sharing one cell: the first is stored bare, the second takes the
        // `else` branch that creates a Compound, then a third takes the `is Compound` branch.
        val sparse = SparseGrid().apply { multiplier = 1.0 }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val closer = StubObject(bbox, t = 0.2)
        sparse.add(StubObject(bbox, t = 0.6))
        sparse.add(closer)
        sparse.add(StubObject(bbox, t = 0.9))
        sparse.initialize()

        val field = SparseGrid::class.java.getDeclaredField("cellsX").apply { isAccessible = true }
        val cells = field.get(sparse) as Map<Int, Any>
        cells.values.any { it is Compound } shouldBe true

        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        sparse.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe closer
        sr.t shouldBe 0.2
    }

    "sparse grid steps across multiple cells along the y axis" {
        val sparse = SparseGrid().apply { multiplier = 6.0 }
        val spacer = StubObject(BBox(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.1, 1.0)), shouldHit = false)
        val target = StubObject(BBox(Point3D(0.0, 1.9, 0.0), Point3D(1.0, 2.0, 1.0)), t = 2.95)
        sparse.add(spacer)
        sparse.add(target)
        sparse.initialize()

        val ray = Ray(Point3D(0.5, -1.0, 0.5), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        sparse.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
        sr.t shouldBe 2.95
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

    "grid hit picks initial cell from origin when ray starts inside the grid" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val target = StubObject(bbox, t = 0.1)
        grid.add(target)
        grid.initialize()

        // origin (0.5,0.5,0.5) is strictly inside the unit grid -> isInside branch
        val ray = Ray(Point3D(0.5, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        grid.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
        sr.t shouldBe 0.1
        sr.normal shouldBe Normal.UP
    }

    "grid hit reports the inner object, not the compound, for a crowded cell" {
        val grid = TunableGrid().apply { tune(multiplierValue = 1.0) }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val closer = StubObject(bbox, t = 0.2)
        val farther = StubObject(bbox, t = 0.6)
        grid.add(closer)
        grid.add(farther)
        grid.initialize()

        // both objects share the single cell -> stored as a Compound; the
        // reported geometricObject must come from the inner hit, not the Compound
        val cells = grid.cellsField().get(grid) as Array<*>
        cells.any { it is Compound } shouldBe true

        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        grid.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe closer
        sr.t shouldBe 0.2
    }

    "sparse grid hit skips empty cells and reports the object in a later cell" {
        val sparse = SparseGrid().apply { multiplier = 4.0 }
        // single object occupying only the far end of a wide box so leading cells stay empty
        val target = StubObject(BBox(Point3D(0.9, 0.0, 0.0), Point3D(1.0, 1.0, 1.0)), t = 1.95)
        sparse.add(target)
        sparse.initialize()

        val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        sparse.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
        sr.t shouldBe 1.95
    }

    "sparse grid hit returns false when ray misses bounding box" {
        val sparse = SparseGrid().apply { multiplier = 1.0 }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        sparse.add(StubObject(bbox))
        sparse.initialize()

        val ray = Ray(Point3D(-1.0, 2.0, 0.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        sparse.hit(ray, sr) shouldBe false
        sr.geometricObject shouldBe null
    }

    "sparse grid hit traverses correctly for negative direction steps" {
        val sparse = SparseGrid().apply { multiplier = 1.0 }
        val bbox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0))
        val target = StubObject(bbox, t = 0.4)
        sparse.add(target)
        sparse.initialize()

        val ray = Ray(Point3D(2.0, 0.5, 0.5), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        sparse.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe target
        sr.t shouldBe 0.4
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
