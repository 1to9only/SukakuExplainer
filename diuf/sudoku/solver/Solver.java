/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2009 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.solver;

import java.security.*;
import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.checks.*;
import diuf.sudoku.solver.rules.*;
import diuf.sudoku.solver.rules.chaining.*;
import diuf.sudoku.solver.rules.unique.*;
import diuf.sudoku.tools.*;

/**
 * The solver for Sudoku grids.
 * Used to:
 * <ul>
 * <li>Build or rebuild the potential values of empty cells of a grid
 * <li>Get all available hints, excluding those requiring chaining rules
 * <li>Get the next available hint that follows a given list of hints, in
 * increasing order of difficulty
 * <li>Solve a grid using brute-force
 * <li>Solve a grid using logical hints, and get a rating of the grid as well as a
 * list of the rules that were used.
 * <li>Check the validity of a grid
 * </ul>
 * In all cases, a validity check is automatically enforced as soon as an invalid grid
 * would cause performance loss or any other problems.
 * <p>
 * The solving techniques themselves are implemented in the various classes of the
 * packages {@link diuf.sudoku.solver.rules}, {@link diuf.sudoku.solver.rules.chaining}
 * and {@link diuf.sudoku.solver.rules.unique}. Checks for validity are
 * implemented in classes of the package {@link diuf.sudoku.solver.checks}.
 */
public class Solver {

//  private static final String ADVANCED_WARNING1 =
//      "This Sudoku seems to require advanced techniques\n" +
//      "that may take a very long computing time.\n" +
//      "Do you want to continue anyway?";
//  private static final String ADVANCED_WARNING2 =
//      "The next solving techniques are advanced ones\n" +
//      "that may take a very long computing time.\n" +
//      "Do you want to continue anyway?";

    public double difficulty;
    public double pearl;
    public double diamond;
    public char want;

    private Grid grid;
    private List<HintProducer> directHintProducers;
    private List<IndirectHintProducer> indirectHintProducers;
    private List<WarningHintProducer> validatorHintProducers;
    private List<WarningHintProducer> warningHintProducers;
    private List<WarningHintProducer> UniqueSolutionHintProducers;
    private List<IndirectHintProducer> chainingHintProducers;
    private List<IndirectHintProducer> chainingHintProducers2;
    private List<IndirectHintProducer> advancedHintProducers;
    private List<IndirectHintProducer> experimentalHintProducers;

//  private boolean isUsingAdvanced = false;


    private class DefaultHintsAccumulator implements HintsAccumulator {

        private final List<Hint> result;

        private DefaultHintsAccumulator(List<Hint> result) {
            super();
            this.result = result;
        }

        public void add(Hint hint) throws InterruptedException {
            if (!result.contains(hint))
                result.add(hint);
        }

    } // class DefaultHintsAccumulator

    private void addIfWorth(SolvingTechnique technique, Collection<HintProducer> coll, HintProducer producer) {
        if (Settings.getInstance().getTechniques().contains(technique))
            coll.add(producer);
    }

    private void addIfWorth(SolvingTechnique technique, Collection<IndirectHintProducer> coll, IndirectHintProducer producer) {
        if (Settings.getInstance().getTechniques().contains(technique))
            coll.add(producer);
    }

