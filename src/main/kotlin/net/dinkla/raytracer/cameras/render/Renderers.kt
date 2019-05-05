package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector

enum class Renderers private constructor(val create: (ISingleRayRenderer, IColorCorrector) -> IRenderer)  {
    SEQUENTIAL( { r, c -> SequentialRenderer(r, c) }),
    FORK_JOIN({ r, c -> ForkJoinRenderer(r, c) }),
    PARALLEL({ r, c -> ParallelRenderer(r, c) }),
    COROUTINE({ r, c -> CoroutineRenderer(r, c) });

    companion object {
        val map: Map<String, Renderers> = hashMapOf(
                Pair("sequential", Renderers.SEQUENTIAL),
                Pair("forkjoin", Renderers.FORK_JOIN),
                Pair("parallel", Renderers.PARALLEL),
                Pair("coroutine", Renderers.COROUTINE)
        )

        fun get(s: String): Renderers {
            val p = map[s]
            return if (null == p) FORK_JOIN else p
        }
    }
}