/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku;

import java.util.*;

/**
 * A Sudoku grid.
 * <p>
 * Contains the 9x9 array of cells, as well as methods
 * to manipulate regions (rows, columns and blocks).
 * <p>
 * Horizontal coordinates (for Cells) range from 0 (leftmost) to
 * 8 (rightmost). Vertical coordinates range from 0 (topmost) to
 * 8 (bottommost).
 */
public class Grid {

    /*
     * Cells of the grid. First array index is the vertical index (from top
     * to bottom), and second index is horizontal index (from left to right).
     */
    private Cell[][] cells = new Cell[9][9];

    // Views
    private Row[] rows = new Row[9];
    private Column[] columns = new Column[9];
    private Block[] blocks = new Block[9];

    private int isSudoku;  // 1=isSudoku (default), 0=isSukaku (set when Sukaku is loaded)

    private Diagonal[] diagonal = new Diagonal[1];
    private AntiDiagonal[] antidiagonal = new AntiDiagonal[1];
    private DisjointGroup[] disjointgroups = new DisjointGroup[9];
    private Windoku[] windokus = new Windoku[9];
    private Asterisk[] asterisk = new Asterisk[1];                  // <- one of
    private CenterDot[] centerdot = new CenterDot[1];               // <- one of
    private Girandola[] girandola = new Girandola[1];               // <- one of
    private Halloween[] halloween = new Halloween[3];
    private PerCent[] percent = new PerCent[3];
    private SdokuBand[] sdokuband = new SdokuBand[3];
    private SdokuStack[] sdokustack = new SdokuStack[3];
    private Custom[] custom = new Custom[Settings.getInstance().getCount()];

    private boolean isVanilla = true;   // =false if solving gattai, unused!!!

    private boolean isLatinSquare = false;
    private boolean isDiagonals = false;
    private boolean isXDiagonal = true;
    private boolean isXAntiDiagonal = true;
    private boolean isDisjointGroups = false;
    private boolean isWindoku = false;
    private boolean isAsterisk = false;
    private boolean isCenterDot = false;
    private boolean isGirandola = false;
    private boolean isHalloween = false;
    private boolean isPerCent = false;
    private boolean isSdoku = false;
    private boolean isCustom = false;
    private int CustomNum = Settings.getInstance().getCount();
    private boolean isOddEven = false;

