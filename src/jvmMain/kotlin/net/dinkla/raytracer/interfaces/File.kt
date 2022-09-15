package net.dinkla.raytracer.interfaces

import java.io.File

fun read(fileName: String): List<String> = File(fileName).readLines()

