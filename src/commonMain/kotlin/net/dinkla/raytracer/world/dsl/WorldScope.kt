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

/**
 * The top-level receiver of the scene DSL: `Builder.build { ... }` runs its body against a
 * `WorldScope`, which accumulates the camera, view plane, ambient light, lights, materials and
 * objects and exposes them as a finished [World] through [world].
 *
 * Besides the structural blocks ([camera], [ambientLight], [lights], [materials], [objects],
 * [metadata]) it offers terse constructor shorthands — `p`/`v`/`n` for points/vectors/normals and
 * `c`/`cInt` for colours — so scenes read compactly. See `README.md` for a full example.
 */
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

    /** The assembled [World] from everything declared so far in the DSL body. */
    val world: World
        get() = World(metadata, camera, viewPlane, ambientLight, lights, materials, objects, compound)

    /** Shorthand for a [Point3D] from integer coordinates. */
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
    ): Color = Color(red / 255.0, green / 255.0, blue / 255.0)

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

    /**
     * Sets the camera as a [Pinhole] lens looking from [eye] towards [lookAt] with the given [up]
     * vector. [d] is the view-plane distance (focal length); larger [d] narrows the field of view.
     */
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

    /** Sets a uniform [Ambient] ambient light of the given [color] scaled by intensity [ls]. */
    fun ambientLight(
        color: Color = Color.WHITE,
        ls: Double = 1.0,
    ) {
        ambientLight = Ambient(ls, color)
    }

    /**
     * Replaces the ambient term with an [AmbientOccluder] that softens ambient light by [sampler]ing
     * occlusion with [numSamples] rays per shading point.
     */
    fun ambientOccluder(
        sampler: Sampler,
        numSamples: Int,
    ) {
        ambientLight = AmbientOccluder(sampler, numSamples)
    }

    /** Declares the scene's lights; [builder] runs against a fresh [LightsScope]. */
    fun lights(builder: LightsScope.() -> Unit) {
        val scope = LightsScope()
        scope.builder()
        lights = scope.lights
    }

    /** Declares the named materials; [builder] runs against a fresh [MaterialsScope]. */
    fun materials(builder: MaterialsScope.() -> Unit) {
        val scope = MaterialsScope()
        scope.builder()
        materials = scope.materials
    }

    /**
     * Declares the scene geometry; [builder] runs against an [ObjectsScope] that resolves material
     * ids against the materials declared above and adds objects into the world's root [compound].
     */
    fun objects(builder: ObjectsScope.() -> Unit) {
        val scope = ObjectsScope(materials, compound)
        scope.builder()
        objects = scope.objects
    }

    /** Declares the scene's [Metadata] (id, title, description); [builder] runs against a [MetadataScope]. */
    fun metadata(builder: MetadataScope.() -> Unit) {
        val scope = MetadataScope()
        scope.builder()
        metadata = scope.metadata
    }
}
