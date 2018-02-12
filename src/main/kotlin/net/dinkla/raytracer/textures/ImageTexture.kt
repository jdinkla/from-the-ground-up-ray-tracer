package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Histogram
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.utilities.Resolution

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 21:36:34
 * To change this template use File | Settings | File Templates.
 */
class ImageTexture @Throws(IOException::class)
constructor(fileName: String) : Texture() {

    var res: Resolution
    var mapping: Mapping? = null

    var hRow: Histogram
    var hColumn: Histogram

    protected var image: BufferedImage

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
            //Sphere s = (Sphere) sr.getObject();
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

        return Color.createFromInt(rgb)
    }

}
