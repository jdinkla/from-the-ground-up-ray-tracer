package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.utilities.Resolution

class Block(
    val xStart: Int,
    val xEnd: Int,
    val yStart: Int,
    val yEnd: Int,
) {
    companion object {
        /**
         * Partitions the film `[0, width) x [0, height)` into a grid of blocks that tile it
         * exactly: no gaps, no overlaps, no zero-size blocks, and every pixel covered exactly
         * once — for any resolution and any [numBlocks].
         *
         * Each dimension is split into `min(numBlocks, dimension)` contiguous segments whose
         * lengths differ by at most one: the first `dimension % effectiveBlocks` segments get an
         * extra unit so the remainder is distributed instead of dropped. Capping the segment count
         * at the dimension avoids the zero-size blocks (and silent under-fill) that integer
         * truncation produced when `dimension < numBlocks`.
         */
        fun partitionIntoBlocks(
            numBlocks: Int,
            resolution: Resolution,
        ): List<Block> {
            require(numBlocks > 0) { "numBlocks must be positive, was $numBlocks" }
            val xBounds = splitDimension(resolution.width, numBlocks)
            val yBounds = splitDimension(resolution.height, numBlocks)
            return buildList {
                for (j in 0 until yBounds.size - 1) {
                    for (i in 0 until xBounds.size - 1) {
                        add(Block(xBounds[i], xBounds[i + 1], yBounds[j], yBounds[j + 1]))
                    }
                }
            }
        }

        /**
         * Returns the segment boundaries that split `[0, dimension)` into at most [numBlocks]
         * contiguous, non-empty segments of near-equal length. The result has one more entry than
         * the number of segments (the leading `0` and the trailing `dimension`); an empty
         * dimension yields an empty boundary list so no blocks are produced for a zero-area film.
         */
        private fun splitDimension(
            dimension: Int,
            numBlocks: Int,
        ): IntArray {
            if (dimension <= 0) return IntArray(0)
            val segments = minOf(numBlocks, dimension)
            val base = dimension / segments
            val remainder = dimension % segments
            val bounds = IntArray(segments + 1)
            var position = 0
            for (k in 0 until segments) {
                bounds[k] = position
                // The first `remainder` segments absorb one extra unit each, so the leftover rows
                // or columns are rendered rather than silently dropped.
                position += base + if (k < remainder) 1 else 0
            }
            bounds[segments] = position
            return bounds
        }
    }
}
