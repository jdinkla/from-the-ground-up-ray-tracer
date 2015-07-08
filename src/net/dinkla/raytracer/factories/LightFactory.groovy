package net.dinkla.raytracer.factories

import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.lights.DirectionalLight
import net.dinkla.raytracer.lights.EnvironmentLight

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 22.04.2010
 * Time: 20:32:12
 * To change this template use File | Settings | File Templates.
 */
class LightFactory extends AbstractFactory {

    static final Map map = [
            "ambientOccluder": LightFactory.&createAmbientOccluder,
            "areaLight": LightFactory.&createAreaLight,
            "directional" : LightFactory.&createDirectional,
            "directionalLight" : LightFactory.&createDirectional,
            "environment" : LightFactory.&createEnvironment,
            "environmentLight" : LightFactory.&createEnvironment,
            "pointLight": LightFactory.&createPointLight
    ]

    static PointLight createPointLight(Map map) {
        PointLight p = new PointLight(map.location);
        if (null != map.shadows) p.shadows = map.shadows
        if (null != map.ls) p.ls = map.ls
        if (null != map.color) p.color = map.color
        return p
    }

    static AreaLight createAreaLight(Map map) {
        needs(map, "areaLight", ["object"])
        AreaLight l = new AreaLight()
        l.object = map.object
//        if (map.material) l.material = new Emissive(); 
        if (map.numSamples) l.numSamples = map.numSamples; 
        return l
    }

    static AmbientOccluder createAmbientOccluder(Map map) {
        needs(map, "ambientOccluder", ["sampler", "numSamples"])

        AmbientOccluder ao = null
        if (null != map.minAmount) {
            ao = new AmbientOccluder(map.minAmount, map.sampler, map.numSamples)
        } else {
            ao = new AmbientOccluder(map.sampler, map.numSamples)
        }
        if (null != map.ls) ao.ls = map.ls
        if (null != map.color) ao.color = map.color
        return ao
    }

    static DirectionalLight createDirectional(Map map) {
        DirectionalLight p = new DirectionalLight();
        if (null != map.shadows) p.shadows = map.shadows
        if (null != map.ls) p.ls = map.ls
        if (null != map.color) p.color = map.color
        if (null != map.direction) p.direction = map.direction
        return p
    }

    static EnvironmentLight createEnvironment(Map map) {
        EnvironmentLight p = new EnvironmentLight();
        if (null != map.shadows) p.shadows = map.shadows
        if (null != map.ls) p.ls = map.ls
        if (null != map.color) p.color = map.color
        if (null != map.direction) p.direction = map.direction
        return p
    }

}
