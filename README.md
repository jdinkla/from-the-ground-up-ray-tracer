Ray Tracer in Kotlin
=============================

*Remark*: This project is currently rewritten from Java and Groovy to Kotlin. The old code with Java and 
Groovy is available in the branch `groovy-java`. It is not fully functional at the moment. There is a JavaFX interface 
and some example worlds written in Kotlin DSL.

While reading the excellent book
"[Ray tracing from the ground up](http://www.raytracegroundup.com/)"
by Kevin Suffern I

* translated the C++ code to Java in 2010, in 2018 started to port the code to Kotlin
* made the code more object oriented
* made the code thread-safe for parallel execution
* wrote a DSL for easy scene creation and manipulation first 2010 in Groovy, then 2019 in Kotlin
* Refactored the code (ongoing) quite a bit to develop nice object-functional program

For easier manipulation of scenes I implemented a DSL for scenes.

The following image
![Rendered image](http://dinkla.net/images/rendered/BasicExample.png)

is described by the following Kotlin program: 

```kotlin
package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDef

object World48 : WorldDef {

    override fun world() = Builder.build("World48") {

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

Start with

```
$ gradle javafx
```

Then choose an example from the `examples` directory.

Some examples require PLY files that are available on the net.

Here is another example:

![Rendered image](http://dinkla.net/images/rendered/VariousObjectsWithReflections.png)

Requirements
--------

Java 8 SDK is needed.

On a Mac use something similiar to the following:

```sh
$ export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home
$ ./gradlew javafx
```

Keywords
--------
ray tracing, rendering, Java, Groovy, DSL, ambient occlusion

Author
------
Written by [Jörn Dinkla](http://www.dinkla.net).

Remarks
-------
The folder resources contains the following:

* TwoTriangles.ply is from Kevin Suffern, the author of the book.
* Isis.ply was downloaded from [Cyberware](http://cyberware.com/) (dissolved since 2011 )


