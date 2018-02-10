package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.MathUtils;
import net.dinkla.raytracer.objects.compound.Compound;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.05.2010
 * Time: 21:17:44
 * To change this template use File | Settings | File Templates.
 */
public class CompoundTest {

    @Test
    public void testGetBoundingBox() throws Exception {
        Sphere s = new Sphere(1.0);
        Compound c = new Compound();
        c.add(s);

        BBox bboxC = c.getBoundingBox();
        BBox bboxS = s.getBoundingBox();

        assertEquals(bboxC.getP(), bboxS.getP().minus(MathUtils.INSTANCE.getK_EPSILON()));
        assertEquals(bboxC.getQ(), bboxS.getQ().plus(MathUtils.INSTANCE.getK_EPSILON()));
    }

}
