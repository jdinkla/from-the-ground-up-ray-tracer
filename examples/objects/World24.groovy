import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Vector3D

builder.world(id: "World24") {

  camera(d: 500, lookAt: p(2, 0, 0), eye: p(0, 3, 5))

  ambientLight(ls: 0.5)

  lights {
      pointLight(location: p(2, 5, 3), ls: 1)
  }

  materials {
      //matte(id: "m1", cd: c(1, 1, 0), ka: 0.75f, kd: 0.75f)
      matte(id: "m1", cd: c(1, 0.7, 0), ka: 0.75f, kd: 0.75f)
      matte(id: "m2", cd: c(1), ka: 0.75f, kd: 0.75f)
      //matte(id: "m3", cd: c(0, 0, 1), ka: 0.25f, kd: 0.5)
      matte(id: "m3", cd: c(0.2, 0.5, 0.4), ka: 0.25f, kd: 0.5)
  }

  objects {
      solidCylinder(material: "m1", y0: 0.2f, y1: 1.5f, radius: 0.9f)
      plane(material: "m2", point: Point3D.ORIGIN, normal: Normal.UP)
      alignedBox(material: "m3", p: p(4, 0, 0), q: p(5, 1.5, 1))
  }

}