    public Solver(Grid grid) {
        this.grid = grid;
        directHintProducers = new ArrayList<HintProducer>();
        addIfWorth(SolvingTechnique.HiddenSingle, directHintProducers, new HiddenSingle());
        addIfWorth(SolvingTechnique.DirectPointing, directHintProducers, new Locking(true));
        addIfWorth(SolvingTechnique.DirectHiddenPair, directHintProducers, new HiddenSet(2, true));
        addIfWorth(SolvingTechnique.NakedSingle, directHintProducers, new NakedSingle());
        addIfWorth(SolvingTechnique.DirectHiddenTriplet, directHintProducers, new HiddenSet(3, true));
        indirectHintProducers = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.PointingClaiming, indirectHintProducers, new Locking(false));
        addIfWorth(SolvingTechnique.NakedPair, indirectHintProducers, new NakedSet(2));
        addIfWorth(SolvingTechnique.XWing, indirectHintProducers, new Fisherman(2));
        addIfWorth(SolvingTechnique.HiddenPair, indirectHintProducers, new HiddenSet(2, false));
        addIfWorth(SolvingTechnique.NakedTriplet, indirectHintProducers, new NakedSet(3));
        addIfWorth(SolvingTechnique.Swordfish, indirectHintProducers, new Fisherman(3));
        addIfWorth(SolvingTechnique.HiddenTriplet, indirectHintProducers, new HiddenSet(3, false));
        addIfWorth(SolvingTechnique.XYWing, indirectHintProducers, new XYWing(false));
        addIfWorth(SolvingTechnique.XYZWing, indirectHintProducers, new XYWing(true));
        addIfWorth(SolvingTechnique.UniqueLoop, indirectHintProducers, new UniqueLoops());
        addIfWorth(SolvingTechnique.NakedQuad, indirectHintProducers, new NakedSet(4));
        addIfWorth(SolvingTechnique.Jellyfish, indirectHintProducers, new Fisherman(4));
        addIfWorth(SolvingTechnique.HiddenQuad, indirectHintProducers, new HiddenSet(4, false));
        addIfWorth(SolvingTechnique.BivalueUniversalGrave, indirectHintProducers, new BivalueUniversalGrave());
        chainingHintProducers = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.AlignedPairExclusion, chainingHintProducers, new AlignedPairExclusion());
        addIfWorth(SolvingTechnique.ForcingChainCycle, chainingHintProducers, new Chaining(false, false, false, 0, true, 0));
        addIfWorth(SolvingTechnique.AlignedTripletExclusion, chainingHintProducers, new AlignedExclusion(3));
        addIfWorth(SolvingTechnique.NishioForcingChain, chainingHintProducers, new Chaining(false, true, true, 0, true, 0));
        addIfWorth(SolvingTechnique.MultipleForcingChain, chainingHintProducers, new Chaining(true, false, false, 0, true, 0));
        addIfWorth(SolvingTechnique.DynamicForcingChain, chainingHintProducers, new Chaining(true, true, false, 0, true, 0));
        chainingHintProducers2 = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.DynamicForcingChainPlus, chainingHintProducers2, new Chaining(true, true, false, 1, true, 0));
        // These rules are not really solving techs. They check the validity of the puzzle
        validatorHintProducers = new ArrayList<WarningHintProducer>();
        validatorHintProducers.add(new NoDoubles());
        warningHintProducers = new ArrayList<WarningHintProducer>();
        warningHintProducers.add(new NumberOfFilledCells());
        warningHintProducers.add(new NumberOfValues());
        warningHintProducers.add(new BruteForceAnalysis(false));
        UniqueSolutionHintProducers = new ArrayList<WarningHintProducer>();
        UniqueSolutionHintProducers.add(new BruteForceAnalysis(false));
        // These are very slow. We add them only as "rescue"
        advancedHintProducers = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.NestedForcingChain, advancedHintProducers, new Chaining(true, true, false, 2, true, 0));
        addIfWorth(SolvingTechnique.NestedForcingChain, advancedHintProducers, new Chaining(true, true, false, 3, true, 0));
        experimentalHintProducers = new ArrayList<IndirectHintProducer>(); // Two levels of nesting !?
        addIfWorth(SolvingTechnique.NestedForcingChain, experimentalHintProducers, new Chaining(true, true, false, 4, true, 0));
        addIfWorth(SolvingTechnique.NestedForcingChain, experimentalHintProducers, new Chaining(true, true, false, 4, true, 1));
        addIfWorth(SolvingTechnique.NestedForcingChain, experimentalHintProducers, new Chaining(true, true, false, 4, true, 2));
    }

    public Solver(Grid grid, int multi) {
        this.grid = grid;
        directHintProducers = new ArrayList<HintProducer>();
        addIfWorth(SolvingTechnique.HiddenSingle, directHintProducers, new HiddenSingle());
        addIfWorth(SolvingTechnique.DirectPointing, directHintProducers, new Locking(true));
        addIfWorth(SolvingTechnique.DirectHiddenPair, directHintProducers, new HiddenSet(2, true));
        addIfWorth(SolvingTechnique.NakedSingle, directHintProducers, new NakedSingle());
        addIfWorth(SolvingTechnique.DirectHiddenTriplet, directHintProducers, new HiddenSet(3, true));
        indirectHintProducers = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.PointingClaiming, indirectHintProducers, new Locking(false));
        addIfWorth(SolvingTechnique.NakedPair, indirectHintProducers, new NakedSet(2));
        addIfWorth(SolvingTechnique.XWing, indirectHintProducers, new Fisherman(2));
        addIfWorth(SolvingTechnique.HiddenPair, indirectHintProducers, new HiddenSet(2, false));
        addIfWorth(SolvingTechnique.NakedTriplet, indirectHintProducers, new NakedSet(3));
        addIfWorth(SolvingTechnique.Swordfish, indirectHintProducers, new Fisherman(3));
        addIfWorth(SolvingTechnique.HiddenTriplet, indirectHintProducers, new HiddenSet(3, false));
        addIfWorth(SolvingTechnique.XYWing, indirectHintProducers, new XYWing(false));
        addIfWorth(SolvingTechnique.XYZWing, indirectHintProducers, new XYWing(true));
      if ( multi == 0 )
        addIfWorth(SolvingTechnique.UniqueLoop, indirectHintProducers, new UniqueLoops());
        addIfWorth(SolvingTechnique.NakedQuad, indirectHintProducers, new NakedSet(4));
        addIfWorth(SolvingTechnique.Jellyfish, indirectHintProducers, new Fisherman(4));
        addIfWorth(SolvingTechnique.HiddenQuad, indirectHintProducers, new HiddenSet(4, false));
      if ( multi == 0 )
        addIfWorth(SolvingTechnique.BivalueUniversalGrave, indirectHintProducers, new BivalueUniversalGrave());
        chainingHintProducers = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.AlignedPairExclusion, chainingHintProducers, new AlignedPairExclusion());
        addIfWorth(SolvingTechnique.ForcingChainCycle, chainingHintProducers, new Chaining(false, false, false, 0, true, 0));
        addIfWorth(SolvingTechnique.AlignedTripletExclusion, chainingHintProducers, new AlignedExclusion(3));
        addIfWorth(SolvingTechnique.NishioForcingChain, chainingHintProducers, new Chaining(false, true, true, 0, true, 0));
        addIfWorth(SolvingTechnique.MultipleForcingChain, chainingHintProducers, new Chaining(true, false, false, 0, true, 0));
        addIfWorth(SolvingTechnique.DynamicForcingChain, chainingHintProducers, new Chaining(true, true, false, 0, true, 0));
        chainingHintProducers2 = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.DynamicForcingChainPlus, chainingHintProducers2, new Chaining(true, true, false, 1, true, 0));
        // These rules are not really solving techs. They check the validity of the puzzle
        validatorHintProducers = new ArrayList<WarningHintProducer>();
        validatorHintProducers.add(new NoDoubles());
        warningHintProducers = new ArrayList<WarningHintProducer>();
        warningHintProducers.add(new NumberOfFilledCells());
        warningHintProducers.add(new NumberOfValues());
        warningHintProducers.add(new BruteForceAnalysis(false));
        UniqueSolutionHintProducers = new ArrayList<WarningHintProducer>();
        UniqueSolutionHintProducers.add(new BruteForceAnalysis(false));
        // These are very slow. We add them only as "rescue"
        advancedHintProducers = new ArrayList<IndirectHintProducer>();
        addIfWorth(SolvingTechnique.NestedForcingChain, advancedHintProducers, new Chaining(true, true, false, 2, true, 0));
        addIfWorth(SolvingTechnique.NestedForcingChain, advancedHintProducers, new Chaining(true, true, false, 3, true, 0));
        experimentalHintProducers = new ArrayList<IndirectHintProducer>(); // Two levels of nesting !?
        addIfWorth(SolvingTechnique.NestedForcingChain, experimentalHintProducers, new Chaining(true, true, false, 4, true, 0));
        addIfWorth(SolvingTechnique.NestedForcingChain, experimentalHintProducers, new Chaining(true, true, false, 4, true, 1));
        addIfWorth(SolvingTechnique.NestedForcingChain, experimentalHintProducers, new Chaining(true, true, false, 4, true, 2));
    }

    /**
     * This is the basic Sudoku rule: If a cell contains a value,
     * that value can be removed from the potential values of
     * all cells in the same block, row or column.
     * @param partType the Class of the part to cancel in
     * (block, row or column)
     */
    private <T extends Grid.Region> void cancelBy(Class<T> partType) {
        Grid.Region[] parts = grid.getRegions(partType);
        for (Grid.Region part : parts) {
            for (int i = 0; i < 9; i++) {
                Cell cell = part.getCell(i);
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    // Remove the cell value from the potential values of other cells
                    for (int j = 0; j < 9; j++)
                        part.getCell(j).removePotentialValue(value);
                }
            }
        }
    }

    /**
     * Rebuild, for each empty cell, the set of potential values.
     */
    public void rebuildPotentialValues() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = grid.getCell(x, y);
                if (cell.getValue() == 0) {
                    for (int value = 1; value <= 9; value++)
                        cell.addPotentialValue(value);
                }
            }
        }
        cancelPotentialValues();
    }

    /**
     * Remove all illegal potential values according
     * to the current values of the cells.
     * Can be invoked after a new cell gets a value.
     */
    public void cancelPotentialValues() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = grid.getCell(x, y);
                if (cell.getValue() != 0) {
                    cell.clearPotentialValues();
                }
                else {
                  if (grid.isOddEven()) {
                    char ch = Settings.getInstance().getOddEven().charAt(cell.getY()*9+cell.getX());
                    if ( ch=='o' || ch=='O' ) { // isOdd cell, remove Even candidates
                        if ( cell.hasPotentialValue( 2) ) { cell.removePotentialValue( 2); }
                        if ( cell.hasPotentialValue( 4) ) { cell.removePotentialValue( 4); }
                        if ( cell.hasPotentialValue( 6) ) { cell.removePotentialValue( 6); }
                        if ( cell.hasPotentialValue( 8) ) { cell.removePotentialValue( 8); }
                    }
                    if ( ch=='e' || ch=='E' ) { // isEven cell, remove Odd candidates
                        if ( cell.hasPotentialValue( 1) ) { cell.removePotentialValue( 1); }
                        if ( cell.hasPotentialValue( 3) ) { cell.removePotentialValue( 3); }
                        if ( cell.hasPotentialValue( 5) ) { cell.removePotentialValue( 5); }
                        if ( cell.hasPotentialValue( 7) ) { cell.removePotentialValue( 7); }
                        if ( cell.hasPotentialValue( 9) ) { cell.removePotentialValue( 9); }
                    }
                  }
                }
            }
        }
        if ( !grid.isLatinSquare() ) {
            cancelBy(Grid.Block.class);
        }
        cancelBy(Grid.Row.class);
        cancelBy(Grid.Column.class);

        if ( grid.isDiagonals() ) {
          if ( grid.isXDiagonal() ) {
            cancelBy(Grid.Diagonal.class);
          }
          if ( grid.isXAntiDiagonal() ) {
            cancelBy(Grid.AntiDiagonal.class);
          }
        }
        if ( grid.isDisjointGroups() ) {
            cancelBy(Grid.DisjointGroup.class);
        }
        if ( grid.isWindoku() ) {
            cancelBy(Grid.Windoku.class);
        }
        if ( grid.isAsterisk() ) {
            cancelBy(Grid.Asterisk.class);
        }
        if ( grid.isCenterDot() ) {
            cancelBy(Grid.CenterDot.class);
        }
        if ( grid.isGirandola() ) {
            cancelBy(Grid.Girandola.class);
        }
        if ( grid.isHalloween() ) {
            cancelBy(Grid.Halloween.class);
        }
        if ( grid.isPerCent() ) {
            cancelBy(Grid.PerCent.class);
        }
        if ( grid.isSdoku() ) {
            cancelBy(Grid.SdokuBand.class);
            cancelBy(Grid.SdokuStack.class);
        }
        if ( grid.isCustom() ) {
            cancelBy(Grid.Custom.class);
        }
    }

    /**
     * Lower the current thread's priority.
     * @return the previous thread's priority
     */
    private int lowerPriority() {
        try {
            int result = Thread.currentThread().getPriority();
            Thread.currentThread().setPriority((Thread.NORM_PRIORITY + Thread.MIN_PRIORITY * 2) / 3);
            return result;
        } catch (AccessControlException ex) {}
        return 0;
    }

    /**
     * Reset the current thread's priority to the given value.
     * Typically, the given value is the value returned by
     * {@link #lowerPriority()}.
     * @param priority the new priority
     */
    private void normalPriority(int priority) {
        try {
            Thread.currentThread().setPriority(priority);
        } catch (AccessControlException ex) {}
    }

    /**
     * Get the first available validity warning hint.
     * This can be used to check the validity of a
     * Sudoku grid. If the sudoku is valid, <code>null</code>
     * is returned; else, a warning hint.
     * @return a warning hint if the sudoku is invalid, <code>null</code>
     * if the sudoku is valid.
     */
    public Hint checkValidity() {
        int oldPriority = lowerPriority();
        SingleHintAccumulator accu = new SingleHintAccumulator();
        try {
            for (WarningHintProducer producer : validatorHintProducers)
                producer.getHints(grid, accu);
            for (WarningHintProducer producer : warningHintProducers)
                producer.getHints(grid, accu);
        } catch (InterruptedException willProbablyHappen) {}
        normalPriority(oldPriority);
        return accu.getHint();
    }

    public Hint checkUniqueSolution() {
        int oldPriority = lowerPriority();
        SingleHintAccumulator accu = new SingleHintAccumulator();
        try {
            for (WarningHintProducer producer : UniqueSolutionHintProducers)
                producer.getHints(grid, accu);
        } catch (InterruptedException willProbablyHappen) {}
        normalPriority(oldPriority);
        return accu.getHint();
    }

    /**
     * Get the first available validity warning hint.
     * This can be used to check the validity of a
     * Sudoku grid. If the sudoku is valid, <code>null</code>
     * is returned; else, a warning hint.
     * @return a warning hint if the sudoku is invalid, <code>null</code>
     * if the sudoku is valid.
     */
    public Hint quickValidity() {
        int oldPriority = lowerPriority();
        SingleHintAccumulator accu = new SingleHintAccumulator();
        try {
            for (WarningHintProducer producer : validatorHintProducers)
                producer.getHints(grid, accu);
        //  for (WarningHintProducer producer : warningHintProducers)
        //      producer.getHints(grid, accu);
        } catch (InterruptedException willProbablyHappen) {}
        normalPriority(oldPriority);
        return accu.getHint();
    }

    private void gatherProducer(List<Hint> previousHints, List<Hint> curHints,
            HintsAccumulator accu, HintProducer producer) throws InterruptedException {
        // Get last hint producer. Because the last producer may not have produced
        // all its hints, we will need to restart from scratch with it.
        HintProducer lastProducer = null;
        if (!previousHints.isEmpty())
            lastProducer = previousHints.get(previousHints.size() - 1).getRule();

        if (curHints.size() < previousHints.size() && producer != lastProducer) {
            // Reuse previously computed hints of this producer
            Hint hint = null;
            hint = previousHints.get(curHints.size());
            while (hint.getRule() == producer) {
                accu.add(hint);
                hint = previousHints.get(curHints.size());
            }
        } else
            // Compute now
            producer.getHints(grid, accu);
    }

    public void gatherHints(List<Hint> previousHints, final List<Hint> result,
            HintsAccumulator accu, Asker asker) {

        int oldPriority = lowerPriority();
    //  boolean isAdvanced = false;
        try {
            for (HintProducer producer : directHintProducers)
                gatherProducer(previousHints, result, accu, producer);
            for (HintProducer producer : indirectHintProducers)
                gatherProducer(previousHints, result, accu, producer);
            for (HintProducer producer : validatorHintProducers)
                gatherProducer(previousHints, result, accu, producer);
            if (result.isEmpty()) {
                for (HintProducer producer : warningHintProducers)
                    gatherProducer(previousHints, result, accu, producer);
            }
            for (HintProducer producer : chainingHintProducers)
                gatherProducer(previousHints, result, accu, producer);
            for (HintProducer producer : chainingHintProducers2)
                gatherProducer(previousHints, result, accu, producer);
            boolean hasWarning = false;
            for (Hint hint : result) {
                if (hint instanceof WarningHint)
                    hasWarning = true;
            }
        //  // We have not been interrupted yet. So no rule has been found yet
        //  if (!hasWarning &&
        //          !(advancedHintProducers.isEmpty() && experimentalHintProducers.isEmpty()) &&
        //          (isUsingAdvanced || asker.ask(ADVANCED_WARNING2))) {
        //      isAdvanced = true;
        //      isUsingAdvanced = true;
                for (HintProducer producer : advancedHintProducers)
                    gatherProducer(previousHints, result, accu, producer);
                for (HintProducer producer : experimentalHintProducers) {
        //          if (result.isEmpty() && Settings.getInstance().isUsingAllTechniques())
                        gatherProducer(previousHints, result, accu, producer);
                }
        //  }
        } catch (InterruptedException willProbablyHappen) {}
    //  if (!isAdvanced)
    //      isUsingAdvanced = false;
        normalPriority(oldPriority);
    }

    public List<Hint> getAllHints(Asker asker) {
        int oldPriority = lowerPriority();
        List<Hint> result = new ArrayList<Hint>();
        HintsAccumulator accu = new DefaultHintsAccumulator(result);
        try {
            for (HintProducer producer : directHintProducers)
                producer.getHints(grid, accu);
            for (IndirectHintProducer producer : indirectHintProducers)
                producer.getHints(grid, accu);
            for (WarningHintProducer producer : validatorHintProducers)
                producer.getHints(grid, accu);
            if (result.isEmpty()) {
                for (WarningHintProducer producer : warningHintProducers)
                    producer.getHints(grid, accu);
            }
            if (result.isEmpty()) {
                for (IndirectHintProducer producer : chainingHintProducers)
                    producer.getHints(grid, accu);
            }
            if (result.isEmpty()) {
                for (IndirectHintProducer producer : chainingHintProducers2)
                    producer.getHints(grid, accu);
            }
            if (result.isEmpty()) {
        //      &&  !(advancedHintProducers.isEmpty() && experimentalHintProducers.isEmpty()) &&
        //          (isUsingAdvanced || asker.ask(ADVANCED_WARNING2))) {
        //      isUsingAdvanced = true;
                for (IndirectHintProducer producer : advancedHintProducers) {
        //          if (result.isEmpty())
                        producer.getHints(grid, accu);
                }
            }
            if (result.isEmpty()) {
                for (IndirectHintProducer producer : experimentalHintProducers) {
        //          if (result.isEmpty() && Settings.getInstance().isUsingAllTechniques())
                        producer.getHints(grid, accu);
                }
            }
        } catch (InterruptedException cannotHappen) {}
        normalPriority(oldPriority);
        return result;
    }

    public List<Hint> getAllMoreHints(Asker asker) {
        int oldPriority = lowerPriority();
        List<Hint> result = new ArrayList<Hint>();
        HintsAccumulator accu = new DefaultHintsAccumulator(result);
        try {
            for (HintProducer producer : directHintProducers)
                producer.getHints(grid, accu);
            for (IndirectHintProducer producer : indirectHintProducers)
                producer.getHints(grid, accu);
            for (WarningHintProducer producer : validatorHintProducers)
                producer.getHints(grid, accu);
        //  if (result.isEmpty()) {
                for (WarningHintProducer producer : warningHintProducers)
                    producer.getHints(grid, accu);
        //  }
        //  if (result.isEmpty()) {
                for (IndirectHintProducer producer : chainingHintProducers)
                    producer.getHints(grid, accu);
        //  }
        //  if (result.isEmpty()) {
                for (IndirectHintProducer producer : chainingHintProducers2)
                    producer.getHints(grid, accu);
        //  }
            if (result.isEmpty()) {
        //      &&  !(advancedHintProducers.isEmpty() && experimentalHintProducers.isEmpty()) &&
        //          (isUsingAdvanced || asker.ask(ADVANCED_WARNING2))) {
        //      isUsingAdvanced = true;
                for (IndirectHintProducer producer : advancedHintProducers) {
        //          if (result.isEmpty())
                        producer.getHints(grid, accu);
                }
            }
            if (result.isEmpty()) {
                for (IndirectHintProducer producer : experimentalHintProducers) {
        //          if (result.isEmpty() && Settings.getInstance().isUsingAllTechniques())
                        producer.getHints(grid, accu);
                }
            }
        } catch (InterruptedException cannotHappen) {}
        normalPriority(oldPriority);
        return result;
    }

    public boolean isSolved() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (grid.getCellValue(x, y) == 0)
                    return false;
            }
        }
        return true;
    }

    private class RuleComparer implements Comparator<Rule> {

        public int compare(Rule r1, Rule r2) {
            double d1 = r1.getDifficulty();
            double d2 = r2.getDifficulty();
            if (d1 < d2)
                return -1;
            else if (d1 > d2)
                return 1;
            else
                return r1.getName().compareTo(r2.getName());
        }

    }

    /**
     * Solve the Sudoku passed to the constructor.
     * <p>
     * Returns a sorted map between the rules that were used and
     * their frequency. Rules are sorted by difficulty.
     * @return the map between used rules and their frequency
     * @throws UnsupportedOperationException if the Sudoku cannot
     * be solved without recursive guessing (brute-force).
     */
    public Map<Rule,Integer> solve(Asker asker) {
        int oldPriority = lowerPriority();
        // rebuildPotentialValues();
        Map<Rule,Integer> usedRules = new TreeMap<Rule,Integer>(new RuleComparer());
    //  boolean isUsingAdvanced = false;
        while (!isSolved()) {
            SingleHintAccumulator accu = new SingleHintAccumulator();
            try {
                for (HintProducer producer : directHintProducers)
                    producer.getHints(grid, accu);
                for (IndirectHintProducer producer : indirectHintProducers)
                    producer.getHints(grid, accu);
                for (IndirectHintProducer producer : chainingHintProducers)
                    producer.getHints(grid, accu);
                for (IndirectHintProducer producer : chainingHintProducers2)
                    producer.getHints(grid, accu);
            //  if (!(advancedHintProducers.isEmpty() && experimentalHintProducers.isEmpty()) &&
            //          (asker == null || isUsingAdvanced || asker.ask(ADVANCED_WARNING1))) {
            //      isUsingAdvanced = true;
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers) {
            //          if (Settings.getInstance().isUsingAllTechniques())
                            producer.getHints(grid, accu);
                    }
            //  }
            } catch (InterruptedException willHappen) {}
            Hint hint = accu.getHint();
            if (hint == null)
                throw new UnsupportedOperationException("Failed to solve this Sudoku");
//a         assert hint instanceof Rule;
            Rule rule = (Rule)hint;
            if (usedRules.containsKey(rule))
                usedRules.put(rule, usedRules.get(rule) + 1);
            else
                usedRules.put(rule, 1);
            hint.apply(grid);
        }
        normalPriority(oldPriority);
        return usedRules;
    }

    /**
     * Get whether the grid's difficulty is between the two
     * bounds or not. If yes, return the actual difficulty.
     * If no, return a value less than <tt>min</tt> if the
     * grid is less difficult than <tt>min</tt> and a value
     * greater than <tt>max</tt> if the grid is more
     * difficult than <tt>max</tt>.
     * @param min the minimal difficulty (inclusive)
     * @param max the maximal difficulty (inclusive)
     * @return The actual difficulty if it is between the
     * given bounds. An arbitrary out-of-bounds value else.
     */
    public double analyseDifficulty(double min, double max) {
        int oldPriority = lowerPriority();
        try {
            double difficulty = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                  if ( max > 2.5 )
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                  if ( max > 6.2 )
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                  if ( max > 8.5 )
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    // Only used for generator. Ignore advanced/experimental techniques
                //if ( max > 9.0 )
                //  for (IndirectHintProducer producer : advancedHintProducers)
                //      producer.getHints(grid, accu);
                //if ( max > 9.5 )
                //  for (IndirectHintProducer producer : experimentalHintProducers)
                //      producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
                //  System.err.println("Failed to solve:\n" + grid.toString());
                    return 20.0;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
            //  if (difficulty >= min && max >= 12.0)
            //      break;
            //  if (difficulty > max)
            //      break;
                hint.apply(grid);
            }
            return difficulty;
        } finally {
            normalPriority(oldPriority);
        }
    }

    /**
     * Get whether the grid's difficulty is between the two
     * bounds or not. If yes, return the actual difficulty.
     * If no, return a value less than <tt>min</tt> if the
     * grid is less difficult than <tt>min</tt> and a value
     * greater than <tt>max</tt> if the grid is more
     * difficult than <tt>max</tt>.
     * @param min the minimal difficulty (inclusive)
     * @param max the maximal difficulty (inclusive)
     * @return The actual difficulty if it is between the
     * given bounds. An arbitrary out-of-bounds value else.
     */
    public double analyseNFCDifficulty(double min, double max) {
        int oldPriority = lowerPriority();
        try {
            double difficulty = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    // Only used for generator. Ignore advanced/experimental techniques
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
                //  System.err.println("Failed to solve:\n" + grid.toString());
                    return 20.0;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);
            }
            return difficulty;
        } finally {
            normalPriority(oldPriority);
        }
    }

    public void getDifficulty() {
        Grid backup = new Grid();
        grid.copyTo(backup);
        try {
            difficulty = 0.0;
            pearl = 0.0;
            diamond = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
                    difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);
                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        if (want == 'd' && difficulty > diamond) {
                            difficulty = 20.0;
                            break;
                        }
                        pearl = difficulty;
                    }
                }
                else if (want != 0 && difficulty > pearl) {
                    difficulty = 20.0;
                    break;
                }
            }
        } finally {
            backup.copyTo(grid);
        }
    }

    public void getHintsHint() {
            difficulty = 0.0;
            pearl = 0.0;
            diamond = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                long tt = System.currentTimeMillis(); long hh, mm, ss, ms;
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                tt = System.currentTimeMillis() - tt;
                Hint hint = accu.getHint();
                if (hint == null) {
                    difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);

                String s = "";
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCellValue(i % 9, i / 9);
                    s += (n==0)?".":n;
                }
                s += " ";
                ms= tt % 1000; tt = tt / 1000;
                ss= tt % 60;   tt = tt / 60;
                mm= tt % 60;   hh = tt / 60;
            //  if ( hh < 10 ) { s += "0"; } s += "" + hh + ":";
                if ( mm < 10 ) { s += "0"; } s += "" + mm + ":";
                if ( ss < 10 ) { s += "0"; } s += "" + ss + ".";
                if ( ms < 100) { s += "0"; }
                if ( ms < 10 ) { s += "0"; } s += "" + ms + " ";
                int w = (int)((ruleDiff + 0.05) * 10);
                int p = w % 10;
                w /= 10;
                String t = "" + w + "." + p + ", " + hint.toString();
                System.err.println(t);
                System.err.flush();
                s += w + "." + p;
                s += ", " + hint.toString2();
                if (hint instanceof IndirectHint) {
                    IndirectHint iHint = (IndirectHint)hint;
                    if ( iHint.isWorth() ) {
                        int countCells = 0;
                        Map<Cell, BitSet> getPots = iHint.getRemovablePotentials();
                        Map<Integer, BitSet> remPots = new TreeMap<Integer, BitSet>();
                        for (Cell cell : getPots.keySet()) {
                            remPots.put(cell.getY()*9+cell.getX(), getPots.get(cell));
                        }
                        for (int cellindex : remPots.keySet()) {
                            BitSet cellPots = remPots.get(cellindex);
                            if ( countCells == 0 ) { s += ":"; }
                            if ( countCells > 0 ) { s += ","; }
                            s += " r" + (cellindex/9+1) + "c" + (cellindex%9+1) + "<>";
                            int countPots = 0;
                            for (int pv=1; pv<=9; pv++ ) {
                                if ( cellPots.get( pv) ) { if ( countPots != 0 ) { s += ","; } s += pv; countPots++; }
                            }
                            countCells++;
                        }
                        Cell cell = iHint.getCell();
                        if (cell != null) {
                            s += ", r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                        }
                    }
                }
                if (hint instanceof DirectHint) {
                    DirectHint iHint = (DirectHint)hint;
                    Cell cell = iHint.getCell();
                    if (cell != null) {
                        s += ": r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                    }
                }
                System.out.println(s);
                System.out.flush();

                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        pearl = difficulty;
                    }
                }
            }
    }

    public void getReHintsHint() {
    //      difficulty = 0.0;
    //      pearl = 0.0;
    //      diamond = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                long tt = System.currentTimeMillis(); long hh, mm, ss, ms;
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                tt = System.currentTimeMillis() - tt;
                Hint hint = accu.getHint();
                if (hint == null) {
                    difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);

                String s = "";
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCellValue(i % 9, i / 9);
                    s += (n==0)?".":n;
                }
                s += " ";
                ms= tt % 1000; tt = tt / 1000;
                ss= tt % 60;   tt = tt / 60;
                mm= tt % 60;   hh = tt / 60;
            //  if ( hh < 10 ) { s += "0"; } s += "" + hh + ":";
                if ( mm < 10 ) { s += "0"; } s += "" + mm + ":";
                if ( ss < 10 ) { s += "0"; } s += "" + ss + ".";
                if ( ms < 100) { s += "0"; }
                if ( ms < 10 ) { s += "0"; } s += "" + ms + " ";
                int w = (int)((ruleDiff + 0.05) * 10);
                int p = w % 10;
                w /= 10;
                String t = "" + w + "." + p + ", " + hint.toString();
                System.err.println(t);
                System.err.flush();
                s += w + "." + p;
                s += ", " + hint.toString2();
                if (hint instanceof IndirectHint) {
                    IndirectHint iHint = (IndirectHint)hint;
                    if ( iHint.isWorth() ) {
                        int countCells = 0;
                        Map<Cell, BitSet> getPots = iHint.getRemovablePotentials();
                        Map<Integer, BitSet> remPots = new TreeMap<Integer, BitSet>();
                        for (Cell cell : getPots.keySet()) {
                            remPots.put(cell.getY()*9+cell.getX(), getPots.get(cell));
                        }
                        for (int cellindex : remPots.keySet()) {
                            BitSet cellPots = remPots.get(cellindex);
                            if ( countCells == 0 ) { s += ":"; }
                            if ( countCells > 0 ) { s += ","; }
                            s += " r" + (cellindex/9+1) + "c" + (cellindex%9+1) + "<>";
                            int countPots = 0;
                            for (int pv=1; pv<=9; pv++ ) {
                                if ( cellPots.get( pv) ) { if ( countPots != 0 ) { s += ","; } s += pv; countPots++; }
                            }
                            countCells++;
                        }
                        Cell cell = iHint.getCell();
                        if (cell != null) {
                            s += ", r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                        }
                    }
                }
                if (hint instanceof DirectHint) {
                    DirectHint iHint = (DirectHint)hint;
                    Cell cell = iHint.getCell();
                    if (cell != null) {
                        s += ": r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                    }
                }
                System.out.println(s);
                System.out.flush();

                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        pearl = difficulty;
                    }
                }
            }
    }

    public void getSolution() {
            difficulty = 0.0;
            pearl = 0.0;
            diamond = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
                    difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);

                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        pearl = difficulty;
                    }
                }
            }
    }

    public void getPencilMarks() {
            difficulty = 0.0;
            pearl = 0.0;
            diamond = 0.0;
            while (!isSolved()) {
                String s = "";

                int crd = 1;
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCell(i % 9, i / 9).getPotentialValues().cardinality();
                    if ( n > crd ) { crd = n; }
                }
                if ( crd > 1 )
                {
                    for (int i=0; i<3; i++ ) {
                        s = "+";
                        for (int j=0; j<3; j++ ) {
                            for (int k=0; k<3; k++ ) { s += "-";
                                for (int l=0; l<crd; l++ ) { s += "-";
                                }
                            }
                            s += "-+";
                        }
                        System.out.println(s);
                        System.out.flush();

                        for (int j=0; j<3; j++ ) {
                            s = "|";
                            for (int k=0; k<3; k++ ) {
                                for (int l=0; l<3; l++ ) {
                                    s += " ";
                                    int cnt = 0;
                                    int c = ((((i*3)+j)*3)+k)*3+l;
                                    Cell cell = grid.getCell(c % 9, c / 9);
                                    int n = cell.getValue();
                                    if ( n != 0 ) {
                                        s += n;
                                        cnt += 1;
                                    }
                                    if ( n == 0 ) {
                                        for (int pv=1; pv<=9; pv++ ) {
                                            if ( cell.hasPotentialValue( pv) ) {
                                                s += pv;
                                                cnt += 1;
                                            }
                                        }
                                    }
                                    for (int pad=cnt; pad<crd; pad++ ) { s += " ";
                                    }
                                }
                                s += " |";
                            }
                            System.out.println(s);
                            System.out.flush();
                        }
                    }

                    s = "+";
                    for (int j=0; j<3; j++ ) {
                        for (int k=0; k<3; k++ ) { s += "-";
                            for (int l=0; l<crd; l++ ) { s += "-";
                            }
                        }
                        s += "-+";
                    }
                    System.out.println(s);
                    System.out.flush();
                }

                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
                    difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);

                s = "";
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCellValue(i % 9, i / 9);
                    s += (n==0)?".":n;
                }
                s += " ";
                int w = (int)((ruleDiff + 0.05) * 10);
                int p = w % 10;
                w /= 10;
                String t = "" + w + "." + p + ", " + hint.toString();
                System.err.println(t);
                System.err.flush();
                s += w + "." + p;
                s += ", " + hint.toString2();
                if (hint instanceof IndirectHint) {
                    IndirectHint iHint = (IndirectHint)hint;
                    if ( iHint.isWorth() ) {
                        int countCells = 0;
                        Map<Cell, BitSet> getPots = iHint.getRemovablePotentials();
                        Map<Integer, BitSet> remPots = new TreeMap<Integer, BitSet>();
                        for (Cell cell : getPots.keySet()) {
                            remPots.put(cell.getY()*9+cell.getX(), getPots.get(cell));
                        }
                        for (int cellindex : remPots.keySet()) {
                            BitSet cellPots = remPots.get(cellindex);
                            if ( countCells == 0 ) { s += ":"; }
                            if ( countCells > 0 ) { s += ","; }
                            s += " r" + (cellindex/9+1) + "c" + (cellindex%9+1) + "<>";
                            int countPots = 0;
                            for (int pv=1; pv<=9; pv++ ) {
                                if ( cellPots.get( pv) ) { if ( countPots != 0 ) { s += ","; } s += pv; countPots++; }
                            }
                            countCells++;
                        }
                        Cell cell = iHint.getCell();
                        if (cell != null) {
                            s += ", r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                        }
                    }
                }
                if (hint instanceof DirectHint) {
                    DirectHint iHint = (DirectHint)hint;
                    Cell cell = iHint.getCell();
                    if (cell != null) {
                        s += ": r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                    }
                }
                System.out.println(s);
                System.out.flush();

                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        pearl = difficulty;
                    }
                }
            }
    }

    public void dumpPencilMarks() {
                String s = "";

                int crd = 1;
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCell(i % 9, i / 9).getPotentialValues().cardinality();
                    if ( n > crd ) { crd = n; }
                }
