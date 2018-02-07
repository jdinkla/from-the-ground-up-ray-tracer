package net.dinkla.raytracer;

import net.dinkla.raytracer.cameras.IColorCorrector;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.samplers.MultiJittered;
import net.dinkla.raytracer.samplers.Sampler;
import net.dinkla.raytracer.utilities.AppProperties;
import net.dinkla.raytracer.utilities.Resolution;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.04.2010
 * Time: 18:18:21
 * To change this template use File | Settings | File Templates.
 */
public class ViewPlane<C extends Color> implements IColorCorrector<C> {

    // Resolution
    public Resolution resolution;

    // Size of a pixel [world coordinates]
    public float size;

    // Color correction
    private float gamma;
    private float invGamma;

    // Used for debugging
    public boolean showOutOfGamut;

    // maximal recursion depth
    public int maxDepth;

    public ViewPlane() {
        final int width = AppProperties.getAsInteger("render.resolution.width");
        final int height = AppProperties.getAsInteger("render.resolution.height");
        this.resolution = new Resolution(width, height);
        this.size = 1.0f;
        this.gamma = 1.0f;
        this.invGamma = 1.0f;
        this.showOutOfGamut = false;
        this.maxDepth = 5;
    }
    
    public float getGamma() {
        return gamma;
    }

    public void setGamma(final float gamma) {
        this.gamma = gamma;
        this.invGamma = 1.0f / gamma;
    }

    public Color correct(final Color color) {
        Color newColor;
        if (showOutOfGamut) {
            newColor = color.clampToColor();
        } else {
            newColor = color.maxToOne();
        }
        if (gamma != 1) {
            newColor = newColor.pow(invGamma);
        }
        return newColor;
    }

    @Override
    public String toString() {
        return "Viewplane: resolution=" + resolution + ", size=" + size + ", gamma=" + gamma
                + ", invGamma=" + invGamma + ", showOutOfGamut=" + showOutOfGamut
                + ", maxDepth=" + maxDepth;
    }
}
