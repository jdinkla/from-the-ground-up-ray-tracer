# Ray Tracer in Kotlin

A long time ago in 2010 I read the excellent book "[Ray tracing from the ground up](http://www.raytracegroundup.com/)" by Kevin Suffern. 
While reading this book 

* I translated the C++ code to Javan
* made the code more object oriented
* made the code thread-safe for parallel execution
* wrote a DSL for easy scene creation and manipulation in Groovy
* Refactored the code quite a bit to develop nice object-functional program

## Rewrite to Kotlin

From 2018-2020 I ported the code to Kotlin. The groovy DSL is not a Kotlin DSL.
In 2022/2023 I experimented with Kotlin multiplatform and it worked, but the APIs were changed too often. 
So I reverted back to JVM only in 2024. 

The old code with Java and Groovy is available in the branch `groovy-java`.

### Running

Run the different versions with

```bash
$ ./gradlew build
$ ./gradlew swing
$ ./gradlew run --args="--world=World20AreaDisk.kt --tracer=AREA --renderer=FORK_JOIN --resolution=1080p"
$ ./gradlew run --args="--world=World66.kt --renderer=FORK_JOIN --resolution=720p"
$ ./gradlew run --args="--world=World42.kt --renderer=COROUTINE --resolution=2160p"
```

![Rendered image](https://jdinkla.github.io/images/FromTheGroundUpRaytracerGUI.png)

Choose a scene file with a left click, see the source code and click render to render it.

Some examples require PLY files that are available on the net.

## Describing scenes with a DSL

For easier manipulation of scenes I implemented a DSL for scenes.

The following image
![Rendered image](https://jdinkla.github.io/images/rendered/BasicExample.png)

is described by the following Kotlin program: 

```kotlin
package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDef

object World48 : WorldDef {

    override fun world() = Builder.build {

        camera(d = 1250.0, eye = p(0.0, 0.1, 10.0), lookAt = p(0, -1, 0))

        ambientLight(color = Color.WHITE, ls = 0.5)

        lights {
            pointLight(location = p(0, 5, 0), color = c(1.0), ls = 1.0)
        }

        materials {
            phong(id = "sky", cd = c(0.1, 0.7, 1.0), ka = 0.75, kd = 1.0)
            reflective(id = "white", ks = 0.7, cd = c(1.0, 1.0, 1.0), ka = 0.5, kd = 0.75, exp = 2.0)
            phong(id = "red", ks = 0.9, cd = c(0.9, 0.4, 0.1), ka = 0.5, kd = 0.75, exp = 10.0)
            phong(id = "orange", ks = 0.9, cd = c(0.9, 0.7, 0.1), ka = 0.5, kd = 0.75, exp = 10.0)
        }

        objects {
            smoothTriangle(a = p(-5, 0, -1), b = p(-5, -1, 1), c = p(-3, 0, 1), material = "orange")
            plane(point = p(0.0,-1.1,0.0), normal = n(0, 1, 0), material = "white")
            ply(material = "red", fileName = "resources/TwoTriangles.ply")
            sphere(center = p(2.5, 0.5, 0.5), radius = 0.5, material = "orange")
            sphere(center = p(1.5, 1.5, 1.5), radius = 0.5, material = "sky")
            triangle(a = p(-3, 0, -1), b = p(-3, -1, 1), c = p(-1, 0, 1), material = "orange")
            triangle(a = p(3, 0, -1), b = p(3, -1, 1), c = p(5, 0, 1), material = "orange")
        }
    }
}
```

See [my homepage for further information](http://dinkla.net/de/programming/groovy-rendering.html)

See [my home page for more information and examples](https://jdinkla.github.io/software-development/2015/07/08/ray-tracing-with-groovy-and-java.html).

Here is another example:

![Rendered image](https://jdinkla.github.io/images/rendered/VariousObjectsWithReflections.png)

## Requirements

The Swing user interface runs with all Java versions.

## Keywords

ray tracing, rendering, Java, Groovy, DSL, ambient occlusion, Kotlin

## Author

Written by [Jörn Dinkla](http://www.dinkla.net).

## Remarks

The folder resources contains the following:

* TwoTriangles.ply is from Kevin Suffern, the author of the book.
* Isis.ply was downloaded from [Cyberware](http://cyberware.com/) (dissolved since 2011 )
