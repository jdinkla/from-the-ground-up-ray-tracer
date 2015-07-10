import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3DF

builder.world(id: "World5") {

    camera(d: 500, eye: p(0, 100, 200), lookAt: p(0, 0, 0))

    ambientLight(color: RGBColor.WHITE, ls: 0.75f)
    
    lights {
        pointLight(location: new Point3DF(0, 100, 0), ls: 2)
    }

    materials {
        matte(id: "m1", cd: new RGBColor(1, 0, 0))
        matte(id: "m2", cd: new RGBColor(1, 1, 0))
        matte(id: "m3", cd: new RGBColor(0, 0.3f, 0))
    }

    objects {
        sphere(material: "m1", center: new Point3DF(0, -25, 0), radius: 80)
        sphere(material: "m2", center: new Point3DF(0, 30, 0), radius: 60)
        plane(material: "m3", point: Point3DF.ORIGIN, normal: new Normal(0, 1, 1))
    }

}
