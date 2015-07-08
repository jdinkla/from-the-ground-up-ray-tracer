package net.dinkla.raytracer.worlds

import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.factories.MaterialFactory
import net.dinkla.raytracer.factories.LightFactory
import net.dinkla.raytracer.factories.GeometricObjectFactory
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.utilities.MaterialMap
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.utilities.StepCounter
import org.apache.log4j.Logger
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.Timer
import net.dinkla.raytracer.objects.mesh.Mesh

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.factories.TracerFactory
import net.dinkla.raytracer.factories.CameraFactory
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 20.04.2010
 * Time: 21:44:16
 */
class WorldBuilder<C extends Color> extends BuilderSupport {

    static final Logger LOGGER = Logger.getLogger(WorldBuilder.class);

    World world
    boolean isInLights
    boolean isInObjects
    boolean isInMaterials
    boolean isTopLevel
    boolean isWorldLevel
    final MaterialMap mMap
    final GeometricObjectFactory gof

    WorldBuilder(World<C> world) {
        this.world = world
        isInLights = false
        isInObjects = false
        isInMaterials = false
        isWorldLevel = false
        isTopLevel = true
        mMap = new MaterialMap()
        gof = new GeometricObjectFactory(mMap)
    }

    static World create(File file) {
        World world = new World()
        WorldBuilder<C> builder = new WorldBuilder<C>(world)
        builder.build(file)        
        return world
    }

    void build(File file) {
        Timer timer = new Timer()
        timer.start()
        Map map = ['builder': this] + Definitions.defs
        def sh = new GroovyShell(new Binding(map))
        sh.evaluate(file)
        timer.stop()
        LOGGER.info("World Builder took " + timer.duration + " ms")
        LOGGER.info("ViewPlane: " + world.viewPlane)
    }

    protected Object createNode(Object o) {
        if (o == "lights") {
            isInLights = true
            isInObjects = false
            isInMaterials = false
            isWorldLevel = false
            return o
        } else if (o == "objects") {
            isInLights = false
            isInObjects = true
            isInMaterials = false
            isWorldLevel = false
            return world
        } else if (o == "materials") {
            isInLights = false
            isInObjects = false
            isInMaterials = true
            isWorldLevel = false
            return o
        } else if (o == "viewPlane") {
            world.viewPlane = new ViewPlane()
            return world.viewPlane
        } else if (o == "grid") {
            return new Grid()
        } else if (o == "kdtree") {
            return new KDTree()
        } else {
            throw new RuntimeException("Unknown node ${o}")
        }
        return null; 
    }

    protected Object createNode(Object o, Map map) {
        if (isTopLevel) {
            if (o == "world" && isTopLevel) {
                isTopLevel = false
                isWorldLevel = true
                boolean dynamic = false
                int start = -1;
                int end = -1;
                int step = 1;
                boolean cyclic = true;
                if (null != map.backgroundColor) world.backgroundColor = map.backgroundColor
                if (null != map.background) world.backgroundColor = map.background
                if (null != map.errorColor) world.errorColor = map.errorColor
                if (null != map.dynamic) {
                    dynamic = map.dynamic
                }
                if (null != map.start) start = map.start
                if (null != map.end) end = map.end
                if (null != map.step) step = map.step
                if (dynamic) {
                    world.dynamic = true
                    world.stepCounter = new StepCounter(start, end, step, cyclic);
                }

                return o
            } else {
                def cl = gof.map[o]
                if (cl) {
                    def obj = cl.call(map)
                    return obj
                } else {
                    throw new RuntimeException("Unknown toplevel node ${o}")
                }
            }
        } else if (isWorldLevel) {
            if (o == "ambientLight") {
                if (null != map.ls) world.ambientLight.ls = map.ls
                if (null != map.color) world.ambientLight.color = map.color
                return world.ambientLight
            } else if (o == "ambientOccluder") {
                AmbientOccluder ao = LightFactory.createAmbientOccluder(map)
                world.ambientLight = ao
                return ao
            } else if (o == "viewPlane") {
                if (null != map.resolution) world.viewPlane.resolution = map.resolution
//                if (null != map.numSamples) world.viewPlane.numSamples = map.numSamples
//                if (null != map.sampler) world.viewPlane.sampler = map.sampler
//                if (null != map.maxDepth) world.viewPlane.maxDepth = map.maxDepth
                return world.viewPlane
            } else if (o == "camera") {
                def cl = CameraFactory.map[o]
                if (cl) {
                    world.camera = cl.call(world, map)
                    return o
                } else {
                    throw new RuntimeException("Unknown tracer node '${o}'")
                }
            } else if (o == "tracer") {
                def cl = TracerFactory.map[o]
                if (cl) {
                    world.tracer = cl.call(world, map)
                    return o
                } else {
                    throw new RuntimeException("Unknown tracer node '${o}'")
                }                
            } else {
                throw new RuntimeException("Unknown node '${o}'")
            }
        } else if (isInLights) {
            def cl = LightFactory.map[o]
            if (cl) {
                def l = cl.call(map)
                world.lights.add(l)
                return l
            } else {
                throw new RuntimeException("Unknown light node '${o}'")
            }            
        } else if (isInMaterials) {
            def cl = MaterialFactory.map[o]
            if (cl) {
                def m = cl.call(map)
                mMap.insert(map, m)
                return m
            } else {
                throw new RuntimeException("Unknown material node '${o}'")
            }
        } else if (isInObjects) {
            def cl = gof.map[o]
            if (cl) {
                def obj = cl.call(map)
                return obj
            } else {
                throw new RuntimeException("Unknown object node '${o}'")
            }
        }
        return null;
    }

    protected Object createNode(Object o, Map map, Object o1) {
        return null;  
    }

    protected Object createNode(Object o, Object o1) {
        if (current instanceof Instance) {
            if (o == "scale") {
                current.scale(o1);
            } else if (o == "translate") {
                current.translate(o1);
            } else if (o == "rotateX") {
                current.rotateX(o1);
            } else if (o == "rotateY") {
                current.rotateY(o1);
            } else if (o == "rotateZ") {
                current.rotateZ(o1);
            } else {
                throw new RuntimeException("Unknown instance method '${o}'")
            }
        }
        return null;
    }

    protected void setParent(Object o, Object o1) {
        if (isInObjects) {
            if (o instanceof Grid && o1 instanceof Mesh) {
                o.setMesh(o1)
            } else if (o instanceof World) {
                LOGGER.debug("ADDING ($o1) TO ($o)")
                o.add(o1);
            } else if (o instanceof Compound) {
                LOGGER.debug("ADDING ($o1) TO ($o)")
                o.add(o1);
            }
        }
    }

}
