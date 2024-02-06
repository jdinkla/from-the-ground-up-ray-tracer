package net.dinkla.raytracer.utilities

import java.io.File

fun read(fileName: String): List<String> = File(fileName).readLines()
