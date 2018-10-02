package net.dinkla.raytracer.factories

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.MaterialMap
import net.dinkla.raytracer.objects.utilities.GridUtilities
import net.dinkla.raytracer.objects.utilities.PlyReader
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.objects.beveled.BeveledBox
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.acceleration.SparseGrid
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.textures.ImageTexture
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.objects.utilities.CompoundUtilities
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.Plane
import net.dinkla.raytracer.objects.Triangle
import net.dinkla.raytracer.objects.SmoothTriangle
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.objects.Box
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.Torus

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 22.04.2010
 * Time: 20:39:46
 * To change this template use File | Settings | File Templates.
 */
class GeometricObjectFactory extends AbstractFactory {

    final Map<String, Closure> map; 
    final MaterialMap materials;

    GeometricObjectFactory(final MaterialMap materials) {
        assert(null != materials)
        this.materials = materials
        map = [
                "alignedBox": this.&createAlignedBox,
                "beveledBox": this.&createBeveledBox,
                "box": this.&createBox,
                "disk": this.&createDisk,
                "grid": this.&createGrid,
                "imageTexture": this.&createImageTexture,
                "instance": this.&createInstance,
                "kdtree":  this.&createKDTree,
                "openCylinder": this.&createOpenCylinder,
                "plane": this.&createPlane,
                "ply": this.&createFromPly,
                "xx": this.&xx,
                "rectangle": this.&createRectangle,
                "rectangleLight": this.&createRectangleLight,
                "smoothTriangle": this.&createSmoothTriangle,
                "solidCylinder": this.&createSolidCylinder,
                "sphere": this.&createSphere,
                "tesselatedFlatSphere": this.&createTesselatedFlatSphere,
                "tesselatedSmoothSphere": this.&createTesselatedSmoothSphere,
                "torus": this.&createTorus,
                "triangle": this.&createTriangle
        ]
    }

    Sphere createSphere(Map map) {
        needs(map, "sphere", ["radius"])
        Sphere s = new Sphere(Point3D.ORIGIN, map.radius)
        if (null != map.center) s.center = map.center
        if (null != map.shadows) s.shadows = map.shadows
        if (null != map.material) s.material = materials.get(map, "sphere")
        return s
    }

    Plane createPlane(Map map) {
        //needs(map, "plane", ["material"])
        Plane p = new Plane();
        if (null != map.point) p.point = map.point
        if (null != map.normal) p.normal = map.normal
        if (null != map.shadows) p.shadows = map.shadows
        if (null != map.material) p.material = materials.get(map, "plane")
        return p
    }

    Triangle createTriangle(Map map) {
        needs(map, "triangle", ["a", "b", "c"])
        Triangle t = new Triangle(map.a, map.b, map.c)
        if (null != map.material) t.material = materials.get(map, "triangle")
        if (null != map.shadows) p.shadows = map.shadows
        return t
    }

    SmoothTriangle createSmoothTriangle(Map map) {
        needs(map, "triangle", ["a", "b", "c"])
        SmoothTriangle t = new SmoothTriangle(map.a, map.b, map.c)
        if (null != map.n1) t.n1 = map.n1
        if (null != map.n2) t.n2 = map.n2
        if (null != map.material) t.material = materials.get(map, "smoothTriangle")
        if (null != map.shadows) p.shadows = map.shadows
        return t
    }

    Disk createDisk(Map map) {
        needs(map, "disk", ["center", "radius", "normal"])
        Disk d = new Disk(map.center, map.radius, map.normal)
        if (null != map.material) d.material = materials.get(map, "disk")
        if (null != map.sampler) d.sampler = map.sampler
        if (null != map.shadows) p.shadows = map.shadows
        return d
    }

    Rectangle createRectangle(Map map) {
        needs(map, "rectangle", ["p0", "a", "b"] )
        Rectangle r = new Rectangle(map.p0, map.a, map.b)
        if (null != map.normal) r.normal = map.normal
        if (null != map.material) r.material = materials.get(map, "rectangle")
        if (null != map.sampler) r.sampler = map.sampler
        if (null != map.shadows) p.shadows = map.shadows
        return r
    }

    RectangleLight createRectangleLight(Map map) {
        needs(map, "rectangleLight", ["p0", "a", "b"] )
        RectangleLight r = new RectangleLight(map.p0, map.a, map.b)
        if (null != map.normal) r.normal = map.normal
        if (null != map.material) r.material = materials.get(map, "rectangle")
        if (null != map.sampler) r.sampler = map.sampler
        if (null != map.shadows) p.shadows = map.shadows
        return r
    }

    OpenCylinder createOpenCylinder(Map map) {
        needs(map, "openCylinder", ["y0", "y1", "radius"])
        OpenCylinder oc = new OpenCylinder(map.y0, map.y1, map.radius)
        if (null != map.material) oc.material = materials.get(map, "openCylinder")
        if (null != map.shadows) p.shadows = map.shadows
        return oc
    }

