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
```
-d      Diagonal [/] in Single-Diagonal Sudoku, -X must be specified
-a      AntiDiagonal [\] in Single-Diagonal Sudoku, -X must be specified
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

### Options - support for rating sudokus with multiple solutions!
```
-M      for sudokus with multiple solutions (this also disables the UL and BUG solving techniques)
```
For more info see: http://forum.enjoysudoku.com/help-with-sudoku-explainer-t6677-270.html#p305813

Examples:
```
java.exe -Xrs -Xmx1g -cp SukakuExplainer.jar diuf.sudoku.test.serate -M --input=sample.txt --output=sample.se

java.exe -Xrs -Xmx1g -cp SukakuExplainer.jar diuf.sudoku.test.pencilmarks -M --input=sample.txt >sample.p

java.exe -Xrs -Xmx1g -cp SukakuExplainer.jar diuf.sudoku.test.hints -M --input=sample.txt >sample.h
```
### Options - support for rating cUstom sudokus with 2-4 or more extra regions
```
-U      for cUstom sudokus with 2-4 or more extra regions
```
To use this feature in command-line: serate, hints, pencilmarks and solve:
In GUI, setup the sudoku with the correct extra regions layout. Exit GUI. This saves the layout in SukakuExplainer.json.
Example:
```
java.exe -Xrs -Xmx1g -cp SukakuExplainer.jar diuf.sudoku.test.serate -U -X --input=custom.txt --output=custom.se
```
### Options - support for odd/eVen sudokus
```
-V      for odd/eVen sudokus (odd or even, or both odd and even)
```
To use this feature in command-line: serate, hints, pencilmarks and solve:
In GUI, setup the sudoku with the correct odd/even layout. Exit GUI. This saves the layout in SukakuExplainer.json.
Example:
```
java.exe -Xrs -Xmx1g -cp SukakuExplainer.jar diuf.sudoku.test.serate -V --input=oddeven.txt --output=oddeven.se
```
## Hidden Features
### Get more hints
If you hold down the Ctrl or Shift key, and click on the 'Get all hints' button - you'll get even more hints.

The feature does not work with the accelerator key (F6) or from the 'Tools' drop down menu.

As expected, getting more hints takes longer!

&nbsp;

