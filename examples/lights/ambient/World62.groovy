// Brown bunny in green grass
// White bunny p. 309
// rendering took 530510 ms
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered;

//def NUM_AMBIENT_SAMPLES = 16
def NUM_AMBIENT_SAMPLES = 1

String path = 'C://opt/rendering/ply'
//String path = '//opt/rendering/ply'
//Grid bunny = builder.ply(file: "${path}/bunny/bunny69K.ply", multiplier: 2.0, smooth: true)
Grid bunny = builder.ply(file: "${path}/bunny/bunny16K.ply", multiplier: 2.0, smooth: true)
//Grid bunny = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)

Sampler sampler1 = new Sampler(new MultiJittered(), 2500, 10)
sampler1.mapSamplesToHemiSphere(1.0)

builder.world(id: "World61", backgroundColor: c(1)) {

    viewPlane(resolution: Resolution.RESOLUTION_480, numSamples: 4, maxDepth: 5)

    camera(d: 1000, eye: p(8, 1, 7), lookAt: p(11.2, 1, 0), numThreads: 5 )

    ambientOccluder(sampler: sampler1, numSamples: NUM_AMBIENT_SAMPLES)

    lights {
        pointLight(location: p(0, 5, 5), color: c(1, 1, 1), ls: 1)
    }

    materials {
        matte(id: "white", cd: c(1), ka: 0.0, kd: 1.0)
    }

    objects {
        plane(material: "white", point: Point3DF.ORIGIN, normal: Normal.UP)

        instance(object: bunny, material: "white") {
            rotateY(-55)
            scale(v(15, 15, 15))
            translate(v(11, 0, 0))
        }
    }

}
