package net.dinkla.raytracer.audit

import net.dinkla.raytracer.textures.Texture
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.IdentityHashMap

private const val MAX_DEPTH = 6
private const val MAX_ELEMENTS = 64

/**
 * The packages a [Texture] can hide in: a material holds BRDFs/BTDFs, those hold a texture, and a
 * texture can wrap another texture (e.g. `RampFBmTexture` wraps a `Ramp`). The walk only descends into
 * objects from these packages, so it never wanders into samplers, colours, math or image pixel arrays
 * — which both keeps it correct and stops it exploding on scenes with thousands of materials.
 */
private val RECURSABLE_PACKAGES =
    listOf(
        "net.dinkla.raytracer.materials",
        "net.dinkla.raytracer.brdf",
        "net.dinkla.raytracer.btdf",
        "net.dinkla.raytracer.textures",
    )

/**
 * Finds the concrete [Texture] classes reachable from a set of root objects (in practice, a scene's
 * materials) by a bounded reflective walk. A direct field read is not enough because textures live
 * inside materials and their BRDFs. The walk is bounded for safety: it descends only into
 * [RECURSABLE_PACKAGES], caps recursion at [MAX_DEPTH], visits each instance once (by identity), and
 * looks at most [MAX_ELEMENTS] into any collection. Returns fully-qualified class names.
 */
object TextureCollector {
    fun collect(roots: Collection<Any>): Set<String> {
        val found = mutableSetOf<String>()
        val visited = IdentityHashMap<Any, Boolean>()
        roots.forEach { walk(it, found, visited, 0) }
        return found
    }

    private fun walk(
        value: Any?,
        found: MutableSet<String>,
        visited: IdentityHashMap<Any, Boolean>,
        depth: Int,
    ) {
        if (value == null || depth > MAX_DEPTH) return
        if (value is Texture) found.add(value::class.java.name)
        when (value) {
            is Iterable<*> -> value.take(MAX_ELEMENTS).forEach { walk(it, found, visited, depth + 1) }
            is Map<*, *> -> value.values.take(MAX_ELEMENTS).forEach { walk(it, found, visited, depth + 1) }
            is Array<*> -> value.take(MAX_ELEMENTS).forEach { walk(it, found, visited, depth + 1) }
            else -> walkFields(value, found, visited, depth)
        }
    }

    private fun walkFields(
        value: Any,
        found: MutableSet<String>,
        visited: IdentityHashMap<Any, Boolean>,
        depth: Int,
    ) {
        if (!isRecursable(value)) return
        if (visited.put(value, true) != null) return
        declaredFields(value::class.java).forEach { field ->
            walk(readField(field, value), found, visited, depth + 1)
        }
    }

    private fun isRecursable(value: Any): Boolean {
        val name = value::class.java.name
        return RECURSABLE_PACKAGES.any { name.startsWith(it) }
    }

    /** Instance fields declared across the class hierarchy (within recursable packages); statics skipped. */
    private fun declaredFields(start: Class<*>): List<Field> {
        val fields = mutableListOf<Field>()
        var clazz: Class<*>? = start
        while (clazz != null) {
            val current = clazz
            if (RECURSABLE_PACKAGES.none { current.name.startsWith(it) }) break
            current.declaredFields
                .filter { !Modifier.isStatic(it.modifiers) && !it.isSynthetic }
                .forEach { fields.add(it) }
            clazz = current.superclass
        }
        return fields
    }

    private fun readField(
        field: Field,
        owner: Any,
    ): Any? =
        try {
            field.isAccessible = true
            field.get(owner)
        } catch (_: ReflectiveOperationException) {
            null
        } catch (_: RuntimeException) {
            null
        }
}