    SolidCylinder createSolidCylinder(Map map) {
        needs(map, "solidCylinder", ["y0", "y1", "radius"])
        SolidCylinder oc = new SolidCylinder(map.y0, map.y1, map.radius)
        if (null != map.material) oc.material = materials.get(map, "solidCylinder")
        if (null != map.shadows) p.shadows = map.shadows
        return oc
    }

    AlignedBox createAlignedBox(Map map) {
        needs(map, "alignedBox", ["p", "q"])
        AlignedBox b = new AlignedBox(map.p, map.q)
        if (null != map.material) b.material = materials.get(map, "alignedBox")
        if (null != map.shadows) p.shadows = map.shadows
        return b
    }

    Box createBox(Map map) {
        needs(map, "box", ["p0", "a", "b", "c"])
        Box b = new Box(map.p0, map.a, map.b, map.c)
        if (null != map.material) b.material = materials.get(map, "box")
        if (null != map.shadows) p.shadows = map.shadows
        return b
    }

    Instance createInstance(Map map) {
        needs(map, "instance", ["object"])
        Instance instance = new Instance(map.object)
        if (null != map.material) instance.material = materials.get(map, "instance")
        if (null != map.shadows) p.shadows = map.shadows
        return instance
    }

    List createTesselatedFlatSphere(Map map) {
        needs(map, "tessellatedFlatSphere", ["m", "n"])
        List ls = new ArrayList();
        GridUtilities.tessellateFlatSphere(ls, map.m, map.n)
        if (null != map.material) {
            def material = materials.get(map, "tessellatedFlatSphere")
            for (GeometricObject go : ls) {
                go.material = material
            }
        }
        return ls;
    }

    List createTesselatedSmoothSphere(Map map) {
        needs(map, "tessellatedSmoothSphere", ["m", "n"])
        List ls = new ArrayList();
        GridUtilities.tessellateSmoothSphere(ls, map.m, map.n)
        if (null != map.material) {
            def material = materials.get(map, "tessellatedSmoothSphere")
            for (GeometricObject go : ls) {
                go.material = material
            }
        }
        return ls;
    }

    Compound xx(LinkedHashMap lhm) {
        println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
        return createFromPly(lhm)
    }

    Compound createFromPly(Map map) {
        needs(map, "ply", ["file"])

        // TODO: Gibt es einen Grund, das so kompliziert zu machen?
        boolean reverseNormal = false
        if (null != map.reverseNormal) reverseNormal = map.reverseNormal

        boolean isSmooth = false
        if (null != map.smooth) isSmooth = map.smooth


        def c = Grid
        if (null != map.type) {
            c = map.type
        }
        Mesh mesh = new Mesh()
        Compound compound = c.newInstance(mesh)

        PlyReader.read(compound, map.file, reverseNormal, isSmooth)

        boolean normalized = true
        if (null != map.normalized) normalized = map.normalized
        if (normalized) {
            // TODO: Parametrisierbar machen
//            compound = CompoundUtilities.norm2(compound)
            compound = CompoundUtilities.norm(compound)
        }

        if (null != map.material) compound.material = materials.get(map, "ply")

        // TODO: Nur für Grid und Sparsegrid
        if (null != map.multiplier) compound.multiplier = map.multiplier

        if (null != map.builder) {
            def bc = map.builder
            compound.builder = bc.newInstance()

            if (null != map.maxDepth) {
                compound.builder.maxDepth = map.maxDepth 
            }
            if (null != map.minChildren) {
                compound.builder.minChildren = map.minChildren
            }
        }

        return compound
    }

    Grid createGrid(Map map) {
        Grid grid = null
        if (null != map.sparse) {
            grid = new SparseGrid()
        } else {
            grid = new Grid()
        }
        if (null != map.multiplier) grid.multiplier = map.multiplier
        return grid
    }

    BeveledBox createBeveledBox(Map map) {
        needs(map, "beveledBox", ["p0", "p1", "rb"])
        boolean isWireFrame = false
        if (null != map.wireFrame) {
            isWireFrame = map.wireFrame 
        }
        BeveledBox bbox = new BeveledBox(map.p0, map.p1, map.rb, isWireFrame)
        if (null != map.material) bbox.material = materials.get(map, "beveledBox")
        return bbox
    }

    Torus createTorus(Map map) {
        needs(map, "torus", ["a", "b"])
        Torus torus = new Torus(map.a, map.b)
        if (null != map.material) torus.material = materials.get(map, "torus")
        return torus
    }

    ImageTexture createImageTexture(Map map) {
        needs(map, "imageTexture", ["fileName"])
        ImageTexture t = new ImageTexture(map.fileName);
        if (null != map.mapping) t.mapping = map.mapping
        return t;
    }

    KDTree createKDTree(Map map) {
        KDTree tree = new KDTree()
        if (null != map.builder) {
            def c = map.builder
            tree.builder = c.newInstance() 
        }
        return tree
    }

}
