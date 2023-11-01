package net.dinkla.raytracer.utilities

import java.io.File

actual fun read(fileName: String): List<String> = File(fileName).readLines()
