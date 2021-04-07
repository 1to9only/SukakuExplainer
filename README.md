# SukakuExplainer

SukakuExplainer is my modifications to SudokuExplainer to also solve pencilmark sudoku (sukaku).

There is discussion about sukaku here: http://forum.enjoysudoku.com/pencilmark-only-sudoku-t4929.html

This version of SukakuExplainer was first released here: http://forum.enjoysudoku.com/post280335.html (5 Aug 2019).

There is an improved version (has more solving techniques) of SukakuExplainer here: https://github.com/SudokuMonster/SukakuExplainer

## Usage - GUI
```
java.exe -jar SukakuExplainer.jar
```
## Usage - serate
```
java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.serate --input=puzzles.txt --output=puzzles.rated.txt [options]
```
The --format="%g ED=%r/%p/%d" option is not needed, as this is the default.
## Usage - hints
```
java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.hints --input=puzzle.txt [options]
```
## Usage - pencilmarks
```
java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.pencilmarks --input=puzzle.txt [options]
```
## Usage - solve
```
java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.solve --input=puzzle.txt [options]
```
## Usage - Tester (Analyzer) - for vanilla sudoku only
```
java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.Tester puzzles.txt puzzles.log
```
## Usage - basics
```
java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.basics puzzles.txt puzzles.log
```
## Options
None, one or more of the following:
```
-L      Latin Square
-X      Diagonals (X)
-D      Disjoint Groups
-W      Windoku
```
None or one of the following (can be combined with above options):
```
-A      Asterisk
-C      Center Dots
-G      Girandola
```
None or one of the following (can be combined with Diagonals (X) only):
```
-H      Halloween
-P      Per Cent
-S      S-doku
```
##### The options entered are not validated by the program, i.e. enter rubbish options, expect rubbish output!
##### If no options are specified, it is a Vanilla sudoku.
&nbsp;

