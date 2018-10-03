package net.dinkla.raytracer.factories

import org.junit.jupiter.api.BeforeEach

// TODO DSL in kotlin
class AbstractFactoryTest {

    internal var x: X = X()
    internal var map: MutableMap<String, String> = mutableMapOf()
    internal var ls: MutableList<String> = mutableListOf()

    internal inner class X : AbstractFactory()

    @BeforeEach
    fun setUp() {
        x = X()
        map = mutableMapOf()
        ls = mutableListOf()
    }

    //    @Test
    fun testNeeds0() {
        AbstractFactory.needs(map, "x", ls)
    }

    //    @Test(expectedExceptions = RuntimeException.class)
    fun testNeeds1() {
        ls.add("a")
        AbstractFactory.needs(map, "x", ls)
    }

    //    @Test(expectedExceptions = RuntimeException.class)
    fun testNeeds2() {
        ls.add("a")
        ls.add("b")
        AbstractFactory.needs(map, "x", ls)
    }

    //    @Test
    fun testNeeds3() {
        ls.add("a")
        ls.add("b")
        map["a"] = "x"
        map["b"] = "x"
        AbstractFactory.needs(map, "x", ls)
    }

}
