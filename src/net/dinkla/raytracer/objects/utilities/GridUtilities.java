package net.dinkla.raytracer.objects.utilities;

import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.objects.SmoothTriangle;
import net.dinkla.raytracer.objects.Triangle;

import java.util.List;

import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.PI;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 10:19:32
 * To change this template use File | Settings | File Templates.
 */
public class GridUtilities {

    static public void tessellateFlatSphere(List list, final int horizontalSteps, final int verticalSteps) {

        // define the top triangles which all touch the north pole
        int k = 1;

        for (int j = 0; j <= horizontalSteps - 1; j++) {
            // define vertices

            Point3DF v0 = new Point3DF(0, 1, 0); 						// top (north pole)

            Point3DF v1 = new Point3DF((float) (sin(2.0f * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 			// bottom left
                        (float) (cos(PI * k / verticalSteps)),
                        (float) (cos(2.0f * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)));

            Point3DF v2 = new Point3DF((float)(sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)), 		// bottom  right
                        (float)(cos(PI * k / verticalSteps)),
                        (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)));

            Triangle triangle_ptr = new Triangle(v0, v1, v2);
            list.add(triangle_ptr);
        }

        // define the bottom triangles which all touch the south pole
        k = verticalSteps - 1;
        for (int j = 0; j <= horizontalSteps - 1; j++) {
            // define vertices

            Point3DF v0 = new Point3DF((float)(sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 			// top left
                        (float)(cos(PI * k / verticalSteps)),
                        (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)	));

            Point3DF v1 = new Point3DF(0, -1, 0);																		// bottom (south pole)

            Point3DF v2 = new Point3DF((float)(sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)), 		// top right
                        (float)(cos(PI * k / verticalSteps)),
                        (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps))	);

            Triangle triangle_ptr = new Triangle(v0, v1, v2);
            list.add(triangle_ptr);
        }

        //  define the other triangles
        for (k = 1; k <= verticalSteps - 2; k++) {
            for (int j = 0; j <= horizontalSteps - 1; j++) {
                // define the first triangle

                // vertices

                Point3DF v0 = new Point3DF((float)(sin(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)), 				// bottom left, use k + 1, j
                            (float)(cos(PI * (k + 1) / verticalSteps)),
                            (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps))	);

                Point3DF v1 = new Point3DF((float)(	sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)), 		// bottom  right, use k + 1, j + 1
                            (float)(cos(PI * (k + 1) / verticalSteps)),
                            (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)	));

                Point3DF v2 = new Point3DF((float)(	sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 					// top left, 	use k, j
                            (float)(cos(PI * k / verticalSteps)),
                            (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)	));

                Triangle triangle_ptr1 = new Triangle(v0, v1, v2);
                list.add(triangle_ptr1);


                // define the second triangle

                // vertices

                v0 = new Point3DF((float)(	sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)), 			// top right, use k, j + 1
                                (float)(cos(PI * k / verticalSteps)),
                                (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)) );

                v1 = new Point3DF((float)(	sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 				// top left, 	use k, j
                                (float)(cos(PI * k / verticalSteps)),
                                (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)	));

                v2 = new Point3DF((float)(	sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)), 	// bottom  right, use k + 1, j + 1
                                (float)(cos(PI * (k + 1) / verticalSteps)),
                                (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps))	);

                Triangle triangle_ptr2 = new Triangle(v0, v1, v2);
                list.add(triangle_ptr2);
            }
        }
    }

    static public void tessellateSmoothSphere(List list, final int horizontalSteps, final int verticalSteps) {
        // define the top triangles which all touch the north pole
        int k = 1;

        for (int j = 0; j <= horizontalSteps - 1; j++) {
            // define vertices

            Point3DF v0 = new Point3DF(0, 1, 0); 						// top (north pole)

            Point3DF v1 = new Point3DF((float) (sin(2.0f * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 			// bottom left
                        (float) (cos(PI * k / verticalSteps)),
                        (float) (cos(2.0f * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)));

            Point3DF v2 = new Point3DF((float)(sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)), 		// bottom  right
                        (float)(cos(PI * k / verticalSteps)),
                        (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)));

            SmoothTriangle triangle = new SmoothTriangle(v0, v1, v2);
            triangle.setN0(new Normal(v0.toVector()));
            triangle.setN1(new Normal(v1.toVector()));
            triangle.setN2(new Normal(v2.toVector()));
            list.add(triangle);
        }

        // define the bottom triangles which all touch the south pole
        k = verticalSteps - 1;
        for (int j = 0; j <= horizontalSteps - 1; j++) {
            // define vertices

            Point3DF v0 = new Point3DF((float)(sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 			// top left
                        (float)(cos(PI * k / verticalSteps)),
                        (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)	));

            Point3DF v1 = new Point3DF(0, -1, 0);																		// bottom (south pole)

            Point3DF v2 = new Point3DF((float)(sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)), 		// top right
                        (float)(cos(PI * k / verticalSteps)),
                        (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps))	);

            SmoothTriangle triangle = new SmoothTriangle(v0, v1, v2);
            triangle.setN0(new Normal(v0.toVector()));
            triangle.setN1(new Normal(v1.toVector()));
            triangle.setN2(new Normal(v2.toVector()));
            list.add(triangle);
        }

        //  define the other triangles
        for (k = 1; k <= verticalSteps - 2; k++) {
            for (int j = 0; j <= horizontalSteps - 1; j++) {
                // define the first triangle

                // vertices

                Point3DF v0 = new Point3DF((float)(sin(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)), 				// bottom left, use k + 1, j
                            (float)(cos(PI * (k + 1) / verticalSteps)),
                            (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps))	);

                Point3DF v1 = new Point3DF((float)(	sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)), 		// bottom  right, use k + 1, j + 1
                            (float)(cos(PI * (k + 1) / verticalSteps)),
                            (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)	));

                Point3DF v2 = new Point3DF((float)(	sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 					// top left, 	use k, j
                            (float)(cos(PI * k / verticalSteps)),
                            (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)	));

                SmoothTriangle triangle = new SmoothTriangle(v0, v1, v2);
                triangle.setN0(new Normal(v0.toVector()));
                triangle.setN1(new Normal(v1.toVector()));
                triangle.setN2(new Normal(v2.toVector()));
                list.add(triangle);


                // define the second triangle

                // vertices

                v0 = new Point3DF((float)(	sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)), 			// top right, use k, j + 1
                                (float)(cos(PI * k / verticalSteps)),
                                (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps)) );

                v1 = new Point3DF((float)(	sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)), 				// top left, 	use k, j
                                (float)(cos(PI * k / verticalSteps)),
                                (float)(cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps)	));

                v2 = new Point3DF((float)(	sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps)), 	// bottom  right, use k + 1, j + 1
                                (float)(cos(PI * (k + 1) / verticalSteps)),
                                (float)(cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps))	);

                SmoothTriangle triangle2 = new SmoothTriangle(v0, v1, v2);
                triangle.setN0(new Normal(v0.toVector()));
                triangle.setN1(new Normal(v1.toVector()));
                triangle.setN2(new Normal(v2.toVector()));
                list.add(triangle2);
            }
        }
    }

}
