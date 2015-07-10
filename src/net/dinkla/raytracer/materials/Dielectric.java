package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.BRDF;
import net.dinkla.raytracer.brdf.FresnelReflector;
import net.dinkla.raytracer.btdf.BTDF;
import net.dinkla.raytracer.btdf.FresnelTransmitter;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.math.WrappedFloat;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 15.05.2010
 * Time: 11:20:26
 * To change this template use File | Settings | File Templates.
 */
public class Dielectric extends Phong {

    FresnelReflector fresnelBrdf;
    FresnelTransmitter fresnelBtdf;

    RGBColor cfIn;
    RGBColor cfOut;

    public Dielectric() {
        super();
        fresnelBrdf = new FresnelReflector();
        fresnelBtdf = new FresnelTransmitter();
        cfIn = RGBColor.WHITE;
        cfOut = RGBColor.WHITE;        
    }

    @Override
    public Color shade(World world, Shade sr) {
        Color L = super.shade(world, sr);
        Vector3DF wo = sr.ray.getD().negate();
        WrappedFloat t = WrappedFloat.createMax();
        BRDF.Sample sample = fresnelBrdf.sampleF(sr, wo);
        Ray reflectedRay = new Ray(sr.getHitPoint(), sample.wi);
        float nDotWi = sr.getNormal().dot(sample.wi);

        if (fresnelBtdf.isTir(sr)) {
            Color lr = world.getTracer().trace(reflectedRay, t, sr.depth+1);
            if (nDotWi < 0) {
                // reflected ray is inside
                L = L.plus(cfIn.pow(t.value).mult(lr));
            } else {
                L = L.plus(cfOut.pow(t.value).mult(lr));                
            }
        } else {
            // no total internal reflection
            BTDF.Sample sampleT = fresnelBtdf.sampleF(sr, wo);
            Ray transmittedRay = new Ray(sr.getHitPoint(), sampleT.wt);
            float nDotWt = sr.getNormal().dot(sampleT.wt);
            if (nDotWi < 0) {
                // reflected ray is inside
                Color c1 = world.getTracer().trace(reflectedRay, t, sr.depth+1);
                Color c2 = c1.mult(Math.abs(nDotWi));
                Color lr = sample.color.mult(c2);
                L = L.plus(cfIn.pow(t.value).mult(lr));

                // transmitted ray is outside
                Color c3 = world.getTracer().trace(transmittedRay, t, sr.depth+1);
                Color c4 = c3.mult(Math.abs(nDotWt));
                Color lt = sampleT.color.mult(c4);
                L = L.plus(cfOut.pow(t.value).mult(lt));
            } else {
                // reflected ray is inside
                Color c1 = world.getTracer().trace(reflectedRay, t, sr.depth+1);
                Color c2 = c1.mult(Math.abs(nDotWi));
                Color lr = sample.color.mult(c2);
                L = L.plus(cfOut.pow(t.value).mult(lr));

                // transmitted ray is outside
                Color c3 = world.getTracer().trace(transmittedRay, t, sr.depth+1);
                Color c4 = c3.mult(Math.abs(nDotWt));
                Color lt = sampleT.color.mult(c4);
                L = L.plus(cfIn.pow(t.value).mult(lt));
            }

        }

        return L;
    }

    public void setEtaIn(final float etaIn) {

    }

    public void setEtaOut(final float etaOut) {

    }


    public void setCfIn(final RGBColor cfIn) {

    }

    public void setCfOut(final RGBColor cfOut) {

    }

}
