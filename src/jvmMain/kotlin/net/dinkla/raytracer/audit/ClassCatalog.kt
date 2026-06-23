package net.dinkla.raytracer.audit

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.StereoCamera
import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.NullObject
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.textures.Texture

private const val ROOT_PACKAGE = "net.dinkla.raytracer"
private const val EXAMPLES_INFIX = ".examples."
const val ACCELERATION_INFIX = ".objects.acceleration."

/**
 * Concrete production classes that are scene *infrastructure*, not authored features: the root
 * [Compound] container every world wraps its objects in, and the [NullObject] sentinel that fills
 * empty acceleration cells. Excluding them keeps the geometry denominator to things a scene can
 * actually declare.
 */
private val GEOMETRY_BLOCKLIST =
    setOf(
        Compound::class.java.name,
        NullObject::class.java.name,
    )

/**
 * The full set of concrete, instantiable production classes per [Category] — the *denominator* the
 * audit measures example coverage against. Built by scanning the production packages with classgraph
 * ([scan]), so a newly added primitive/material/light/… is picked up automatically with no edit here.
 *
 * Keys and values are fully-qualified class names.
 */
class ClassCatalog(
    val byCategory: Map<Category, Set<String>>,
) {
    fun denominator(category: Category): Set<String> = byCategory[category].orEmpty()

    /** Every tracked class across all categories. */
    fun allClasses(): Set<String> = byCategory.values.flatten().toSet()

    companion object {
        fun scan(): ClassCatalog =
            ClassGraph()
                .enableClassInfo()
                .acceptPackages(ROOT_PACKAGE)
                .scan()
                .use { result -> ClassCatalog(catalogFrom(result)) }

        private fun catalogFrom(result: ScanResult): Map<Category, Set<String>> {
            fun implementing(base: Class<*>): Set<String> =
                concreteProduction(result.getClassesImplementing(base.name)).toSet()

            val geometry = concreteProduction(result.getClassesImplementing(IGeometricObject::class.java.name))
            val (acceleration, primitives) = geometry.partition { it.contains(ACCELERATION_INFIX) }
            return mapOf(
                Category.GEOMETRY to (primitives.toSet() - GEOMETRY_BLOCKLIST),
                Category.ACCELERATION to acceleration.toSet(),
                Category.MATERIALS to implementing(IMaterial::class.java),
                Category.LIGHTS to implementing(Light::class.java),
                Category.LENSES to implementing(ILens::class.java),
                Category.TEXTURES to implementing(Texture::class.java),
                Category.CAMERAS to cameras(result),
            )
        }

        /**
         * The camera types have no shared base ([StereoCamera] does not extend [Camera]), so the two
         * concrete cameras are named explicitly, unioned with any future [Camera] subclasses.
         */
        private fun cameras(result: ScanResult): Set<String> =
            (
                concreteProduction(result.getSubclasses(Camera::class.java.name)) +
                    Camera::class.java.name +
                    StereoCamera::class.java.name
            ).toSet()

        /** The FQNs of the concrete, instantiable, non-example classes in [list]. */
        private fun concreteProduction(list: Iterable<ClassInfo>): List<String> =
            list
                .filter { it.isStandardClass && !it.isAbstract && !it.isInterface }
                .map { it.name }
                .filterNot { it.contains(EXAMPLES_INFIX) }
    }
}
