package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.objects.IGeometricObject

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
        val go = cellsX[index]
        cellsX[index] =
            if (go == null) {
                `object`
            } else {
                go.combineInCell(`object`)
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
