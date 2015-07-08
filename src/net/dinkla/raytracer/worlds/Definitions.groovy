package net.dinkla.raytracer.worlds

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.Normal

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 06.05.2010
 * Time: 20:48:09
 * To change this template use File | Settings | File Templates.
 */
class Definitions {

    static final Map defs = [
            'p': Definitions.&p,
            'c': Definitions.&c,
            'v': Definitions.&v,
            'n': Definitions.&n
    ]

    static def p(x, y, z) {
        new Point3D((float)x, (float)y, (float)z)
    }

    static def p(x) {
        new Point3D((float)x, (float)x, (float)x)
    }

    static def c(rgb) {
        if (rgb instanceof Number) {
            return new RGBColor((float)rgb)
        } else if (rgb instanceof String) {
            float rf = Integer.valueOf(rgb[0,1], 16) / 255.0f
            float gf = Integer.valueOf(rgb[2,3], 16) / 255.0f
            float bf = Integer.valueOf(rgb[4,5], 16) / 255.0f
            return new RGBColor(rf, gf, bf)
        } else {
            throw new RuntimeException("Unknown color $rgb")
        }

    }

    static def c(r, g, b) {
        new RGBColor((float)r, (float)g, (float)b)
    }

    static def v(x, y, z) {
        new Vector3D((float)x, (float)y, (float)z)
    }

    static def n(x, y, z) {
        new Normal(x, y, z)
    }

}
