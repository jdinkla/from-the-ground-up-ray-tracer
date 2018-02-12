package net.dinkla.raytracer.objects.utilities

import java.util.HashMap

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

        val map: MutableMap<String, PlyType>

        init {
            map = HashMap()
            map["char"] = CHAR
            map["uchar"] = UCHAR
            map["short"] = SHORT
            map["ushort"] = USHORT
            map["int"] = INT
            map["uint"] = UINT
            map["float"] = FLOAT
            map["double"] = DOUBLE
        }
    }

}