//              if ( crd > 1 )
//              {
                    for (int i=0; i<3; i++ ) {
                        s = "+";
                        for (int j=0; j<3; j++ ) {
                            for (int k=0; k<3; k++ ) { s += "-";
                                for (int l=0; l<crd; l++ ) { s += "-";
                                }
                            }
                            s += "-+";
                        }
                        System.out.println(s);
                        System.out.flush();

                        for (int j=0; j<3; j++ ) {
                            s = "|";
                            for (int k=0; k<3; k++ ) {
                                for (int l=0; l<3; l++ ) {
                                    s += " ";
                                    int cnt = 0;
                                    int c = ((((i*3)+j)*3)+k)*3+l;
                                    Cell cell = grid.getCell(c % 9, c / 9);
                                    int n = cell.getValue();
                                    if ( n != 0 ) {
                                        s += n;
                                        cnt += 1;
                                    }
                                    if ( n == 0 ) {
                                        for (int pv=1; pv<=9; pv++ ) {
                                            if ( cell.hasPotentialValue( pv) ) {
                                                s += pv;
                                                cnt += 1;
                                            }
                                        }
                                    }
                                    for (int pad=cnt; pad<crd; pad++ ) { s += " ";
                                    }
                                }
                                s += " |";
                            }
                            System.out.println(s);
                            System.out.flush();
                        }
                    }

                    s = "+";
                    for (int j=0; j<3; j++ ) {
                        for (int k=0; k<3; k++ ) { s += "-";
                            for (int l=0; l<crd; l++ ) { s += "-";
                            }
                        }
                        s += "-+";
                    }
                    System.out.println(s);
                    System.out.flush();
