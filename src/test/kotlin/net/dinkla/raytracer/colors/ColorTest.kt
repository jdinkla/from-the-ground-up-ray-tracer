package net.dinkla.raytracer.colors

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.colors.Color.Companion.BLUE
import net.dinkla.raytracer.colors.Color.Companion.GREEN
import net.dinkla.raytracer.colors.Color.Companion.RED
import net.dinkla.raytracer.colors.Color.Companion.fromInt
import net.dinkla.raytracer.colors.Color.Companion.fromString

internal class ColorTest : AnnotationSpec() {

    @Test
    fun plus() {
        Color(0.1) + Color(0.1, 0.3, 0.4) shouldBe Color(0.2, 0.4, 0.5)
    }

    @Test
    fun `multiplication of two colors`() {
        Color(0.5) * Color(0.5) shouldBe Color(0.25)
    }

    @Test
    fun `multiplication with scalar`() {
        Color(0.1) * 2.5 shouldBe Color(0.25)
    }

    @Test
    fun `multiplication with scalar from left`() {
        2.5 * Color(0.1) shouldBe Color(0.25)
    }

    @Test
    fun pow() {
        Color(0.1, 0.2, 0.3).pow(2.0) shouldBe Color(0.1*0.1, 0.2*0.2, 0.3*0.3)
    }

    @Test
    fun asInt() {
        val c = Color(0.0, 0.0, 1.0);
        c.toInt() shouldBe 255
    }

    @Test
    fun createFromInt() {
        val red = 3.0 / 255.0
        val green = 31.0 / 255.0
        val blue = 139.0 / 255.0
        val rgb = Color(red, green, blue).toInt();
        val c = fromInt(rgb)

        c.red shouldBe red
        c.green shouldBe green
        c.blue shouldBe blue
    }

    @Test
    fun createFromInts() {
        val color = Color.fromRGB(127, 0, 255)

        color.red shouldBe 127.0/255.0
        color.green shouldBe 0.0
        color.blue shouldBe 1.0
    }

    @Test
    fun createFromString() {
        fromString("FF0000") shouldBe RED
        fromString("00FF00") shouldBe GREEN
        fromString("0000FF") shouldBe BLUE
    }

    @Test
    fun clamp() {
        Color(1.1, 2.2, 3.3).clamp() shouldBe RED
    }

    @Test
    fun `clamp should return input if not clamped`() {
        val c = Color(0.1, 0.2, 0.3)
        c.clamp() shouldBe c
    }

    @Test
    fun maxToOne() {
        Color(0.5, 0.5, 2.0).maxToOne() shouldBe Color(0.25, 0.25, 1.0)
        Color(0.5, 2.0, 0.5).maxToOne() shouldBe Color(0.25, 1.0, 0.25)
        Color(2.0, 0.5, 0.5).maxToOne() shouldBe Color(1.0, 0.25, 0.25)
    }

    @Test
    fun equals() {
        Color(0.5, 0.2, 0.3) shouldBe Color(0.5, 0.2, 0.3)
        3.0 shouldNotBe Color(0.5, 0.2, 0.3)
        Color(0.5, 0.2, 0.3) shouldNotBe null
    }
}