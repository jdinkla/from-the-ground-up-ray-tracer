package net.dinkla.raytracer.objects.utilities

enum class PlyType private constructor(internal val key: String, internal val clazz: Class<*>, internal val size: Int) {

    CHAR("char", Short::class.java, 1),
    UCHAR("uchar", Short::class.java, 1),
    SHORT("short", Short::class.java, 2),
    USHORT("ushort", Short::class.java, 2),
    INT("int", Int::class.java, 4),
    UINT("uint", Int::class.java, 4),
    FLOAT("float", Int::class.java, 4),
    DOUBLE("double", Int::class.java, 8);

    companion object {
        val map: Map<String, PlyType> = hashMapOf(
                Pair("char", CHAR),
                Pair("uchar", UCHAR),
                Pair("short", SHORT),
                Pair("ushort", USHORT),
                Pair("int", INT),
                Pair("uint", UINT),
                Pair("float", FLOAT),
                Pair("double", DOUBLE)
        )
    }
}


