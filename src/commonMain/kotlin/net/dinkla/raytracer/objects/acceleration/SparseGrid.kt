package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.compound.Compound

/**
 * A [Grid] whose cells are stored sparsely in a map keyed by linear cell index, so empty cells
 * cost nothing. It reuses all of [Grid]'s sizing heuristic, insertion scaffold, statistics and
 * 3D-DDA traversal; the cell storage is the only divergent concern, expressed through the handful
 * of `protected open` hooks overridden below.
 *
 * Two behaviours that follow from the sparse storage are preserved from the original implementation:
 *  - it keeps the bounding box it already has rather than recomputing it via `super.initialize()`;
 *  - it never promotes a crowded cell into a nested sub-grid, so there are no sub-cells to
 *    initialise and no diagnostic counters are emitted.
 */
class SparseGrid : Grid() {
    // protected GeometricObject[] cells;
    private var cellsX: MutableMap<Int, IGeometricObject> = mutableMapOf()

    override fun prepareInitialization() {
        isInitialized = true
        // super.initialize();
    }

    override fun allocateCells(numCells: Int) {
        // cells = new GeometricObject[numCells];
        cellsX = mutableMapOf()
        // cellsX.ensureCapacity(numCells/10);
    }

    override fun insertIntoCell(
        index: Int,
        `object`: IGeometricObject,
    ) {
        when (val go = cellsX[index]) {
            null -> {
                cellsX[index] = `object`
            }
            is Compound -> {
                go.add(`object`)
            }
            else -> {
                val c = Compound()
                c.add(go)
                c.add(`object`)
                cellsX[index] = c
            }
        }
    }

    override fun initializeSubcells() {
        // sparse cells are never promoted to sub-grids, so there is nothing to initialise
    }

    override fun cellAt(index: Int): IGeometricObject? = cellsX[index]

    override fun count(event: String) {
        // the sparse grid intentionally emits no diagnostic counters
    }
}