//              }

    }

    public void getClosureDifficulty() {
        Grid backup = new Grid();
        grid.copyTo(backup);
        try {
            difficulty = 0.0;
            pearl = 0.0;
            diamond = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
//closure           difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);
                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        if (want == 'd' && difficulty > diamond) {
                            difficulty = 20.0;
                            break;
                        }
                        pearl = difficulty;
                    }
                }
                else if (want != 0 && difficulty > pearl) {
                    difficulty = 20.0;
                    break;
                }
            }
        } finally {
            backup.copyTo(grid);
        }
    }

    public void getClosureHintsHint() {
            difficulty = 0.0;
            pearl = 0.0;
            diamond = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
//closure           difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);

                String s = "";
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCellValue(i % 9, i / 9);
                    s += (n==0)?".":n;
                }
                s += " ";
                int w = (int)((ruleDiff + 0.05) * 10);
                int p = w % 10;
                w /= 10;
                String t = "" + w + "." + p + ", " + hint.toString();
                System.err.println(t);
                System.err.flush();
                s += w + "." + p;
                s += ", " + hint.toString2();
                if (hint instanceof IndirectHint) {
                    IndirectHint iHint = (IndirectHint)hint;
                    if ( iHint.isWorth() ) {
                        int countCells = 0;
                        Map<Cell, BitSet> getPots = iHint.getRemovablePotentials();
                        Map<Integer, BitSet> remPots = new TreeMap<Integer, BitSet>();
                        for (Cell cell : getPots.keySet()) {
                            remPots.put(cell.getY()*9+cell.getX(), getPots.get(cell));
                        }
                        for (int cellindex : remPots.keySet()) {
                            BitSet cellPots = remPots.get(cellindex);
                            if ( countCells == 0 ) { s += ":"; }
                            if ( countCells > 0 ) { s += ","; }
                            s += " r" + (cellindex/9+1) + "c" + (cellindex%9+1) + "<>";
                            int countPots = 0;
                            for (int pv=1; pv<=9; pv++ ) {
                                if ( cellPots.get( pv) ) { if ( countPots != 0 ) { s += ","; } s += pv; countPots++; }
                            }
                            countCells++;
                        }
                        Cell cell = iHint.getCell();
                        if (cell != null) {
                            s += ", r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                        }
                    }
                }
                if (hint instanceof DirectHint) {
                    DirectHint iHint = (DirectHint)hint;
                    Cell cell = iHint.getCell();
                    if (cell != null) {
                        s += ": r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                    }
                }
                System.out.println(s);
                System.out.flush();

                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        pearl = difficulty;
                    }
                }
            }
    }

    public void getReClosureHintsHint() {
    //      difficulty = 0.0;
    //      pearl = 0.0;
    //      diamond = 0.0;
            while (!isSolved()) {
                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
//closure           difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);

                String s = "";
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCellValue(i % 9, i / 9);
                    s += (n==0)?".":n;
                }
                s += " ";
                int w = (int)((ruleDiff + 0.05) * 10);
                int p = w % 10;
                w /= 10;
                String t = "" + w + "." + p + ", " + hint.toString();
                System.err.println(t);
                System.err.flush();
                s += w + "." + p;
                s += ", " + hint.toString2();
                if (hint instanceof IndirectHint) {
                    IndirectHint iHint = (IndirectHint)hint;
                    if ( iHint.isWorth() ) {
                        int countCells = 0;
                        Map<Cell, BitSet> getPots = iHint.getRemovablePotentials();
                        Map<Integer, BitSet> remPots = new TreeMap<Integer, BitSet>();
                        for (Cell cell : getPots.keySet()) {
                            remPots.put(cell.getY()*9+cell.getX(), getPots.get(cell));
                        }
                        for (int cellindex : remPots.keySet()) {
                            BitSet cellPots = remPots.get(cellindex);
                            if ( countCells == 0 ) { s += ":"; }
                            if ( countCells > 0 ) { s += ","; }
                            s += " r" + (cellindex/9+1) + "c" + (cellindex%9+1) + "<>";
                            int countPots = 0;
                            for (int pv=1; pv<=9; pv++ ) {
                                if ( cellPots.get( pv) ) { if ( countPots != 0 ) { s += ","; } s += pv; countPots++; }
                            }
                            countCells++;
                        }
                        Cell cell = iHint.getCell();
                        if (cell != null) {
                            s += ", r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                        }
                    }
                }
                if (hint instanceof DirectHint) {
                    DirectHint iHint = (DirectHint)hint;
                    Cell cell = iHint.getCell();
                    if (cell != null) {
                        s += ": r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                    }
                }
                System.out.println(s);
                System.out.flush();

                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        pearl = difficulty;
                    }
                }
            }
    }

    public void getClosurePencilMarks() {
            difficulty = 0.0;
            pearl = 0.0;
            diamond = 0.0;
            while (!isSolved()) {
                String s = "";

                int crd = 1;
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCell(i % 9, i / 9).getPotentialValues().cardinality();
                    if ( n > crd ) { crd = n; }
                }
                if ( crd > 1 )
                {
                    for (int i=0; i<3; i++ ) {
                        s = "+";
                        for (int j=0; j<3; j++ ) {
                            for (int k=0; k<3; k++ ) { s += "-";
                                for (int l=0; l<crd; l++ ) { s += "-";
                                }
                            }
                            s += "-+";
                        }
                        System.out.println(s);
                        System.out.flush();

                        for (int j=0; j<3; j++ ) {
                            s = "|";
                            for (int k=0; k<3; k++ ) {
                                for (int l=0; l<3; l++ ) {
                                    s += " ";
                                    int cnt = 0;
                                    int c = ((((i*3)+j)*3)+k)*3+l;
                                    Cell cell = grid.getCell(c % 9, c / 9);
                                    int n = cell.getValue();
                                    if ( n != 0 ) {
                                        s += n;
                                        cnt += 1;
                                    }
                                    if ( n == 0 ) {
                                        for (int pv=1; pv<=9; pv++ ) {
                                            if ( cell.hasPotentialValue( pv) ) {
                                                s += pv;
                                                cnt += 1;
                                            }
                                        }
                                    }
                                    for (int pad=cnt; pad<crd; pad++ ) { s += " ";
                                    }
                                }
                                s += " |";
                            }
                            System.out.println(s);
                            System.out.flush();
                        }
                    }

                    s = "+";
                    for (int j=0; j<3; j++ ) {
                        for (int k=0; k<3; k++ ) { s += "-";
                            for (int l=0; l<crd; l++ ) { s += "-";
                            }
                        }
                        s += "-+";
                    }
                    System.out.println(s);
                    System.out.flush();
                }

                SingleHintAccumulator accu = new SingleHintAccumulator();
                try {
                    for (HintProducer producer : directHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : indirectHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : chainingHintProducers2)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : advancedHintProducers)
                        producer.getHints(grid, accu);
                    for (IndirectHintProducer producer : experimentalHintProducers)
                        producer.getHints(grid, accu);
                } catch (InterruptedException willHappen) {}
                Hint hint = accu.getHint();
                if (hint == null) {
//closure           difficulty = 20.0;
                    break;
                }
