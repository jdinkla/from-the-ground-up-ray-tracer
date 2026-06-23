package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.arealights.DiskLight
import net.dinkla.raytracer.samplers.PureRandom
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder

class AuditTracerTest : StringSpec({
    "a plain point-lit matte scene uses the Whitted tracer" {
        val world =
            Builder.build {
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientLight(ls = 0.5)
                lights { pointLight(location = p(0, 5, 5), ls = 1.0) }
                materials { matte(id = "m", cd = c(1.0)) }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        chooseTracer(world) shouldBe AuditTracer.WHITTED
    }

    "a scene with an emissive material uses the area tracer" {
        val world =
            Builder.build {
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientLight(ls = 0.5)
                lights { pointLight(location = p(0, 5, 5), ls = 1.0) }
                materials {
                    matte(id = "m", cd = c(1.0))
                    emissive(id = "e", ce = c(1.0), le = 1.0)
                }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        chooseTracer(world) shouldBe AuditTracer.AREA
    }

    "a scene lit by ambient occlusion uses the area tracer" {
        val world =
            Builder.build {
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientOccluder(sampler = Sampler(PureRandom, 1, 1), numSamples = 1)
                materials { matte(id = "m", cd = c(1.0)) }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        chooseTracer(world) shouldBe AuditTracer.AREA
    }

    "a scene with an area light uses the area tracer" {
        val disk =
            DiskLight(
                sampler = Sampler(PureRandom, 1, 1),
                center = Point3D(0.0, 5.0, 0.0),
                radius = 1.0,
                normal = Normal.DOWN,
            ).apply { material = Matte() } // non-emissive: isolates the area-light condition
        val world =
            Builder.build {
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientLight(ls = 0.5)
                materials { matte(id = "m", cd = c(1.0)) }
                lights { areaLight(of = disk, numSamples = 1) }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        chooseTracer(world) shouldBe AuditTracer.AREA
    }

    "a scene that declares no tracer falls back to the heuristic: plain matte scene -> WHITTED" {
        val world =
            Builder.build {
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientLight(ls = 0.5)
                lights { pointLight(location = p(0, 5, 5), ls = 1.0) }
                materials { matte(id = "m", cd = c(1.0)) }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        auditTracer(world) shouldBe Tracers.WHITTED
    }

    "a scene that declares no tracer falls back to the heuristic: emissive scene -> AREA" {
        val world =
            Builder.build {
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientLight(ls = 0.5)
                materials {
                    matte(id = "m", cd = c(1.0))
                    emissive(id = "e", ce = c(1.0), le = 1.0)
                }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        auditTracer(world) shouldBe Tracers.AREA
    }

    "a scene's declared preferred tracer wins over the heuristic" {
        // A plain point-lit matte scene the heuristic would render with WHITTED, but it declares
        // MULTIPLE_OBJECTS — the hint must take precedence (this is the MultipleObjects.kt case).
        val world =
            Builder.build {
                metadata { preferredTracer(Tracers.MULTIPLE_OBJECTS) }
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientLight(ls = 0.5)
                lights { pointLight(location = p(0, 5, 5), ls = 1.0) }
                materials { matte(id = "m", cd = c(1.0)) }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        auditTracer(world) shouldBe Tracers.MULTIPLE_OBJECTS
    }

    "a declared path tracer wins even though the heuristic would pick AREA for an emissive scene" {
        // The CornellBox.kt case: an emissive panel makes the heuristic choose AREA, but the scene
        // needs PATH_TRACE. The declared hint must override the heuristic.
        val world =
            Builder.build {
                metadata { preferredTracer(Tracers.PATH_TRACE) }
                camera(eye = p(0, 0, 5), lookAt = p(0, 0, 0))
                ambientLight(ls = 0.0)
                materials {
                    matte(id = "m", cd = c(1.0))
                    emissive(id = "light", ce = c(1.0), le = 1.0)
                }
                objects { sphere(material = "m", center = p(0, 0, 0), radius = 1.0) }
            }

        auditTracer(world) shouldBe Tracers.PATH_TRACE
    }
})
