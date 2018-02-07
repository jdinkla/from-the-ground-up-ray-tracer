package net.dinkla.raytracer.btdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.math.Vector3D;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 01.05.2010
 * Time: 09:56:21
 * To change this template use File | Settings | File Templates.
 */
public class PerfectTransmitter<C extends Color> extends BTDF<C> {

    public float ior;
    public float kt;

    public PerfectTransmitter() {
        kt = 1.0f;
        ior = 1.0f;
    }
    
    @Override
    public C f(Shade sr, Vector3D wo, Vector3D wi) {
        throw new RuntimeException("PerfectTransmitter.f");
    }

    @Override
    public C rho(Shade sr, Vector3D wo) {
        throw new RuntimeException("PerfectTransmitter.rho");
    }

    @Override
    public Sample sampleF(Shade sr, Vector3D wo) {
        Sample result = new Sample();
        Normal n = sr.getNormal();
        float cosThetaI = n.dot(wo);
        float eta = ior;
        if (cosThetaI < 0) {
            cosThetaI = -cosThetaI;
            n = n.negate();
            eta = 1.0f / eta;
        }
        float cosThetaTSqr = 1.0f - (1.0f - cosThetaI * cosThetaI) / (eta * eta);
        float cosThetaT = (float) Math.sqrt(cosThetaTSqr);
        result.wt = wo.mult(-eta).minus(n.mult(cosThetaT - cosThetaI / eta));
        float f1 = kt / (eta*eta);
        float f2 = sr.getNormal().dot(result.wt);
        result.color = (C) C.WHITE.mult(f1 / Math.abs(f2));
        return result;
    }

    @Override
    public boolean isTir(Shade sr) {
        Vector3D wo = sr.ray.getD().mult(-1);
        float cosThetaI = wo.dot(sr.getNormal());
        float eta = ior;
        if (cosThetaI < 0) {
            eta = 1.0f / eta;
        }
        float cosThetaTSqr = 1.0f - (1.0f - cosThetaI * cosThetaI) / (eta * eta);
        return cosThetaTSqr < 0;
    }
    
}
