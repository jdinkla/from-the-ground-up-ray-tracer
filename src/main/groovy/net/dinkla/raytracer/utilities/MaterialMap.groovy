package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.materials.Material

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 24.04.2010
 * Time: 09:10:34
 * To change this template use File | Settings | File Templates.
 *
 * TODO id vs. material?
 */
class MaterialMap {
    
    Map<String, Material> materials = [:]

    public Material get(Map map, String o) {
        Material m = materials[map.material]
        if (null == m) {
            throw new RuntimeException("Material '${map.material}' is unknown for '${o}'")
        } else {
            return m
        }
    }

    public void insert(Map map, Material m) {
        if (materials[map["id"]]) {
            throw new RuntimeException("Material '${map.id}' already exists")
        }
        materials[map["id"]] = m
    }

}
