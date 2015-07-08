package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.Histogram;

/**
* Created by IntelliJ IDEA.
* User: jorndinkla
* Date: 11.06.2010
* Time: 11:43:35
* To change this template use File | Settings | File Templates.
*/
public class Statistics {
    int numObjects;
    int numInner;
    int numLeafs;
    int numObjectsInLeafs;

    Histogram depthsLeafs;
    Histogram depthsNodes;
    Histogram numChildrenInLeafs;
    Histogram numObjectsShared;

    public Statistics() {
        numInner = 0;
        numLeafs = 0;
        numObjectsInLeafs = 0;

        depthsNodes = new Histogram();
        depthsLeafs = new Histogram();
        numChildrenInLeafs = new Histogram();
    }

    public static void cs(AbstractNode node, Statistics s, int depth) {
        if (node instanceof Leaf) {
            Leaf n = (Leaf) node;
            s.numLeafs++;
            s.depthsLeafs.add(depth);
            int sz = n.size();
            s.numChildrenInLeafs.add(sz);
            s.numObjectsInLeafs += sz;
        } else if (node instanceof InnerNode) {
            InnerNode n = (InnerNode) node;
            s.numInner++;
            s.depthsNodes.add(depth);
            cs(n.left, s, depth+1);
            cs(n.right, s, depth+1);
        }
    }

    public static void statistics(KDTree tree) {

        Statistics s = new Statistics();

        cs(tree.root, s, 0);

        System.out.println("num objects=" + tree.size());
        System.out.println("inner nodes=" + s.numInner);
        System.out.println("leaf nodes=" + s.numLeafs);
        System.out.println("objects in leafs=" + s.numObjectsInLeafs);

        System.out.println("depthsLeafs");
        s.depthsLeafs.println();

        System.out.println("depthsNodes");
        s.depthsNodes.println();


        System.out.println("numChildrenInLeafs");
        s.numChildrenInLeafs.println();

        // Anzahl Innernodes + Anzahl Leafs
        // Anzahl Objekte

        // maxDepth
        // minDepth Leaf
        // Histogram Knoten in Leafs
        // Histogram Object in Knoten (shared objects)
        // Histogramm Knoten pro Tiefe

    }
}
