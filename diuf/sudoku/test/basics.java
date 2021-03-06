/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.test;

import java.io.*;
import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;


public class basics {

    /**
     * Analyze all the Sudokus of a given file, and store the results
     * in another given file. The content of the result file is also
     * printed on the console.
     * @param args the two file names
     */
    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Expected arguments: filename logfilename");
        String fileName = args[0];
        String logFile = args[1];
        LineNumberReader reader = null;
        PrintWriter writer = null;
        Settings.getInstance().setNoSaves();
        Settings.getInstance().NoDirectTechniques();
        try {
            Reader reader0 = new FileReader(fileName);
            reader = new LineNumberReader(reader0);
            Writer writer0 = new FileWriter(logFile);
            BufferedWriter writer1 = new BufferedWriter(writer0);
            writer = new PrintWriter(writer1);
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.length() >= 81) {

                    int HiddenSingle = 0;
                    int NakedSingle = 0;
                    int Pointing = 0;
                    int Claiming = 0;
                    int NakedPair = 0;
                    int HiddenPair = 0;
                    int XWing = 0;
                    int Swordfish = 0;
                    int Jellyfish = 0;
                    int XYWing = 0;
                    int XYZWing = 0;
                    int NakedTriplet = 0;
                    int HiddenTriplet = 0;
                    int NakedQuad = 0;
                    int HiddenQuad = 0;
                    int total = 0;

                    int UniqueRectangle = 0;
                    int UniqueLoop = 0;

                    System.out.println("Analyzing Sudoku #" + reader.getLineNumber());
                    Grid grid = new Grid();
                    for (int i = 0; i < 81; i++) {
                        char ch = line.charAt(i);
                        if (ch >= '1' && ch <= '9') {
                            int value = (ch - '0');
                            grid.setCellValue(i % 9, i / 9, value);
                        }
                    }
                    Solver solver = new Solver(grid);
                    solver.rebuildPotentialValues();
                    try {
                        Map<Rule,Integer> rules = solver.solve(null);
                        Map<String,Integer> ruleNames = solver.toNamedList(rules);
                        double difficulty = 0;
                        String hardestRule = "";
                        for (Rule rule : rules.keySet()) {
                            if (rule.getDifficulty() > difficulty) {
                                difficulty = rule.getDifficulty();
                                hardestRule = rule.getName();
                            }
                        }
                        for (String rule : ruleNames.keySet()) {

                            if ( rule.equals( "Hidden Single" ) ) { HiddenSingle = 1;  }
                            if ( rule.equals( "Naked Single"  ) ) { NakedSingle = 1;   }
                            if ( rule.equals( "Pointing"      ) ) { Pointing = 1;      }
                            if ( rule.equals( "Claiming"      ) ) { Claiming = 1;      }
                            if ( rule.equals( "Naked Pair"    ) ) { NakedPair = 1;     }
                            if ( rule.equals( "Hidden Pair"   ) ) { HiddenPair = 1;    }
                            if ( rule.equals( "X-Wing"        ) ) { XWing = 1;         }
                            if ( rule.equals( "Swordfish"     ) ) { Swordfish = 1;     }
                            if ( rule.equals( "Jellyfish"     ) ) { Jellyfish = 1;     }
                            if ( rule.equals( "XY-Wing"       ) ) { XYWing = 1;        }
                            if ( rule.equals( "XYZ-Wing"      ) ) { XYZWing = 1;       }
                            if ( rule.equals( "Naked Triplet" ) ) { NakedTriplet = 1;  }
                            if ( rule.equals( "Hidden Triplet") ) { HiddenTriplet = 1; }
                            if ( rule.equals( "Naked Quad"    ) ) { NakedQuad = 1;     }
                            if ( rule.equals( "Hidden Quad"   ) ) { HiddenQuad = 1;    }

                            if ( rule.equals( "Unique Rectangle type 1") ) { UniqueRectangle = 1; }
                            if ( rule.equals( "Unique Rectangle type 2") ) { UniqueRectangle = 1; }
                            if ( rule.equals( "Unique Rectangle type 3") ) { UniqueRectangle = 1; }
                            if ( rule.equals( "Unique Rectangle type 4") ) { UniqueRectangle = 1; }

                            if ( rule.equals( "Unique Loop type 1") ) { UniqueLoop = 1; }
                            if ( rule.equals( "Unique Loop type 2") ) { UniqueLoop = 1; }
                            if ( rule.equals( "Unique Loop type 3") ) { UniqueLoop = 1; }
                            if ( rule.equals( "Unique Loop type 4") ) { UniqueLoop = 1; }

                        }

                        total = total + HiddenSingle + NakedSingle;
                        total = total + Pointing + Claiming;
                        total = total + NakedPair + HiddenPair;
                        total = total + XWing + Swordfish + Jellyfish;
                        total = total + XYWing + XYZWing;
                        total = total + NakedTriplet + HiddenTriplet;
                        total = total + NakedQuad + HiddenQuad;

                    if ( total >= 12 && UniqueRectangle == 0 && UniqueLoop == 0 ) {
                        writer.println("Analyzing Sudoku #" + reader.getLineNumber());
                        writer.println( line + " (" + total + ")");
                        System.out.println( line + " (" + total + ")");
                        for (String rule : ruleNames.keySet()) {
                            int count = ruleNames.get(rule);
                            writer.println(Integer.toString(count) + " " + rule);
                            System.out.println(Integer.toString(count) + " " + rule);
                        }
                        writer.println();
                        writer.flush();
                    }

                    //  writer.println("Hardest technique: " + hardestRule);
                    //  System.out.println("Hardest technique: " + hardestRule);
                    //  writer.println("Difficulty: " + difficulty);
                    //  System.out.println("Difficulty: " + difficulty);
                    } catch (UnsupportedOperationException ex) {
                        writer.println("Failed !");
                        System.out.println("Failed !");
                    }

                    System.out.println();
                } else
                    System.out.println("Skipping incomplete line: " + line);
                line = reader.readLine();
            }
            writer.close();
            reader.close();
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.print("Finished.");
    }

}
