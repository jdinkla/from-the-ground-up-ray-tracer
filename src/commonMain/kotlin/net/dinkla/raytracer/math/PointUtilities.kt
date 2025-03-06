package net.dinkla.raytracer.math

object PointUtilities {
    fun minimum(
        v: Array<Point3D>,
        n: Int,
    ): Triple<Double, Double, Double> {
        var x0 = MathUtils.K_HUGE_VALUE
        var y0 = MathUtils.K_HUGE_VALUE
        var z0 = MathUtils.K_HUGE_VALUE

        for (j in 0 until n) {
            if (v[j].x < x0) {
                x0 = v[j].x
            }
            if (v[j].y < y0) {
                y0 = v[j].y
            }
            if (v[j].z < z0) {
                z0 = v[j].z
            }
        }
        return Triple(x0, y0, z0)
    }

    fun maximum(
        v: Array<Point3D>,
        n: Int,
    ): Triple<Double, Double, Double> {
        var x1 = -MathUtils.K_HUGE_VALUE
        var y1 = -MathUtils.K_HUGE_VALUE
        var z1 = -MathUtils.K_HUGE_VALUE

        for (j in 0 until n) {
            if (v[j].x > x1) {
                x1 = v[j].x
            }
            if (v[j].y > y1) {
                y1 = v[j].y
            }
            if (v[j].z > z1) {
                z1 = v[j].z
            }
        }

        return Triple(x1, y1, z1)
    }
}
