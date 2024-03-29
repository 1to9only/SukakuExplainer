/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.solver.rules;

import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;


/**
 * Implementation of the Naked Single solving techniques.
 */
public class NakedSingle implements DirectHintProducer {

    /**
     * Check if a cell has only one potential value, and accumulate
     * corresponding hints
     */
    public void getHints(Grid grid, HintsAccumulator accu) throws InterruptedException {



            // Iterate on cells
            for (int index = 0; index < 81; index++) {
                Cell cell = grid.getCell(index%9,index/9);
              if ( cell.getValue() == 0 ) {
                // Get the cell's potential values
                BitSet potentialValues = cell.getPotentialValues();
                if (potentialValues.cardinality() == 1) {
                    // One potential value -> solution found
                //  int uniqueValue = potentialValues.nextSetBit(0);
                    accu.add(new NakedSingleHint(this, null, cell, potentialValues.nextSetBit(0)));
                }
              }
            }

    }

    @Override
    public String toString() {
        return "Naked Singles";
    }

}
