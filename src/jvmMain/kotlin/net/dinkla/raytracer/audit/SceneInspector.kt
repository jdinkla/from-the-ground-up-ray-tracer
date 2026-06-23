package net.dinkla.raytracer.audit

import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.world.World

/**
 * Determines which production classes a built [World] actually *uses*, grouped by [Category] — the
 * numerator the [SceneAuditor] compares against the [ClassCatalog] denominator.
 *
 * It inspects the world as authored, **before** `initialize()`: the geometry tree is walked through
 * [Compound.objects] and [Instance]'s wrapped child, and acceleration structures (Grid/KDTree, which
 * are themselves compounds) expose their authored children the same way — so the spheres inside a
 * `grid { … }` are counted as geometry and the grid as an acceleration structure, without the
 * post-initialize noise of populated cells and `NullObject` sentinels. The walk is transitive: the
 * internal primitives a composite (e.g. a beveled box) is built from are counted too, reflecting what
 * a render of the scene exercises.
 */
object SceneInspector {
    fun inspect(world: World): Map<Category, Set<String>> {
        val used = Category.entries.associateWith { mutableSetOf<String>() }
        val materials = mutableSetOf<IMaterial>()

        world.compound.objects.forEach { walkGeometry(it, used, materials) }
        // Area-light shapes (DiskLight/RectangleLight) are GeometricObjects that live inside an
        // AreaLight's source rather than in the object tree, so credit them as geometry too.
        world.lights
            .filterIsInstance<AreaLight>()
            .mapNotNull { it.source as? IGeometricObject }
            .forEach { walkGeometry(it, used, materials) }
        materials.addAll(world.materials.values)

        materials.mapTo(used.getValue(Category.MATERIALS)) { it::class.java.name }
        used.getValue(Category.TEXTURES).addAll(TextureCollector.collect(materials))

        (world.lights + world.ambientLight).mapTo(used.getValue(Category.LIGHTS)) { it::class.java.name }
        used.getValue(Category.CAMERAS).add(world.camera::class.java.name)
        world.stereoCamera?.let { used.getValue(Category.CAMERAS).add(it::class.java.name) }
        used.getValue(Category.LENSES).add(world.camera.lens::class.java.name)

        return used.mapValues { it.value.toSet() }
    }

    private fun walkGeometry(
        node: IGeometricObject,
        used: Map<Category, MutableSet<String>>,
        materials: MutableSet<IMaterial>,
    ) {
        val name = node::class.java.name
        val category = if (name.contains(ACCELERATION_INFIX)) Category.ACCELERATION else Category.GEOMETRY
        used.getValue(category).add(name)
        node.material?.let { materials.add(it) }
        childrenOf(node).forEach { walkGeometry(it, used, materials) }
    }

    private fun childrenOf(node: IGeometricObject): List<IGeometricObject> =
        when (node) {
            is Compound -> node.objects.toList()
            is Instance -> listOfNotNull(instanceChild(node))
            else -> emptyList()
        }

    /** [Instance] keeps its wrapped object in a private field; read it reflectively to recurse. */
    private fun instanceChild(instance: Instance): IGeometricObject? =
        try {
            val field = Instance::class.java.getDeclaredField("geometricObject")
            field.isAccessible = true
            field.get(instance) as? IGeometricObject
        } catch (_: ReflectiveOperationException) {
            null
        }
}
