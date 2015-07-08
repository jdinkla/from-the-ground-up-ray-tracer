package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.objects.GeometricObject;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.06.2010
 * Time: 14:33:02
 * To change this template use File | Settings | File Templates.
 */
public interface IKDTreeBuilder {

    public AbstractNode build(KDTree tree, BBox voxel);

    public void setMaxDepth(int maxDepth);

    public int getMaxDepth();

}
