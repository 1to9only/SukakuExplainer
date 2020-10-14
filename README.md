# SukakuExplainer

SukakuExplainer is my modifications to SudokuExplainer to also solve pencilmark sudoku (sukaku).

There is discussion about sukaku here: http://forum.enjoysudoku.com/pencilmark-only-sudoku-t4929.html

This version of SukakuExplainer was first released here: http://forum.enjoysudoku.com/post280335.html (5 Aug 2019).

There is an improved (faster) version of SukakuExplainer here: https://github.com/SudokuMonster/SukakuExplainer

## Usage - GUI

  java.exe -jar SukakuExplainer.jar

## Usage - serate

 java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.serate --format="%g ED=%r/%p/%d" --input=puzzles.txt --output=puzzles.rated.txt

## Usage - hints

  java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.hints --input=puzzle.txt

## Usage - pencilmarks

  java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.pencilmarks --input=puzzle.txt

## Usage - Tester (Analyzer)

  java.exe -Xrs -Xmx500m -cp SukakuExplainer.jar diuf.sudoku.test.Tester puzzles.txt puzzles.log

