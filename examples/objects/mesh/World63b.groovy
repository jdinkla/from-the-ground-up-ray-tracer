package objects.mesh
// Brown bunny in green grass
// rendering took 530510 ms
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.objects.acceleration.Grid;

//def NUM_AMBIENT_SAMPLES = 16
def NUM_AMBIENT_SAMPLES = 16

//String path = 'F://opt/rendering/ply'
String path = '/opt/rendering/ply'
//Grid bunny = builder.ply(file: "${path}/bunny/bunny69K.ply", multiplier: 2.0, smooth: true)
//Grid bunny = builder.ply(file: "${path}/bunny/bunny16K.ply", multiplier: 2.0, smooth: true)
Grid bunny = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)

Sampler sampler1 = new Sampler(new MultiJittered(), 2500, 10)
sampler1.mapSamplesToHemiSphere(1.0)

builder.world(id: "World61", backgroundColor: c("87cefa")) {

    viewPlane(resolution: Resolution.RESOLUTION_720, numSamples: 4, maxDepth: 5)

    camera(d: 1000, eye: p(8, 1, 7), lookAt: p(11.2, 1, 0), numThreads: 5 )

    lights {
        pointLight(location: p(0, 5, 5), color: c(1, 1, 1), ls: 1)
    }

    materials {
        reflective(id: "gray", cd: c(1), ka: 0.0, kd: 0.0, ks: 1.0)
        phong(id: "yellow", cd: c(1, 1, 0), ka: 0.5, kd: 0.5, ks: 0.25, exp: 4)
        phong(id: "orange", cd: c(1, 0.5, 0), ka: 0.5, kd: 0.25, ks: 0.55, exp: 2)
        phong(id: "chocolate", cd: c(0.5647, 0.1294, 0), ka: 0.5, kd: 0.25, ks: 0.55, exp: 2)
        phong(id: "ivory", cd: c(1, 1, 0.9412), ka: 0.5, kd: 0.25, ks: 0.55, exp: 2)
        phong(id: "Antique White", cd: c("faebd7"), ka: 0.5, kd: 0.25, ks: 0.55, exp: 2)
        phong(id: "Green Yellow", cd: c("adff2f"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 2)
        matte(id: "Light Sky Blue", cd: c("87cefa"), ka: 0.5, kd: 0.75)

        phong(id: "Moccasin", cd: c("ffe4b5"), ka: 0.25, kd: 0.75, ks: 0.95, exp: 20)
    }

    objects {
        plane(material: "Green Yellow", point: Point3D.ORIGIN, normal: Normal.UP)
        //plane(material: "gray", point: p(0, 0, -5), normal: Normal.FRONT)

        instance(object: bunny, material: "Moccasin") {
            rotateY(-55)
            scale(v(15, 15, 15))
            translate(v(11, 0, 0))
        }
    }

}
