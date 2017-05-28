package net.dinkla.raytracer.utilities;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 03.05.2010
 * Time: 09:30:40
 */
public class Resolution {

    public static Resolution RESOLUTION_32 = new Resolution(32);
    public static Resolution RESOLUTION_320 = new Resolution(320);
    public static Resolution RESOLUTION_480 = new Resolution(480);
    public static Resolution RESOLUTION_720 = new Resolution(720);
    public static Resolution RESOLUTION_1080 = new Resolution(1080);
    public static Resolution RESOLUTION_1440 = new Resolution(1440);
    public static Resolution RESOLUTION_2160 = new Resolution(2160);

    // Resolution
    public final int hres;
    public final int vres;

    public Resolution(final int vres) {
        this.hres = (int) (vres / 9 * 16);
        this.vres = vres;
    }

    public Resolution(final int hres, final int vres) {
        this.hres = hres;
        this.vres = vres;
    }

    @Override
    public String toString() {
        return "(" + hres + "," + vres + ")";
    }
}
