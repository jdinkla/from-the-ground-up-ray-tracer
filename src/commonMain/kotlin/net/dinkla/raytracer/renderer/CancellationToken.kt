package net.dinkla.raytracer.renderer

import java.util.concurrent.atomic.AtomicBoolean

/**
 * A cooperative cancellation signal a [renderer][IRenderer] polls at a coarse granularity (per row or
 * per block, never per pixel) to stop in-flight CPU work promptly. It is *cooperative*: setting it does
 * not interrupt anything by itself — the renderer must check [isCancelled] and return early.
 *
 * The check is deliberately coarse: polling once per row/block keeps the per-pixel inner loop free of
 * any cancellation overhead, so a render that is never cancelled behaves exactly as before.
 */
interface CancellationToken {
    /** `true` once cancellation has been requested; the renderer should stop and return without finishing. */
    val isCancelled: Boolean
}

/**
 * The default token: never cancelled. Existing callers that pass no token (and the test suite) get this,
 * so the cancellation checks are no-ops and behaviour — and performance — is unchanged.
 */
object NoCancellation : CancellationToken {
    override val isCancelled: Boolean = false
}

/**
 * A thread-safe, settable [CancellationToken] backed by an [AtomicBoolean] so the EDT (or any other
 * thread) can request cancellation while render worker threads poll it. Cancellation is one-way: once
 * [cancel] is called the token stays cancelled.
 */
class AtomicCancellationToken : CancellationToken {
    private val cancelled = AtomicBoolean(false)

    override val isCancelled: Boolean
        get() = cancelled.get()

    /** Requests cancellation. Idempotent and safe to call from any thread. */
    fun cancel() {
        cancelled.set(true)
    }
}
