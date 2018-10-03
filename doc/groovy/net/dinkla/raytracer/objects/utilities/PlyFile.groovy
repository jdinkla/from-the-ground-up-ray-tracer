package net.dinkla.raytracer.objects.utilities

import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 14.06.2010
 * Time: 09:45:22
 * To change this template use File | Settings | File Templates.
 */
class PlyFile {

    static final Logger LOGGER = Logger.getLogger(PlyFile.class)

    String filename

    int numVertices = 0
    Map<String, PlyType> vertexProperties = [:]

    int numFaces = 0
    List facesProperties = []
    String format = ""
    String formatVersion = ""

    int headerLength = 0
//    File file


    public PlyFile(filename) {
        this.filename = filename
        readHeader();
        //file = new File(filename)
    }

    public int getVertexDataLength() {
        int result = 0;
        vertexProperties.each { k, v ->
            result += v.size
        }
        return result;
    }

    public int getFacesDataLength() {
        int result = 0;
        return result;
    }

    public void readHeader() {
        LOGGER.info("PLY: reading file '${filename}'")

        boolean isInHeader = true
        boolean isInVertexDef = false
        boolean isInFaceDef = false
        int numLine = 0

        BufferedReader br = new BufferedReader(new FileReader(filename));
        assert br.markSupported()
        String line;
        while ((line = br.readLine()) != null && isInHeader) {
            headerLength += line.size() + 1
            numLine++
            if (line =~ /end_header/) {
                isInHeader = false
            } else if (line =~ /^element\W+vertex/) {
                line = line.replaceFirst(/element\W+vertex\W+/, '')
                numVertices = Integer.valueOf(line)
                isInVertexDef = true
                isInFaceDef = false
                LOGGER.info("PLY: ${numVertices} vertices")
            } else if (line =~ /^element\W+face/) {
                line = line.replaceFirst(/element\W+face\W+/, '')
                numFaces = Integer.valueOf(line)
                isInVertexDef = false
                isInFaceDef = true
                LOGGER.info("PLY: ${numFaces} faces")
            } else if (line =~ /^property/) {
                line = line.replaceFirst(/property\W+/, '')
                if (isInVertexDef) {
                    def parts = line.split(/ /)
                    assert parts.size() == 2
                    vertexProperties.put(parts[1], PlyType.map.get(parts[0]))
                } else if (isInFaceDef) {
                    facesProperties.add(line)
                } else {
                    throw new RuntimeException("Unknown error in PLY file")
                }
            } else if (line =~ /^format/) {
                line = line.replaceFirst(/format\W+/, '')
                def parts = line.split(/ /)
                assert parts.size() == 2
                format = parts[0]
                formatVersion = parts[1]
            }
        }
        br.close();        
    }

    public float getFloat(byte[] buf, int i) {
        int a = 0x000000FF & (int) buf[i];
        int b = 0x000000FF & (int) buf[i+1];
        int c = 0x000000FF & (int) buf[i+2];
        int d = 0x000000FF & (int) buf[i+3];
        long bits = 0xFFFFFFFF & ((a << 24) + (b << 16) + (c << 8) + d);

        long sign = (bits & 0x0000000080000000) >> 31;
        long expo = ((bits & 0x000000007F800000) >> 23) - 127;
        long frac = bits & 0x00000000007FFFFF;
        if (frac != 0) {
            frac |= 0x0000000000800000;
        }

        float f = frac * Math.pow(2, -23) * Math.pow(2, expo);
        if (sign > 0) {
            f = -f;
        }

//        long bits = 0xFFFFFFFF & ((a << 0) + (b << 8) + (c << 16) + (direction << 24));
        println "a=$a, b=$b, c=$c, direction=$d, bits=$bits, sign=$sign, expo=$expo, frac=$frac, f=$f"
        //int bits = a + b << 8 + c << 16 + direction << 24;
        return Float.intBitsToFloat((int) bits);
    }

    public void read() {
        File file = new File(filename);

        println "filesize=${file.size()}"
        
        FileInputStream is = new FileInputStream(file);

        println "headerLength=$headerLength"
        is.skip(headerLength);

        // vertices
        int vSize = numVertices * getVertexDataLength();
        byte[] buf = new byte[vSize];

        println "vSize=$vSize"

        is.read(buf, 0, vSize);
        for (int i=0; i<numVertices; i++) {
            int index = i * getVertexDataLength();

            float x = getFloat(buf, index);
            float y = getFloat(buf, index + 4);
            float z = getFloat(buf, index + 8);

            //System.out.println("i=" + i + ":" + buf[i]);
            println "x=$x, y=$y, z=$z"
            if (i>10)
            break;
        }

        int restSize = file.size() - vSize - headerLength
        println "restSize=${restSize}"
        
        // faces
        int fSize = numFaces * getFacesDataLength();
        
        is.close();
    }

}
