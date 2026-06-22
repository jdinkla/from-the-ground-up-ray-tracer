package net.dinkla.raytracer.renderer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.utilities.Resolution

// Every (x, y) inside [0, width) x [0, height) that the blocks cover. A correct partition visits
// each such pixel exactly once.
private fun coveredPixels(blocks: List<Block>): List<Pair<Int, Int>> =
    buildList {
        for (block in blocks) {
            for (y in block.yStart until block.yEnd) {
                for (x in block.xStart until block.xEnd) {
                    add(x to y)
                }
            }
        }
    }

private fun allPixels(resolution: Resolution): Set<Pair<Int, Int>> =
    buildSet {
        for (y in 0 until resolution.height) {
            for (x in 0 until resolution.width) {
                add(x to y)
            }
        }
    }

// Asserts the blocks tile [0, width) x [0, height) exactly: full coverage, and no pixel covered
// twice (size of the visited list equals the number of distinct pixels).
private fun shouldTileExactly(
    numBlocks: Int,
    resolution: Resolution,
) {
    val blocks = Block.partitionIntoBlocks(numBlocks, resolution)

    val visited = coveredPixels(blocks)
    visited.toSet() shouldBe allPixels(resolution)
    visited shouldHaveSize resolution.width * resolution.height
}

class BlockTest : StringSpec({

    "blocks tile a film evenly divisible by the block count" {
        shouldTileExactly(numBlocks = 4, resolution = Resolution(width = 8, height = 8))
    }

    "blocks tile a film smaller than the block grid without zero-size blocks" {
        // width 5 < 8 blocks: integer truncation would have produced zero-width blocks and no
        // coverage. The fix caps the segment count at the dimension, so every block is non-empty.
        val blocks = Block.partitionIntoBlocks(numBlocks = 8, resolution = Resolution(width = 5, height = 3))

        for (block in blocks) {
            (block.xEnd - block.xStart) shouldBeGreaterThanOrEqual 1
            (block.yEnd - block.yStart) shouldBeGreaterThanOrEqual 1
        }
        shouldTileExactly(numBlocks = 8, resolution = Resolution(width = 5, height = 3))
    }

    "blocks tile a non-divisible film, distributing the remainder rows and columns" {
        // 10 / 3 = 3 remainder 1, 7 / 3 = 2 remainder 1: the remainder must be absorbed, not
        // dropped. Without the fix the last row/column band would be missing.
        shouldTileExactly(numBlocks = 3, resolution = Resolution(width = 10, height = 7))
    }

    "blocks tile a single-row, single-column film" {
        shouldTileExactly(numBlocks = 8, resolution = Resolution(width = 1, height = 1))
    }

    "blocks tile a one-pixel-wide tall film" {
        shouldTileExactly(numBlocks = 32, resolution = Resolution(width = 1, height = 40))
    }

    "non-divisible block sizes differ by at most one unit" {
        // 10 columns across 3 segments -> widths 4, 3, 3 (max - min == 1). Near-equal blocks keep
        // the parallel workload balanced.
        val blocks = Block.partitionIntoBlocks(numBlocks = 3, resolution = Resolution(width = 10, height = 10))

        val widths = blocks.map { it.xEnd - it.xStart }.distinct().sorted()
        (widths.last() - widths.first()) shouldBe 1
    }

    "a zero-area film produces no blocks" {
        Block.partitionIntoBlocks(numBlocks = 8, resolution = Resolution(width = 0, height = 0)) shouldHaveSize 0
    }

    "a non-positive block count is rejected" {
        shouldThrow<IllegalArgumentException> {
            Block.partitionIntoBlocks(numBlocks = 0, resolution = Resolution(width = 4, height = 4))
        }
    }
})
