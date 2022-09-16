package net.dinkla.raytracer.utilities

expect class Image {

    fun getRGB(x: Int, y: Int): Int

    companion object {
        fun readImage(filename: String): Image
    }
}
