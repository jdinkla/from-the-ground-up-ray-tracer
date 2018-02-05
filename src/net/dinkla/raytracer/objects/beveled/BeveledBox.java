package net.dinkla.raytracer.objects.beveled;

import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.objects.*;
import net.dinkla.raytracer.objects.compound.Compound;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 26.05.2010
 * Time: 22:46:43
 * To change this template use File | Settings | File Templates.
 */
public class BeveledBox extends Compound {

    public final Point3D p0;
    public final Point3D p1;
    public final float rb;
    public BBox bbox;

    public BeveledBox(final Point3D p0, final Point3D p1, final float rb, boolean isWiredFrame) {
        super();

        this.p0 = p0;
        this.p1 = p1;
        this.rb = rb;

        Instance top_front_edge = new Instance (new OpenCylinder(-(p1.getX() - p0.getX() - 2 * rb) / 2, (p1.getX() - p0.getX() - 2 * rb) / 2, rb));	// top front edge
        top_front_edge.rotateZ(90);
        top_front_edge.translate((p0.getX() + p1.getX()) / 2, p1.getY() - rb, p1.getZ() - rb);
        //top_front_edge.transform_texture(false);
        objects.add(top_front_edge);

        // top back (-ve z)

        Instance top_back_edge = new Instance (new OpenCylinder(-(p1.getX() - p0.getX() - 2 * rb) / 2, (p1.getX() - p0.getX() - 2 * rb) / 2, rb));	// top back edge
        top_back_edge.rotateZ(90);
        top_back_edge.translate((p0.getX() + p1.getX()) / 2, p1.getY() - rb, p0.getZ() + rb);
        //top_back_edge->transform_texture(false);
        objects.add(top_back_edge);


        // top right (+ve x)

        Instance top_right_edge = new Instance (new OpenCylinder(-(p1.getZ() - p0.getZ() - 2 * rb) / 2, (p1.getZ() - p0.getZ() - 2 * rb) / 2, rb)); // top right edge
        top_right_edge.rotateX(90);
        top_right_edge.translate(p1.getX() - rb, p1.getY() - rb, (p0.getZ() + p1.getZ()) / 2);
        //top_right_edge->transform_texture(false);
        objects.add(top_right_edge);


        // top left (-ve x)

        Instance top_left_edge = new Instance (new OpenCylinder(-(p1.getZ() - p0.getZ() - 2 * rb) / 2, (p1.getZ() - p0.getZ() - 2 * rb) / 2, rb)); // top left edge
        top_left_edge.rotateX(90);
        top_left_edge.translate(p0.getX() + rb, p1.getY() - rb, (p0.getZ() + p1.getZ()) / 2);
        //top_left_edge->transform_texture(false);
        objects.add(top_left_edge);

        // bottom edges  (-ve y)

        // bottom front  (+ve z)

        Instance bottom_front_edge = new Instance (new OpenCylinder(-(p1.getX() - p0.getX() - 2 * rb) / 2, (p1.getX() - p0.getX() - 2 * rb) / 2, rb));	// bottom fromt edge
        bottom_front_edge.rotateZ(90);
        bottom_front_edge.translate((p0.getX() + p1.getX()) / 2, p0.getY() + rb, p1.getZ() - rb);
        //bottom_front_edge->transform_texture(false);
        objects.add(bottom_front_edge);


        // bottom back  (-ve z)

        Instance bottom_back_edge = new Instance (new OpenCylinder(-(p1.getX() - p0.getX() - 2 * rb) / 2, (p1.getX() - p0.getX() - 2 * rb) / 2, rb));	// bottom back edge
        bottom_back_edge.rotateZ(90);
        bottom_back_edge.translate((p0.getX() + p1.getX()) / 2, p0.getY() + rb, p0.getZ() + rb);
        //bottom_back_edge->transform_texture(false);
        objects.add(bottom_back_edge);


        // bottom right (-ve x, -ve y)

        Instance bottom_right_edge = new Instance (new OpenCylinder(-(p1.getZ() - p0.getZ() - 2 * rb) / 2, (p1.getZ() - p0.getZ() - 2 * rb) / 2, rb)); // bottom right edge
        bottom_right_edge.rotateX(90);
        bottom_right_edge.translate(p1.getX() - rb, p0.getY() + rb, (p0.getZ() + p1.getZ()) / 2);
        //bottom_right_edge->transform_texture(false);
        objects.add(bottom_right_edge);

        // bottom left (-ve x, -ve y)

        Instance bottom_left_edge = new Instance (new OpenCylinder(-(p1.getZ() - p0.getZ() - 2 * rb) / 2, (p1.getZ() - p0.getZ() - 2 * rb) / 2, rb)); // bottom left edge
        bottom_left_edge.rotateX(90);
        bottom_left_edge.translate(p0.getX() + rb, p0.getY() + rb, (p0.getZ() + p1.getZ()) / 2);
        //bottom_left_edge->transform_texture(false);
        objects.add(bottom_left_edge);


        // vertical edges

        // vertical right front  (+ve x, +ve z)

        Instance vertical_right_front_edge = new Instance (new OpenCylinder(p0.getY() + rb, p1.getY() - rb, rb));
        vertical_right_front_edge.translate(p1.getX() - rb, 0, p1.getZ() - rb);
        //vertical_right_front_edge->transform_texture(false);
        objects.add(vertical_right_front_edge);

        // vertical left front  (-ve x, +ve z)

        Instance vertical_left_front_edge = new Instance (new OpenCylinder(p0.getY() + rb, p1.getY() - rb, rb));
        vertical_left_front_edge.translate(p0.getX() + rb, 0, p1.getZ() - rb);
        //vertical_left_front_edge->transform_texture(false);
        objects.add(vertical_left_front_edge);

        // vertical left back  (-ve x, -ve z)

        Instance vertical_left_back_edge = new Instance (new OpenCylinder(p0.getY() + rb, p1.getY() - rb, rb));
        vertical_left_back_edge.translate(p0.getX() + rb, 0, p0.getZ() + rb);
        //vertical_left_back_edge->transform_texture(false);
        objects.add(vertical_left_back_edge);


        // vertical right back  (+ve x, -ve z)

        Instance vertical_right_back_edge = new Instance (new OpenCylinder(p0.getY() + rb, p1.getY() - rb, rb));
        vertical_right_back_edge.translate(p1.getX() - rb, 0, p0.getZ() + rb);
        //vertical_right_back_edge->transform_texture(false);
        objects.add(vertical_right_back_edge);

        // corner spheres

        // top right front

        Sphere top_right_front_corner = new Sphere(new Point3D(p1.getX() - rb, p1.getY() - rb, p1.getZ() - rb), rb);
        objects.add(top_right_front_corner);

        // top left front  (-ve x)

        Sphere top_left_front_corner = new Sphere(new Point3D(p0.getX() + rb, p1.getY() - rb, p1.getZ() - rb), rb);
        objects.add(top_left_front_corner);

        // top left back

        Sphere top_left_back_corner = new Sphere(new Point3D(p0.getX() + rb, p1.getY() - rb, p0.getZ() + rb), rb);
        objects.add(top_left_back_corner);

        // top right back

        Sphere top_right_back_corner = new Sphere(new Point3D(p1.getX() - rb, p1.getY() - rb, p0.getZ() + rb), rb);
        objects.add(top_right_back_corner);

        // bottom right front

        Sphere bottom_right_front_corner = new Sphere(new Point3D(p1.getX() - rb, p0.getY() + rb, p1.getZ() - rb), rb);
        objects.add(bottom_right_front_corner);

        // bottom left front

        Sphere bottom_left_front_corner = new Sphere(new Point3D(p0.getX() + rb, p0.getY() + rb, p1.getZ() - rb), rb);
        objects.add(bottom_left_front_corner);

        // bottom left back

        Sphere bottom_left_back_corner = new Sphere(new Point3D(p0.getX() + rb, p0.getY() + rb, p0.getZ() + rb), rb);
        objects.add(bottom_left_back_corner);

        // bottom right back

        Sphere bottom_right_back_corner = new Sphere(new Point3D(p1.getX() - rb, p0.getY() + rb, p0.getZ() + rb), rb);
        objects.add(bottom_right_back_corner);


        // the faces

        // bottom face: -ve y

        if (isWiredFrame) {
            return;
        }
        
        Rectangle bottom_face = new Rectangle(new Point3D(p0.getX() + rb, p0.getY(), p0.getZ() + rb),
                                              new Vector3D(0, 0, (p1.getZ() - rb) - (p0.getZ() + rb)),
                                              new Vector3D((p1.getX() - rb) - (p0.getX() + rb), 0, 0),
                                              new Normal(0, -1, 0));
        objects.add(bottom_face);


        // bottom face: +ve y

        Rectangle top_face = new Rectangle(new Point3D(p0.getX() + rb, p1.getY(), p0.getZ() + rb),
                                           new Vector3D(0, 0, (p1.getZ() - rb) - (p0.getZ() + rb)),
                                           new Vector3D((p1.getX() - rb) - (p0.getX() + rb), 0, 0),
                                           new Normal(0, 1, 0));
        objects.add(top_face);


        // back face: -ve z

        Rectangle back_face 	= new Rectangle(new Point3D(p0.getX() + rb, p0.getY() + rb, p0.getZ()),
                                                new Vector3D((p1.getX() - rb) - (p0.getX() + rb), 0, 0),
                                                new Vector3D(0, (p1.getY() - rb) - (p0.getY() + rb), 0),
                                                new Normal(0, 0 , -1));
        objects.add(back_face);


        // front face: +ve z

        Rectangle front_face 	= new Rectangle(new Point3D(p0.getX() + rb, p0.getY() + rb, p1.getZ()),
                                                new Vector3D((p1.getX() - rb) - (p0.getX() + rb), 0, 0),
                                                new Vector3D(0, (p1.getY() - rb) - (p0.getY() + rb), 0),
                                                new Normal(0, 0 , 1));
        objects.add(front_face);


        // left face: -ve x

        Rectangle left_face 	= new Rectangle(new Point3D(p0.getX(), p0.getY() + rb, p0.getZ() + rb),
                                                new Vector3D(0, 0, (p1.getZ() - rb) - (p0.getZ() + rb)),
                                                new Vector3D(0, (p1.getY() - rb) - (p0.getY() + rb), 0),
                                                new Normal(-1, 0 , 0));
        objects.add(left_face);


        // right face: +ve x

        Rectangle right_face 	= new Rectangle(new Point3D(p1.getX(), p0.getY() + rb, p0.getZ() + rb),
                                                new Vector3D(0, 0, (p1.getZ() - rb) - (p0.getZ() + rb)),
                                                new Vector3D(0, (p1.getY() - rb) - (p0.getY() + rb), 0),
                                                new Normal(1, 0 , 0));
        objects.add(right_face);
    }
    
}
