/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.solver.rules;

import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;
import diuf.sudoku.tools.*;

/**
 * Implementation of hidden set solving techniques
 * (Hidden Pair, Hidden Triplet, Hidden Quad).
 * <p>
 * Only used for degree 2 and below. Degree 1 (hidden single)
 * is implemented in {@link diuf.sudoku.solver.rules.HiddenSingle}.
 */
public class HiddenSet implements IndirectHintProducer {

    private final int degree;
    private final boolean isDirect;


    public HiddenSet(int degree, boolean isDirect) {
//a     assert degree > 1 && degree <= 4;
        this.degree = degree;
        this.isDirect = isDirect;
    }

    public void getHints(Grid grid, HintsAccumulator accu) throws InterruptedException {
        if ( !grid.isLatinSquare() ) {
            getHints(grid, Grid.Block.class, accu);
        }
        getHints(grid, Grid.Column.class, accu);
        getHints(grid, Grid.Row.class, accu);

        if ( grid.isDiagonals() ) {
          if ( grid.isXDiagonal() ) {
            getHints(grid, Grid.Diagonal.class, accu);
          }
          if ( grid.isXAntiDiagonal() ) {
            getHints(grid, Grid.AntiDiagonal.class, accu);
          }
        }
        if ( grid.isDisjointGroups() ) {
            getHints(grid, Grid.DisjointGroup.class, accu);
        }
        if ( grid.isWindoku() ) {
            getHints(grid, Grid.Windoku.class, accu);
        }
        if ( grid.isAsterisk() ) {
            getHints(grid, Grid.Asterisk.class, accu);
        }
        if ( grid.isCenterDot() ) {
            getHints(grid, Grid.CenterDot.class, accu);
        }
        if ( grid.isGirandola() ) {
            getHints(grid, Grid.Girandola.class, accu);
        }
        if ( grid.isHalloween() ) {
            getHints(grid, Grid.Halloween.class, accu);
        }
        if ( grid.isPerCent() ) {
            getHints(grid, Grid.PerCent.class, accu);
        }
        if ( grid.isSdoku() ) {
            getHints(grid, Grid.SdokuBand.class, accu);
            getHints(grid, Grid.SdokuStack.class, accu);
        }
        if ( grid.isCustom() ) {
            getHints(grid, Grid.Custom.class, accu);
        }
    }

    /**
     * For each parts of the given type, check if a n-tuple of cells have
     * a common n-tuple of potential values, and no other potential value.
     * @param regionType the type of the parts to check
     * @param degree the degree of the tuples to search
     */
    private <T extends Grid.Region> void getHints(Grid grid, Class<T> regionType,
            HintsAccumulator accu) throws InterruptedException {
        Grid.Region[] regions = grid.getRegions(regionType);
        // Iterate on parts
        for (Grid.Region region : regions) {
            int nbEmptyCells = region.getEmptyCellCount();
            if (nbEmptyCells > degree * 2 || (isDirect && nbEmptyCells > degree)) {
                Permutations perm = new Permutations(degree, 9);
                // Iterate on tuple of values
                while (perm.hasNext()) {
                    int[] values = perm.nextBitNums();
//a                 assert values.length == degree;

                    // Build the value tuple
                    for (int i = 0; i < values.length; i++)
                        values[i] += 1; // 0..8 -> 1..9

                    // Build potential positions for each value of the tuple
                    BitSet[] potentialIndexes = new BitSet[degree];
                    for (int i = 0; i < degree; i++)
                        potentialIndexes[i] = region.getPotentialPositions(values[i]);

                    // Look for a common tuple of potential positions, with same degree
                    BitSet commonPotentialPositions =
                        CommonTuples.searchCommonTuple(potentialIndexes, degree);
                    if (commonPotentialPositions != null) {
                        // Hint found
                        IndirectHint hint = createHiddenSetHint(region, values, commonPotentialPositions);
                        if (hint != null && hint.isWorth())
                            accu.add(hint);
                    }
                }
            }
        }
    }

    private IndirectHint createHiddenSetHint(Grid.Region region, int[] values,
            BitSet commonPotentialPositions) {
        // Create set of fixed values, and set of other values
        BitSet valueSet = new BitSet(10);
        for (int i = 0; i < values.length; i++)
            valueSet.set(values[i], true);

        Cell[] cells = new Cell[degree];
        int dstIndex = 0;
        // Look for concerned potentials and removable potentials
        Map<Cell,BitSet> cellPValues = new LinkedHashMap<Cell,BitSet>();
        Map<Cell,BitSet> cellRemovePValues = new HashMap<Cell,BitSet>();
        for (int index = 0; index < 9; index++) {
            Cell cell = region.getCell(index);
            if (commonPotentialPositions.get(index)) {
                cellPValues.put(cell, valueSet);
                // Look for the potential values we can remove
                BitSet removablePotentials = new BitSet(10);
                for (int value = 1; value <= 9; value++) {
                    if (!valueSet.get(value) && cell.hasPotentialValue(value))
                        removablePotentials.set(value);
                }
                if (!removablePotentials.isEmpty())
                    cellRemovePValues.put(cell, removablePotentials);
                cells[dstIndex++] = cell;
            }
        }
        if (isDirect) {
            // Look for Hidden Single
            for (int value = 1; value <= 9; value++) {
                if (!valueSet.get(value)) {
                    BitSet positions = region.copyPotentialPositions(value);
                    if (positions.cardinality() > 1) {
                        positions.andNot(commonPotentialPositions);
                        if (positions.cardinality() == 1) {
                            // Hidden single found
                            int index = positions.nextSetBit(0);
                            Cell cell = region.getCell(index);
                            return new DirectHiddenSetHint(this, cells, values, cellPValues,
                                    cellRemovePValues, region, cell, value);
                        }
                    }
                }
            }
            // Nothing found
            return null;
        } else {
            return new HiddenSetHint(this, cells, values,
                    cellPValues, cellRemovePValues, region);
        }
    }

    @Override
    public String toString() {
        if (degree == 2) {
            if (isDirect) {
                return "Direct Hidden Pairs";
            } else {
                return "Hidden Pairs";
            }
        } else if (degree == 3) {
            if (isDirect) {
                return "Direct Hidden Triplets";
            } else {
                return "Hidden Triplets";
            }
        } else if (degree == 4) {
            return "Hidden Quads";
        }
        return "Hidden Sets " + degree;
    }

}
