package net.dinkla.raytracer.factories

import net.dinkla.raytracer.worlds.World
import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.render.ISingleRayRenderer
import net.dinkla.raytracer.cameras.render.SimpleRenderer
import net.dinkla.raytracer.cameras.render.IRenderer
import net.dinkla.raytracer.cameras.render.ParallelRenderer
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.cameras.render.ForkJoinRenderer

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 09.06.2010
 * Time: 19:30:01
 * To change this template use File | Settings | File Templates.
 */
class CameraFactory extends AbstractFactory {

    static final Map map = [
        "camera": CameraFactory.&createCamera
    ]

    static Camera createCamera(World world, Map map) {

        // Camera(final ILens lens, final IRenderer render2)
        // Lens(ViewPlane)
        // Render2(final ISingleRayRenderer render, final IColorCorrector corrector)
        // ISingleRayRenderer(final ILens lens, final Tracer tracer)

        // ILens
        def c = Pinhole.class
        if (null != map.type) {
            c = map.type
        } else if (null != map.lens) {
            c = map.lens
        }
        ILens lens = c.newInstance(world.viewPlane)

        if (null != map.d) {
            lens.d = map.d
        }
        
        // ISingleRayRenderer
        c = SimpleRenderer.class
        if (null != map.ray) {
            c = map.ray
        }
        ISingleRayRenderer srRender = c.newInstance(lens, world.tracer)

        if (null != map.raySampler) {
            srRender.sampler = map.raySampler
        }

        if (null != map.rayNumSamples) {
            srRender.numSamples = map.rayNumSamples
        }

        // IColorCorrector
        IColorCorrector corrector
        if (null != map.corrector) {
            c = map.corrector
            corrector = c.newInstance()
        } else {
            corrector = world.viewPlane
        }

        // IRenderer
        IRenderer render2 = null
        if (null != map.render) {
            c = map.render
            render2 = c.newInstance(srRender, corrector) 
        } else {
            // render2 = new ParallelRenderer(srRender, corrector)
            render2 = new ForkJoinRenderer(srRender, corrector)
        }

        if (null != map.numThreads) {
            render2.numThreads = map.numThreads
        }

        // Camera
        Camera camera = new Camera(lens, render2)
        if (null != map.eye) camera.eye = map.eye
        if (null != map.lookAt) camera.lookAt = map.lookAt
        if (null != map.up) camera.up = map.up
        camera.computeUVW()

        return camera
    }
    
}
