package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.interfaces.Random

class RandomTest : AnnotationSpec() {

    private val NUM = 1000

    @Test
    fun randInt() {
        for (i in 0 until NUM) {
            val r = Random.int(10)
            r shouldBeGreaterThanOrEqual 0
            r shouldBeLessThan 10
        }

        for (i in 0 until NUM) {
            val r = Random.int(18, 44)
            r shouldBeGreaterThanOrEqual 18
            r shouldBeLessThan 44
        }
    }

    @Test
    fun randFloat() {
        for (i in 0 until NUM) {
            val r = Random.double()
            r shouldBeGreaterThanOrEqual 0.0
            r shouldBeLessThan 1.0
        }
    }

    @Test
    fun randomShuffle() {
        val ls = ArrayList<Int>()
        ls.size shouldBe 0

        Random.randomShuffle(ls)
        ls.size shouldBe 0

        ls.add(11)
        ls.add(12)
        ls.add(13)

        ls.size shouldBe 3

        val i0 = Histogram()
        val i1 = Histogram()
        val i2 = Histogram()

        for (i in 0 until NUM) {
            Random.randomShuffle(ls)
            ls.size shouldBe 3
            ls shouldContainExactlyInAnyOrder setOf(11, 12, 13)
            i0.add(ls[0])
            i1.add(ls[1])
            i2.add(ls[2])
        }

        i0.keys().size shouldBe 3
        i1.keys().size shouldBe 3
        i2.keys().size shouldBe 3
    }

}
