// Versuch: Kernschatten und größe der AreaLight


import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.tracers.AreaLighting
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler

def sampler1 = new Sampler(new MultiJittered(), 2500, 100);
sampler1.mapSamplesToUnitDisk()

def r1
def r2

int numSamples = 16

builder.world(id: "World59") {

    tracer(type: AreaLighting)

    camera(d: 2000, eye: p(2, 1, 12), lookAt: p(2, 1, 0))

    ambientLight(color: Color.WHITE, ls: 0.0)

    materials {
        phong(id: "green", cd: c(0, 1, 0), ka: 0.0, kd: 0.75, ks: 0.25, exp: 10)
        matte(id: "gray", cd: c(0.5, 0.5, 0.5), ka: 0.25, kd: 0.75, ks: 0.25, exp: 10)
        emissive(id: "emissive", ce: c(1.0, 0.0, 1.0), le: 1.0)
    }

    objects {
        plane(material: "gray", point: Point3D.ORIGIN, normal: Normal.UP)
        sphere(center: p(1, 1, 0), radius: 0.25, material: "green")
        sphere(center: p(1, 3, 0), radius: 0.25, material: "green")
        sphere(center: p(3, 1, 0), radius: 0.25, material: "green")
        sphere(center: p(3, 3, 0), radius: 0.25, material: "green")
        plane(material: "gray", point: p(2, 0, 0), normal: Normal.RIGHT)
//        rectangle(p0: p(2, 0, -10), a: v(0, 0, 10), b: v(0, 10, 0), material: "gray")
        r1 = rectangleLight(p0: p(0.95, 2, -0.05), a: v(0.1, 0, 0), b: v(0, 0, 0.1), sampler: sampler1, material: "emissive")
        r2 = rectangleLight(p0: p(2.65, 2, -0.35), a: v(0.7, 0, 0), b: v(0, 0, 0.7), sampler: sampler1, material: "emissive")
    }

    lights {
        areaLight(object: r1, numSamples: numSamples)
        areaLight(object: r2, numSamples: numSamples)
    }

}
