package net.dinkla.raytracer.objects.utilities

class PlyFile(val x: String) {
    var filename: String = ""
    var numVertices: Int = 0
    var vertexProperties: Map<String, String> = hashMapOf()
    var numFaces: Int = 0
    var facesProperties: List<String> = listOf()
    var format: String = ""
    var formatVersion: String = ""
    var vertexDataLength: Int? = null

    fun read() {
        
    }

}