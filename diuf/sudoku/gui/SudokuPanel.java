/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.*;

import diuf.sudoku.*;

/**
 * The panel representing the sudoku grid. Includes the legend.
 * <p>
 * All user actions are redirected to the {@link diuf.sudoku.gui.SudokuExplainer}
 * class.
 * @see diuf.sudoku.gui.SudokuFrame
 * @see diuf.sudoku.gui.SudokuExplainer
 */
public class SudokuPanel extends JPanel {

    private static final long serialVersionUID = 3709127163156966626L;

    private int CELL_OUTER_SIZE = 45;
    private int CELL_INNER_SIZE = 39;
    private int GRID_GAP_SIZE = 2;
    private int LEGEND_GAP_SIZE = 42;
    private int CELL_PAD = (CELL_OUTER_SIZE - CELL_INNER_SIZE) / 2;
    private int GRID_SIZE = CELL_OUTER_SIZE * 9;
    private String CLUE_FONT_NAME = "Arial Bold";
    private String FONT_NAME = "Arial";
    private int FONT_SIZE_SMALL = 12;
    private int FONT_SIZE_BIG = 36;
    private int FONT_SIZE_LEGEND = 24;

    private Grid grid;
    private Cell focusedCell = null;
    private Cell selectedCell = null;
    private int focusedCandidate = 0;
    private Map<Cell,BitSet> redPotentials;
    private Map<Cell,BitSet> greenPotentials;
    private Map<Cell,BitSet> bluePotentials;
    private Collection<Cell> greenCells;
    private Collection<Cell> redCells;
    private Grid.Region[] blueRegions;
    private Collection<Link> links;

    private SudokuFrame parent;
    private SudokuExplainer engine;

    private Font smallFont;
    private Font bigFont;
    private Font legendFont;

    // 1to9only: all the colors used are defined here.

    private Color backgroundColor = new Color(248, 248, 248, 127);      // background
    private Color altBackgroundColor = new Color(184, 184, 184, 127);   // background

    private Color borderColor = new Color(64, 64, 64);                  // border
    private Color legendColor = Color.gray;                             // legend
    private Color completedColor = new Color(170, 170, 170);            // legend completed
    private Color shadowColor = new Color(140, 140, 140);               // legend completed

    private Color givenValueColor = Color.black;                        // given cell text color
    private Color solvedValueColor = Color.blue;                        // solved cell text color
    private Color selectedColor = new Color(255, 217, 85);              // selected cell background color
    private Color focusedColor = new Color(255, 255, 85);               // focused cell background color
    private Color selectedfocusedValueColor = Color.black;              // selected and focused candidate
    private Color selectedfocusedColor = Color.magenta;                 // selected and focused candidate

    private Color candidateMaskColor = new Color(0, 255, 255, 127);     // candidate mask
    private Color potentialMaskColor = new Color(0, 255, 0, 127);       // potential mask

    private Color potentialColor = new Color(120, 120, 120);            // candidate text color
    private Color potentialAltColor = new Color(80, 80, 80);            // candidate (in selected cell)
    private Color highlight13DColor = Color.black;                      // candidate highlighted 3D
    private Color highlight23DColor = Color.yellow;                     // candidate highlighted 3D

    private Color redCellColor = Color.red;                             // hint red cell background
    private Color greenCellColor = new Color(192, 255, 255);            // hint green cell background (turquoise!)

    private Color hintGreenRegionColor = new  Color(0, 192, 0);         // hint green region
    private Color hintBlueRegionColor = Color.blue;                     // hint blue region

    private Color redPotentialColor = Color.red;                        // hint red potential
    private Color greenPotentialColor = new  Color(0, 224, 0);          // hint green potential
    private Color orangePotentialColor = new  Color(224, 128, 0);       // hint orange potential (red alt)
    private Color bluePotentialColor = Color.blue;                      // hint blue potential
    private Color linkColor = Color.red;                                // hint link

    private Color DG1 =  new Color(208, 238, 227);
    private Color DG2 =  new Color(237, 222, 194);
    private Color DG3 =  new Color(227, 221, 232);
    private Color DG4 =  new Color(209, 211, 237);
    private Color DG5 =  new Color(241, 241, 201);
    private Color DG6 =  new Color(242, 216, 210);
    private Color DG7 =  new Color(242, 210, 237);
    private Color DG8 =  new Color(208, 232, 238);
    private Color DG9 =  new Color(210, 240, 202);
    private Color[] DG_Colors = {DG1, DG2, DG3, DG4, DG5, DG6, DG7, DG8, DG9};
    private Color starColor = new Color(219, 121, 219);
    private Color halloweenColor = new Color(255, 187, 59); // h:26  s:240 l:148
    private Color percentColor = new Color(122, 255, 178);  // h:97  s:240 l:177
    private Color sdokuColor = new Color(248, 177, 248);    // h:200 s:200 l:200
    private Color customColor = new Color(244, 138, 138);   // h:0   s:200 l:180

