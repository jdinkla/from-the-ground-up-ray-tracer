package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Metadata
import net.dinkla.raytracer.world.World

@Suppress("TooManyFunctions")
class WorldScope {
    private var metadata: Metadata = Metadata("someId")
    private var camera: Camera =
        Camera({ p, uvw -> Pinhole(ViewPlane(), p, uvw) }, Point3D.ORIGIN, Point3D.ORIGIN, Vector3D.UP)
    private val viewPlane = ViewPlane()
    private var ambientLight = Ambient()
    private var lights: List<Light> = listOf()
    private var materials: Map<String, IMaterial> = mapOf()
    private var objects: List<GeometricObject> = listOf()
    private val compound: Compound = Compound()

    val world: World
        get() = World(metadata, camera, viewPlane, ambientLight, lights, materials, objects, compound)

    fun p(
        x: Int,
        y: Int,
        z: Int,
    ) = Point3D(x.toDouble(), y.toDouble(), z.toDouble())

    fun p(
        x: Double,
        y: Double,
        z: Double,
    ) = Point3D(x, y, z)

    fun c(v: Double) = Color(v)

    fun c(
        red: Double,
        green: Double,
        blue: Double,
    ) = Color(red, green, blue)

    fun c(
        red: Int,
        green: Int,
        blue: Int,
    ): Color {
        require(red == 0 || red == 1)
        require(green == 0 || green == 1)
        require(blue == 0 || blue == 1)
        return Color(red.toDouble(), green.toDouble(), blue.toDouble())
    }

    fun cInt(
        red: Int,
        green: Int,
        blue: Int,
    ): Color = Color(red / 255.0, green / 255.0, blue / 255.0)

    fun c(hexCode: String) = Color.fromString(hexCode)

    fun n(
        x: Int,
        y: Int,
        z: Int,
    ) = Normal(x.toDouble(), y.toDouble(), z.toDouble())

    fun n(
        x: Double,
        y: Double,
        z: Double,
    ) = Normal(x, y, z)

    fun v(
        x: Int,
        y: Int,
        z: Int,
    ) = Vector3D(x.toDouble(), y.toDouble(), z.toDouble())

    fun v(
        x: Double,
        y: Double,
        z: Double,
    ) = Vector3D(x, y, z)

    fun camera(
        d: Double = 1.0,
        eye: Point3D = Point3D(5.0, 50.0, 50.0),
        lookAt: Point3D = Point3D.ORIGIN,
        up: Vector3D = Vector3D.UP,
    ) {
        camera =
            Camera({ eye, uvw ->
                val p = Pinhole(viewPlane, eye, uvw)
                p.d = d
                p
            }, eye, lookAt, up)
    }

    fun ambientLight(
        color: Color = Color.WHITE,
        ls: Double = 1.0,
    ) {
        ambientLight = Ambient(ls, color)
    }

    fun ambientOccluder(
        sampler: Sampler,
        numSamples: Int,
    ) {
        ambientLight = AmbientOccluder(sampler, numSamples)
    }

    fun lights(builder: LightsScope.() -> Unit) {
        val scope = LightsScope()
        scope.builder()
        lights = scope.lights
    }

    fun materials(builder: MaterialsScope.() -> Unit) {
        val scope = MaterialsScope()
        scope.builder()
        materials = scope.materials
    }

    fun objects(builder: ObjectsScope.() -> Unit) {
        val scope = ObjectsScope(materials, compound)
        scope.builder()
        objects = scope.objects
    }

    fun metadata(builder: MetadataScope.() -> Unit) {
        val scope = MetadataScope()
        scope.builder()
        metadata = scope.metadata
    }
}
