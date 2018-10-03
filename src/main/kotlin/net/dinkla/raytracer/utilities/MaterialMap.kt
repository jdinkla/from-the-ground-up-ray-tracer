package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.materials.IMaterial

class MaterialMap {

    val  materials : MutableMap<String, IMaterial> = mutableMapOf()

    fun get(map: Map<String, String>, o: String): IMaterial? {
        val m = materials.get(map.get("material"))
        if (null == m) {
            throw RuntimeException("")
        } else {
            return m
        }
    }

    fun insert(map: Map<String, String>, m: IMaterial) {
        if (materials.containsKey(map.get("id"))) {
            throw RuntimeException("IMaterial already exists")
        }
        materials.put(map.get("id").orEmpty(), m)
    }

}