import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.tracers.Whitted
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered

Sampler sampler = new Sampler(new MultiJittered(), 2500, 100)

builder.world(id: "World34") {
    
    viewPlane(
            resolution: Resolution.RESOLUTION_720,
            numSamples: 0,
            sampler: sampler,
            maxDepth: 5)

    tracer(type: Whitted)
    
    camera(eye: p(-4, 1, 4),lookAt: p(3, -0.2, 1), up: v(0,1,0), d: 1000)

    ambientLight(color: Color.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(0, 0, 5), ls: 1.0f)
    }

    materials {
        matte(id: "sky", cd: c(0.3, 0.6, 1.0), ka: 1.0, kd: 0.9)
        phong(id: "grey", ks: 0.4, cd: c(0.4), ka: 0.25, kd: 0.6, exp: 10)
        phong(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "rr", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        reflective(id: "gr", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "br", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        // TODO: Energieerhaltung !!
        transparent(id: "rt", ks: 0.1, ka: 0.1, kd: 0.2, kt: 0.5, kr: 0.1, exp: 10, ior: 1.02, cd: Color.RED, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "gt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.3, kr: 0.1, exp: 10, ior: 1.02, cd: Color.GREEN, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "bt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.3, kr: 0.1, exp: 10, ior: 1.02, cd: Color.BLUE, cs: Color.WHITE, cr: Color.WHITE)
    }

    objects {
        plane(material: "grey", point: p(0, -1, 0), normal: n(0, 1, 0))
//        plane(material: "sky", point: p(0, 10000, 0), normal: n(0, -1, 0))
        plane(material: "r", point: p(0, 10000, 0), normal: n(0, -1, 0))

        sphere(material: "rt", center: p(1, 0, 1), radius: 0.9);
        sphere(material: "gt", center: p(3, 0, 1), radius: 0.9);
        sphere(material: "bt", center: p(5, 0, 1), radius: 0.9);

    }

}
