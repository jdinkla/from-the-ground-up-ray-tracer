package net.dinkla.raytracer.textures;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Histogram;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.utilities.Resolution;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 21:36:34
 * To change this template use File | Settings | File Templates.
 */
public class ImageTexture<C extends Color> extends Texture<C> {

    public Resolution res;
    public Mapping mapping;

    public Histogram hRow;
    public Histogram hColumn;

    protected BufferedImage image;

    public ImageTexture(String fileName) throws IOException {
        image = ImageIO.read(new File(fileName));
        res = new Resolution(image.getWidth(), image.getHeight());
        hRow = new Histogram();
        hColumn = new Histogram();
    }

    @Override
    public C getColor(Shade sr) {
        int row = 0;
        int column = 0;

        if (null != mapping) {
            Point3D p = sr.getLocalHitPoint();
            //Sphere s = (Sphere) sr.getObject();
            //p = new Point3D(p.minus(s.center));
            //Point3D p = new Point3D(sr.getNormal());
            Mapping.Mapped m = mapping.getTexelCoordinates(p, res);
            row = m.row;
            column = m.column;
        } else {
            // TODO Shade u und v
            //row = (int) ();
            //m.column = (int) ();
        }

//        System.out.println("row=" +row + ", column=" + column);
        
//        if (row >= res.vres) {
//            row = res.vres -1;
//        }

//        if (row < 0) {
//            row = 0;
//        }

        if (column >= res.hres()) {
            column = res.hres() -1;
        }

        if (column < 0) {
            column = 0;
        }
        int rgb = 0;
        hRow.add(row);
        hColumn.add(column);
        try {
            rgb = image.getRGB(column, res.vres() - 1 - row);
            if (rgb == 0) {
                int a = 3;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            int a = 2;
        }
        return (C) C.getWhite().createFromInt(rgb);
    }
    
}
