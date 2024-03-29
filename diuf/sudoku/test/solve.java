/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2009 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 * this entry point by gsf @ www.sudoku.com/boards (The Player's Forum)
 */
package diuf.sudoku.test;

import java.io.*;
import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;

public class solve {
    static String FORMAT = "%s ED=%r/%p/%d";
    /**
     * Solve input puzzles and print results according to the output format.
     * @param args 81-char puzzles
     */
    public static void main(String[] args) {
        String          format = FORMAT;
        String          input = null;
        String          output = "-";
        String          a;
        String          s;
        String          v;
        String          puzzle;
        BufferedReader  reader = null;
    //  PrintWriter     writer = null;
        int             arg;
        char            c;
        Settings.getInstance().setNoSaves();
        try {
            for (arg = 0; arg < args.length; arg++) {
                a = s = args[arg];
                if (s.charAt(0) != '-')
                    break;
                v = null;
                if (s.charAt(1) == '-') {
                    if (s.length() == 2) {
                        arg++;
                        break;
                    }
                    s = s.substring(2);
                    for (int i = 2; i < s.length(); i++)
                        if (s.charAt(i) == '=') {
                            v = s.substring(i+1);
                            s = s.substring(0, i);
                        }
                    if (s.equals("in") || s.equals("input"))
                        c = 'i';
                    else if (s.equals("out") || s.equals("output"))
                        c = 'o';
                    else
                        c = '?';
                }
                else {
                    c = s.charAt(1);
                    if (s.length() > 2)
                        v = s.substring(2);
                    else if ( ( c=='i' || c=='o') && ( (arg+1) < args.length) )
                        v = args[++arg];
                }
                switch (c) {
                case 'i':
                    input = v;
                    break;
                case 'o':
                    output = v;
                    break;

                case 'L':   // LatinSquare
                    Settings.getInstance().setLatinSquare(true);
                    break;
                case 'X':   // Diagonals
                    Settings.getInstance().setDiagonals(true);
                    break;
                case 'D':   // DisjointGroups
                    Settings.getInstance().setDisjointGroups(true);
                    break;
                case 'W':   // Windoku
                    Settings.getInstance().setWindoku(true);
                    break;

                case 'A':   // Asterisk
                    Settings.getInstance().setAsterisk(true);
                    break;
                case 'C':   // CenterDot
                    Settings.getInstance().setCenterDot(true);
                    break;
                case 'G':   // Girandola
                    Settings.getInstance().setGirandola(true);
                    break;

                case 'H':   // Halloween
                    Settings.getInstance().setHalloween(true);
                    break;
                case 'P':   // PerCent
                    Settings.getInstance().setPerCent(true);
                    break;
                case 'S':   // S-doku
                    Settings.getInstance().setSdoku(true);
                    break;

                case 'U':   // Custom
                    Settings.getInstance().setCustom(true);
                    break;
                case 'V':   // OddEven
                    Settings.getInstance().setOddEven(true);
                    break;

                case 'd':   // diagonal
                    Settings.getInstance().setXAntiDiagonal(false);
                    break;
                case 'a':   // antidiagonal
                    Settings.getInstance().setXDiagonal(false);
                    break;

                case 'T':   // used saved techniques
                    Settings.getInstance().unpackmethods();
                    break;

                default:
                    break;
                }
            }
            if (input != null) {
                if (input.equals("-")) {
                    InputStreamReader reader0 = new InputStreamReader(System.in);
                    reader = new BufferedReader(reader0);
                }
                else {
                    Reader reader0 = new FileReader(input);
                    reader = new BufferedReader(reader0);
                }
            }
        //  if (output.equals("-")) {
        //      OutputStreamWriter writer0 = new OutputStreamWriter(System.out);
        //      BufferedWriter writer1 = new BufferedWriter(writer0);
        //      writer = new PrintWriter(writer1);
        //  }
        //  else {
        //      Writer writer0 = new FileWriter(output);
        //      BufferedWriter writer1 = new BufferedWriter(writer0);
        //      writer = new PrintWriter(writer1);
        //  }
            for (;;) {
                if (reader != null) {
                    puzzle = reader.readLine();
                    if (puzzle == null)
                        break;
                }
                else if (arg < args.length)
                    puzzle = args[arg++];
                else
                    break;
                if (puzzle.length() >= 81) {
                    Grid grid = new Grid();
                if (puzzle.length() >= 729)
                {
                    for (int i = 0; i < 81; i++) {
                        grid.setCellValue(i % 9, i / 9, 0);
                    }
                    for (int i = 0; i < 729; i++) {
                        int cl = i / 9;  // cell
                        char ch = puzzle.charAt(i);

                        if (ch >= '1' && ch <= '9') {
                            int value = (ch - '0');
                            Cell cell = grid.getCell(cl % 9, cl / 9);
                            cell.addPotentialValue(value);
                        }
                    }
                    // fix naked singles
                    for (int i = 0; i < 81; i++) {
                        Cell cell = grid.getCell(i % 9, i / 9);
                        if ( cell.getPotentialValues().cardinality() == 1 ) {
                            int singleclue = cell.getPotentialValues().nextSetBit(0);
                            boolean isnakedsingle = true;
                            for (Cell housecell : cell.getHouseCells()) {
                                if ( housecell.hasPotentialValue(singleclue) ) {
                                    isnakedsingle = false;
                                    break;
                                }
                            }
                            if ( isnakedsingle )
                            {
                                cell.setValue( singleclue);
                                cell.getPotentialValues().clear();
                            }
                        }
                    }
                    grid.setSukaku();
                }
                else
                {
                    for (int i = 0; i < 81; i++) {
                        char ch = puzzle.charAt(i);
                        if (ch >= '1' && ch <= '9') {
                            int value = (ch - '0');
                            grid.setCellValue(i % 9, i / 9, value);
                        }
                    }
                }
                //  System.out.println(puzzle); System.out.flush();
                    Solver solver = new Solver(grid);
                    solver.want = 0;
                if (puzzle.length() >= 81 && puzzle.length() < 729)
                {
                    solver.rebuildPotentialValues();
                }
                    try {
                        solver.getSolution();
                    } catch (UnsupportedOperationException ex) {
                        solver.difficulty = solver.pearl = solver.diamond = 0.0;
                    }
                    s = "";
                    for (int i = 0; i < format.length(); i++) {
                        int             w;
                        int             p;
                        char    f = format.charAt(i);
                        if (f != '%' || ++i >= format.length())
                            s += f;
                        else
                            switch (format.charAt(i)) {
                            case 'g':
                                s += puzzle;
                                break;
                            case 'r':
                                w = (int)((solver.difficulty + 0.05) * 10);
                                p = w % 10;
                                w /= 10;
                                s += w + "." + p;
                                break;
                            case 'p':
                                w = (int)((solver.pearl + 0.05) * 10);
                                p = w % 10;
                                w /= 10;
                                s += w + "." + p;
                                break;
                            case 'd':
                                w = (int)((solver.diamond + 0.05) * 10);
                                p = w % 10;
                                w /= 10;
                                s += w + "." + p;
                                break;
                            case 's':
                                for (int j = 0; j < 81; j++) {
                                    int n = grid.getCellValue(j % 9, j / 9);
                                    s += (n==0)?".":n;
                                }
                                break;
                            default:
                                s += f;
                                break;
                            }
                    }
                    System.out.println(s);
                    System.out.flush();
                }
            }
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            //  if (writer != null)
            //      writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
