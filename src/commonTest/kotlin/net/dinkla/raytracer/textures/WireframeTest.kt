package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

class WireframeTest :
    StringSpec({
        val wireframe =
            Wireframe(
                size = 1.0,
                wireWidth = 0.05,
                fillColor = Color.WHITE,
                wireColor = Color.BLACK,
            )

        "a point in the cell interior is painted the fill colour" {
            // (0.5, 0.5): 0.5 from every boundary -> interior -> fill
            wireframe.colorAt(0.5, 0.5) shouldBe Color.WHITE
        }

        "a point just past a cell boundary along x is painted the wire colour" {
            // x = 0.02 -> 0.02 from the boundary at 0 (< wireWidth 0.05) -> wire
            wireframe.colorAt(0.02, 0.5) shouldBe Color.BLACK
        }

        "a point just before the next cell boundary along x is painted the wire colour" {
            // x = 0.98 -> 0.02 from the boundary at 1 -> wire
            wireframe.colorAt(0.98, 0.5) shouldBe Color.BLACK
        }

        "a point near a boundary along z is painted the wire colour" {
            // z = 0.03 -> near the boundary at 0 -> wire, even though x is interior
            wireframe.colorAt(0.5, 0.03) shouldBe Color.BLACK
        }

        "the boundary band width is honoured: just outside it stays fill" {
            // x = 0.1 -> 0.1 from the boundary (> wireWidth 0.05) -> interior -> fill
            wireframe.colorAt(0.1, 0.5) shouldBe Color.WHITE
        }

        "the wireframe reads the xz coordinates of the local hit point" {
            // y ignored; interior point -> fill
            wireframe.getColor(testShade(Point3D(0.5, 3.0, 0.5))) shouldBe Color.WHITE
            // near a boundary -> wire
            wireframe.getColor(testShade(Point3D(0.01, 3.0, 0.5))) shouldBe Color.BLACK
        }
    })
