package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.utilities.Resolution

class Block(val xStart: Int, val xEnd: Int, val yStart: Int, val yEnd: Int) {
    companion object {
        fun partitionIntoBlocks(numBlocks: Int, resolution: Resolution): List<Block> {
            val blockHeight: Int = resolution.height / numBlocks
            val blockWidth: Int = resolution.width / numBlocks
            return buildList {
                for (j in 0 until numBlocks) {
                    for (i in 0 until numBlocks) {
                        val x = i * blockWidth
                        val y = j * blockHeight
                        add(Block(x, x + blockWidth, y, y + blockHeight))
                    }
                }
            }
        }
    }
}
