package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.materials.IMaterial

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
    
    Map<String, IMaterial> materials = [:]

    public IMaterial get(Map map, String o) {
        IMaterial m = materials[map.material]
        if (null == m) {
            throw new RuntimeException("IMaterial '${map.material}' is unknown for '${o}'")
        } else {
            return m
        }
    }

    public void insert(Map map, IMaterial m) {
        if (materials[map["id"]]) {
            throw new RuntimeException("IMaterial '${map.id}' already exists")
        }
        materials[map["id"]] = m
    }

}
