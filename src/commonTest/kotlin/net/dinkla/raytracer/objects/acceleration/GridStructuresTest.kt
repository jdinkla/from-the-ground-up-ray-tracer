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
