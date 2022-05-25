package net.dinkla.raytracer

import org.junit.jupiter.api.Assertions.assertTrue

const val PLY_EXAMPLE = "resources/TwoTriangles.ply"

const val PLY_BINARY_EXAMPLE = "resources/Isis.ply"

// TODO rewrite for kotest
inline fun <S, reified T> assertType(ls: List<S>, i: Int) {
    assertTrue(ls.size > i)
    assertTrue(ls[i] is T)
}

