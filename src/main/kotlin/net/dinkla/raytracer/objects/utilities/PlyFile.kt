package net.dinkla.raytracer.objects.utilities

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader

class PlyFile(val filename: String) {
    var numVertices: Int = 0
    var vertexProperties: Map<String, String> = hashMapOf()
    var numFaces: Int = 0
    var facesProperties: List<String> = listOf()
    var format: String = ""
    var formatVersion: String = ""
    var vertexDataLength: Int? = null
    private var headerLength = 0

    init {
        readHeader()
    }

    private fun readHeader() {
        LOGGER.info("PLY: reading file '${filename}'")

        val isInHeader = true
        var isInVertexDef = false
        var isInFaceDef = false
        var numLine = 0

        val br = BufferedReader(FileReader(filename));
        assert(br.markSupported())
        var line: String;
        var isAtEndOfFile = true
        while (!isAtEndOfFile && isInHeader) {
            line = br.readLine()
            isAtEndOfFile = line == null
            if (isAtEndOfFile) break
            headerLength += line.length + 1
            numLine++
//            if (line =~ /end_header/) {
//                isInHeader = false
//            } else if ("""^element\W+vertex""".toRegex().containsMatchIn(line) ) {
//                line = line.replaceFirst(/element\W+vertex\W+/, '')
//                numVerticesLeft = Integer.valueOf(line)
//                isInVertexDef = true
//                isInFaceDef = false
//                LOGGER.info("PLY: ${numVerticesLeft} vertices")
//            } else if (line =~ /^element\W+face/) {
//                line = line.replaceFirst(/element\W+face\W+/, '')
//                numFacesLeft = Integer.valueOf(line)
//                isInVertexDef = false
//                isInFaceDef = true
//                LOGGER.info("PLY: ${numFacesLeft} faces")
//            } else if (line =~ /^property/) {
//                line = line.replaceFirst(/property\W+/, '')
//                if (isInVertexDef) {
//                    def parts = line.split(/ /)
//                    assert parts.size() == 2
//                    vertexProperties.put(parts[1], PlyType.map.get(parts[0]))
//                } else if (isInFaceDef) {
//                    facesProperties.add(line)
//                } else {
//                    throw new RuntimeException("Unknown error in PLY file")
//                }
//            } else if (line =~ /^format/) {
//                line = line.replaceFirst(/format\W+/, '')
//                def parts = line.split(/ /)
//                assert parts.size() == 2
//                format = parts[0]
//                formatVersion = parts[1]
//            }
        }
        br.close();
    }

    fun read() {
        
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this::class.java)
    }

}