package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.StereoCamera
import net.dinkla.raytracer.cameras.StereoMode
import net.dinkla.raytracer.cameras.StereoViewing
import net.dinkla.raytracer.cameras.lenses.FishEye
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.lenses.Spherical
import net.dinkla.raytracer.cameras.lenses.ThinLens
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
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.UnitDiskSampler
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
    private var stereoCamera: StereoCamera? = null

    /** The assembled [World] from everything declared so far in the DSL body. */
    val world: World
        get() = World(metadata, camera, viewPlane, ambientLight, lights, materials, objects, compound, stereoCamera)

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
     * [exposureTime] (default `1.0`) scales the radiance of every primary ray — reduce it to keep an
     * interior view through a dense transparent medium from washing out (Suffern §28.6.3); at the
     * default it leaves the image unchanged.
     */
    fun camera(
        d: Double = 1.0,
        eye: Point3D = Point3D(5.0, 50.0, 50.0),
        lookAt: Point3D = Point3D.ORIGIN,
        up: Vector3D = Vector3D.UP,
        exposureTime: Double = 1.0,
    ) {
        camera =
            Camera({ eye, uvw ->
                val p = Pinhole(viewPlane, eye, uvw)
                p.d = d
                p
            }, eye, lookAt, up).apply { this.exposureTime = exposureTime }
    }

    /**
     * Sets the camera as a [ThinLens] depth-of-field lens (Suffern ch. 10) looking from [eye] towards
     * [lookAt] with the given [up] vector. [d] is the view-plane distance (focal length); [f] is the
     * focal-plane distance (scene geometry at distance [f] renders sharp); [lensRadius] is the aperture
     * radius (`0.0` is a pinhole, larger values blur out-of-focus geometry more); [sampler] supplies the
     * unit-disk points that jitter each ray's origin across the lens.
     *
     * Note: depth-of-field blur is only visible when many lens samples are averaged per pixel; the
     * current single-ray render path uses the lens centre and therefore renders sharp (see TASK-26).
     */
    fun thinLensCamera(
        d: Double = 1.0,
        f: Double = 1.0,
        lensRadius: Double = 1.0,
        eye: Point3D = Point3D(5.0, 50.0, 50.0),
        lookAt: Point3D = Point3D.ORIGIN,
        up: Vector3D = Vector3D.UP,
        sampler: UnitDiskSampler = defaultLensSampler(),
    ) {
        camera =
            Camera({ eye, uvw ->
                ThinLens(viewPlane, eye, uvw).apply {
                    this.d = d
                    this.f = f
                    this.lensRadius = lensRadius
                    this.sampler = sampler
                }
            }, eye, lookAt, up)
    }

    /**
     * Sets the camera as a [FishEye] fisheye lens (Suffern ch. 11) looking from [eye] towards [lookAt]
     * with the given [up] vector. [maxPsi] is the field of view as a half-angle in degrees; only pixels
     * inside the unit image circle map to a ray, producing the characteristic circular image (the frame
     * corners stay background). Unlike [camera] there is no view-plane distance — the fisheye normalises
     * the view plane to the unit square.
     */
    fun fishEyeCamera(
        maxPsi: Double = 180.0,
        eye: Point3D = Point3D(5.0, 50.0, 50.0),
        lookAt: Point3D = Point3D.ORIGIN,
        up: Vector3D = Vector3D.UP,
    ) {
        camera =
            Camera({ eye, uvw ->
                FishEye(viewPlane, eye, uvw).apply {
                    this.maxPsi = maxPsi
                }
            }, eye, lookAt, up)
    }

    /**
     * Sets the camera as a [Spherical] panoramic lens (Suffern ch. 11) looking from [eye] towards
     * [lookAt] with the given [up] vector. [maxLambda] (azimuth) and [maxPsi] (polar) are half-angles
     * in degrees: the defaults `180`/`90` unroll a full 360°×180° panorama across the view plane. Every
     * pixel maps to a valid ray (no circular vignette). There is no view-plane distance.
     */
    fun sphericalCamera(
        maxLambda: Double = 180.0,
        maxPsi: Double = 90.0,
        eye: Point3D = Point3D(5.0, 50.0, 50.0),
        lookAt: Point3D = Point3D.ORIGIN,
        up: Vector3D = Vector3D.UP,
    ) {
        camera =
            Camera({ eye, uvw ->
                Spherical(viewPlane, eye, uvw).apply {
                    this.maxLambda = maxLambda
                    this.maxPsi = maxPsi
                }
            }, eye, lookAt, up)
    }

    /**
     * Selects a stereo camera (Suffern ch. 9): the scene is rendered from two eye positions offset
     * by `±separation/2` along the camera's right axis and the two views are composited per [viewing]
     * (side-by-side or anaglyph). [mode] chooses parallel vs. transverse (toed-in) convergence and
     * [d] is the view-plane distance (focal length).
     *
     * A base [Pinhole] [camera] is also set so the assembled world stays valid for any code that
     * reads [World.camera]; the actual stereo render reads [World.stereoCamera].
     */
    fun stereoCamera(
        eye: Point3D,
        lookAt: Point3D,
        up: Vector3D = Vector3D.UP,
        separation: Double,
        mode: StereoMode = StereoMode.PARALLEL,
        viewing: StereoViewing = StereoViewing.SIDE_BY_SIDE,
        d: Double = 1.0,
    ) {
        camera(d = d, eye = eye, lookAt = lookAt, up = up)
        stereoCamera = StereoCamera(eye, lookAt, up, separation, mode, viewing, d)
    }

    /**
     * Sets the per-pixel sample count for anti-aliasing. The default of `1` casts a single ray
     * through each pixel centre (no anti-aliasing — every existing scene's historical behaviour);
     * values `> 1` cast that many jittered samples per pixel and average them, smoothing edges and —
     * with a [thinLensCamera] — making depth-of-field blur visible. [n] must be positive.
     */
    fun samples(n: Int) {
        require(n > 0) { "samples must be positive, was $n" }
        viewPlane.numSamples = n
    }

    /**
     * Sets the tracer's maximal recursion depth (reflection/refraction bounces). The default of `5`
     * (left unchanged unless this is called, so every existing scene renders identically) suits most
     * scenes, but deeply nested transparent media — e.g. concentric
     * [net.dinkla.raytracer.materials.Dielectric] shells — need a higher limit so the innermost
     * transmitted rays reach a surface instead of being truncated to the background. [n] must be
     * positive.
     */
    fun maxDepth(n: Int) {
        require(n > 0) { "maxDepth must be positive, was $n" }
        viewPlane.maximalRecursionDepth = n
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

    companion object {
        private const val DEFAULT_LENS_SAMPLES = 2500
        private const val DEFAULT_LENS_SETS = 10

        /** A jittered [Sampler] mapped onto the unit disk, the default aperture sampler for [thinLensCamera]. */
        private fun defaultLensSampler(): Sampler =
            Sampler(MultiJittered, DEFAULT_LENS_SAMPLES, DEFAULT_LENS_SETS).also { it.mapSamplesToUnitDisk() }
    }
}