//a             assert hint instanceof Rule;
                Rule rule = (Rule)hint;
                double ruleDiff = rule.getDifficulty();
                if (ruleDiff > difficulty)
                    difficulty = ruleDiff;
                hint.apply(grid);

                s = "";
                for (int i = 0; i < 81; i++) {
                    int n = grid.getCellValue(i % 9, i / 9);
                    s += (n==0)?".":n;
                }
                s += " ";
                int w = (int)((ruleDiff + 0.05) * 10);
                int p = w % 10;
                w /= 10;
                String t = "" + w + "." + p + ", " + hint.toString();
                System.err.println(t);
                System.err.flush();
                s += w + "." + p;
                s += ", " + hint.toString2();
                if (hint instanceof IndirectHint) {
                    IndirectHint iHint = (IndirectHint)hint;
                    if ( iHint.isWorth() ) {
                        int countCells = 0;
                        Map<Cell, BitSet> getPots = iHint.getRemovablePotentials();
                        Map<Integer, BitSet> remPots = new TreeMap<Integer, BitSet>();
                        for (Cell cell : getPots.keySet()) {
                            remPots.put(cell.getY()*9+cell.getX(), getPots.get(cell));
                        }
                        for (int cellindex : remPots.keySet()) {
                            BitSet cellPots = remPots.get(cellindex);
                            if ( countCells == 0 ) { s += ":"; }
                            if ( countCells > 0 ) { s += ","; }
                            s += " r" + (cellindex/9+1) + "c" + (cellindex%9+1) + "<>";
                            int countPots = 0;
                            for (int pv=1; pv<=9; pv++ ) {
                                if ( cellPots.get( pv) ) { if ( countPots != 0 ) { s += ","; } s += pv; countPots++; }
                            }
                            countCells++;
                        }
                        Cell cell = iHint.getCell();
                        if (cell != null) {
                            s += ", r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                        }
                    }
                }
                if (hint instanceof DirectHint) {
                    DirectHint iHint = (DirectHint)hint;
                    Cell cell = iHint.getCell();
                    if (cell != null) {
                        s += ": r" + (cell.getY()+1) + "c" + (cell.getX()+1) + "=" + iHint.getValue();
                    }
                }
                System.out.println(s);
                System.out.flush();

                if (pearl == 0.0) {
                    if (diamond == 0.0)
                        diamond = difficulty;
                    if (hint.getCell() != null) {
                        pearl = difficulty;
                    }
                }
            }
    }

    public Map<String, Integer> toNamedList(Map<Rule, Integer> rules) {
        Map<String, Integer> hints = new LinkedHashMap<String, Integer>();
        for (Rule rule : rules.keySet()) {
            int count = rules.get(rule);
            String name = rule.getName();
            if (hints.containsKey(name))
                hints.put(name, hints.get(name) + count);
            else
                hints.put(name, count);
        }
        return hints;
    }

    public Hint analyse(Asker asker) {
        Grid copy = new Grid();
        grid.copyTo(copy);
        try {
            SingleHintAccumulator accu = new SingleHintAccumulator();
            try {
                for (WarningHintProducer producer : validatorHintProducers)
                    producer.getHints(grid, accu);
                for (WarningHintProducer producer : warningHintProducers)
                    producer.getHints(grid, accu);
                Analyser engine = new Analyser(this, asker);
                engine.getHints(grid, accu);
            } catch (InterruptedException willProbablyHappen) {}
            return accu.getHint();
        } finally {
            copy.copyTo(grid);
        }
    }

    public Hint bruteForceSolve() {
        SingleHintAccumulator accu = new SingleHintAccumulator();
        try {
            for (WarningHintProducer producer : validatorHintProducers)
                producer.getHints(grid, accu);
            Solution engine = new Solution();
            engine.getHints(grid, accu);
        } catch (InterruptedException willProbablyHappen) {}
        return accu.getHint();
    }

}
