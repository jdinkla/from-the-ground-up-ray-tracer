package net.dinkla.raytracer.factories

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 22.04.2010
 * Time: 20:36:41
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractFactory {

    static void needs(Map map, def o, List<String> keys) {
        for (String key : keys) {
            needs(map, o, key)
        }
    }

    static void needs(Map map, def o, String key) {
        if (null == map[key]) {
            throw new RuntimeException("Object '${o}' needs ${key}")
        }
    }

}
