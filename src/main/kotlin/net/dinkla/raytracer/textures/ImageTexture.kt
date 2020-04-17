package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Histogram
import net.dinkla.raytracer.utilities.Resolution

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException

class ImageTexture @Throws(IOException::class) constructor(fileName: String) : Texture() {

    private var res: Resolution
    private var mapping: Mapping? = null

    private var hRow: Histogram
    private var hColumn: Histogram

    private var image: BufferedImage

    init {
        image = ImageIO.read(File(fileName))
        res = Resolution(image.width, image.height)
        hRow = Histogram()
        hColumn = Histogram()
    }

    override fun getColor(sr: Shade): Color {
        var row = 0
        var column = 0

        if (null != mapping) {
            val p = sr.localHitPoint
            //Sphere s = (Sphere) sr.getGeometricObject();
            //p = new Point3D(p.minus(s.center));
            //Point3D p = new Point3D(sr.getNormal());
            val m = mapping!!.getTexelCoordinates(p, res)
            row = m.row
            column = m.column
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

        if (column >= res.hres) {
            column = res.hres - 1
        }

        if (column < 0) {
            column = 0
        }
        var rgb = 0
        hRow.add(row)
        hColumn.add(column)
        try {
            rgb = image.getRGB(column, res.vres - 1 - row)
            if (rgb == 0) {
                val a = 3
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            val a = 2
        }

        return Color.fromInt(rgb)
    }

}