    // Diagonal
    private int[][] DiagonalCells = { { 8,16,24,32,40,48,56,64,72},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] DiagonalAt = { {-1,-1,-1,-1,-1,-1,-1,-1, 0},{-1,-1,-1,-1,-1,-1,-1, 0,-1},{-1,-1,-1,-1,-1,-1, 0,-1,-1},{-1,-1,-1,-1,-1, 0,-1,-1,-1},{-1,-1,-1,-1, 0,-1,-1,-1,-1},{-1,-1,-1, 0,-1,-1,-1,-1,-1},{-1,-1, 0,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1,-1,-1,-1,-1,-1},{ 0,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] DiagonalIndexOf = { {-1,-1,-1,-1,-1,-1,-1,-1, 0},{-1,-1,-1,-1,-1,-1,-1, 1,-1},{-1,-1,-1,-1,-1,-1, 2,-1,-1},{-1,-1,-1,-1,-1, 3,-1,-1,-1},{-1,-1,-1,-1, 4,-1,-1,-1,-1},{-1,-1,-1, 5,-1,-1,-1,-1,-1},{-1,-1, 6,-1,-1,-1,-1,-1,-1},{-1, 7,-1,-1,-1,-1,-1,-1,-1},{ 8,-1,-1,-1,-1,-1,-1,-1,-1}};

    // AntiDiagonal
    private int[][] AntiDiagonalCells = { { 0,10,20,30,40,50,60,70,80},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] AntiDiagonalAt = { { 0,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1,-1,-1,-1,-1,-1},{-1,-1, 0,-1,-1,-1,-1,-1,-1},{-1,-1,-1, 0,-1,-1,-1,-1,-1},{-1,-1,-1,-1, 0,-1,-1,-1,-1},{-1,-1,-1,-1,-1, 0,-1,-1,-1},{-1,-1,-1,-1,-1,-1, 0,-1,-1},{-1,-1,-1,-1,-1,-1,-1, 0,-1},{-1,-1,-1,-1,-1,-1,-1,-1, 0}};
    private int[][] AntiDiagonalIndexOf = { { 0,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 1,-1,-1,-1,-1,-1,-1,-1},{-1,-1, 2,-1,-1,-1,-1,-1,-1},{-1,-1,-1, 3,-1,-1,-1,-1,-1},{-1,-1,-1,-1, 4,-1,-1,-1,-1},{-1,-1,-1,-1,-1, 5,-1,-1,-1},{-1,-1,-1,-1,-1,-1, 6,-1,-1},{-1,-1,-1,-1,-1,-1,-1, 7,-1},{-1,-1,-1,-1,-1,-1,-1,-1, 8}};

    // DisjointGroup
    private int[][] DisjointGroupCells = { { 0, 3, 6,27,30,33,54,57,60},{ 1, 4, 7,28,31,34,55,58,61},{ 2, 5, 8,29,32,35,56,59,62},{ 9,12,15,36,39,42,63,66,69},{10,13,16,37,40,43,64,67,70},{11,14,17,38,41,44,65,68,71},{18,21,24,45,48,51,72,75,78},{19,22,25,46,49,52,73,76,79},{20,23,26,47,50,53,74,77,80}};
    private int[][] DisjointGroupAt = { { 0, 1, 2, 0, 1, 2, 0, 1, 2},{ 3, 4, 5, 3, 4, 5, 3, 4, 5},{ 6, 7, 8, 6, 7, 8, 6, 7, 8},{ 0, 1, 2, 0, 1, 2, 0, 1, 2},{ 3, 4, 5, 3, 4, 5, 3, 4, 5},{ 6, 7, 8, 6, 7, 8, 6, 7, 8},{ 0, 1, 2, 0, 1, 2, 0, 1, 2},{ 3, 4, 5, 3, 4, 5, 3, 4, 5},{ 6, 7, 8, 6, 7, 8, 6, 7, 8}};
    private int[][] DisjointGroupIndexOf = { { 0, 0, 0, 1, 1, 1, 2, 2, 2},{ 0, 0, 0, 1, 1, 1, 2, 2, 2},{ 0, 0, 0, 1, 1, 1, 2, 2, 2},{ 3, 3, 3, 4, 4, 4, 5, 5, 5},{ 3, 3, 3, 4, 4, 4, 5, 5, 5},{ 3, 3, 3, 4, 4, 4, 5, 5, 5},{ 6, 6, 6, 7, 7, 7, 8, 8, 8},{ 6, 6, 6, 7, 7, 7, 8, 8, 8},{ 6, 6, 6, 7, 7, 7, 8, 8, 8}};

    // Windoku
    private int[][] WindokuCells = { {10,11,12,19,20,21,28,29,30},{14,15,16,23,24,25,32,33,34},{46,47,48,55,56,57,64,65,66},{50,51,52,59,60,61,68,69,70},{ 1, 2, 3,37,38,39,73,74,75},{ 5, 6, 7,41,42,43,77,78,79},{ 9,13,17,18,22,26,27,31,35},{45,49,53,54,58,62,63,67,71},{ 0, 4, 8,36,40,44,72,76,80}};
    private int[][] WindokuAt = { { 8, 4, 4, 4, 8, 5, 5, 5, 8},{ 6, 0, 0, 0, 6, 1, 1, 1, 6},{ 6, 0, 0, 0, 6, 1, 1, 1, 6},{ 6, 0, 0, 0, 6, 1, 1, 1, 6},{ 8, 4, 4, 4, 8, 5, 5, 5, 8},{ 7, 2, 2, 2, 7, 3, 3, 3, 7},{ 7, 2, 2, 2, 7, 3, 3, 3, 7},{ 7, 2, 2, 2, 7, 3, 3, 3, 7},{ 8, 4, 4, 4, 8, 5, 5, 5, 8}};
    private int[][] WindokuIndexOf = { { 0, 0, 1, 2, 1, 0, 1, 2, 2},{ 0, 0, 1, 2, 1, 0, 1, 2, 2},{ 3, 3, 4, 5, 4, 3, 4, 5, 5},{ 6, 6, 7, 8, 7, 6, 7, 8, 8},{ 3, 3, 4, 5, 4, 3, 4, 5, 5},{ 0, 0, 1, 2, 1, 0, 1, 2, 2},{ 3, 3, 4, 5, 4, 3, 4, 5, 5},{ 6, 6, 7, 8, 7, 6, 7, 8, 8},{ 6, 6, 7, 8, 7, 6, 7, 8, 8}};

    // Asterisk
    private int[][] AsteriskCells = { {13,20,24,37,40,43,56,60,67},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] AsteriskAt = { {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1, 0,-1,-1,-1,-1},{-1,-1, 0,-1,-1,-1, 0,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1, 0,-1,-1, 0,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1, 0,-1,-1,-1, 0,-1,-1},{-1,-1,-1,-1, 0,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] AsteriskIndexOf = { {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1, 0,-1,-1,-1,-1},{-1,-1, 1,-1,-1,-1, 2,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 3,-1,-1, 4,-1,-1, 5,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1, 6,-1,-1,-1, 7,-1,-1},{-1,-1,-1,-1, 8,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};

    // CenterDot
    private int[][] CenterDotCells = { {10,13,16,37,40,43,64,67,70},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] CenterDotAt = { {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1, 0,-1,-1, 0,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1, 0,-1,-1, 0,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1, 0,-1,-1, 0,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] CenterDotIndexOf = { {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1, 1,-1,-1, 2,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 3,-1,-1, 4,-1,-1, 5,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 6,-1,-1, 7,-1,-1, 8,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};

    // Girandola
    private int[][] GirandolaCells = { { 0, 8,13,37,40,43,67,72,80},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] GirandolaAt = { { 0,-1,-1,-1,-1,-1,-1,-1, 0},{-1,-1,-1,-1, 0,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1, 0,-1,-1, 0,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1, 0,-1,-1,-1,-1},{ 0,-1,-1,-1,-1,-1,-1,-1, 0}};
    private int[][] GirandolaIndexOf = { { 0,-1,-1,-1,-1,-1,-1,-1, 1},{-1,-1,-1,-1, 2,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 3,-1,-1, 4,-1,-1, 5,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1, 6,-1,-1,-1,-1},{ 7,-1,-1,-1,-1,-1,-1,-1, 8}};

    // Halloween
    private int[][] HalloweenCells = {{10,19,20,21,28,30,37,38,39},{16,23,24,25,32,34,41,42,43},{55,57,59,61,65,66,67,68,69},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] HalloweenAt = {{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1,-1,-1,-1, 1,-1},{-1, 0, 0, 0,-1, 1, 1, 1,-1},{-1, 0,-1, 0,-1, 1,-1, 1,-1},{-1, 0, 0, 0,-1, 1, 1, 1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 2,-1, 2,-1, 2,-1, 2,-1},{-1,-1, 2, 2, 2, 2, 2,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] HalloweenIndexOf = {{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1,-1,-1,-1,-1, 0,-1},{-1, 1, 2, 3,-1, 1, 2, 3,-1},{-1, 4,-1, 5,-1, 4,-1, 5,-1},{-1, 6, 7, 8,-1, 6, 7, 8,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0,-1, 1,-1, 2,-1, 3,-1},{-1,-1, 4, 5, 6, 7, 8,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};

    // PerCent
    private int[][] PerCentCells = {{10,11,12,19,20,21,28,29,30},{ 8,16,24,32,40,48,56,64,72},{50,51,52,59,60,61,68,69,70},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] PerCentAt = {{-1,-1,-1,-1,-1,-1,-1,-1, 1},{-1, 0, 0, 0,-1,-1,-1, 1,-1},{-1, 0, 0, 0,-1,-1, 1,-1,-1},{-1, 0, 0, 0,-1, 1,-1,-1,-1},{-1,-1,-1,-1, 1,-1,-1,-1,-1},{-1,-1,-1, 1,-1, 2, 2, 2,-1},{-1,-1, 1,-1,-1, 2, 2, 2,-1},{-1, 1,-1,-1,-1, 2, 2, 2,-1},{ 1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] PerCentIndexOf = {{-1,-1,-1,-1,-1,-1,-1,-1, 0},{-1, 0, 1, 2,-1,-1,-1, 1,-1},{-1, 3, 4, 5,-1,-1, 2,-1,-1},{-1, 6, 7, 8,-1, 3,-1,-1,-1},{-1,-1,-1,-1, 4,-1,-1,-1,-1},{-1,-1,-1, 5,-1, 0, 1, 2,-1},{-1,-1, 6,-1,-1, 3, 4, 5,-1},{-1, 7,-1,-1,-1, 6, 7, 8,-1},{ 8,-1,-1,-1,-1,-1,-1,-1,-1}};

    // SdokuBand
    private int[][] SdokuBandCells = { { 2, 3, 4, 5, 6,10,16,17,18},{27,37,38,39,40,41,42,43,53},{62,63,64,70,74,75,76,77,78},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] SdokuBandAt = { {-1,-1, 0, 0, 0, 0, 0,-1,-1},{-1, 0,-1,-1,-1,-1,-1, 0, 0},{ 0,-1,-1,-1,-1,-1,-1,-1,-1},{ 1,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 1, 1, 1, 1, 1, 1, 1,-1},{-1,-1,-1,-1,-1,-1,-1,-1, 1},{-1,-1,-1,-1,-1,-1,-1,-1, 2},{ 2, 2,-1,-1,-1,-1,-1, 2,-1},{-1,-1, 2, 2, 2, 2, 2,-1,-1}};
    private int[][] SdokuBandIndexOf = { {-1,-1, 0, 1, 2, 3, 4,-1,-1},{-1, 5,-1,-1,-1,-1,-1, 6, 7},{ 8,-1,-1,-1,-1,-1,-1,-1,-1},{ 0,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 1, 2, 3, 4, 5, 6, 7,-1},{-1,-1,-1,-1,-1,-1,-1,-1, 8},{-1,-1,-1,-1,-1,-1,-1,-1, 0},{ 1, 2,-1,-1,-1,-1,-1, 3,-1},{-1,-1, 4, 5, 6, 7, 8,-1,-1}};

    // SdokuStack
    private int[][] SdokuStackCells = { { 2,10,18,27,37,38,63,64,74},{ 3, 4, 5,39,40,41,75,76,77},{ 6,16,17,42,43,53,62,70,78},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] SdokuStackAt = { {-1,-1, 0, 1, 1, 1, 2,-1,-1},{-1, 0,-1,-1,-1,-1,-1, 2, 2},{ 0,-1,-1,-1,-1,-1,-1,-1,-1},{ 0,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 0, 0, 1, 1, 1, 2, 2,-1},{-1,-1,-1,-1,-1,-1,-1,-1, 2},{-1,-1,-1,-1,-1,-1,-1,-1, 2},{ 0, 0,-1,-1,-1,-1,-1, 2,-1},{-1,-1, 0, 1, 1, 1, 2,-1,-1}};
    private int[][] SdokuStackIndexOf = { {-1,-1, 0, 0, 1, 2, 0,-1,-1},{-1, 1,-1,-1,-1,-1,-1, 1, 2},{ 2,-1,-1,-1,-1,-1,-1,-1,-1},{ 3,-1,-1,-1,-1,-1,-1,-1,-1},{-1, 4, 5, 3, 4, 5, 3, 4,-1},{-1,-1,-1,-1,-1,-1,-1,-1, 5},{-1,-1,-1,-1,-1,-1,-1,-1, 6},{ 6, 7,-1,-1,-1,-1,-1, 7,-1},{-1,-1, 8, 6, 7, 8, 8,-1,-1}};

    // Custom
    private int[][] CustomCells = { {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] CustomAt = { {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    private int[][] CustomIndexOf = { {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};

    /**
     * Create a new 9x9 Sudoku grid. All cells are set to empty
     */
    public Grid() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                cells[y][x] = new Cell(this, x, y);
            }
        }
        // Build subparts views
        for (int i = 0; i < 9; i++) {
            rows[i] = new Row(i);
            columns[i] = new Column(i);
            blocks[i] = new Block(i / 3, i % 3);
            disjointgroups[i] = new DisjointGroup(i);
            windokus[i] = new Windoku(i);
        }
        diagonal[0] = new Diagonal(0);
        antidiagonal[0] = new AntiDiagonal(0);
        asterisk[0] = new Asterisk(0);
        centerdot[0] = new CenterDot(0);
        girandola[0] = new Girandola(0);
        for (int i = 0; i < 3; i++) {
            halloween[i] = new Halloween(i);
            percent[i] = new PerCent(i);
            sdokuband[i] = new SdokuBand(i);
            sdokustack[i] = new SdokuStack(i);
        }
        for (int i = 0; i < CustomNum; i++) {
            custom[i] = new Custom(i);
        }
        isSudoku = 1;

        isLatinSquare = Settings.getInstance().isLatinSquare();
        isDiagonals = Settings.getInstance().isDiagonals();
        isXDiagonal = Settings.getInstance().isXDiagonal();
        isXAntiDiagonal = Settings.getInstance().isXAntiDiagonal();
        isDisjointGroups = Settings.getInstance().isDisjointGroups();
        isWindoku = Settings.getInstance().isWindoku();
        isAsterisk = Settings.getInstance().isAsterisk();
        isCenterDot = Settings.getInstance().isCenterDot();
        isGirandola = Settings.getInstance().isGirandola();
        isHalloween = Settings.getInstance().isHalloween();
        isPerCent = Settings.getInstance().isPerCent();
        isSdoku = Settings.getInstance().isSdoku();
        isCustom = Settings.getInstance().isCustom();
        if ( isCustom && Settings.getInstance().getCustom() != null ) {
            customInitialize( Settings.getInstance().getCustom());
        }
        isOddEven = Settings.getInstance().isOddEven();
    }

    public int isSudoku() {
        return this.isSudoku;
    }

    public void setSukaku() {
        this.isSudoku = 0;
    }

    public boolean isVanilla() { return this.isVanilla; }
    public boolean isLatinSquare() { return this.isLatinSquare; }
    public boolean isDiagonals() { return this.isDiagonals; }
    public boolean isDisjointGroups() { return this.isDisjointGroups; }
    public boolean isWindoku() { return this.isWindoku; }
    public boolean isAsterisk() { return this.isAsterisk; }
    public boolean isCenterDot() { return this.isCenterDot; }
    public boolean isGirandola() { return this.isGirandola; }
    public boolean isHalloween() { return this.isHalloween; }
    public boolean isPerCent() { return this.isPerCent; }
    public boolean isSdoku() { return this.isSdoku; }
    public boolean isCustom() { return this.isCustom; }
    public boolean isOddEven() { return this.isOddEven; }

    public void setVanilla() { this.isVanilla = true; }
    public void setLatinSquare() { this.isLatinSquare = true; }
    public void setDiagonals() { this.isDiagonals = true; }
    public void setDisjointGroups() { this.isDisjointGroups = true; }
    public void setWindoku() { this.isWindoku = true; }
    public void setAsterisk() { this.isAsterisk = true; }
    public void setCenterDot() { this.isCenterDot = true; }
    public void setGirandola() { this.isGirandola = true; }
    public void setHalloween() { this.isHalloween = true; }
    public void setPerCent() { this.isPerCent = true; }
    public void setSdoku() { this.isSdoku = true; }
    public void setCustom() { this.isCustom = true; }
    public void setOddEven() { this.isOddEven = true; }

    public void resetVanilla() { this.isVanilla = false; }

    public void setVanilla(boolean b) { this.isVanilla = b; }
    public void setLatinSquare(boolean b) { this.isLatinSquare = b; }
    public void setDiagonals(boolean b) { this.isDiagonals = b; }
    public void setDisjointGroups(boolean b) { this.isDisjointGroups = b; }
    public void setWindoku(boolean b) { this.isWindoku = b; }
    public void setAsterisk(boolean b) { this.isAsterisk = b; }
    public void setCenterDot(boolean b) { this.isCenterDot = b; }
    public void setGirandola(boolean b) { this.isGirandola = b; }
    public void setHalloween(boolean b) { this.isHalloween = b; }
    public void setPerCent(boolean b) { this.isPerCent = b; }
    public void setSdoku(boolean b) { this.isSdoku = b; }
    public void setCustom(boolean b) { this.isCustom = b; }
    public void setOddEven(boolean b) { this.isOddEven = b; }

    public void updateLatinSquare() { this.isLatinSquare = Settings.getInstance().isLatinSquare(); reset_regionTypes(); }
    public void updateDiagonals() { this.isDiagonals = Settings.getInstance().isDiagonals(); reset_regionTypes(); }
    public void updateDisjointGroups() { this.isDisjointGroups = Settings.getInstance().isDisjointGroups(); reset_regionTypes(); }
    public void updateWindoku() { this.isWindoku = Settings.getInstance().isWindoku(); reset_regionTypes(); }
    public void updateAsterisk() { this.isAsterisk = Settings.getInstance().isAsterisk(); reset_regionTypes(); }
    public void updateCenterDot() { this.isCenterDot = Settings.getInstance().isCenterDot(); reset_regionTypes(); }
    public void updateGirandola() { this.isGirandola = Settings.getInstance().isGirandola(); reset_regionTypes(); }
    public void updateHalloween() { this.isHalloween = Settings.getInstance().isHalloween(); reset_regionTypes(); }
    public void updatePerCent() { this.isPerCent = Settings.getInstance().isPerCent(); reset_regionTypes(); }
    public void updateSdoku() { this.isSdoku = Settings.getInstance().isSdoku(); reset_regionTypes(); }
    public void updateCustom() { this.isCustom = Settings.getInstance().isCustom(); reset_regionTypes(); }
    public void updateOddEven() { this.isOddEven = Settings.getInstance().isOddEven(); reset_regionTypes(); }

    public boolean isXDiagonal() { return this.isXDiagonal; }
    public void setXDiagonal() { this.isXDiagonal = true; }
    public void setXDiagonal(boolean b) { this.isXDiagonal = b; }
    public void updateXDiagonal() { this.isXDiagonal = Settings.getInstance().isXDiagonal(); reset_regionTypes(); }

    public boolean isXAntiDiagonal() { return this.isXAntiDiagonal; }
    public void setXAntiDiagonal() { this.isXAntiDiagonal = true; }
    public void setXAntiDiagonal(boolean b) { this.isXAntiDiagonal = b; }
    public void updateXAntiDiagonal() { this.isXAntiDiagonal = Settings.getInstance().isXAntiDiagonal(); reset_regionTypes(); }

    public void updateVanilla() { reset_regionTypes(); }

    public void fixGivens() {
        for (int i = 0; i < 81; i++) {
            if ( getCellValue(i%9,i/9) != 0 ) {
                getCell(i%9,i/9).setGiven();
            }
            else {
                getCell(i%9,i/9).resetGiven();
            }
        }
    }

    /**
     * Get the cell at the given coordinates
     * @param x the x coordinate (0=leftmost, 8=rightmost)
     * @param y the y coordinate (0=topmost, 8=bottommost)
     * @return the cell at the given coordinates
     */
    public Cell getCell(int x, int y) {
        return this.cells[y][x];
    }

    /**
     * Get the 9 regions of the given type
     * @param regionType the type of the regions to return. Must be one of
     * {@link Grid.Block}, {@link Grid.Row} or {@link Grid.Column}.
     * @return the 9 regions of the given type
     */
    public Region[] getRegions(Class<? extends Region> regionType) {
        if (regionType == Row.class)
            return this.rows;
        else if (regionType == Column.class)
            return this.columns;
        else if (regionType == Block.class)
            return this.blocks;
        else if (regionType == Diagonal.class)
            return this.diagonal;
        else if (regionType == AntiDiagonal.class)
            return this.antidiagonal;
        else if (regionType == DisjointGroup.class)
            return this.disjointgroups;
        else if (regionType == Windoku.class)
            return this.windokus;
        else if (regionType == Asterisk.class)
            return this.asterisk;
        else if (regionType == CenterDot.class)
            return this.centerdot;
        else if (regionType == Girandola.class)
            return this.girandola;
        else if (regionType == Halloween.class)
            return this.halloween;
        else if (regionType == PerCent.class)
            return this.percent;
        else if (regionType == SdokuBand.class)
            return this.sdokuband;
        else if (regionType == SdokuStack.class)
            return this.sdokustack;
        else if (regionType == Custom.class)
            return this.custom;
        else
            return null;
    }

    /**
     * Get the 9 regions of the given type
     * @param regionType the type of the regions to return. Must be one of
     * {@link Grid.Block}, {@link Grid.Row} or {@link Grid.Column}.
     * @return the 9 regions of the given type
     */
    public int getRegionMax(Class<? extends Region> regionType) {
        if (regionType == Row.class)
            return 9;
        else if (regionType == Column.class)
            return 9;
        else if (regionType == Block.class)
            return 9;
        else if (regionType == Diagonal.class)
            return 1;
        else if (regionType == AntiDiagonal.class)
            return 1;
        else if (regionType == DisjointGroup.class)
            return 9;
        else if (regionType == Windoku.class)
            return 9;
        else if (regionType == Asterisk.class)
            return 1;
        else if (regionType == CenterDot.class)
            return 1;
        else if (regionType == Girandola.class)
            return 1;
        else if (regionType == Halloween.class)
            return 3;
        else if (regionType == PerCent.class)
            return 3;
        else if (regionType == SdokuBand.class)
            return 3;
        else if (regionType == SdokuStack.class)
            return 3;
        else if (regionType == Custom.class)
            return CustomNum;
        else
            return 0;
    }

    /**
     * Get the row at the given index.
     * Rows are numbered from top to bottom.
     * @param num the index of the row to get, between 0 and 8, inclusive
     * @return the row at the given index
     */
    public Row getRow(int num) {
        return this.rows[num];
    }

    /**
     * Get the column at the given index.
     * Columns are numbered from left to right.
     * @param num the index of the column to get, between 0 and 8, inclusive
     * @return the column at the given index
     */
    public Column getColumn(int num) {
        return this.columns[num];
    }

    /**
     * Get the block at the given index.
     * Blocks are numbered from left to right, top to bottom.
     * @param num the index of the block to get, between 0 and 8, inclusive
     * @return the block at the given index
     */
    public Block getBlock(int num) {
        return this.blocks[num];
    }

    /**
     * Get the block at the given location
     * @param vPos the vertical position, between 0 to 2, inclusive
     * @param hPos the horizontal position, between 0 to 2, inclusive
     * @return the block at the given location
     */
    public Block getBlock(int vPos, int hPos) {
        return this.blocks[vPos * 3 + hPos];
    }

    /**
     * Get the diagonal at the given index.
     * Diagonals are numbered from left to right, top to bottom.
     * @param num the index of the diagonal to get, between 0 and 0, inclusive
     * @return the diagonal at the given index
     */
    public Diagonal getDiagonal(int num) {
        if ( num == 0 ) {
            return this.diagonal[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the antidiagonal at the given index.
     * AntiDiagonals are numbered from left to right, top to bottom.
     * @param num the index of the antidiagonal to get, between 0 and 0, inclusive
     * @return the antidiagonal at the given index
     */
    public AntiDiagonal getAntiDiagonal(int num) {
        if ( num == 0 ) {
            return this.antidiagonal[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the disjointgroup at the given index.
     * DisjointGroups are numbered from left to right, top to bottom.
     * @param num the index of the disjointgroup to get, between 0 and 8, inclusive
     * @return the disjointgroup at the given index
     */
    public DisjointGroup getDisjointGroup(int num) {
        return this.disjointgroups[num];
    }

    /**
     * Get the windoku at the given index.
     * Windokus are numbered from left to right, top to bottom.
     * @param num the index of the windoku to get, between 0 and 8, inclusive
     * @return the windoku at the given index
     */
    public Windoku getWindoku(int num) {
        return this.windokus[num];
    }

    /**
     * Get the asterisk at the given index.
     * Asterisks are numbered from left to right, top to bottom.
     * @param num the index of the asterisk to get, between 0 and 0, inclusive
     * @return the asterisk at the given index
     */
    public Asterisk getAsterisk(int num) {
        if ( num == 0 ) {
            return this.asterisk[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the centerdot at the given index.
     * CenterDots are numbered from left to right, top to bottom.
     * @param num the index of the centerdot to get, between 0 and 0, inclusive
     * @return the centerdot at the given index
     */
    public CenterDot getCenterDot(int num) {
        if ( num == 0 ) {
            return this.centerdot[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the girandola at the given index.
     * Girandolas are numbered from left to right, top to bottom.
     * @param num the index of the girandola to get, between 0 and 0, inclusive
     * @return the girandola at the given index
     */
    public Girandola getGirandola(int num) {
        if ( num == 0 ) {
            return this.girandola[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the halloween at the given index.
     * Halloweens are numbered from left to right, top to bottom.
     * @param num the index of the halloween to get, between 0 and 3, inclusive
     * @return the halloween at the given index
     */
    public Halloween getHalloween(int num) {
        if ( num < 3 ) {
            return this.halloween[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the percent at the given index.
     * PerCents are numbered from left to right, top to bottom.
     * @param num the index of the percent to get, between 0 and 3, inclusive
     * @return the percent at the given index
     */
    public PerCent getPerCent(int num) {
        if ( num < 3 ) {
            return this.percent[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the sdokuband at the given index.
     * SdokuBands are numbered from left to right, top to bottom.
     * @param num the index of the sdokuband to get, between 0 and 3, inclusive
     * @return the sdokuband at the given index
     */
    public SdokuBand getSdokuBand(int num) {
        if ( num < 3 ) {
            return this.sdokuband[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the sdokustack at the given index.
     * SdokuStacks are numbered from left to right, top to bottom.
     * @param num the index of the sdokustack to get, between 0 and 3, inclusive
     * @return the sdokustack at the given index
     */
    public SdokuStack getSdokuStack(int num) {
        if ( num < 3 ) {
            return this.sdokustack[num];
        }
        else {
            return null;
        }
    }

    /**
     * Get the custom at the given index.
     * Customs are numbered from left to right, top to bottom.
     * @param num the index of the custom to get, between 0 and 8, inclusive
     * @return the custom at the given index
     */
    public Custom getCustom(int num) {
        if ( CustomNum != 0 && num < CustomNum ) {
            return this.custom[num];
        }
        else {
            return null;
        }
    }

    // Cell values

    /**
     * Set the value of a cell
     * @param x the horizontal coordinate of the cell
     * @param y the vertical coordinate of the cell
     * @param value the value to set the cell to. Use 0 to clear the cell.
     */
    public void setCellValue(int x, int y, int value) {
        this.cells[y][x].setValue(value);
        this.cells[y][x].setGiven();
    }

    /**
     * Get the value of a cell
     * @param x the horizontal coordinate of the cell
     * @param y the vertical coordinate of the cell
     * @return the value of the cell, or 0 if the cell is empty
     */
    public int getCellValue(int x, int y) {
        return this.cells[y][x].getValue();
    }

    /**
     * Get the row at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the row at the given coordinates
     */
    public Row getRowAt(int x, int y) {
        return this.rows[y];
    }

    /**
     * Get the column at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the column at the given location
     */
    public Column getColumnAt(int x, int y) {
        return this.columns[x];
    }

    /**
     * Get the 3x3 block at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the block at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public Block getBlockAt(int x, int y) {
        return this.blocks[(y / 3) * 3 + (x / 3)];
    }

    /**
     * Get the diagonal at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the diagonal at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public Diagonal getDiagonalAt(int x, int y) {
        int index = DiagonalAt[y][x];
        if ( index != -1 ) {
            return this.diagonal[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the antidiagonal at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the antidiagonal at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public AntiDiagonal getAntiDiagonalAt(int x, int y) {
        int index = AntiDiagonalAt[y][x];
        if ( index != -1 ) {
            return this.antidiagonal[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the disjointgroup at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the disjointgroup at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public DisjointGroup getDisjointGroupAt(int x, int y) {
        return this.disjointgroups[ DisjointGroupAt[y][x]];
    }

    /**
     * Get the windoku at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the windoku at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public Windoku getWindokuAt(int x, int y) {
        return this.windokus[ WindokuAt[y][x]];
    }

    /**
     * Get the asterisk at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the asterisk at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public Asterisk getAsteriskAt(int x, int y) {
        int index = AsteriskAt[y][x];
        if ( index != -1 ) {
            return this.asterisk[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the centerdot at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the centerdot at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public CenterDot getCenterDotAt(int x, int y) {
        int index = CenterDotAt[y][x];
        if ( index != -1 ) {
            return this.centerdot[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the girandola at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the girandola at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public Girandola getGirandolaAt(int x, int y) {
        int index = GirandolaAt[y][x];
        if ( index != -1 ) {
            return this.girandola[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the halloween at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the halloween at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public Halloween getHalloweenAt(int x, int y) {
        int index = HalloweenAt[y][x];
        if ( index != -1 ) {
            return this.halloween[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the percent at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the percent at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public PerCent getPerCentAt(int x, int y) {
        int index = PerCentAt[y][x];
        if ( index != -1 ) {
            return this.percent[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the sdokuband at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the sdokuband at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public SdokuBand getSdokuBandAt(int x, int y) {
        int index = SdokuBandAt[y][x];
        if ( index != -1 ) {
            return this.sdokuband[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the sdokustack at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the sdokustack at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public SdokuStack getSdokuStackAt(int x, int y) {
        int index = SdokuStackAt[y][x];
        if ( index != -1 ) {
            return this.sdokustack[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the custom at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the custom at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public Custom getCustomAt(int x, int y) {
        int index = CustomAt[y][x];
        if ( index != -1 ) {
            return this.custom[ index];
        }
        else {
            return null;
        }
    }

    /**
     * Get the custom number at the given location
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the custom number at the given coordinates (the coordinates
     * are coordinates of a cell)
     */
    public int getCustomNumAt(int x, int y) {
        return CustomAt[y][x];
    }

    public Grid.Region getRegionAt(Class<? extends Grid.Region> regionType, int x, int y) {
        if (Grid.Row.class.equals(regionType))
            return getRowAt(x, y);
        else if (Grid.Column.class.equals(regionType))
            return getColumnAt(x, y);
        else if (Grid.Block.class.equals(regionType))
            return getBlockAt(x, y);
        else if (Grid.Diagonal.class.equals(regionType))
            return getDiagonalAt(x, y);
        else if (Grid.AntiDiagonal.class.equals(regionType))
            return getAntiDiagonalAt(x, y);
        else if (Grid.DisjointGroup.class.equals(regionType))
            return getDisjointGroupAt(x, y);
        else if (Grid.Windoku.class.equals(regionType))
            return getWindokuAt(x, y);
        else if (Grid.Asterisk.class.equals(regionType))
            return getAsteriskAt(x, y);
        else if (Grid.CenterDot.class.equals(regionType))
            return getCenterDotAt(x, y);
        else if (Grid.Girandola.class.equals(regionType))
            return getGirandolaAt(x, y);
        else if (Grid.Halloween.class.equals(regionType))
            return getHalloweenAt(x, y);
        else if (Grid.PerCent.class.equals(regionType))
            return getPerCentAt(x, y);
        else if (Grid.SdokuBand.class.equals(regionType))
            return getSdokuBandAt(x, y);
        else if (Grid.SdokuStack.class.equals(regionType))
            return getSdokuStackAt(x, y);
        else if (Grid.Custom.class.equals(regionType))
            return getCustomAt(x, y);
        else
            return null;
    }

    public Grid.Region getRegionAt(Class<? extends Grid.Region> regionType, Cell cell) {
        return getRegionAt(regionType, cell.getX(), cell.getY());
    }

    public int getRegionNum(Class<? extends Grid.Region> regionType, int x, int y) {
        if (Grid.Row.class.equals(regionType))
            return getRowAt(x, y).getRowNum();
        else if (Grid.Column.class.equals(regionType))
            return getColumnAt(x, y).getColumnNum();
        else if (Grid.Block.class.equals(regionType))
            return getBlockAt(x, y).getBlockNum();
        else if (Grid.Diagonal.class.equals(regionType))
            return getDiagonalAt(x, y).getDiagonalNum();
        else if (Grid.AntiDiagonal.class.equals(regionType))
            return getAntiDiagonalAt(x, y).getAntiDiagonalNum();
        else if (Grid.DisjointGroup.class.equals(regionType))
            return getDisjointGroupAt(x, y).getDisjointGroupNum();
        else if (Grid.Windoku.class.equals(regionType))
            return getWindokuAt(x, y).getWindokuNum();
        else if (Grid.Asterisk.class.equals(regionType))
            return getAsteriskAt(x, y).getAsteriskNum();
        else if (Grid.CenterDot.class.equals(regionType))
            return getCenterDotAt(x, y).getCenterDotNum();
        else if (Grid.Girandola.class.equals(regionType))
            return getGirandolaAt(x, y).getGirandolaNum();
        else if (Grid.Halloween.class.equals(regionType))
            return getHalloweenAt(x, y).getHalloweenNum();
        else if (Grid.PerCent.class.equals(regionType))
            return getPerCentAt(x, y).getPerCentNum();
        else if (Grid.SdokuBand.class.equals(regionType))
            return getSdokuBandAt(x, y).getSdokuBandNum();
        else if (Grid.SdokuStack.class.equals(regionType))
            return getSdokuStackAt(x, y).getSdokuStackNum();
        else if (Grid.Custom.class.equals(regionType))
            return getCustomAt(x, y).getCustomNum();
        else
            return -1;
    }

    public int getRegionNum(Class<? extends Grid.Region> regionType, Cell cell) {
        return getRegionNum(regionType, cell.getX(), cell.getY());
    }

    public String getRegionName(Class<? extends Grid.Region> regionType, int x, int y) {
        if (Grid.Row.class.equals(regionType))
            return getRowAt(x, y).toString();
        else if (Grid.Column.class.equals(regionType))
            return getColumnAt(x, y).toString();
        else if (Grid.Block.class.equals(regionType))
            return getBlockAt(x, y).toString();
        else if (Grid.Diagonal.class.equals(regionType))
            return getDiagonalAt(x, y).toString();
        else if (Grid.AntiDiagonal.class.equals(regionType))
            return getAntiDiagonalAt(x, y).toString();
        else if (Grid.DisjointGroup.class.equals(regionType))
            return getDisjointGroupAt(x, y).toString();
        else if (Grid.Windoku.class.equals(regionType))
            return getWindokuAt(x, y).toString();
        else if (Grid.Asterisk.class.equals(regionType))
            return getAsteriskAt(x, y).toString();
        else if (Grid.CenterDot.class.equals(regionType))
            return getCenterDotAt(x, y).toString();
        else if (Grid.Girandola.class.equals(regionType))
            return getGirandolaAt(x, y).toString();
        else if (Grid.Halloween.class.equals(regionType))
            return getHalloweenAt(x, y).toString();
        else if (Grid.PerCent.class.equals(regionType))
            return getPerCentAt(x, y).toString();
        else if (Grid.SdokuBand.class.equals(regionType))
            return getSdokuBandAt(x, y).toString();
        else if (Grid.SdokuStack.class.equals(regionType))
            return getSdokuStackAt(x, y).toString();
        else if (Grid.Custom.class.equals(regionType))
            return getCustomAt(x, y).toString();
        else
            return null;
    }

    public String getRegionName(Class<? extends Grid.Region> regionType, Cell cell) {
        return getRegionName(regionType, cell.getX(), cell.getY());
    }

    public ArrayList<Grid.Region> getRegionsAt(Cell cell) {
        ArrayList<Grid.Region> regions = new ArrayList<Grid.Region>();
        for (Class<? extends Grid.Region> regionType : getRegionTypes()) {
            Grid.Region region = getRegionAt(regionType, cell.getX(), cell.getY());
            if ( region != null ) {
                regions.add(region);
            }
        }
        return regions;
    }

    private List<Class<? extends Grid.Region>> _regionTypes = null;

    /**
     * Get a list containing the three classes corresponding to the
     * three region types (row, column and block)
     * @return a list of the three region types. The resulting list
     * can not be modified
     */
    public List<Class<? extends Grid.Region>> getRegionTypes() {
        if (_regionTypes == null) {
            int count = 3;
          if (  isLatinSquare ) { count -= 1; }
            if ( isDiagonals && isXDiagonal ) { count += 1; }
            if ( isDiagonals && isXAntiDiagonal ) { count += 1; }
            if ( isDisjointGroups ) { count += 1; }
            if ( isWindoku ) { count += 1; }
            if ( isAsterisk ) { count += 1; }
            if ( isCenterDot ) { count += 1; }
            if ( isGirandola ) { count += 1; }
            if ( isHalloween ) { count += 1; }
            if ( isPerCent ) { count += 1; }
            if ( isSdoku ) { count += 2; }
            if ( isCustom ) { count += 1; }
            _regionTypes = new ArrayList<Class<? extends Grid.Region>>(count);
          if ( !isLatinSquare ) {
            _regionTypes.add(Grid.Block.class);
          }
            _regionTypes.add(Grid.Row.class);
            _regionTypes.add(Grid.Column.class);
            if ( isDiagonals ) {
              if ( isXDiagonal ) {
                _regionTypes.add(Grid.Diagonal.class);
              }
              if ( isXAntiDiagonal ) {
                _regionTypes.add(Grid.AntiDiagonal.class);
              }
            }
            if ( isDisjointGroups ) {
                _regionTypes.add(Grid.DisjointGroup.class);
            }
            if ( isWindoku ) {
                _regionTypes.add(Grid.Windoku.class);
            }
            if ( isAsterisk ) {
                _regionTypes.add(Grid.Asterisk.class);
            }
            if ( isCenterDot ) {
                _regionTypes.add(Grid.CenterDot.class);
            }
            if ( isGirandola ) {
                _regionTypes.add(Grid.Girandola.class);
            }
            if ( isHalloween ) {
                _regionTypes.add(Grid.Halloween.class);
            }
            if ( isPerCent ) {
                _regionTypes.add(Grid.PerCent.class);
            }
            if ( isSdoku ) {
                _regionTypes.add(Grid.SdokuBand.class);
                _regionTypes.add(Grid.SdokuStack.class);
            }
            if ( isCustom ) {
                _regionTypes.add(Grid.Custom.class);
            }
            _regionTypes = Collections.unmodifiableList(_regionTypes);
        }
        return _regionTypes;
    }

    private List<Class<? extends Grid.Region>> _regionTypes3 = null;

    /**
     * Get a list containing the three classes corresponding to the
     * three region types (row, column and block)
     * @return a list of the three region types. The resulting list
     * can not be modified
     */
    public List<Class<? extends Grid.Region>> getRegionTypes3() {
        if (_regionTypes3 == null) {
            int count = 3;
          if (  isLatinSquare ) { count -= 1; }
            _regionTypes3 = new ArrayList<Class<? extends Grid.Region>>(count);
          if ( !isLatinSquare ) {
            _regionTypes3.add(Grid.Block.class);
          }
            _regionTypes3.add(Grid.Row.class);
            _regionTypes3.add(Grid.Column.class);
            _regionTypes3 = Collections.unmodifiableList(_regionTypes3);
        }
        return _regionTypes3;
    }

    private void reset_regionTypes() {
        _regionTypes = null;
        _regionTypes3 = null;
    }

    // Grid regions implementation (rows, columns, 3x3 squares)

    /**
     * Abstract class representing a region of a sudoku grid. A region
     * is either a row, a column or a 3x3 block.
     */
    public abstract class Region {

        /**
         * Get a cell of this region by index. The order in which cells are
         * returned according to the index is not defined, but is guaranted
         * to be consistant accross multiple invocations of this method.
         * @param index the index of the cell to get, between 0 (inclusive)
         * and 9 (exclusive).
         * @return the cell at the given index
         */
        public abstract Cell getCell(int index);

        /**
         * Get the index of the given cell within this region.
         * <p>
         * The returned value is consistent with {@link #getCell(int)}.
         * @param cell the cell whose index to get
         * @return the index of the cell, or -1 if the cell does not belong to
         * this region.
         */
        public int indexOf(Cell cell) {
            /*
             * This code is not really used. The method is always overriden
             */
            for (int i = 0; i < 9; i++) {
                if (cell.equals(getCell(i)))
                    return i;
            }
            return -1;
        }

        /**
         * Test whether this region contains the given value, that is,
         * is a cell of this region is filled with the given value.
         * @param value the value to check for
         * @return whether this region contains the given value
         */
        public boolean contains(int value) {
            for (int i = 0; i < 9; i++) {
                if (getCell(i).getValue() == value)
                    return true;
            }
            return false;
        }

        /**
         * Get the potential positions of the given value within this region.
         * The bits of the returned bitset correspond to indexes of cells, as
         * in {@link #getCell(int)}. Only the indexes of cells that have the given
         * value as a potential value are included in the bitset (see
         * {@link Cell#getPotentialValues()}).
         * @param value the value whose potential positions to get
         * @return the potential positions of the given value within this region
         * @see Cell#getPotentialValues()
         */
        public BitSet getPotentialPositions(int value) {
            BitSet result = new BitSet(9);
            for (int index = 0; index < 9; index++) {
            //  result.set(index, getCell(index).hasPotentialValue(value));
                if ( getCell(index).hasPotentialValue(value) ) {
                    result.set(index);
                }
            }
            return result;
        }

        public BitSet copyPotentialPositions(int value) {
            return getPotentialPositions(value); // No need to clone, this is alreay hand-made
        }

        /**
         * Get the cells of this region. The iteration order of the result
         * matches the order of the cells returned by {@link #getCell(int)}.
         * @return the cells of this region.
         */
        public Set<Cell> getCellSet() {
            Set<Cell> result = new LinkedHashSet<Cell>();
            for (int i = 0; i < 9; i++)
                result.add(getCell(i));
            return result;
        }

        /**
         * Return the cells that are common to this region and the
         * given region
         * @param other the other region
         * @return the cells belonging to this region and to the other region
         */
        public Set<Cell> commonCells(Region other) {
            Set<Cell> result = this.getCellSet();
            result.retainAll(other.getCellSet());
            return result;
        }

        /**
         * Test whether thsi region crosses an other region.
         * <p>
         * A region crosses another region if they have at least one
         * common cell. In particular, any rows cross any columns.
         * @param other the other region
         * @return whether this region crosses the other region.
         */
        public boolean crosses(Region other) {
            return !commonCells(other).isEmpty();
        }

        /**
         * Get the number of cells of this region that are still empty.
         * @return the number of cells of this region that are still empty
         */
        public int getEmptyCellCount() {
            int result = 0;
            for (int i = 0; i < 9; i++)
                if (getCell(i).isEmpty())
                    result++;
            return result;
        }

        /**
         * Get a string representation of this region's type
         */
        @Override
        public abstract String toString();

        public abstract String toString2();

        /**
         * Get a string representation of this region
         * @return a string representation of this region
         */
        public abstract String toFullString();

    }

    /**
     * A row of a sudoku grid.
     */
    public class Row extends Region {

        private int rowNum;

        public Row(int rowNum) {
            this.rowNum = rowNum;
        }

        public int getRowNum() {
            return this.rowNum;
        }

        @Override
        public Cell getCell(int index) {
            return cells[rowNum][index];
        }

        @Override
        public int indexOf(Cell cell) {
            return cell.getX();
        }

        @Override
        public boolean crosses(Region other) {
            if (other instanceof Block) {
                Block square = (Block)other;
                return rowNum / 3 == square.vNum;
            } else if (other instanceof Column) {
                return true;
            } else if (other instanceof Row) {
                Row row = (Row)other;
                return this.rowNum == row.rowNum;
            } else {
                return super.crosses(other);
            }
        }

        @Override
        public String toString() {
            return "row";
        }

        @Override
        public String toString2() {
            return "row" + " " + (rowNum + 1);
        }

        @Override
        public String toFullString() {
            Settings settings = Settings.getInstance();
            if (settings.isRCNotation())
                return toString() + " R" + (rowNum + 1);
            else
                return toString() + " " + (rowNum + 1);
        }

    }

    /**
     * A column a sudoku grid
     */
    public class Column extends Region {

        private int columnNum;

        public Column(int columnNum) {
            this.columnNum = columnNum;
        }

        public int getColumnNum() {
            return this.columnNum;
        }

        @Override
        public Cell getCell(int index) {
            return cells[index][columnNum];
        }

        @Override
        public int indexOf(Cell cell) {
            return cell.getY();
        }

        @Override
        public boolean crosses(Region other) {
            if (other instanceof Block) {
                Block square = (Block)other;
                return columnNum / 3 == square.hNum;
            } else if (other instanceof Row) {
                return true;
            } else if (other instanceof Column) {
                Column column = (Column)other;
                return this.columnNum == column.columnNum;
            } else {
                return super.crosses(other);
            }
        }

        @Override
        public String toString() {
            return "column";
        }

        @Override
        public String toString2() {
            return "column" + " " + (columnNum + 1);
        }

        @Override
        public String toFullString() {
            Settings settings = Settings.getInstance();
            if (settings.isRCNotation())
                return toString() + " C" + (columnNum + 1);
            else
                return toString() + " " + (char)('A' + columnNum);
        }

    }

    /**
     * A 3x3 block of a sudoku grid.
     */
    public class Block extends Region {

        private int vNum, hNum;

        public Block(int vNum, int hNum) {
            this.vNum = vNum;
            this.hNum = hNum;
        }

        public int getVIndex() {
            return this.vNum;
        }

        public int getHIndex() {
            return this.hNum;
        }

        public int getBlockNum() {
            return this.vNum * 3 + this.hNum;
        }

        @Override
        public Cell getCell(int index) {
            return cells[vNum * 3 + index / 3][hNum * 3 + index % 3];
        }

        @Override
        public int indexOf(Cell cell) {
            return (cell.getY() % 3) * 3 + (cell.getX() % 3);
        }

        @Override
        public boolean crosses(Region other) {
            if (other instanceof Row) {
                return ((Row)other).crosses(this);
            } else if (other instanceof Column) {
                return ((Column)other).crosses(this);
            } else if (other instanceof Block) {
                Block square = (Block)other;
                return this.vNum == square.vNum && this.hNum == square.hNum;
            } else {
                return super.crosses(other);
            }
        }

        @Override
        public String toString() {
            return "block";
        }

        @Override
        public String toString2() {
            return "block" + " " + (vNum * 3 + hNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (vNum * 3 + hNum + 1);
        }

    }

    /**
     * A Diagonal (/) constraint of a sudoku grid.
     */
    public class Diagonal extends Region {

        private int diagonalNum;

        public Diagonal(int diagonalNum) {
            this.diagonalNum = diagonalNum;
        }

        public int getDiagonalNum() {
            return this.diagonalNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = DiagonalCells[this.diagonalNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return DiagonalIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public boolean crosses(Region other) {
            if (other instanceof Row) {
                return true;
            } else if (other instanceof Column) {
                return true;
            } else {
                return super.crosses(other);
            }
        }

        @Override
        public String toString() {
            return "diagonal(/)";
        }

        @Override
        public String toString2() {
            return "diagonal (/)";
        }

        @Override
        public String toFullString() {
            return toString() + " " + (diagonalNum + 1);
        }

    }

    /**
     * An AntiDiagonal (/) constraint of a sudoku grid.
     */
    public class AntiDiagonal extends Region {

        private int antidiagonalNum;

        public AntiDiagonal(int antidiagonalNum) {
            this.antidiagonalNum = antidiagonalNum;
        }

        public int getAntiDiagonalNum() {
            return this.antidiagonalNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = AntiDiagonalCells[this.antidiagonalNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return AntiDiagonalIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public boolean crosses(Region other) {
            if (other instanceof Row) {
                return true;
            } else if (other instanceof Column) {
                return true;
            } else {
                return super.crosses(other);
            }
        }

        @Override
        public String toString() {
            return "antidiagonal(\\)";
        }

        @Override
        public String toString2() {
            return "antidiagonal (\\)";
        }

        @Override
        public String toFullString() {
            return toString() + " " + (antidiagonalNum + 1);
        }

    }

    /**
     * A disjointgroup of a sudoku grid.
     */
    public class DisjointGroup extends Region {

        private int disjointgroupNum;

        public DisjointGroup(int disjointgroupNum) {
            this.disjointgroupNum = disjointgroupNum;
        }

        public int getDisjointGroupNum() {
            return this.disjointgroupNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = DisjointGroupCells[this.disjointgroupNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return DisjointGroupIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return "disjointgroup";
        }

        @Override
        public String toString2() {
            return "disjointgroup" + " " + (disjointgroupNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (disjointgroupNum + 1);
        }

    }

    /**
     * A windoku of a sudoku grid.
     */
    public class Windoku extends Region {

        private int windokuNum;

        public Windoku(int windokuNum) {
            this.windokuNum = windokuNum;
        }

        public int getWindokuNum() {
            return this.windokuNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = WindokuCells[this.windokuNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return WindokuIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return "windoku";
        }

        @Override
        public String toString2() {
            return "windoku" + " " + (windokuNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (windokuNum + 1);
        }

    }

    /**
     * An Asterisk constraint of a sudoku grid.
     */
    public class Asterisk extends Region {

        private int asteriskNum;

        public Asterisk(int asteriskNum) {
            this.asteriskNum = asteriskNum;
        }

        public int getAsteriskNum() {
            return this.asteriskNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = AsteriskCells[this.asteriskNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return AsteriskIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return "asterisk";
        }

        @Override
        public String toString2() {
            return "asterisk";
        }

        @Override
        public String toFullString() {
            return toString() + " " + (asteriskNum + 1);
        }

    }

    /**
     * A CenterDot constraint of a sudoku grid.
     */
    public class CenterDot extends Region {

        private int centerdotNum;

        public CenterDot(int centerdotNum) {
            this.centerdotNum = centerdotNum;
        }

        public int getCenterDotNum() {
            return this.centerdotNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = CenterDotCells[this.centerdotNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return CenterDotIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return "centerdot";
        }

        @Override
        public String toString2() {
            return "centerdot";
        }

        @Override
        public String toFullString() {
            return toString() + " " + (centerdotNum + 1);
        }

    }

    /**
     * A Girandola constraint of a sudoku grid.
     */
    public class Girandola extends Region {

        private int girandolaNum;

        public Girandola(int girandolaNum) {
            this.girandolaNum = girandolaNum;
        }

        public int getGirandolaNum() {
            return this.girandolaNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = GirandolaCells[this.girandolaNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return GirandolaIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return "girandola";
        }

        @Override
        public String toString2() {
            return "girandola";
        }

        @Override
        public String toFullString() {
            return toString() + " " + (girandolaNum + 1);
        }

    }

    /**
     * A Halloween constraint of a sudoku grid.
     */
    public class Halloween extends Region {

        private int halloweenNum;

        public Halloween(int halloweenNum) {
            this.halloweenNum = halloweenNum;
        }

        public int getHalloweenNum() {
            return this.halloweenNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = HalloweenCells[this.halloweenNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return HalloweenIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return "halloween";
        }

        @Override
        public String toString2() {
            return "halloween" + " " + (halloweenNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (halloweenNum + 1);
        }

    }

    /**
     * A PerCent constraint of a sudoku grid.
     */
    public class PerCent extends Region {

        private int percentNum;

        public PerCent(int percentNum) {
            this.percentNum = percentNum;
        }

        public int getPerCentNum() {
            return this.percentNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = PerCentCells[this.percentNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return PerCentIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return "percent";
        }

        @Override
        public String toString2() {
            return "percent" + " " + (percentNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (percentNum + 1);
        }

    }

    /**
     * A SdokuBand constraint of a sudoku grid.
     */
    public class SdokuBand extends Region {

        private int sdokubandNum;

        public SdokuBand(int sdokubandNum) {
            this.sdokubandNum = sdokubandNum;
        }

        public int getSdokuBandNum() {
            return this.sdokubandNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = SdokuBandCells[this.sdokubandNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return SdokuBandIndexOf[cell.getY()][cell.getX()];
        }

        public int At(int x, int y) {
            return SdokuBandAt[y][x];
        }

        @Override
        public String toString() {
            return "s-doku band";
        }

        @Override
        public String toString2() {
            return "s-doku band" + " " + (sdokubandNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (sdokubandNum + 1);
        }

    }

    /**
     * A SdokuStack constraint of a sudoku grid.
     */
    public class SdokuStack extends Region {

        private int sdokustackNum;

        public SdokuStack(int sdokustackNum) {
            this.sdokustackNum = sdokustackNum;
        }

        public int getSdokuStackNum() {
            return this.sdokustackNum;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = SdokuStackCells[this.sdokustackNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        @Override
        public int indexOf(Cell cell) {
            return SdokuStackIndexOf[cell.getY()][cell.getX()];
        }

        public int At(int x, int y) {
            return SdokuStackAt[y][x];
        }

        @Override
        public String toString() {
            return "s-doku stack";
        }

        @Override
        public String toString2() {
            return "s-doku stack" + " " + (sdokustackNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (sdokustackNum + 1);
        }

    }

    /**
     * A Custom constraint of a sudoku grid.
     */
    public class Custom extends Region {

        private int customNum;

        private int numRows = 0;
        private int numColumns = 0;

        public Custom(int customNum) {
            this.customNum = customNum;
        }

        public int getCustomNum() {
            return this.customNum;
        }

        public void setNumColumns(int numColumns) {
            this.numColumns = numColumns;
        }
        public void setNumRows(int numRows) {
            this.numRows = numRows;
        }

        public int getNumColumns() {
            return this.numColumns;
        }
        public int getNumRows() {
            return this.numRows;
        }

        @Override
        public Cell getCell(int index) {
            int cellIndex = CustomCells[this.customNum][index];
            return cells[cellIndex / 9][cellIndex % 9];
        }

        public int At(int x, int y) {
            return CustomAt[y][x];
        }

        @Override
        public int indexOf(Cell cell) {
            return CustomIndexOf[cell.getY()][cell.getX()];
        }

        @Override
        public String toString() {
            return ((CustomNum!=9)?"extra region":"jigsaw");
        }

        @Override
        public String toString2() {
            return ((CustomNum!=9)?"extra region":"jigsaw") + " " + (customNum + 1);
        }

        @Override
        public String toFullString() {
            return toString() + " " + (customNum + 1);
        }

    }

    /**
     * custom regions - region numbered 1-9, ideally 2-4 regions is good.
     */
    public void customInitialize(String regions) {
        regions = regions.replace( "A", "1"); regions = regions.replace( "B", "2"); regions = regions.replace( "C", "3"); regions = regions.replace( "D", "4"); regions = regions.replace( "E", "5"); regions = regions.replace( "F", "6"); regions = regions.replace( "G", "7"); regions = regions.replace( "H", "8"); regions = regions.replace( "I", "9");
        CustomNum = 0; regions = regions.replace( ".", "0");
        for (int y = 0; y < 9; y++) { int x = 0;
            for (int z = 0; z < 9; z++) { CustomCells[ y][ z] = -1; }
            for (int c = 0; c < 81; c++) {
                if ( regions.charAt( c) == '0'+y+1 ) { CustomCells[ y][ x] = c; x++;
                    if ( CustomNum < y+1 ) { CustomNum = y+1; }
                }
            }
        }
        Settings.getInstance().setCount( CustomNum);
        for (int c = 0; c < 81; c++) { CustomAt[ c/9][ c%9] = -1; CustomIndexOf[ c/9][ c%9] = -1;
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if ( CustomCells[ y][ x] == c) { CustomAt[ c/9][ c%9] = y; CustomIndexOf[ c/9][ c%9] = x; }
                }
            }
        }
    }

    /**
     * rotates the sudoku clockwise 90 degrees
     */
    public void rotateClockwise() {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {           // rotate
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        for (int y = 0; y < 9; y++) {           // set givens
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                cell.setValue(copy[8-x][y]);
                if ( clue[8-x][y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
    }

    /**
     * rotates the sudoku anti-clockwise 90 degrees
     */
    public void rotateAntiClockwise() {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {           // rotate
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        for (int y = 0; y < 9; y++) {           // set givens
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                cell.setValue(copy[x][8-y]);
                if ( clue[x][8-y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
    }

    /**
     * rotates the sudoku along horizontal axis
     */
    public void rotateHorizontal() {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                cell.setValue(copy[8-y][x]);
                if ( clue[8-y][x] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
    }

    /**
     * rotates the sudoku along vertical axis
     */
    public void rotateVertical() {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                cell.setValue(copy[y][8-x]);
                if ( clue[y][8-x] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
    }

    /**
     * rotates the sudoku along diagonal (/) axis
     */
    public void rotateDiagonal() {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                cell.setValue(copy[8-x][8-y]);
                if ( clue[8-x][8-y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
    }

    /**
     * rotates the sudoku along anti-diagonal (\) axis
     */
    public void rotateAntiDiagonal() {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                cell.setValue(copy[x][y]);
                if ( clue[x][y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
    }

    /**
     * rotates the custom clockwise 90 degrees
     */
    public void rotateCustomClockwise() {
      if ( isCustom ) {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        String newlayout = "";
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                newlayout += "" + (char)( CustomAt[8-x][y] +'1');
                Cell cell = getCell( x, y);
                cell.setValue(copy[8-x][y]);
                if ( clue[8-x][y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
        Settings.getInstance().setCustom( newlayout);
        customInitialize( newlayout);
      }
    }

    /**
     * rotates the custom anti-clockwise 90 degrees
     */
    public void rotateCustomAntiClockwise() {
      if ( isCustom ) {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        String newlayout = "";
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                newlayout += "" + (char)( CustomAt[x][8-y] +'1');
                Cell cell = getCell( x, y);
                cell.setValue(copy[x][8-y]);
                if ( clue[x][8-y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
        Settings.getInstance().setCustom( newlayout);
        customInitialize( newlayout);
      }
    }

    /**
     * rotates the custom along horizontal axis
     */
    public void rotateCustomHorizontal() {
      if ( isCustom ) {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        String newlayout = "";
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                newlayout += "" + (char)( CustomAt[8-y][x] +'1');
                Cell cell = getCell( x, y);
                cell.setValue(copy[8-y][x]);
                if ( clue[8-y][x] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
        Settings.getInstance().setCustom( newlayout);
        customInitialize( newlayout);
      }
    }

    /**
     * rotates the custom along vertical axis
     */
    public void rotateCustomVertical() {
      if ( isCustom ) {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        String newlayout = "";
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                newlayout += "" + (char)( CustomAt[y][8-x] +'1');
                Cell cell = getCell( x, y);
                cell.setValue(copy[y][8-x]);
                if ( clue[y][8-x] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
        Settings.getInstance().setCustom( newlayout);
        customInitialize( newlayout);
      }
    }

    /**
     * rotates the custom along diagonal (/) axis
     */
    public void rotateCustomDiagonal() {
      if ( isCustom ) {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        String newlayout = "";
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                newlayout += "" + (char)( CustomAt[8-x][8-y] +'1');
                Cell cell = getCell( x, y);
                cell.setValue(copy[8-x][8-y]);
                if ( clue[8-x][8-y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
        Settings.getInstance().setCustom( newlayout);
        customInitialize( newlayout);
      }
    }

    /**
     * rotates the custom along anti-diagonal (\) axis
     */
    public void rotateCustomAntiDiagonal() {
      if ( isCustom ) {
        int[][] copy = new int[9][9];
        boolean[][] clue = new boolean[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell( x, y);
                copy[y][x] = cell.getValue();
                clue[y][x] = cell.isGiven();
            }
        }
        String newlayout = "";
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                newlayout += "" + (char)( CustomAt[x][y] +'1');
                Cell cell = getCell( x, y);
                cell.setValue(copy[x][y]);
                if ( clue[x][y] ) {
                    cell.setGiven();
                } else {
                    cell.resetGiven();
                }
            }
        }
        Settings.getInstance().setCustom( newlayout);
        customInitialize( newlayout);
      }
    }

    /**
     * Get the first cell that cancels the given cell.
     * <p>
     * More precisely, get the first cell that:
     * <ul>
     * <li>is in the same row, column or block of the given cell
     * <li>contains the given value
     * </ul>
     * The order used for the "first" is not defined, but is guaranted to be
     * consistent accross multiple invocations.
     * @param target the cell
     * @param value the value
     * @return the first cell that share a region with the given cell, and has
     * the given value
     */
    public Cell getFirstCancellerOf(Cell target, int value) {
        for (Class<? extends Region> regionType : getRegionTypes()) {
            Region region = getRegionAt(regionType, target.getX(), target.getY());
          if ( region != null ) {
            for (int i = 0; i < 9; i++) {
                Cell cell = region.getCell(i);
                if (!cell.equals(target) && cell.getValue() == value)
                    return cell;
            }
          }
        }
        return null;
    }

    /**
     * Copy the content of this grid to another grid.
     * The values of the cells and their potential values
     * are copied.
     * @param other the grid to copy this grid to
     */
    public void copyTo(Grid other) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                this.cells[y][x].copyTo(other.cells[y][x]);
            }
        }
    }

    /**
     * Get the number of occurances of a given value in this grid
     * @param value the value
     * @return the number of occurances of a given value in this grid
     */
    public int getCountOccurancesOfValue(int value) {
        int result = 0;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (cells[y][x].getValue() == value)
                    result++;
            }
        }
        return result;
    }

    /**
     * Get a string representation of this grid. For debugging
     * purpose only.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int value = getCellValue(x, y);
                if (value == 0)
                    result.append('.');
                else
                    result.append(value);
            }
            result.append('\n');
        }
        return result.toString();
    }

    /**
     * Compare two grids for equality. Comparison is based on the values
     * of the cells and on the potential values of the empty cells.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grid))
            return false;
        Grid other = (Grid)o;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell thisCell = this.getCell(x, y);
                Cell otherCell = other.getCell(x, y);
                if (thisCell.getValue() != otherCell.getValue())
                    return false;
                if (!thisCell.getPotentialValues().equals(otherCell.getPotentialValues()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell(x, y);
                result ^= cell.getValue();
                result ^= cell.getPotentialValues().hashCode();
            }
        }
        return result;
    }

}
