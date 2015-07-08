package net.dinkla.raytracer.textures;

import net.dinkla.raytracer.math.MathUtils;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.utilities.Resolution;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 21:17:38
 * To change this template use File | Settings | File Templates.
 */
public class LightProbeMap extends Mapping {

    public enum Type {
        LIGHT_PROBE,
        PANORAMIC
    }

    public Type type;

    public LightProbeMap() {
        this.type = Type.LIGHT_PROBE;
    }

    @Override
    public Mapped getTexelCoordinates(Point3D p, Resolution res) {
        Mapped result = new Mapped();

        float d = (float) Math.sqrt(p.x * p.x + p.y * p.y);
        float sinBeta = p.y / d;
        float cosBeta = p.x / d;

        float alpha;

        switch (type) {
            case LIGHT_PROBE:
                alpha = (float) Math.acos(p.z);
                break;
            case PANORAMIC:
                alpha = (float) Math.acos(-p.z);
                break;
            default:
                throw new RuntimeException("LightProbeMap.getTexelCoordinates unknown type");
        }

        float r = alpha * MathUtils.INV_PI;
        float u = (1.0f + r * cosBeta) * 0.5f;
        float v = (1.0f + r * sinBeta) * 0.5f;

        result.column = (int) ((res.hres -1) * u);
        result.row = (int) ((res.vres -1) * v);

        return result;
    }
}



