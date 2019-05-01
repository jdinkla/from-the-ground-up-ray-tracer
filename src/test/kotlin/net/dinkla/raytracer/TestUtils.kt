package net.dinkla.raytracer

import org.junit.jupiter.api.Assertions.assertTrue

class TestUtils {

    companion object {

        @JvmStatic
        public val PLY_EXAMPLE = "resources/TwoTriangles.ply"

        @JvmStatic
        public val PLY_BINARY_EXAMPLE = "resources/Isis.ply"
    }
}

inline fun <S, reified T> assertType(ls: List<S>, i: Int) {
    assertTrue(ls.size > i)
    assertTrue(ls[i] is T)
}

