package net.dinkla.raytracer.factories

import net.dinkla.raytracer.worlds.World
import net.dinkla.raytracer.tracers.Tracer

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 09.06.2010
 * Time: 19:20:20
 * To change this template use File | Settings | File Templates.
 */
class TracerFactory extends AbstractFactory {

    static final Map map = [
        "tracer": TracerFactory.&createTracer
    ]

    static Tracer createTracer(World world, Map map) {
        needs(map, "tracer", ["type"])
        def c = map.type
        Tracer t = c.newInstance(world)
        return t
    }    

}
