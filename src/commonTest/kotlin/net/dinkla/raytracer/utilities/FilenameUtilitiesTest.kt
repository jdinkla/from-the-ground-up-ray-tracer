package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import korlibs.time.Month
import korlibs.time.Year

internal class FilenameUtilitiesTest :
    StringSpec({

        val fileNameBase = "World73"
        val timeStamp = korlibs.time.DateTime(Year(2022), Month.September, 17, 17, 42, 20, 0)

        "should exchange extension" {
            val s = outputPngFileName("$fileNameBase.kt", timeStamp)
            s shouldBe "20220917174220_$fileNameBase.png"
        }

        "should extract filename from Windows directories" {
            val fileName = "C:\\workspace\\from-the\\kotlin\\net\\dinkla\\raytracer\\examples\\NewWorld3.kt"
            val directory = "C:\\workspace\\from-the\\kotlin\\net\\dinkla\\raytracer"
            fileNameWithoutDirectory(fileName, directory, "\\") shouldBe "examples\\NewWorld3.kt"
        }

        "should extract filename from UNIX directories" {
            val fileName = "/workspace/from-the/kotlin/net/dinkla/raytracer/examples/NewWorld3.kt"
            val directory = "/workspace/from-the/kotlin/net/dinkla/raytracer"
            fileNameWithoutDirectory(fileName, directory, "/") shouldBe "examples/NewWorld3.kt"
        }
    })
