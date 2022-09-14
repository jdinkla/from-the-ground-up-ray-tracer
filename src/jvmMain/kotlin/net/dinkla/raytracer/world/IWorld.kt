package net.dinkla.raytracer.world

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.tracers.Tracer

interface IWorld {

    var tracer: Tracer?
    var lights : List<Light>
    var ambientLight: Ambient
    var backgroundColor: Color

    fun inShadow(ray: Ray, sr: Shade, d: Double): Boolean

}