    public SudokuPanel(SudokuFrame parent) {
        super();
        this.parent = parent;
      if ( Settings.getInstance().isBigCell() ) {
        CELL_OUTER_SIZE = 45+21;
        CELL_INNER_SIZE = 39+21;
        CELL_PAD = (CELL_OUTER_SIZE - CELL_INNER_SIZE) / 2;
        GRID_SIZE = CELL_OUTER_SIZE * 9;
        FONT_SIZE_SMALL = 12+6;
        FONT_SIZE_BIG = 36+12;
      }
        if (getToolkit().getScreenSize().height < 750)
            rescale();
        initialize();
        super.setOpaque(false);
        smallFont = new Font(CLUE_FONT_NAME, Font.PLAIN, FONT_SIZE_SMALL);
        bigFont = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_BIG);
        legendFont = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_LEGEND);
    }

    private void rescale() {
        CELL_OUTER_SIZE = rescale(CELL_OUTER_SIZE);
        CELL_INNER_SIZE = rescale(CELL_INNER_SIZE);
        GRID_GAP_SIZE = rescale(GRID_GAP_SIZE);
        LEGEND_GAP_SIZE = rescale(LEGEND_GAP_SIZE);
        CELL_PAD = rescale(CELL_PAD);
        GRID_SIZE = rescale(GRID_SIZE);
        FONT_SIZE_SMALL = 9;
        FONT_SIZE_BIG = rescale(FONT_SIZE_BIG);
        FONT_SIZE_LEGEND = rescale(FONT_SIZE_LEGEND);
    }

    private int rescale(int value) {
        return value * 2 / 3;
    }

    private void initialize() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setFocusedCell(null);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // Workaround mouseClicked won't be fired while moving mouse
                // http://stackoverflow.com/questions/3382330/mouselistener-for-jpanel-missing-mouseclicked-events
                if (!SudokuPanel.this.contains(e.getPoint()))
                    return;
                Cell target = getCellAt(e.getX(), e.getY());
                if (target == selectedCell && target != null) {
                    int value = getCandidateAt(e.getX(), e.getY());
                    if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3
                            || (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                        if (value != 0) { // Check that the cell is empty
                            // Toggle a candidate
                            engine.candidateMouTyped(target, value);
                            repaintCell(target);
                        }
                    } else {
                        if (target.getValue() == 0) {
                            if ( target.hasPotentialValue( value) )
                            {
                                // Set the cell's value
                                engine.cellValueMouTyped(target, value);
                                repaint();
                            }
                        } else {
                            // Clear the cell's value
                            engine.cellValueMouTyped(target, 0);
                            repaint();
                        }
                    }
                } else {
                    setFocusedCandidate(0);
                    setSelectedCell(target);
                    if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3
                            || (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
                        setFocusedCandidate(getCandidateAt(e.getX(), e.getY()));
                }
                SudokuPanel.super.requestFocusInWindow();
            }
        });

        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                setFocusedCell(getCellAt(e.getX(), e.getY()));
                if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
                    setFocusedCandidate(getCandidateAt(e.getX(), e.getY()));
                else
                    setFocusedCandidate(0);
            }
        });
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
                        code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                    setFocusedCell(null);
                    int x = 4;
                    int y = 5;
                    if (selectedCell != null) {
                        x = selectedCell.getX();
                        y = selectedCell.getY();
                    }
                    if (code == KeyEvent.VK_LEFT)
                        x = (x + 8) % 9;
                    else if (code == KeyEvent.VK_RIGHT)
                        x = (x + 1) % 9;
                    else if (code == KeyEvent.VK_UP)
                        y = (y + 8) % 9;
                    else if (code == KeyEvent.VK_DOWN)
                        y = (y + 1) % 9;
                    setFocusedCandidate(0);
                    if (selectedCell == null) {
                        // Select below the central cell
                        setSelectedCell(grid.getCell(4, 5));
                        x = 4;
                        y = 5;
                    } else {
                        setSelectedCell(grid.getCell(x, y));
                    }
                } else if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE) {
                    if (selectedCell != null) {
                        engine.cellValueKbdTyped(selectedCell, 0);
                        repaint();
                    }
                } else if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
                    if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
                        int value = (code - KeyEvent.VK_0);
                        if (selectedCell != null) {
                            engine.candidateKbdTyped(selectedCell, value);
                            repaintCell(selectedCell);
                        }
                    }
                } else if (code == KeyEvent.VK_ESCAPE) {
                    setSelectedCell(null);
                    engine.clearHints();
                    repaint();
                }
            }
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                boolean isProcessed = false;
                if (selectedCell != null) {
                    char ch = e.getKeyChar();
                    if (ch >= '1' && ch <= '9') {
                        int value = ch - '0';
                        engine.cellValueKbdTyped(selectedCell, value);
                        repaint();
                        isProcessed = true;
                    } else if (ch == ' ' || ch == '0') {
                        engine.cellValueKbdTyped(selectedCell, 0);
                        selectedCell.setValue(0);
                        repaint();
                        isProcessed = true;
                    } else if (ch == '\r' || ch == '\n') {
                        setSelectedCell(null);
                        parent.getBtnApplyHintAndGet().requestFocusInWindow();
                        repaint();
                        isProcessed = true;
                    }
                }
                if (!isProcessed && e.getComponent() != SudokuPanel.this.parent) {
                    e.setSource(SudokuPanel.this.parent);
                    dispatchEvent(e);
                }
            }
        });
    }

    void setEngine(SudokuExplainer explainer) {
        this.engine = explainer;
    }

    private Cell getCellAt(int x, int y) {
        int cx = (x - LEGEND_GAP_SIZE) / CELL_OUTER_SIZE;
        int cy = (y - GRID_GAP_SIZE) / CELL_OUTER_SIZE;
        if (cx < 0 || cx >= 9 || cy < 0 || cy >= 9)
            return null;
        return grid.getCell(cx, cy);
    }

    private int getCandidateAt(int x, int y) {
        // Get cell's corner
        int cx = (x - LEGEND_GAP_SIZE) / CELL_OUTER_SIZE;
        int cy = (y - GRID_GAP_SIZE) / CELL_OUTER_SIZE;
        if (cx < 0 || cx >= 9 || cy < 0 || cy >= 9)
            return 0;
        Cell cell = grid.getCell(cx, cy);
        if (!cell.equals(this.selectedCell))
            return 0;
        if (this.selectedCell != null && this.selectedCell.getValue() != 0)
            return 0;
        // Substract cell's corner
        x = x - cx * CELL_OUTER_SIZE - LEGEND_GAP_SIZE;
        y = y - cy * CELL_OUTER_SIZE - GRID_GAP_SIZE;
        // Get candidate
        int px = (x - CELL_PAD) / (CELL_INNER_SIZE / 3);
        int py = (y - CELL_PAD) / (CELL_INNER_SIZE / 3);
        if (px < 0 || px >= 3 || py < 0 || py >= 3)
            return 0;
        return py * 3 + px + 1;
    }

    public Grid getSudokuGrid() {
        return grid;
    }

    public void setSudokuGrid(Grid sudokuGrid) {
        this.grid = sudokuGrid;
        this.selectedCell = sudokuGrid.getCell(4, 5);
    }

    public Collection<Cell> getGreenCells() {
        return greenCells;
    }

    public void setGreenCells(Collection<Cell> greenCells) {
        this.greenCells = greenCells;
    }

    public Collection<Cell> getRedCells() {
        return redCells;
    }

    public void setRedCells(Collection<Cell> redCells) {
        this.redCells = redCells;
    }

    public Map<Cell, BitSet> getGreenPotentials() {
        return greenPotentials;
    }

    public void setGreenPotentials(Map<Cell, BitSet> greenPotentials) {
        this.greenPotentials = greenPotentials;
    }

    public Map<Cell, BitSet> getRedPotentials() {
        return redPotentials;
    }

    public void setRedPotentials(Map<Cell, BitSet> redPotentials) {
        this.redPotentials = redPotentials;
    }

    public Map<Cell, BitSet> getBluePotentials() {
        return bluePotentials;
    }

    public void setBluePotentials(Map<Cell, BitSet> bluePotentials) {
        this.bluePotentials = bluePotentials;
    }

    public void setBlueRegions(Grid.Region... regions) {
        this.blueRegions = regions;
    }

    public void setLinks(Collection<Link> links) {
        this.links = links;
    }

    public void clearSelection() {
        this.focusedCandidate = 0;
        this.selectedCell = null;
        this.focusedCell = null;
    }

    public void clearFocus() {
        this.focusedCandidate = 0;
        this.focusedCell = null;
    }

    private void repaintCell(Cell cell) {
        if (cell == null)
            return;
        repaint(cell.getX() * CELL_OUTER_SIZE + LEGEND_GAP_SIZE,
                cell.getY() * CELL_OUTER_SIZE + GRID_GAP_SIZE,
                CELL_OUTER_SIZE, CELL_OUTER_SIZE);
    }

    private void setFocusedCell(Cell cell) {
        repaintCell(this.focusedCell);
        this.focusedCell = cell;
        repaintCell(this.focusedCell);
    }

    private void setSelectedCell(Cell cell) {
        repaintCell(this.selectedCell);
        this.selectedCell = cell;
        boolean showing = Settings.getInstance().isShowingCandidateMasks();
        if ( showing == false ) {
            repaintCell(this.selectedCell);
        }
        else {
            repaint();
        }
    }

    private void setFocusedCandidate(int value) {
        if (this.selectedCell == null)
            return;
        this.focusedCandidate = value;
        repaintCell(this.selectedCell);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GRID_SIZE + LEGEND_GAP_SIZE + GRID_GAP_SIZE,
                GRID_SIZE + LEGEND_GAP_SIZE + GRID_GAP_SIZE);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private void drawStringCentered(Graphics g, String s, int x, int y) {
        Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
        double px = x - rect.getWidth() / 2;
        double py = y - rect.getHeight() / 2 - rect.getY();
        g.drawString(s, (int)(px + 0.5), (int)(py + 0.5));
    }

    private void drawStringCentered3D(Graphics g, String s, int x, int y) {
        Color color = g.getColor();
        g.setColor(highlight13DColor);
        drawStringCentered(g, s, x, y + 1);
        g.setColor(highlight23DColor);
        drawStringCentered(g, s, x - 1, y);
        g.setColor(color);
        drawStringCentered(g, s, x, y);
    }

    private boolean initPotentialColor(Graphics g, Cell cell, int value) {
        boolean isHighlighted = false;
        boolean isRed = false;
        Color col = potentialColor;
        if (cell == selectedCell)
            col = potentialAltColor;
        if (cell == selectedCell && value == focusedCandidate)
            col = selectedfocusedValueColor;
        if (redPotentials != null) {
            BitSet redValues = redPotentials.get(cell);
            if (redValues != null && redValues.get(value)) {
                col = redPotentialColor;
                isHighlighted = true;
                isRed = true;
            }
        }
        if (bluePotentials != null) {
            BitSet blueValues = bluePotentials.get(cell);
            if (blueValues != null && blueValues.get(value)) {
                col = bluePotentialColor;
                isHighlighted = true;
            }
        }
        if (greenPotentials != null) {
            BitSet greenValues = greenPotentials.get(cell);
            if (greenValues != null && greenValues.get(value)) {
                if (isRed) {
                    col = orangePotentialColor;
                } else {
                  if ( !isHighlighted ) {
                    col = greenPotentialColor;
                    isHighlighted = true;
                  }
                }
            }
        }
        g.setColor(col);
        return isHighlighted;
    }

    private boolean init2PotentialColor(Graphics g, Cell cell, int value) {
        boolean isHighlighted = false;
        boolean isRed = false;
        Color col = potentialColor;
    //  if (cell == selectedCell)
    //      col = potentialAltColor;
    //  if (cell == selectedCell && value == focusedCandidate)
    //      col = selectedfocusedValueColor;
        if (redPotentials != null) {
            BitSet redValues = redPotentials.get(cell);
            if (redValues != null && redValues.get(value)) {
                col = redPotentialColor;
                isHighlighted = true;
                isRed = true;
            }
        }
        if (bluePotentials != null) {
            BitSet blueValues = bluePotentials.get(cell);
            if (blueValues != null && blueValues.get(value)) {
                col = bluePotentialColor;
                isHighlighted = true;
            }
        }
        if (greenPotentials != null) {
            BitSet greenValues = greenPotentials.get(cell);
            if (greenValues != null && greenValues.get(value)) {
                if (isRed) {
                    col = orangePotentialColor;
                } else {
                  if ( !isHighlighted ) {
                    col = greenPotentialColor;
                    isHighlighted = true;
                  }
                }
            }
        }
        g.setColor(col);
        return isHighlighted;
    }

    private void initFillColor(Graphics g, Cell cell) {
        Color col = backgroundColor;
        if (grid.isDisjointGroups())
            col = DG_Colors[cell.getY()%3*3 + cell.getX()%3];
        if (grid.isWindoku() && !grid.isDisjointGroups()) {
            Grid.Windoku windoku = grid.getWindokuAt(cell.getX(),cell.getY());
            int wdi = windoku.getWindokuNum();
            if ( wdi == 0 || wdi == 1 || wdi == 2 || wdi == 3) {
                col = altBackgroundColor;
            }
        }
        if (grid.isAsterisk() && grid.getAsteriskAt(cell.getX(),cell.getY())!=null)
            col = starColor;
        if (grid.isCenterDot() && grid.getCenterDotAt(cell.getX(),cell.getY())!=null)
            col = starColor;
        if (grid.isGirandola() && grid.getGirandolaAt(cell.getX(),cell.getY())!=null)
            col = starColor;
        if (grid.isHalloween() && grid.getHalloweenAt(cell.getX(),cell.getY())!=null)
            col = halloweenColor;
        if (grid.isPerCent() && grid.getPerCentAt(cell.getX(),cell.getY())!=null)
            col = percentColor;
        if (grid.isSdoku() && grid.getSdokuBandAt(cell.getX(),cell.getY())!=null)
            col = sdokuColor;
        if (grid.isSdoku() && grid.getSdokuStackAt(cell.getX(),cell.getY())!=null)
            col = sdokuColor;
        if (grid.isCustom() && grid.getCustomAt(cell.getX(),cell.getY())!=null)
            col = customColor;
        if (redCells != null && redCells.contains(cell))
            col = redCellColor;
        else if (greenCells != null && greenCells.contains(cell))
            col = greenCellColor;
        else if (cell == selectedCell)
            col = selectedColor;
        else if (cell == focusedCell)
            col = focusedColor;
        else {
            boolean showing = Settings.getInstance().isShowingCandidateMasks();
            if ( showing == true ) {
                // Selected candidates color
                int value = -1;
                if (null != selectedCell) {
                    value = selectedCell.getValue();
                }
                if ( value > 0 ) {
                    if ( value == cell.getValue()) {
                        col = candidateMaskColor;
                    }
                    else
                    if ( cell.hasPotentialValue(value)) {
                        col = potentialMaskColor;
                    }
                }
            }
        }
        g.setColor(col);
    }

    private void init2FillColor(Graphics g, Cell cell) {
        Color col = backgroundColor;
        if (grid.isDisjointGroups())
            col = DG_Colors[cell.getY()%3*3 + cell.getX()%3];
        if (grid.isWindoku() && !grid.isDisjointGroups()) {
            Grid.Windoku windoku = grid.getWindokuAt(cell.getX(),cell.getY());
            int wdi = windoku.getWindokuNum();
            if ( wdi == 0 || wdi == 1 || wdi == 2 || wdi == 3) {
                col = altBackgroundColor;
            }
        }
        if (grid.isAsterisk() && grid.getAsteriskAt(cell.getX(),cell.getY())!=null)
            col = starColor;
        if (grid.isCenterDot() && grid.getCenterDotAt(cell.getX(),cell.getY())!=null)
            col = starColor;
        if (grid.isGirandola() && grid.getGirandolaAt(cell.getX(),cell.getY())!=null)
            col = starColor;
        if (grid.isHalloween() && grid.getHalloweenAt(cell.getX(),cell.getY())!=null)
            col = halloweenColor;
        if (grid.isPerCent() && grid.getPerCentAt(cell.getX(),cell.getY())!=null)
            col = percentColor;
        if (grid.isSdoku() && grid.getSdokuBandAt(cell.getX(),cell.getY())!=null)
            col = sdokuColor;
        if (grid.isSdoku() && grid.getSdokuStackAt(cell.getX(),cell.getY())!=null)
            col = sdokuColor;
        if (grid.isCustom() && grid.getCustomAt(cell.getX(),cell.getY())!=null)
            col = customColor;
        if (redCells != null && redCells.contains(cell))
            col = redCellColor;
        else if (greenCells != null && greenCells.contains(cell))
            col = greenCellColor;
    //  else if (cell == selectedCell)
    //      col = selectedColor;
    //  else if (cell == focusedCell)
    //      col = focusedColor;
        else {
            boolean showing = Settings.getInstance().isShowingCandidateMasks();
            if ( showing == true ) {
                // Selected candidates color
                int value = -1;
                if (null != selectedCell) {
                    value = selectedCell.getValue();
                }
                if ( value > 0 ) {
                    if ( value == cell.getValue()) {
                        col = candidateMaskColor;
                    }
                    else
                    if ( cell.hasPotentialValue(value)) {
                        col = potentialMaskColor;
                    }
                }
            }
        }
        g.setColor(col);
    }

    private void initValueColor(Graphics g, Cell cell) {
        g.setColor(cell.isGiven()?givenValueColor:solvedValueColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        initGraphics(g2);
        paintLegend(g);
        AffineTransform oldTransform = g2.getTransform();
        AffineTransform translate =
            AffineTransform.getTranslateInstance(LEGEND_GAP_SIZE, GRID_GAP_SIZE);
        g2.transform(translate);
        g.clearRect(0, 0, GRID_SIZE, GRID_SIZE);
        paintSelectionAndFocus(g);
        paintGrid(g);
        paintHighlightedRegions(g);
        paintCellsValues(g);
        paintLinks(g);
        paintCellsPotentials(g);
        paintCursor();
        g2.setTransform(oldTransform);
    }

    public void saveAsImage(File file) {
      try {
        BufferedImage bi = new BufferedImage(GRID_SIZE+4, GRID_SIZE+4, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        Graphics2D g2 = (Graphics2D)g;
        initGraphics(g2);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, GRID_SIZE+4, GRID_SIZE+4);
        paint2SelectionAndFocus(g, 2);
        paint2Grid(g, 2);
        paint2HighlightedRegions(g, 2);
        paint2CellsValues(g, 2);
        paint2Links(g, 2);
        paint2CellsPotentials(g, 2);
        ImageIO.write(bi, "PNG", file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Set the given rectangle to match the rectangle occupied by a cell.
     * Only valid if the grpahics context is translated so that the
     * origin matches the upper left corner of the grid.
     * @param x the horizontal coordinate of the cell within the grid (0..8)
     * @param y the vertical coordinate of the cell within the grid (0..8)
     * @param target set to the rectangle occupied by the cell, in pixels
     */
    private void readCellRectangle(int x, int y, Rectangle target) {
        target.x = x * CELL_OUTER_SIZE;
        target.y = y * CELL_OUTER_SIZE;
        target.width = CELL_OUTER_SIZE;
        target.height = CELL_OUTER_SIZE;
    }

    private void initGraphics(Graphics2D g2) {
        if (Settings.getInstance().isAntialiasing()) {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        }
    }

    private void paintLegend(Graphics g) {
        g.setFont(legendFont);
        g.setColor(legendColor);
        Settings settings = Settings.getInstance();
        for (int i = 0; i < 9; i++) {
            String yLegend;
            if (settings.isRCNotation())
                yLegend = "R" + (i + 1);
            else
                yLegend = Integer.toString(i + 1);
            String xLegend;
            if (settings.isRCNotation())
                xLegend = "C" + (i + 1);
            else
                xLegend = Character.toString((char)('A' + i));
            if (engine.isValueAllGiven(grid, i+1)) {
                g.setColor(completedColor);
                drawStringCentered(g, yLegend,
                        LEGEND_GAP_SIZE / 2,
                        CELL_OUTER_SIZE * i + GRID_GAP_SIZE + CELL_OUTER_SIZE / 2);
                g.setColor(shadowColor);
                drawStringCentered(g, yLegend,
                        LEGEND_GAP_SIZE / 2 + 1,
                        CELL_OUTER_SIZE * i + GRID_GAP_SIZE + CELL_OUTER_SIZE / 2 + 1);
            } else {
                g.setColor(legendColor);
                drawStringCentered(g, yLegend,
                        LEGEND_GAP_SIZE / 2,
                        CELL_OUTER_SIZE * i + GRID_GAP_SIZE + CELL_OUTER_SIZE / 2);
            }
            g.setColor(legendColor);
            drawStringCentered(g, xLegend,
                    LEGEND_GAP_SIZE + i * CELL_OUTER_SIZE + CELL_OUTER_SIZE / 2,
                    CELL_OUTER_SIZE * 9 + GRID_GAP_SIZE + LEGEND_GAP_SIZE / 2);
        }
    }

    private void paintSelectionAndFocus(Graphics g) {
        Rectangle clip = g.getClipBounds();
        Rectangle cellRect = new Rectangle();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                readCellRectangle(x, y, cellRect);
                if (clip.intersects(cellRect)) {
                    Cell cell = grid.getCell(x, y);
                    initFillColor(g, cell);
                    g.fillRect(x * CELL_OUTER_SIZE, y * CELL_OUTER_SIZE, CELL_OUTER_SIZE, CELL_OUTER_SIZE);
                }
            }
        }
    }

    private void paint2SelectionAndFocus(Graphics g, int adj) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                    Cell cell = grid.getCell(x, y);
                    init2FillColor(g, cell);
                    g.fillRect(x * CELL_OUTER_SIZE+adj, y * CELL_OUTER_SIZE+adj, CELL_OUTER_SIZE, CELL_OUTER_SIZE);
            }
        }
    }

    private void paintGrid(Graphics g) {
        for (int i = 0; i <= 9; i++) {
            int lineWidth, offset;
            if (i % 9 == 0 || (i % 3 == 0  && !grid.isLatinSquare())) {
                lineWidth = 4;
                g.setColor(borderColor);
            } else {
                lineWidth = 2;
                g.setColor(borderColor);
            }
            offset = lineWidth / 2;
            g.fillRect(i * CELL_OUTER_SIZE - offset, 0 - offset, lineWidth, GRID_SIZE + lineWidth);
            g.fillRect(0 - offset, i * CELL_OUTER_SIZE - offset, GRID_SIZE + lineWidth, lineWidth);
        }
        if (grid.isDiagonals()) {
            g.setColor(borderColor);
            g.drawLine( CELL_OUTER_SIZE / 6, CELL_OUTER_SIZE / 6, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6);
            g.drawLine( CELL_OUTER_SIZE / 6, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6, CELL_OUTER_SIZE / 6);
        }
        if (grid.isWindoku()) {
            int lineWidth = 4;
            int offset = lineWidth / 2;
            g.setColor(borderColor);
            for (int i = 0; i < 4; i++) {
                g.fillRect( ( ( i / 2 ) * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset, (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset, lineWidth, GRID_SIZE / 3 + lineWidth);
                g.fillRect( ( ( i / 2 ) * 4 + 3 + 1 ) * CELL_OUTER_SIZE - offset, (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset, lineWidth, GRID_SIZE / 3 + lineWidth);
                g.fillRect( (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset, ( ( i / 2 ) * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset, GRID_SIZE / 3 + lineWidth, lineWidth);
                g.fillRect( (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset, ( ( i / 2 ) * 4 + 3 + 1 ) * CELL_OUTER_SIZE - offset, GRID_SIZE / 3 + lineWidth, lineWidth);
            }
        }
    }

    private void paint2Grid(Graphics g, int adj) {
        for (int i = 0; i <= 9; i++) {
            int lineWidth, offset;
            if (i % 9 == 0 || (i % 3 == 0  && !grid.isLatinSquare())) {
                lineWidth = 4;
                g.setColor(borderColor);
            } else {
                lineWidth = 2;
                g.setColor(borderColor);
            }
            offset = lineWidth / 2;
            g.fillRect(i * CELL_OUTER_SIZE - offset+adj, 0 - offset+adj, lineWidth, GRID_SIZE + lineWidth);
            g.fillRect(0 - offset+adj, i * CELL_OUTER_SIZE - offset+adj, GRID_SIZE + lineWidth, lineWidth);
        }
        if (grid.isDiagonals()) {
            g.setColor(borderColor);
            g.drawLine( CELL_OUTER_SIZE / 6+adj, CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj);
            g.drawLine( CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj, CELL_OUTER_SIZE / 6+adj);
        }
        if (grid.isWindoku()) {
            int lineWidth = 4;
            int offset = lineWidth / 2;
            g.setColor(borderColor);
            for (int i = 0; i < 4; i++) {
                g.fillRect( ( ( i / 2 ) * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset+adj, (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset+adj, lineWidth, GRID_SIZE / 3 + lineWidth);
                g.fillRect( ( ( i / 2 ) * 4 + 3 + 1 ) * CELL_OUTER_SIZE - offset+adj, (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset+adj, lineWidth, GRID_SIZE / 3 + lineWidth);
                g.fillRect( (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset+adj, ( ( i / 2 ) * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset+adj, GRID_SIZE / 3 + lineWidth, lineWidth);
                g.fillRect( (i % 2 * 4 + 0 + 1 ) * CELL_OUTER_SIZE - offset+adj, ( ( i / 2 ) * 4 + 3 + 1 ) * CELL_OUTER_SIZE - offset+adj, GRID_SIZE / 3 + lineWidth, lineWidth);
            }
        }
    }

    private void paintHighlightedRegions(Graphics g) {
        if (blueRegions != null) {
            Color[] colors = new Color[] {hintBlueRegionColor, hintGreenRegionColor};
            for (int rev = 0; rev < 2; rev++) {
                for (int i = 0; i < blueRegions.length; i++) {
                    int index = (rev == 0 ? i : blueRegions.length - 1 - i);
                    Grid.Region region = blueRegions[index];
                    int x, y, w, h; // coordinates, width, height (in cells)
                    if (region != null) {
                        if (region instanceof Grid.Row || region instanceof Grid.Column || region instanceof Grid.Block) {
                            if (region instanceof Grid.Row) {
                                Grid.Row row = (Grid.Row)region;
                                x = 0;
                                y = row.getRowNum();
                                w = 9;
                                h = 1;
                            } else if (region instanceof Grid.Column) {
                                Grid.Column column = (Grid.Column)region;
                                x = column.getColumnNum();
                                y = 0;
                                w = 1;
                                h = 9;
                            } else {
                                Grid.Block square = (Grid.Block)region;
                                x = square.getHIndex() * 3;
                                y = square.getVIndex() * 3;
                                w = 3;
                                h = 3;
                            }
                            g.setColor(colors[index % 2]);
                            for (int s = -2 + rev; s <= 2; s+= 2) {
                                g.drawRect(x * CELL_OUTER_SIZE + s, y * CELL_OUTER_SIZE + s,
                                        w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                            }
                            if (rev == 0) {
                                Color base = colors[index % 2];
                                g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                        w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                            }
                        }
                        else {
                          if (region instanceof Grid.Windoku) {
                            Grid.Windoku windoku = (Grid.Windoku)region;
                            int wdi = windoku.getWindokuNum();
                            int js = 0, jend = 1, jinc = 1; w = h = 3;
                            if ( wdi == 0 || wdi == 1 || wdi == 2 || wdi == 3) { w = 3; h = 3; }
                            if ( wdi == 4 || wdi == 5) { w = 3; h = 1; jend = 9; jinc = 3; }
                            if ( wdi == 6 || wdi == 7) { w = 1; h = 3; jend = 3; }
                            if ( wdi == 8) { w = 1; h = 1; jend = 9; }
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = windoku.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                for (int s = -2 + rev; s <= 2; s+= 2) {
                                    g.drawRect(x * CELL_OUTER_SIZE + s, y * CELL_OUTER_SIZE + s,
                                            w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.Halloween) {
                            Grid.Halloween halloween = (Grid.Halloween)region;
                            int hwi = halloween.getHalloweenNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = halloween.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ((hwi==0 && (j==0 || j==2 || j==3 || j==7))
                                  ||(hwi==1 && (j==0 || j==1 || j==2 || j==7))
                                  ||(hwi==2 && (j==0 || j==1 || j==2 || j==3 || j==4 || j==6 || j==8)))
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ((hwi==0 && (j==0 || j==1 || j==4 || j==5 || j==6))
                                  ||(hwi==1 && (j==0 || j==1 || j==4 || j==5 || j==6))
                                  ||(hwi==2 && (j==0 || j==1 || j==2 || j==3 || j==4)))
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ((hwi==0 && (j==0 || j==3 || j==4 || j==5 || j==8))
                                  ||(hwi==1 && (j==0 || j==3 || j==4 || j==5 || j==8))
                                  ||(hwi==2 && (j==0 || j==1 || j==2 || j==3 || j==8)))
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ((hwi==0 && (j==2 || j==6 || j==7 || j==8))
                                  ||(hwi==1 && (j==2 || j==6 || j==7 || j==8))
                                  ||(hwi==2 && (j==0 || j==3 || j==4 || j==5 || j==6 || j==7 || j==8)))
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, (y+1) * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.PerCent) {
                            Grid.PerCent percent = (Grid.PerCent)region;
                            int pci = percent.getPerCentNum();
                            int js = 0, jend = 1, jinc = 1; w = h = 3;
                            if ( pci == 0 || pci == 2 ) { w = 3; h = 3; }
                            if ( pci == 1) { w = 1; h = 1; jend = 9; }
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = percent.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                for (int s = -2 + rev; s <= 2; s+= 2) {
                                    g.drawRect(x * CELL_OUTER_SIZE + s, y * CELL_OUTER_SIZE + s,
                                            w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.SdokuBand) {
                            Grid.SdokuBand sdoku = (Grid.SdokuBand)region;
                            int cti = sdoku.getSdokuBandNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = sdoku.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ( y==0 || (y!=0 && cti != sdoku.At(x,y-1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ( x==0 || (x!=0 && cti != sdoku.At(x-1,y)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( x==8 || (x!=8 && cti != sdoku.At(x+1,y)) )
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( y==8 || (y!=8 && cti != sdoku.At(x,y+1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, (y+1) * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.SdokuStack) {
                            Grid.SdokuStack sdoku = (Grid.SdokuStack)region;
                            int cti = sdoku.getSdokuStackNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = sdoku.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ( y==0 || (y!=0 && cti != sdoku.At(x,y-1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ( x==0 || (x!=0 && cti != sdoku.At(x-1,y)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( x==8 || (x!=8 && cti != sdoku.At(x+1,y)) )
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( y==8 || (y!=8 && cti != sdoku.At(x,y+1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, (y+1) * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.Custom) {
                            Grid.Custom custom = (Grid.Custom)region;
                            int cti = custom.getCustomNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = custom.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ( y==0 || (y!=0 && cti != custom.At(x,y-1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ( x==0 || (x!=0 && cti != custom.At(x-1,y)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( x==8 || (x!=8 && cti != custom.At(x+1,y)) )
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset, y * CELL_OUTER_SIZE - offset, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( y==8 || (y!=8 && cti != custom.At(x,y+1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset, (y+1) * CELL_OUTER_SIZE - offset, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else {
                            for (int j = 0; j < 9 ; j++) {
                                Cell cell = region.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                w = 1;
                                h = 1;
                                g.setColor(colors[index % 2]);
                                for (int s = -2 + rev; s <= 2; s+= 2) {
                                    g.drawRect(x * CELL_OUTER_SIZE + s, y * CELL_OUTER_SIZE + s,
                                            w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3, y * CELL_OUTER_SIZE + 3,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          }
                        }
                        index++;
                    }
                }
            }
        }
    }

    private void paint2HighlightedRegions(Graphics g, int adj) {
        if (blueRegions != null) {
            Color[] colors = new Color[] {hintBlueRegionColor, hintGreenRegionColor};
            for (int rev = 0; rev < 2; rev++) {
                for (int i = 0; i < blueRegions.length; i++) {
                    int index = (rev == 0 ? i : blueRegions.length - 1 - i);
                    Grid.Region region = blueRegions[index];
                    int x, y, w, h; // coordinates, width, height (in cells)
                    if (region != null) {
                        if (region instanceof Grid.Row || region instanceof Grid.Column || region instanceof Grid.Block) {
                            if (region instanceof Grid.Row) {
                                Grid.Row row = (Grid.Row)region;
                                x = 0;
                                y = row.getRowNum();
                                w = 9;
                                h = 1;
                            } else if (region instanceof Grid.Column) {
                                Grid.Column column = (Grid.Column)region;
                                x = column.getColumnNum();
                                y = 0;
                                w = 1;
                                h = 9;
                            } else {
                                Grid.Block square = (Grid.Block)region;
                                x = square.getHIndex() * 3;
                                y = square.getVIndex() * 3;
                                w = 3;
                                h = 3;
                            }
                            g.setColor(colors[index % 2]);
                            for (int s = -2 + rev; s <= 2; s+= 2) {
                                g.drawRect(x * CELL_OUTER_SIZE + s+adj, y * CELL_OUTER_SIZE + s+adj,
                                        w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                            }
                            if (rev == 0) {
                                Color base = colors[index % 2];
                                g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                        w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                            }
                        }
                        else {
                          if (region instanceof Grid.Windoku) {
                            Grid.Windoku windoku = (Grid.Windoku)region;
                            int wdi = windoku.getWindokuNum();
                            int js = 0, jend = 1, jinc = 1; w = h = 3;
                            if ( wdi == 0 || wdi == 1 || wdi == 2 || wdi == 3) { w = 3; h = 3; }
                            if ( wdi == 4 || wdi == 5) { w = 3; h = 1; jend = 9; jinc = 3; }
                            if ( wdi == 6 || wdi == 7) { w = 1; h = 3; jend = 3; }
                            if ( wdi == 8) { w = 1; h = 1; jend = 9; }
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = windoku.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                for (int s = -2 + rev; s <= 2; s+= 2) {
                                    g.drawRect(x * CELL_OUTER_SIZE + s+adj, y * CELL_OUTER_SIZE + s+adj,
                                            w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.Halloween) {
                            Grid.Halloween halloween = (Grid.Halloween)region;
                            int hwi = halloween.getHalloweenNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = halloween.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ((hwi==0 && (j==0 || j==2 || j==3 || j==7))
                                  ||(hwi==1 && (j==0 || j==1 || j==2 || j==7))
                                  ||(hwi==2 && (j==0 || j==1 || j==2 || j==3 || j==4 || j==6 || j==8)))
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ((hwi==0 && (j==0 || j==1 || j==4 || j==5 || j==6))
                                  ||(hwi==1 && (j==0 || j==1 || j==4 || j==5 || j==6))
                                  ||(hwi==2 && (j==0 || j==1 || j==2 || j==3 || j==4)))
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ((hwi==0 && (j==0 || j==3 || j==4 || j==5 || j==8))
                                  ||(hwi==1 && (j==0 || j==3 || j==4 || j==5 || j==8))
                                  ||(hwi==2 && (j==0 || j==1 || j==2 || j==3 || j==8)))
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ((hwi==0 && (j==2 || j==6 || j==7 || j==8))
                                  ||(hwi==1 && (j==2 || j==6 || j==7 || j==8))
                                  ||(hwi==2 && (j==0 || j==3 || j==4 || j==5 || j==6 || j==7 || j==8)))
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, (y+1) * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.PerCent) {
                            Grid.PerCent percent = (Grid.PerCent)region;
                            int pci = percent.getPerCentNum();
                            int js = 0, jend = 1, jinc = 1; w = h = 3;
                            if ( pci == 0 || pci == 2 ) { w = 3; h = 3; }
                            if ( pci == 1) { w = 1; h = 1; jend = 9; }
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = percent.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                for (int s = -2 + rev; s <= 2; s+= 2) {
                                    g.drawRect(x * CELL_OUTER_SIZE + s+adj, y * CELL_OUTER_SIZE + s+adj,
                                            w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.SdokuBand) {
                            Grid.SdokuBand sdoku = (Grid.SdokuBand)region;
                            int cti = sdoku.getSdokuBandNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = sdoku.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ( y==0 || (y!=0 && cti != sdoku.At(x,y-1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ( x==0 || (x!=0 && cti != sdoku.At(x-1,y)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( x==8 || (x!=8 && cti != sdoku.At(x+1,y)) )
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( y==8 || (y!=8 && cti != sdoku.At(x,y+1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, (y+1) * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.SdokuStack) {
                            Grid.SdokuStack sdoku = (Grid.SdokuStack)region;
                            int cti = sdoku.getSdokuStackNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = sdoku.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ( y==0 || (y!=0 && cti != sdoku.At(x,y-1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ( x==0 || (x!=0 && cti != sdoku.At(x-1,y)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( x==8 || (x!=8 && cti != sdoku.At(x+1,y)) )
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( y==8 || (y!=8 && cti != sdoku.At(x,y+1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, (y+1) * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else
                          if (region instanceof Grid.Custom) {
                            Grid.Custom custom = (Grid.Custom)region;
                            int cti = custom.getCustomNum();
                            int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                            int lineWidth = 4;
                            int offset = lineWidth / 2;
                            for (int j = js; j < jend ; j+=jinc) {
                                Cell cell = custom.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                g.setColor(colors[index % 2]);
                                if ( y==0 || (y!=0 && cti != custom.At(x,y-1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if ( x==0 || (x!=0 && cti != custom.At(x-1,y)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( x==8 || (x!=8 && cti != custom.At(x+1,y)) )
                                {
                                    g.fillRect((x+1) * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                                }
                                if ( y==8 || (y!=8 && cti != custom.At(x,y+1)) )
                                {
                                    g.fillRect(x * CELL_OUTER_SIZE - offset+adj, (y+1) * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          } else {
                            for (int j = 0; j < 9 ; j++) {
                                Cell cell = region.getCell( j);
                                x = cell.getX();
                                y = cell.getY();
                                w = 1;
                                h = 1;
                                g.setColor(colors[index % 2]);
                                for (int s = -2 + rev; s <= 2; s+= 2) {
                                    g.drawRect(x * CELL_OUTER_SIZE + s+adj, y * CELL_OUTER_SIZE + s+adj,
                                            w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                                }
                                if (rev == 0) {
                                    Color base = colors[index % 2];
                                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                    g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                            w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                                }
                            }
                          }
                        }
                        index++;
                    }
                }
            }
        }
    }

    private void paintCellsPotentials(Graphics g) {
        Rectangle clip = g.getClipBounds();
        Rectangle cellRect = new Rectangle();
        boolean paintIt = Settings.getInstance().isShowingCandidates();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                readCellRectangle(x, y, cellRect);
                if (clip.intersects(cellRect)) {
                    Cell cell = grid.getCell(x, y);
                    // Paint potentials
                    int index = 0;
                    g.setFont(smallFont);
                    for (int value = 1; value <= 9; value++) {
                        if (cell == this.selectedCell && value == this.focusedCandidate) {
                            // Paint magenta selection
                            g.setColor(selectedfocusedColor);
                            g.fillRect(
                                    x * CELL_OUTER_SIZE + CELL_PAD + (index % 3) * (CELL_INNER_SIZE / 3),
                                    y * CELL_OUTER_SIZE + CELL_PAD + (index / 3) * (CELL_INNER_SIZE / 3),
                                    CELL_INNER_SIZE / 3, CELL_INNER_SIZE / 3);
                            paintIt = true;
                        }
                        if (cell.hasPotentialValue(value)) {
                            int cx = x * CELL_OUTER_SIZE + CELL_PAD
                            + (index % 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            int cy = y * CELL_OUTER_SIZE + CELL_PAD
                            + (index / 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            boolean isHighlighted = initPotentialColor(g, cell, value);
                            if (isHighlighted)
                                drawStringCentered3D(g, "" + value, cx, cy);
                            else if (paintIt)
                                drawStringCentered(g, "" + value, cx, cy);
                        }
                        index++;
                    }
                }
            }
        }
    }

    private void paint2CellsPotentials(Graphics g, int adj) {
      if (Settings.getInstance().isShowingCandidates()) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                    Cell cell = grid.getCell(x, y);
                    // Paint potentials
                    int index = 0;
                    g.setFont(smallFont);
                    for (int value = 1; value <= 9; value++) {
                        if (cell.hasPotentialValue(value)) {
                            int cx = x * CELL_OUTER_SIZE + CELL_PAD
                            + (index % 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            int cy = y * CELL_OUTER_SIZE + CELL_PAD
                            + (index / 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            init2PotentialColor(g, cell, value);
                            drawStringCentered(g, "" + value, cx+adj, cy+adj);
                        }
                        index++;
                    }
            }
        }
      }
    }

    private void paintCellsValues(Graphics g) {
        Rectangle clip = g.getClipBounds();
        Rectangle cellRect = new Rectangle();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                readCellRectangle(x, y, cellRect);
                if (clip.intersects(cellRect)) {
                    Cell cell = grid.getCell(x, y);
                    // Paint cell value
                    if (cell.getValue() != 0) {
                        g.setFont(bigFont);
                        int cx = x * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        int cy = y * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        initValueColor(g, cell);
                        drawStringCentered(g, "" + cell.getValue(), cx, cy);
                    }
                }
            }
        }
    }

    private void paint2CellsValues(Graphics g, int adj) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                    Cell cell = grid.getCell(x, y);
                    // Paint cell value
                    if (cell.getValue() != 0) {
                        g.setFont(bigFont);
                        int cx = x * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        int cy = y * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        initValueColor(g, cell);
                        drawStringCentered(g, "" + cell.getValue(), cx+adj, cy+adj);
                    }
            }
        }
    }

    private class Line {

        public final int sx;
        public final int sy;
        public final int ex;
        public final int ey;

        public Line(int sx, int sy, int ex, int ey) {
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
        }

        private int distanceUnscaled(int px, int py) {
            // Vectorial product, without normalization by length
            return (px - sx) * (ey - sy) - (py - sy) * (ex - sx);
        }

        private boolean intervalOverlaps(int s1, int e1, int s2, int e2) {
            if (s1 > e1) {
                // Swap
                s1 = s1 ^ e1;
                e1 = s1 ^ e1;
                s1 = s1 ^ e1;
            }
            if (s2 > e2) {
                // Swap
                s2 = s2 ^ e2;
                e2 = s2 ^ e2;
                s2 = s2 ^ e2;
            }
            return s1 < e2 && e1 > s2;
        }

        public boolean overlaps(Line other) {
            if (distanceUnscaled(other.sx, other.sy) == 0 &&
                    distanceUnscaled(other.ex, other.ey) == 0) {
                // Both lines are on the same right
                return intervalOverlaps(this.sx, this.ex, other.sx, other.ex)
                || intervalOverlaps(this.sy, this.ey, other.sy, other.ey);
            }
            return false;
        }

    }

    private void paintLinks(Graphics g) {
        g.setColor(linkColor);
        if (links != null) {
            Collection<Line> paintedLines = new ArrayList<Line>();
            for (Link link : links) {
                double sx = link.getSrcCell().getX() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                double sy = link.getSrcCell().getY() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                int srcValue = link.getSrcValue();
                if (srcValue > 0) {
                    sx += ((srcValue - 1) % 3) * (CELL_INNER_SIZE / 3);
                    sy += ((srcValue - 1) / 3) * (CELL_INNER_SIZE / 3);
                } else {
                    sx += CELL_INNER_SIZE / 3;
                    sy += CELL_INNER_SIZE / 3;
                }
                double ex = link.getDstCell().getX() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                double ey = link.getDstCell().getY() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                int dstValue = link.getDstValue();
                if (dstValue > 0) {
                    ex += ((dstValue - 1) % 3) * (CELL_INNER_SIZE / 3);
                    ey += ((dstValue - 1) / 3) * (CELL_INNER_SIZE / 3);
                } else {
                    ex += CELL_INNER_SIZE / 3;
                    ey += CELL_INNER_SIZE / 3;
                }
                // Get unity vector
                double length = Math.sqrt((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy));
                double ux = (ex - sx) / length;
                double uy = (ey - sy) / length;
                // Build line object
                Line line = new Line((int)sx, (int)sy, (int)ex, (int)ey);
                // Count number of overlapping lines
                int countOverlap = 0;
                for (Line other : paintedLines) {
                    if (line.overlaps(other))
                        countOverlap++;
                }
                // Move the line perpendicularly to go away from overlapping lines
                double mx = (uy * ((countOverlap + 1) / 2) * 3.0);
                double my = (ux * ((countOverlap + 1) / 2) * 3.0);
                if (countOverlap % 2 == 0)
                    mx = -mx;
                else
                    my = -my;
                if (length >= CELL_INNER_SIZE / 2) {
                    // Truncate end points
                    if (srcValue > 0) {
                        sx += ux * CELL_INNER_SIZE / 6;
                        sy += uy * CELL_INNER_SIZE / 6;
                    }
                    if (dstValue > 0) {
                        ex -= ux * CELL_INNER_SIZE / 6;
                        ey -= uy * CELL_INNER_SIZE / 6;
                    }
                    if (dstValue > 0) {
                        // Draw arrow
                        double lx = ex - ux * 10 + uy * 4;
                        double ly = ey - uy * 10 - ux * 4;
                        double rx = ex - ux * 10 - uy * 4;
                        double ry = ey - uy * 10 + ux * 4;
                        g.fillPolygon(new int[] {(int)(ex + mx), (int)(rx + mx), (int)(lx + mx)},
                                      new int[] {(int)(ey + my), (int)(ry + my), (int)(ly + my)}, 3);
                    }
                    paintedLines.add(line);
                }
                // Draw the line
                g.drawLine((int)(sx + mx), (int)(sy + my), (int)(ex + mx), (int)(ey + my));
            }
        }
    }

    private void paint2Links(Graphics g, int adj) {
        g.setColor(linkColor);
        if (links != null) {
            Collection<Line> paintedLines = new ArrayList<Line>();
            for (Link link : links) {
                double sx = link.getSrcCell().getX() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                double sy = link.getSrcCell().getY() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                int srcValue = link.getSrcValue();
                if (srcValue > 0) {
                    sx += ((srcValue - 1) % 3) * (CELL_INNER_SIZE / 3);
                    sy += ((srcValue - 1) / 3) * (CELL_INNER_SIZE / 3);
                } else {
                    sx += CELL_INNER_SIZE / 3;
                    sy += CELL_INNER_SIZE / 3;
                }
                double ex = link.getDstCell().getX() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                double ey = link.getDstCell().getY() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                int dstValue = link.getDstValue();
                if (dstValue > 0) {
                    ex += ((dstValue - 1) % 3) * (CELL_INNER_SIZE / 3);
                    ey += ((dstValue - 1) / 3) * (CELL_INNER_SIZE / 3);
                } else {
                    ex += CELL_INNER_SIZE / 3;
                    ey += CELL_INNER_SIZE / 3;
                }
                // Get unity vector
                double length = Math.sqrt((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy));
                double ux = (ex - sx) / length;
                double uy = (ey - sy) / length;
                // Build line object
                Line line = new Line((int)sx+adj, (int)sy+adj, (int)ex+adj, (int)ey+adj);
                // Count number of overlapping lines
                int countOverlap = 0;
                for (Line other : paintedLines) {
                    if (line.overlaps(other))
                        countOverlap++;
                }
                // Move the line perpendicularly to go away from overlapping lines
                double mx = (uy * ((countOverlap + 1) / 2) * 3.0);
                double my = (ux * ((countOverlap + 1) / 2) * 3.0);
                if (countOverlap % 2 == 0)
                    mx = -mx;
                else
                    my = -my;
                if (length >= CELL_INNER_SIZE / 2) {
                    // Truncate end points
                    if (srcValue > 0) {
                        sx += ux * CELL_INNER_SIZE / 6;
                        sy += uy * CELL_INNER_SIZE / 6;
                    }
                    if (dstValue > 0) {
                        ex -= ux * CELL_INNER_SIZE / 6;
                        ey -= uy * CELL_INNER_SIZE / 6;
                    }
                    if (dstValue > 0) {
                        // Draw arrow
                        double lx = ex - ux * 10 + uy * 4;
                        double ly = ey - uy * 10 - ux * 4;
                        double rx = ex - ux * 10 - uy * 4;
                        double ry = ey - uy * 10 + ux * 4;
                        g.fillPolygon(new int[] {(int)(ex + mx)+adj, (int)(rx + mx)+adj, (int)(lx + mx)+adj},
                                      new int[] {(int)(ey + my)+adj, (int)(ry + my)+adj, (int)(ly + my)+adj}, 3);
                    }
                    paintedLines.add(line);
                }
                // Draw the line
                g.drawLine((int)(sx + mx)+adj, (int)(sy + my)+adj, (int)(ex + mx)+adj, (int)(ey + my)+adj);
            }
        }
    }

    private void paintCursor() {
//  if (selectedCell == focusedCell)
//      this.setCursor(new Cursor(Cursor.TEXT_CURSOR));
//  else
//      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

}
