package net.dinkla.raytracer.gui;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 24.06.2010
 * Time: 18:05:21
 * To change this template use File | Settings | File Templates.
 */
public class GuiUtilities {

    static public String getOutputPngFileName(final String fileName) {
        String outFileName = fileName.replaceAll(".[a-zA-Z0-9]+$", "");        
        final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        outFileName = "../" + df.format(new Date()) + "_" + outFileName + ".png";
        return outFileName;
    }
    
}
