package net.dinkla.raytracer.worlds;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.cameras.Camera;
import net.dinkla.raytracer.lights.Ambient;
import net.dinkla.raytracer.lights.Light;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode;
import net.dinkla.raytracer.objects.compound.Compound;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.tracers.Tracer;
import net.dinkla.raytracer.tracers.Whitted;
import net.dinkla.raytracer.utilities.Counter;
import net.dinkla.raytracer.utilities.StepCounter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 13.04.2010
 * Time: 22:17:00
 * To change this template use File | Settings | File Templates.
 */
public class World<C extends Color> {

    protected Compound compound;
    protected C backgroundColor;
    protected C errorColor;
    protected List<Light> lights;
    protected Ambient<C> ambientLight;
    protected ViewPlane viewPlane;
    protected Tracer tracer;
    protected Camera camera;
    protected boolean dynamic;
    protected StepCounter stepCounter;

    public World() {
        lights = new LinkedList<Light>();
        ambientLight = new Ambient<C>();
        viewPlane = new ViewPlane();
        tracer = new Whitted(this);
        compound = new Compound();
        dynamic = false;
        stepCounter = null;

        // TODO color
        backgroundColor = (C) C.getBlack();
        errorColor = (C) C.getErrorColor();        
    }

    public Shade hit(Ray ray) {
        Counter.count("World.hit1");
        return compound.hitObjects(this, ray);
    }

    public boolean hit(Ray ray, Hit sr) {
        Counter.count("World.hit2");
        return compound.hit(ray, sr);
    }

    public boolean shadowHit(Ray ray, ShadowHit tmin) {
        Counter.count("World.shadowHit");
        return compound.shadowHit(ray, tmin);
    }

    public boolean inShadow(Ray ray, Shade sr, float d) {
        Counter.count("World.inShadow");
        return compound.inShadow(ray, sr, d);
    }

    public void initialize() {
        compound.initialize();
    }

    public int size() {
        return compound.size();
    }

    public void add(GeometricObject object) {
        compound.add(object);
    }

    public void add(List<GeometricObject> objects) {
        this.compound.add(objects);
    }

    final public Ambient getAmbientLight() {
        return ambientLight;
    }

    final public List<Light> getLights() {
        return lights;
    }

    final public C getBackgroundColor() {
        return backgroundColor;
    }

    final public C getErrorColor() {
        return errorColor;
    }

    public ViewPlane getViewPlane() {
        return viewPlane;
    }

    public Tracer getTracer() {
        return tracer;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean hasNext() {
        return stepCounter.hasNext();
    }

    public void step() {
        final int t = stepCounter.getCurrent();
        if (dynamic) {
            Point3D p = camera.getLens().getEye();
            Point3D p2 = new Point3D(p.getX() +0.1f, p.getY() +0.1f, p.getZ());
            camera.getLens().setEye(p2);
        }
        stepCounter.step();
    }

    public void set() {
    }

    public void render(IFilm imf) {

        net.dinkla.raytracer.utilities.Timer timer = new net.dinkla.raytracer.utilities.Timer();
        timer.start();
        getCamera().render(imf, 0);
        timer.stop();

        Counter.stats(30);      // ???

        System.out.println("Hits");
        InnerNode.hits.println();

        System.out.println("fails");
        InnerNode.fails.println();

        System.out.println("took " + timer.getDuration() + " [ms]");
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public StepCounter getStepCounter() {
        return stepCounter;
    }

    @Override
    public String toString() {
        return "World"; 
    }
}
