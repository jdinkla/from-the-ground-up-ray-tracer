Ray Tracer in Groovy and Kotlin
=============================

*Remark*: This project is currently rewritten from Java and Groovy to Kotlin. The old code with Java and Groovy is available in the branch `groovy-java`.

While reading the excellent book
"[Ray tracing from the ground up](http://www.raytracegroundup.com/)"
by Kevin Suffern I

* translated the C++ code to Java
* made the code more object oriented
* made the code thread-safe for parallel execution
* wrote a DSL for easy scene creation and manipulation

For easier manipulation of scenes i implemented a DSL for scenes.

The following image
![Rendered image](http://dinkla.net/images/rendered/BasicExample.png)

is described by the following Groovy DSL

```
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.utilities.Resolution

builder.world(id: "World48") {

    viewPlane(resolution: Resolution.RESOLUTION_1080, maxDepth: 2)

    camera(d: 1250, eye: p(0, 0.1, 10), lookAt: p(0, -1, 0))

    ambientLight(color: RGBColor.WHITE, ls: 0.5)

    lights {
        pointLight(location: p(0, 5, 0), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.5, kd: 1.0, exp: 10)
        phong(id: "sky", cd: c(0.1, 0.7, 1.0), ka: 0.75, kd: 1.0)
        reflective(id: "white", ks: 0.7, cd: c(1.0, 1.0, 1.0), ka: 0.5, kd: 0.75, exp: 2)
        phong(id: "red", ks: 0.9, cd: c(0.9, 0.4, 0.1), ka: 0.5, kd: 0.75, exp: 10)
        phong(id: "orange", ks: 0.9, cd: c(0.9, 0.7, 0.1), ka: 0.5, kd: 0.75, exp: 10)
    }

    objects {
        plane(point: p(0,-1.1,0), normal: n(0, 1, 0), material: "white")
        sphere(center: p(2.5, 0.5, 0.5), radius: 0.5, material: "orange")
        triangle(a: p(-3, 0, -1), b: p(-3, -1, 1), c: p(-1, 0, 1), material: "orange")
        smoothTriangle(a: p(-5, 0, -1), b: p(-5, -1, 1), c: p(-3, 0, 1), material: "orange")
        ply(file: "resources/TwoTriangles.ply", material: "red")
        grid {
            triangle(a: p(3, 0, -1), b: p(3, -1, 1), c: p(5, 0, 1), material: "orange")
            sphere(center: p(1.5, 1.5, 1.5), radius: 0.5, material: "sky")
        }
    }
}
```

See [my homepage for further information](http://dinkla.net/de/programming/groovy-rendering.html)


Start with

```
$ gradle exec_gui
```

Then choose an example from the `examples` directory.

Some examples require PLY files that are available on the net.

Here is another example:

![Rendered image](http://dinkla.net/images/rendered/VariousObjectsWithReflections.png)

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


