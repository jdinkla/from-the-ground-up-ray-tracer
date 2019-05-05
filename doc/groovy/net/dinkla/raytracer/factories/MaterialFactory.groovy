package net.dinkla.raytracer.factories

import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Phong
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.Reflective
import net.dinkla.raytracer.materials.GlossyReflector
import net.dinkla.raytracer.materials.Transparent
import net.dinkla.raytracer.materials.SVMatte
import net.dinkla.raytracer.materials.SVPhong

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 22.04.2010
 * Time: 20:18:17
 * To change this template use File | Settings | File Templates.
 */
class MaterialFactory extends AbstractFactory {

    static final Map map = [
            "matte": MaterialFactory.&createMatte,
            "phong": MaterialFactory.&createPhong,
            "emissive": MaterialFactory.&createEmissive,
            "reflective": MaterialFactory.&createReflective,
            "glossy": MaterialFactory.&createGlossyReflector,
            "transparent": MaterialFactory.&createTransparent,
            "svMatte": MaterialFactory.&createSVMatte,
            "svPhong": MaterialFactory.&createSVPhong
    ]

//    static Matte createMatte(Map map) {
//        Matte m = new Matte()
//        if (null != map.shadows) m.shadows = map.shadows
//        if (null != map.ka) m.setKa(map.ka)
//        if (null != map.kd) m.setKd(map.kd)
//        if (null != map.cd) m.setCd(map.cd)
//        return m
//    }
//
//    static Phong createPhong(Map map) {
//        Phong m = new Phong()
//        if (null != map.shadows) m.shadows = map.shadows
//        if (null != map.ka) m.setKa(map.ka)
//        if (null != map.kd) m.setKd(map.kd)
//        if (null != map.cd) m.setCd(map.cd)
//        if (null != map.ks) m.setKs(map.ks)
//        if (null != map.cs) m.setCs(map.cs)
//        if (null != map.exp) m.setExp(map.exp)
//        return m
//    }

//    static Emissive createEmissive(Map map) {
//        Emissive m = new Emissive()
//        if (null != map.shadows) m.shadows = map.shadows
//        if (null != map.le) m.ls = map.le
//        if (null != map.ce) m.ce = map.ce
//        return m
//    }

//    static Reflective createReflective(Map map) {
//        Reflective m = new Reflective()
//        if (null != map.shadows) m.shadows = map.shadows
//        if (null != map.ka) m.setKa(map.ka)
//        if (null != map.kd) m.setKd(map.kd)
//        if (null != map.cd) m.setCd(map.cd)
//        if (null != map.ks) m.setKs(map.ks)
//        if (null != map.cs) m.setCs(map.cs)
//        if (null != map.exp) m.setExp(map.exp)
//        if (null != map.kr) m.setKr(map.kr)
//        if (null != map.cr) m.setCr(map.cr)
//        return m
//    }

    static GlossyReflector createGlossyReflector(Map map) {
        needs(map, "glossy", ["sampler"])
        GlossyReflector m = new GlossyReflector()
        if (null != map.shadows) m.shadows = map.shadows
        if (null != map.ka) m.setKa(map.ka)
        if (null != map.kd) m.setKd(map.kd)
        if (null != map.cd) m.setCd(map.cd)
        if (null != map.ks) m.setKs(map.ks)
        if (null != map.cs) m.setCs(map.cs)
        if (null != map.exp) m.setExp(map.exp)
        if (null != map.kr) m.setKs(map.kr)
        if (null != map.cr) m.setCs(map.cr)
        if (null != map.sampler) m.setSampler(map.sampler)
        return m
    }

    static Transparent createTransparent(Map map) {
        Transparent m = new Transparent()
        if (null != map.shadows) m.shadows = map.shadows
        if (null != map.ka) m.ka = map.ka
        if (null != map.kd) m.kd = map.kd
        if (null != map.ks) m.ks = map.ks
        if (null != map.kt) m.ks = map.kt
        if (null != map.kr) m.kr = map.kr
        if (null != map.cd) m.cd = map.cd
        if (null != map.cs) m.cs = map.cs
        if (null != map.cr) m.cr = map.cr
        if (null != map.exp) m.exp = map.exp
        if (null != map.ior) m.ior = map.ior
        return m
    }

    static SVMatte createSVMatte(Map map) {
        SVMatte m = new SVMatte()
        if (null != map.shadows) m.shadows = map.shadows
        if (null != map.ka) m.setKa(map.ka)
        if (null != map.kd) m.setKd(map.kd)
        if (null != map.cd) m.setCd(map.cd)
        if (null != map.map) m.cd.mapping = map.map
        return m
    }

    static SVPhong createSVPhong(Map map) {
        SVPhong m = new SVPhong()
        if (null != map.shadows) m.shadows = map.shadows
        if (null != map.ka) m.setKa(map.ka)
        if (null != map.kd) m.setKd(map.kd)
        if (null != map.cd) m.setCd(map.cd)
        if (null != map.map) m.cd.mapping = map.map
        return m
    }

}
