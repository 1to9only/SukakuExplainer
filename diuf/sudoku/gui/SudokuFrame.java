/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.gui;

import java.security.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.UIManager.*;
import javax.swing.tree.*;

import diuf.sudoku.*;
import static diuf.sudoku.Settings.*;
import diuf.sudoku.solver.*;
import diuf.sudoku.solver.checks.*;
import diuf.sudoku.tools.*;
import javax.swing.ImageIcon;

/**
 * The main window of the application.
 * All the action are redirected to the {@link SudokuExplainer} class.
 */
public class SudokuFrame extends JFrame implements Asker {

    private static final long serialVersionUID = 8247189707924329043L;

    private SudokuExplainer engine;
    private Hint currentHint = null;
    private int viewCount = 1;
    private int viewNum = 0;

    private GenerateDialog generateDialog = null;
    private TechniquesSelectDialog selectDialog = null;

    private JFrame dummyFrameKnife = null;
    private JPanel jContentPane = null;
    private SudokuPanel sudokuPanel = null;
    private JScrollPane hintDetailsPane = null;
    private JTree hintsTree = null;
    private JEditorPane hintDetailArea = null;
    private JPanel jPanel = null;
    private JPanel sudokuContainer = null;
    private JPanel hintDetailContainer = null;
    private JPanel buttonsPane = null;
    private JButton btnGetAllHints = null;
    private JButton btnUndoStep = null;
    private JButton btnApplyHintAndGet = null;
    private JButton btnQuit = null;
    private JPanel buttonsContainer = null;
    private JScrollPane hintsTreeScrollpane = null;
    private JButton btnGetNextHint = null;
    private JPanel viewSelectionPanel = null;
    private JPanel hintsTreePanel = null;
    private JCheckBox chkFilter = null;
    private JButton btnCheckValidity = null;
    private JButton btnApplySingles = null;
    private JButton btnApplyHint = null;
    private JComboBox<String> cmbViewSelector = null;
    private JPanel hintsSouthPanel = null;
    private JPanel ratingPanel = null;
    private JLabel jLabel = null;
    private JLabel lblRating = null;
    private JLabel jLabel2 = null;
    private JMenuBar jJMenuBar = null;
    private JMenu fileMenu = null;
    private JMenuItem mitNew = null;
    private JMenuItem mitRestart = null;
    private JMenuItem mitQuit = null;
    private JMenuItem mitLoad = null;
    private JMenuItem mitSave81 = null;
    private JMenuItem mitSave = null;
    private JMenuItem mitSaveSukaku = null;
    private JMenuItem mitSavePencilMarks = null;
    private JMenuItem mitSaveAsImage = null;
    private JMenuItem mitAddNote = null;
    private JMenuItem mitShowPath = null;
    private JMenuItem mitCopyPath = null;
    private JMenuItem mitSavePath = null;
    private JCheckBoxMenuItem mitIncludePencils = null;
    private JMenu editMenu = null;
    private JMenuItem mitCopy81 = null;
    private JMenuItem mitCopy = null;
    private JMenuItem mitCopySukaku = null;
    private JMenuItem mitCopyPencilMarks = null;
    private JMenuItem mitClear = null;
    private JMenuItem mitPaste = null;
    private JMenu toolMenu = null;
    private JMenuItem mitCheckValidity = null;
    private JMenuItem mitAnalyse = null;
    private JCheckBoxMenuItem mitAnalyseClipboard = null;
    private JMenuItem mitSolveStep = null;
    private JMenuItem mitGetNextHint = null;
    private JMenuItem mitApplyHint = null;
    private JMenuItem mitGetAllHints = null;
    private JMenuItem mitUndoStep = null;
    private JMenuItem mitSolve = null;
    private JMenuItem mitResetPotentials = null;
    private JMenuItem mitClearHints = null;
    private JMenuItem mitRotateClockwise = null;
    private JMenuItem mitRotateAntiClockwise = null;
    private JMenuItem mitRotateHorizontal = null;
    private JMenuItem mitRotateVertical = null;
    private JMenuItem mitRotateDiagonal = null;
    private JMenuItem mitRotateAntiDiagonal = null;
    private File defaultDirectory = new File("").getAbsoluteFile();
    private JRadioButton rdbView1 = null;
    private JRadioButton rdbView2 = null;
    private JMenu optionsMenu = null;
    private JCheckBoxMenuItem mitFilter = null;
    private JRadioButtonMenuItem mitMathMode = null;
    private JRadioButtonMenuItem mitChessMode = null;
    private JRadioButtonMenuItem mitSinglesMode = null;
    private JRadioButtonMenuItem mitBasicsMode = null;
    private JCheckBoxMenuItem mitAntiAliasing = null;
    private JCheckBoxMenuItem mitBig = null;
    private JMenu helpMenu = null;
    private JMenuItem mitAbout = null;
    private JMenuItem mitGetSmallClue = null;
    private JMenuItem mitGetBigClue = null;
    private JMenu mitLookAndFeel = null;
    private JMenuItem mitShowWelcome = null;
    private JMenuItem mitShowFeatures = null;
    private JMenuItem mitUseSolution = null;
    private JMenuItem mitGenerate = null;
    private JCheckBoxMenuItem mitGenerateClipboard = null;
    private JMenuItem mitGenerateSolution = null;
    private JCheckBoxMenuItem mitShowCandidates = null;
    private JCheckBoxMenuItem mitShowCandidateMasks = null;
    private JMenuItem mitSelectTechniques = null;
    private JPanel pnlEnabledTechniques = null;
    private JLabel lblEnabledTechniques = null;

    private JMenu mitRecentFile = null;
    private JMenuItem mitRecentFile01 = null;
    private JMenuItem mitRecentFile02 = null;
    private JMenuItem mitRecentFile03 = null;
    private JMenuItem mitRecentFile04 = null;
    private JMenuItem mitRecentFile05 = null;
    private JMenuItem mitRecentFile06 = null;
    private JMenuItem mitRecentFile07 = null;
    private JMenuItem mitRecentFile08 = null;
    private JMenuItem mitRecentFile09 = null;
    private JMenuItem mitRecentFile10 = null;
    private JMenuItem mitClearList = null;

    private JMenu VariantsMenu = null;
    private JMenuItem mitVanilla = null;
    private JCheckBoxMenuItem mitLatinSquare = null;
    private JCheckBoxMenuItem mitDiagonals = null;
    private JCheckBoxMenuItem mitDisjointGroups = null;
    private JCheckBoxMenuItem mitWindoku = null;
    private JCheckBoxMenuItem mitClover = null;
    private JCheckBoxMenuItem mitAsterisk = null;
    private JCheckBoxMenuItem mitCenterDot = null;
    private JCheckBoxMenuItem mitGirandola = null;
    private JCheckBoxMenuItem mitHalloween = null;
    private JCheckBoxMenuItem mitPerCent = null;
    private JCheckBoxMenuItem mitSdoku = null;
    private JMenuItem mitOddEven = null;
    private JMenuItem mitExtraRegions = null;
    private JMenuItem mitCustomText = null;
    private JMenuItem mitCustomFile = null;
    private JMenuItem mitCustomCopy = null;
    private JMenuItem mitCustomClockwise = null;
    private JMenuItem mitCustomAntiClockwise = null;
    private JMenuItem mitCustomHorizontal = null;
    private JMenuItem mitCustomVertical = null;
    private JMenuItem mitCustomDiagonal = null;
    private JMenuItem mitCustomAntiDiagonal = null;

    public SudokuFrame() {
        super();
        initialize();
        repaintViews();
        AutoBusy.addFullAutoBusy(this);
        showWelcomeText();
        ImageIcon icon = createImageIcon("Sudoku.gif");
        setIconImage(icon.getImage());
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                getSudokuPanel().requestFocusInWindow();
            }
        });
    }

    public void showWelcomeText() {
        String welcomeText = HtmlLoader.loadHtml(this, "Welcome.html");
        setExplanations(welcomeText);
    }

    public void showFeaturesText() {
        String featuresText = HtmlLoader.loadHtml(this, "Features.html");
        setExplanations(featuresText);
    }

    void setEngine(SudokuExplainer explainer) {
        this.engine = explainer;
    }

    public void setHintsTree(HintNode root, HintNode selected, boolean isFilterEnabled) {
        getHintsTree().setEnabled(false);
        DefaultTreeModel model = new DefaultTreeModel(root);
        getHintsTree().setModel(model);
        // Expand any node below the root
        if (root != null) {
            for (int i = 0; i < root.getChildCount(); i++) {
                HintNode child = (HintNode)root.getChildAt(i);
                getHintsTree().expandPath(new TreePath(child.getPath()));
            }
        }
        // Enabled/Disable filter checkbox
        chkFilter.setSelected(engine.isFiltered());
        chkFilter.setEnabled(isFilterEnabled);
        mitFilter.setSelected(chkFilter.isSelected());
        mitFilter.setEnabled(chkFilter.isEnabled());
        // Select any given selected node
        if (selected != null)
            getHintsTree().setSelectionPath(new TreePath(selected.getPath()));
        getHintsTree().setEnabled(true);
    }

    private void repaintHint() {
        Set<Cell> noCells = Collections.emptySet();
        Map<Cell, BitSet> noMap = Collections.emptyMap();
        sudokuPanel.setRedCells(noCells);
        sudokuPanel.setGreenCells(noCells);
        sudokuPanel.setRedPotentials(noMap);
        sudokuPanel.setGreenPotentials(noMap);
        // Highlight as necessary
        if (currentHint != null) {
            sudokuPanel.clearSelection();
            if (currentHint instanceof DirectHint) {
                DirectHint dHint = (DirectHint)currentHint;
                sudokuPanel.setGreenCells(Collections.singleton(dHint.getCell()));
                BitSet values = new BitSet(10);
                values.set(dHint.getValue());
                sudokuPanel.setGreenPotentials(Collections.singletonMap(
                        dHint.getCell(), values));
                getSudokuPanel().setLinks(null);
            } else if (currentHint instanceof IndirectHint) {
                IndirectHint iHint = (IndirectHint)currentHint;
                sudokuPanel.setGreenPotentials(iHint.getGreenPotentials(viewNum));
                sudokuPanel.setRedPotentials(iHint.getRedPotentials(viewNum));
                sudokuPanel.setBluePotentials(iHint.getBluePotentials(sudokuPanel.getSudokuGrid(), viewNum));
                if (iHint.getSelectedCells() != null)
                    sudokuPanel.setGreenCells(Arrays.asList(iHint.getSelectedCells()));
                if (iHint instanceof WarningHint)
                    sudokuPanel.setRedCells(((WarningHint)iHint).getRedCells());
                // Set links (rendered as arrows)
                getSudokuPanel().setLinks(iHint.getLinks(viewNum));
            }
            getSudokuPanel().setBlueRegions(currentHint.getRegions());
        }
        sudokuPanel.repaint();
    }

    public void setCurrentHint(Hint hint, boolean isApplyEnabled) {
        this.currentHint = hint;
        btnApplyHint.setEnabled(isApplyEnabled);
        mitApplyHint.setEnabled(isApplyEnabled);
        if (hint != null) {
            // Select view
            if (hint instanceof IndirectHint) {
                viewCount = ((IndirectHint)hint).getViewCount();
                if (viewNum >= viewCount)
                    viewNum = 0;
            } else {
                viewNum = 0;
                viewCount = 1;
            }
            repaintViews();
            // Set explanations
            setExplanations(hint.toHtml());
            if (hint instanceof Rule) {
                Rule rule = (Rule)hint;
                DecimalFormat format = new DecimalFormat("#0.0");
                lblRating.setText(format.format(rule.getDifficulty()));
            } else if (hint instanceof AnalysisInfo) {
                AnalysisInfo info = (AnalysisInfo)hint;
                DecimalFormat format = new DecimalFormat("#0.0");
                lblRating.setText(format.format(info.getDifficulty()));
            }
            // Set regions
        } else {
            getHintDetailArea().setText(null);
            getSudokuPanel().setBlueRegions();
            getSudokuPanel().setLinks(null);
            viewCount = 1;
            viewNum = 0;
            repaintViews();
        }
        repaintHint();
        this.repaint();
    }

    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = SudokuFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private String makeItem(int viewNum) {
        return "View " + (viewNum + 1);
    }

    private void repaintViews() {
        cmbViewSelector.setEnabled(false);
        cmbViewSelector.removeAllItems();
        for (int i = 0; i < viewCount; i++)
            cmbViewSelector.addItem(makeItem(i));
        cmbViewSelector.setSelectedIndex(viewNum);
        cmbViewSelector.setEnabled(viewCount >= 3);
        cmbViewSelector.setVisible(viewCount >= 3);
        rdbView1.setVisible(viewCount < 3);
        rdbView2.setVisible(viewCount < 3);
        rdbView1.setEnabled(viewCount > 1);
        rdbView2.setEnabled(viewCount > 1);
        if (viewNum == 0)
            rdbView1.setSelected(true);
        else
            rdbView2.setSelected(true);
    }

    public void setExplanations(String htmlText) {
        getHintDetailArea().setText(htmlText);
        getHintDetailArea().setCaretPosition(0);
        lblRating.setText("-");
    }

    public void refreshSolvingTechniques() {
        EnumSet<SolvingTechnique> all = EnumSet.allOf(SolvingTechnique.class);
        EnumSet<SolvingTechnique> enabled = Settings.getInstance().getTechniques();
        int disabled = all.size() - enabled.size();
        String message;
        if (disabled == 1)
            message = "1 solving technique is disabled";
        else
            message = "" + disabled + " solving" +
                    " techniques are disabled";
        lblEnabledTechniques.setText(message);
        pnlEnabledTechniques.setVisible(!Settings.getInstance().isUsingAllTechniques());
    }

    public boolean ask(String message) {
        return JOptionPane.showConfirmDialog(this, message, getTitle(),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private class HintsTreeCellRenderer implements TreeCellRenderer {

        private final DefaultTreeCellRenderer target = new DefaultTreeCellRenderer();


        public HintsTreeCellRenderer() {
            ImageIcon icon = createImageIcon("Light.gif");
            target.setLeafIcon(icon);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            if (!(value instanceof HintNode))
                return target.getTreeCellRendererComponent(tree, value, selected,
                        expanded, leaf, row, hasFocus);
            HintNode node = (HintNode)value;
            boolean isEmptyParent = (!node.isHintNode() && node.getChildCount() == 0);
            return target.getTreeCellRendererComponent(tree, value, selected,
                    expanded || isEmptyParent, leaf && !isEmptyParent, row, hasFocus);
        }

    }

    private void initialize() {
        this.setTitle("Sukaku Explainer " + VERSION + "." + REVISION + "." + SUBREV);
        JMenuBar menuBar = getJJMenuBar();
        setupLookAndFeelMenu();
        this.setJMenuBar(menuBar);
        this.setContentPane(getJContentPane());
        try {
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (SecurityException ex) {
            // May happen in "applet" mode !
        }
        this.getSudokuPanel().requestFocusInWindow();
    }

    private void setupLookAndFeelMenu() {
        String lookAndFeelName = Settings.getInstance().getLookAndFeelClassName();
        if (lookAndFeelName == null)
            lookAndFeelName = UIManager.getSystemLookAndFeelClassName();
        ButtonGroup group = new ButtonGroup();
        for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            final JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(laf.getName());
            menuItem.setName(laf.getClassName());
            try {
                Class<?> lafClass = Class.forName(laf.getClassName());
                LookAndFeel instance = (LookAndFeel)lafClass.newInstance();
                menuItem.setToolTipText(instance.getDescription());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            group.add(menuItem);
            getMitLookAndFeel().add(menuItem);
            if (laf.getClassName().equals(lookAndFeelName))
                menuItem.setSelected(true);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (menuItem.isSelected()) {
                        String lafClassName = menuItem.getName();
                        try {
                            UIManager.setLookAndFeel(lafClassName);
                            Settings.getInstance().setLookAndFeelClassName(lafClassName);
                            SwingUtilities.updateComponentTreeUI(SudokuFrame.this);
                            // Create renderer again to reload the correct icons:
                            hintsTree.setCellRenderer(new HintsTreeCellRenderer());
                            SudokuFrame.this.repaint();
                            if (generateDialog != null && generateDialog.isVisible()) {
                                SwingUtilities.updateComponentTreeUI(generateDialog);
                                generateDialog.pack();
                                generateDialog.repaint();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJPanel(), java.awt.BorderLayout.NORTH);
            jContentPane.add(getHintDetailContainer(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getButtonsContainer(), java.awt.BorderLayout.SOUTH);
        }
        return jContentPane;
    }

    public SudokuPanel getSudokuPanel() {
        if (sudokuPanel == null) {
            sudokuPanel = new SudokuPanel(this);
        }
        return sudokuPanel;
    }

    private JScrollPane getHintsDetailScrollPane() {
        if (hintDetailsPane == null) {
            hintDetailsPane = new JScrollPane();
          if ( Settings.getInstance().isBigCell() ) {
            hintDetailsPane.setPreferredSize(new java.awt.Dimension(1000,200));
          } else {
            hintDetailsPane.setPreferredSize(new java.awt.Dimension(800,200));
          }
            hintDetailsPane.setViewportView(getHintDetailArea());
        }
        return hintDetailsPane;
    }

    private JTree getHintsTree() {
        if (hintsTree == null) {
            hintsTree = new JTree();
            hintsTree.setShowsRootHandles(true);
            hintsTree.getSelectionModel().setSelectionMode(
                    TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            hintsTree.setCellRenderer(new HintsTreeCellRenderer());
            hintsTree.setExpandsSelectedPaths(true);
            hintsTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

                public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
                    if (hintsTree.isEnabled()) {
                        Collection<HintNode> selection = new ArrayList<HintNode>();
                        TreePath[] pathes = hintsTree.getSelectionPaths();
                        if (pathes != null) {
                            for (TreePath path : pathes)
                                selection.add((HintNode)path.getLastPathComponent());
                        }
                        engine.hintsSelected(selection);
                    }
                }
            });
        }
        return hintsTree;
    }

    private JEditorPane getHintDetailArea() {
        if (hintDetailArea == null) {
            hintDetailArea = new JEditorPane("text/html", null) {
                private static final long serialVersionUID = -5658720148768663350L;

                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    super.paint(g);
                }
            };
            hintDetailArea.setEditable(false);
        }
        return hintDetailArea;
    }

    private JScrollPane getHintsTreeScrollPane() {
        if (hintsTreeScrollpane == null) {
            hintsTreeScrollpane = new JScrollPane();
            hintsTreeScrollpane.setPreferredSize(new Dimension(200, 200));
            hintsTreeScrollpane.setViewportView(getHintsTree());
        }
        return hintsTreeScrollpane;
    }

    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(getSudokuContainer(), java.awt.BorderLayout.WEST);
            jPanel.add(getHintsTreePanel(), java.awt.BorderLayout.CENTER);
        }
        return jPanel;
    }

    private JPanel getSudokuContainer() {
        if (sudokuContainer == null) {
            sudokuContainer = new JPanel();
            sudokuContainer.setLayout(new BorderLayout());
            sudokuContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, "Sudoku Grid",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    new java.awt.Color(128, 128, 128)));
            sudokuContainer.add(getSudokuPanel(), java.awt.BorderLayout.CENTER);
            sudokuContainer.add(getViewSelectionPanel(), java.awt.BorderLayout.SOUTH);
        }
        return sudokuContainer;
    }

    private JPanel getHintDetailContainer() {
        if (hintDetailContainer == null) {
            hintDetailContainer = new JPanel();
            hintDetailContainer.setLayout(new BorderLayout());
            hintDetailContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, "Explanations",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    new java.awt.Color(128, 128, 128)));
            hintDetailContainer.add(getHintsDetailScrollPane(), BorderLayout.CENTER);
        }
        return hintDetailContainer;
    }

    private JPanel getButtonsPane() {
        if (buttonsPane == null) {
            GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
            gridBagConstraints0.gridx = 0;
            gridBagConstraints0.weightx = 1.0D;
            gridBagConstraints0.gridy = 0;

            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridy = 0;

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.gridy = 0;

            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 3;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.gridy = 0;

            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 4;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 0;

            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 5;
            gridBagConstraints5.weightx = 1.0D;
            gridBagConstraints5.gridy = 0;

            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 6;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.gridy = 0;

            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 7;
            gridBagConstraints7.weightx = 1.0D;
            gridBagConstraints7.gridy = 0;

            buttonsPane = new JPanel();
            buttonsPane.setLayout(new GridBagLayout());
            buttonsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, "Actions",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),    // null));
                    new java.awt.Color(128, 128, 128)));

            buttonsPane.add(getBtnCheckValidity(),   gridBagConstraints0);
            buttonsPane.add(getBtnApplyHintAndGet(), gridBagConstraints1);
            buttonsPane.add(getBtnGetNextHint(),     gridBagConstraints2);
            buttonsPane.add(getBtnApplySingles(),    gridBagConstraints3);
            buttonsPane.add(getBtnApplyHint(),       gridBagConstraints4);
            buttonsPane.add(getBtnGetAllHints(),     gridBagConstraints5);
            buttonsPane.add(getBtnUndoStep(),        gridBagConstraints6);
            buttonsPane.add(getBtnQuit(),            gridBagConstraints7);
        }
        return buttonsPane;
    }

    private JButton getBtnCheckValidity() {
        if (btnCheckValidity == null) {
            btnCheckValidity = new JButton();
            btnCheckValidity.setText("F2| Check validity");
            btnCheckValidity.setToolTipText("Verify the validity of the entered Sudoku");
            btnCheckValidity.setMnemonic(java.awt.event.KeyEvent.VK_F2);
            btnCheckValidity.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (engine.checkValidity())
                        setExplanations(HtmlLoader.loadHtml(this, "Valid.html"));
                }
            });
        }
        return btnCheckValidity;
    }

    JButton getBtnApplyHintAndGet() {
        if (btnApplyHintAndGet == null) {
            btnApplyHintAndGet = new JButton();
            btnApplyHintAndGet.setText("F3| Solve step");
            btnApplyHintAndGet.setMnemonic(java.awt.event.KeyEvent.VK_F3);
            btnApplyHintAndGet.setToolTipText("Apply the current hint (if any is shown), and get an hint for the next step");
            btnApplyHintAndGet.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            btnApplyHintAndGet.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.applySelectedHintsAndContinue();
                }
            });
        }
        return btnApplyHintAndGet;
    }

    private JButton getBtnGetNextHint() {
        if (btnGetNextHint == null) {
            btnGetNextHint = new JButton();
            btnGetNextHint.setText("F4| Get next hint");
            btnGetNextHint.setToolTipText("Get another, different hint");
            btnGetNextHint.setMnemonic(java.awt.event.KeyEvent.VK_F4);
            btnGetNextHint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.getNextHint();
                }
            });
        }
        return btnGetNextHint;
    }

    private JButton getBtnApplySingles() {
        if (btnApplySingles == null) {
            btnApplySingles = new JButton();
          if ( Settings.getInstance().getApply()==23 ) {
            btnApplySingles.setText("Apply Singles");
            btnApplySingles.setToolTipText("Apply all (hidden and naked) singles");
          }
          if ( Settings.getInstance().getApply()==28 ) {
            btnApplySingles.setText("Apply Basics");
            btnApplySingles.setToolTipText("Apply all (hidden and naked) singles and basics");
          }
            btnApplySingles.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                  if ( Settings.getInstance().getApply()==23 ) {
                    engine.ApplySingles();
                  }
                  if ( Settings.getInstance().getApply()==28 ) {
                    engine.ApplyBasics();
                  }
                }
            });
        }
        return btnApplySingles;
    }

    private JButton getBtnApplyHint() {
        if (btnApplyHint == null) {
            btnApplyHint = new JButton();
            btnApplyHint.setText("F5| Apply hint");
            btnApplyHint.setToolTipText("Apply the selected hint(s)");
            btnApplyHint.setMnemonic(KeyEvent.VK_F5);
            btnApplyHint.setEnabled(false);
            btnApplyHint.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.applySelectedHints();
                }
            });
        }
        return btnApplyHint;
    }

    private JButton getBtnGetAllHints() {
        if (btnGetAllHints == null) {
            btnGetAllHints = new JButton();
            btnGetAllHints.setText("F6| Get all hints");
            btnGetAllHints.setToolTipText("Get all hints applicable on the current situation");
            btnGetAllHints.setMnemonic(KeyEvent.VK_F6);
            btnGetAllHints.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if ( (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0
                      || (e.getModifiers() & ActionEvent.CTRL_MASK) != 0 ) {
                        engine.getAllMoreHints();
                    }
                    else {
                        engine.getAllHints();
                    }
                }
            });
        }
        return btnGetAllHints;
    }

    private JButton getBtnUndoStep() {
        if (btnUndoStep == null) {
            btnUndoStep = new JButton();
            btnUndoStep.setText("Ctrl-Z| Undo step");
            btnUndoStep.setToolTipText("Undo previous solve step or value selection");
            btnUndoStep.setMnemonic(KeyEvent.VK_Z);
            btnUndoStep.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.UndoStep();
                }
            });
        }
        return btnUndoStep;
    }

    private JButton getBtnQuit() {
        if (btnQuit == null) {
            btnQuit = new JButton();
            btnQuit.setText("Ctrl-Q| Quit");
            btnQuit.setToolTipText("Quit the application");
            btnQuit.setMnemonic(java.awt.event.KeyEvent.VK_Q);
            btnQuit.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    quit();
                }
            });
        }
        return btnQuit;
    }

    private JPanel getButtonsContainer() {
        if (buttonsContainer == null) {
            buttonsContainer = new JPanel();
            buttonsContainer.setLayout(new GridLayout(1, 1));
            buttonsContainer.add(getButtonsPane(), null);
        }
        return buttonsContainer;
    }

    private JPanel getViewSelectionPanel() {
        if (viewSelectionPanel == null) {
            viewSelectionPanel = new JPanel();
            viewSelectionPanel.setLayout(new FlowLayout());
            viewSelectionPanel.add(getRdbView1(), null);
            viewSelectionPanel.add(getCmbViewSelector(), null);
            viewSelectionPanel.add(getRdbView2(), null);
            ButtonGroup group = new ButtonGroup();
            group.add(getRdbView1());
            group.add(getRdbView2());
        }
        return viewSelectionPanel;
    }

    private JPanel getHintsTreePanel() {
        if (hintsTreePanel == null) {
            hintsTreePanel = new JPanel();
            hintsTreePanel.setLayout(new BorderLayout());
            hintsTreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, "Hints classification",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),    // null));
                    new java.awt.Color(128, 128, 128)));
            hintsTreePanel.add(getHintsTreeScrollPane(), java.awt.BorderLayout.CENTER);
            hintsTreePanel.add(getHintsSouthPanel(), java.awt.BorderLayout.SOUTH);
        }
        return hintsTreePanel;
    }

    private JCheckBox getChkFilter() {
        if (chkFilter == null) {
            chkFilter = new JCheckBox();
            chkFilter.setText("Filter hints with similar outcome");
            chkFilter.setMnemonic(KeyEvent.VK_I);
            chkFilter.setSelected(true);
            chkFilter.setEnabled(false);
            chkFilter.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    engine.setFiltered(chkFilter.isSelected());
                }
            });
        }
        return chkFilter;
    }

    private JComboBox<String> getCmbViewSelector() {
        if (cmbViewSelector == null) {
            cmbViewSelector = new JComboBox<String>();
            cmbViewSelector.setToolTipText("Toggle view (only for chaining hints)");
            cmbViewSelector.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (cmbViewSelector.isEnabled()) {
                        viewNum = cmbViewSelector.getSelectedIndex();
                        repaintHint();
                    }
                }
            });
        }
        return cmbViewSelector;
    }

    private JRadioButton getRdbView1() {
        if (rdbView1 == null) {
            rdbView1 = new JRadioButton();
            rdbView1.setText("View 1");
            rdbView1.setMnemonic(KeyEvent.VK_1);
            rdbView1.setToolTipText(getCmbViewSelector().getToolTipText());
            rdbView1.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (rdbView1.isSelected()) {
                        viewNum = 0;
                        repaintHint();
                    }
                }
            });
        }
        return rdbView1;
    }

    private JRadioButton getRdbView2() {
        if (rdbView2 == null) {
            rdbView2 = new JRadioButton();
            rdbView2.setText("View 2");
            rdbView2.setMnemonic(KeyEvent.VK_2);
            rdbView2.setToolTipText(getCmbViewSelector().getToolTipText());
            rdbView2.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (rdbView2.isSelected()) {
                        viewNum = 1;
                        repaintHint();
                    }
                }
            });
        }
        return rdbView2;
    }

    private JPanel getHintsSouthPanel() {
        if (hintsSouthPanel == null) {
            hintsSouthPanel = new JPanel();
            hintsSouthPanel.setLayout(new BorderLayout());
            hintsSouthPanel.add(getPnlEnabledTechniques(), BorderLayout.NORTH);
            hintsSouthPanel.add(getChkFilter(), BorderLayout.CENTER);
            hintsSouthPanel.add(getRatingPanel(), BorderLayout.SOUTH);
        }
        return hintsSouthPanel;
    }

    private JPanel getRatingPanel() {
        if (ratingPanel == null) {
            ratingPanel = new JPanel();
            jLabel2 = new JLabel();
            //jLabel2.setText(" / 10");
            lblRating = new JLabel();
            lblRating.setText("0");
            jLabel = new JLabel();
            jLabel.setText("Hint rating: ");
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
            ratingPanel.setLayout(flowLayout);
            ratingPanel.add(jLabel, null);
            ratingPanel.add(lblRating, null);
            ratingPanel.add(jLabel2, null);
        }
        return ratingPanel;
    }

    private JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new JMenuBar();
            jJMenuBar.add(getFileMenu());
            jJMenuBar.add(getEditMenu());
            jJMenuBar.add(getToolMenu());
            jJMenuBar.add(getOptionsMenu());
            jJMenuBar.add(getVariantsMenu());
            jJMenuBar.add(getHelpMenu());
        }
        return jJMenuBar;
    }

    private void setCommand(JMenuItem item, char cmd) {
        item.setAccelerator(KeyStroke.getKeyStroke(cmd, InputEvent.CTRL_MASK));
    }

    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
            fileMenu.add(getMitNew());
            setCommand(getMitNew(), 'N');
            fileMenu.add(getMitUseSolution());
            fileMenu.add(getMitGenerate());
            setCommand(getMitGenerate(), 'G');
            fileMenu.add(getMitGenerateClipboard());
            fileMenu.add(getMitGenerateSolution());
            fileMenu.add(getMitRestart());
            fileMenu.addSeparator();
            fileMenu.add(getMitLoad());
            setCommand(getMitLoad(), 'O');
            fileMenu.add(getMitRecentFile());
            fileMenu.add(getMitSave81());
            fileMenu.add(getMitSave());
            setCommand(getMitSave(), 'S');
            fileMenu.add(getMitSaveSukaku());
            setCommand(getMitSaveSukaku(), 'U');
            fileMenu.add(getMitSavePencilMarks());
            setCommand(getMitSavePencilMarks(), 'P');
            fileMenu.add(getMitSaveAsImage());
            fileMenu.addSeparator();
            fileMenu.add(getMitAddNote());
            fileMenu.add(getMitShowPath());
            fileMenu.add(getMitCopyPath());
            fileMenu.add(getMitSavePath());
            fileMenu.add(getMitIncludePencils());
            fileMenu.addSeparator();
            fileMenu.add(getMitQuit());
            setCommand(getMitQuit(), 'Q');
        }
        return fileMenu;
    }

    private JMenuItem getMitNew() {
        if (mitNew == null) {
            mitNew = new JMenuItem();
            mitNew.setText("New");
            mitNew.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
            mitNew.setMnemonic(java.awt.event.KeyEvent.VK_N);
            mitNew.setToolTipText("Clear the grid");
            mitNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.clearGrid();
                }
            });
        }
        return mitNew;
    }

    private JMenuItem getMitUseSolution() {
        if (mitUseSolution == null) {
            mitUseSolution = new JMenuItem();
            mitUseSolution.setText("Use Solution");
            mitUseSolution.setToolTipText("Use solution to generate a random Sudoku puzzle");
            mitUseSolution.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.useSolution();
                }
            });
        }
        return mitUseSolution;
    }

    private JMenuItem getMitGenerate() {
        if (mitGenerate == null) {
            mitGenerate = new JMenuItem();
            mitGenerate.setText("Generate...");
            mitGenerate.setMnemonic(KeyEvent.VK_G);
            mitGenerate.setToolTipText("Open a dialog to generate a random Sudoku puzzle");
            mitGenerate.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (generateDialog == null || !generateDialog.isVisible()) {
                        generateDialog = new GenerateDialog(SudokuFrame.this, engine);
                        generateDialog.pack();
                        offsetDialog(generateDialog);
                    }
                    generateDialog.setVisible(true);
                }
            });
        }
        return mitGenerate;
    }

    private JCheckBoxMenuItem getMitGenerateClipboard() {
        if (mitGenerateClipboard == null) {
            mitGenerateClipboard = new JCheckBoxMenuItem();
            mitGenerateClipboard.setText("... and Copy to Clipboard");
            mitGenerateClipboard.setToolTipText("Copy the generated sudoku to the clipboard");
            mitGenerateClipboard.setSelected(false);
            mitGenerateClipboard.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    Settings.getInstance().setGenerateToClipboard(mitGenerateClipboard.isSelected());
                }
            });
        }
        return mitGenerateClipboard;
    }

    private JMenuItem getMitGenerateSolution() {
        if (mitGenerateSolution == null) {
            mitGenerateSolution = new JMenuItem();
            mitGenerateSolution.setText("Generate Solution");
            mitGenerateSolution.setToolTipText("Use partial grid to generate a random Sudoku puzzle");
            mitGenerateSolution.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if ( engine.generateSolution() ) {
                        Random random = new Random();
                        BruteForceAnalysis analyser = new BruteForceAnalysis(true);
                        boolean result = analyser.solveRandom(sudokuPanel.getSudokuGrid(), random);
                        if ( !result ) {
                            JOptionPane.showMessageDialog(SudokuFrame.this, "Failed to generate a solution! Multiple failures are bad!!", "Generate Solution", JOptionPane.ERROR_MESSAGE);
                        }
                        repaint();
                    }
                }
            });
        }
        return mitGenerateSolution;
    }

    private void offsetDialog(JDialog dlg) {
        Point frameLocation = SudokuFrame.this.getLocation();
        Dimension frameSize = SudokuFrame.this.getSize();
        Dimension windowSize = dlg.getSize();
        dlg.setLocation(
                frameLocation.x + (frameSize.width * 3) / 5,
                frameLocation.y + (frameSize.height - windowSize.height) / 3);
    }

    private void centerDialog(JDialog dlg) {
        Point frameLocation = SudokuFrame.this.getLocation();
        Dimension frameSize = SudokuFrame.this.getSize();
        Dimension windowSize = dlg.getSize();
        dlg.setLocation(
                frameLocation.x + (frameSize.width - windowSize.width) / 2,
                frameLocation.y + (frameSize.height - windowSize.height) / 3);
    }

    private JMenuItem getMitRestart() {
        if (mitRestart == null) {
            mitRestart = new JMenuItem();
            mitRestart.setText("Restart...");
            mitRestart.setToolTipText("Restart the grid");
            mitRestart.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.restartGrid();
                }
            });
        }
        return mitRestart;
    }

    private void warnAccessError(AccessControlException ex) {
        JOptionPane.showMessageDialog(this,
                "Sorry, this functionality cannot be used from an applet.\n" +
                "Denied permission: " + ex.getPermission().toString() + "\n" +
                "Download the application to access this functionality.",
                "Access denied", JOptionPane.ERROR_MESSAGE);
    }

    private class TextFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            return f.getName().toLowerCase().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return "Text files (*.txt)";
        }

    }

    private class PngFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            return f.getName().toLowerCase().endsWith(".png");
        }

        @Override
        public String getDescription() {
            return "PNG image files (*.png)";
        }

    }

    private JMenuItem getMitLoad() {
        if (mitLoad == null) {
            mitLoad = new JMenuItem();
            mitLoad.setText("Load...");
            mitLoad.setMnemonic(java.awt.event.KeyEvent.VK_O);
            mitLoad.setToolTipText("Open the file selector to load the grid from a file");
            mitLoad.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new TextFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showOpenDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = chooser.getSelectedFile();
                            if ( engine.loadGrid(selectedFile) == 1 ) {
                                addRecentFile(selectedFile.toString()); // add after successful load
                            }
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitLoad;
    }

    private JMenuItem getMitSave81() {
        if (mitSave81 == null) {
            mitSave81 = new JMenuItem();
            mitSave81.setText("Save 81-chars...");
            mitSave81.setToolTipText("Open the file selector to save the (sudoku) grid to a file");
            mitSave81.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new TextFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showSaveDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            try {
                                if (!file.getName().endsWith(".txt")) // &&
                                    //  file.getName().indexOf('.') < 0)
                                    file = new File(file.getCanonicalPath() + ".txt");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (file.exists()) {
                                if (JOptionPane.showConfirmDialog(SudokuFrame.this,
                                        "The file \"" + file.getName() + "\" already exists.\n" +
                                        "Do you want to replace the existing file ?",
                                        "Save", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                                    return;
                            }
                            engine.saveGrid81(file);
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitSave81;
    }

    private JMenuItem getMitSave() {
        if (mitSave == null) {
            mitSave = new JMenuItem();
            mitSave.setText("Save Sudoku...");
            mitSave.setMnemonic(java.awt.event.KeyEvent.VK_S);
            mitSave.setToolTipText("Open the file selector to save the (sudoku) grid to a file");
            mitSave.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new TextFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showSaveDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            try {
                                if (!file.getName().endsWith(".txt")) // &&
                                    //  file.getName().indexOf('.') < 0)
                                    file = new File(file.getCanonicalPath() + ".txt");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (file.exists()) {
                                if (JOptionPane.showConfirmDialog(SudokuFrame.this,
                                        "The file \"" + file.getName() + "\" already exists.\n" +
                                        "Do you want to replace the existing file ?",
                                        "Save", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                                    return;
                            }
                            engine.saveGrid(file);
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitSave;
    }

    private JMenuItem getMitSaveSukaku() {
        if (mitSaveSukaku == null) {
            mitSaveSukaku = new JMenuItem();
            mitSaveSukaku.setText("Save Sukaku...");
            mitSaveSukaku.setMnemonic(java.awt.event.KeyEvent.VK_U);
            mitSaveSukaku.setToolTipText("Open the file selector to save the (sukaku) grid to a file");
            mitSaveSukaku.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new TextFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showSaveDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            try {
                                if (!file.getName().endsWith(".txt")) // &&
                                    //  file.getName().indexOf('.') < 0)
                                    file = new File(file.getCanonicalPath() + ".txt");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (file.exists()) {
                                if (JOptionPane.showConfirmDialog(SudokuFrame.this,
                                        "The file \"" + file.getName() + "\" already exists.\n" +
                                        "Do you want to replace the existing file ?",
                                        "Save", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                                    return;
                            }
                            engine.saveSukaku(file);
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitSaveSukaku;
    }

    private JMenuItem getMitSavePencilMarks() {
        if (mitSavePencilMarks == null) {
            mitSavePencilMarks = new JMenuItem();
            mitSavePencilMarks.setText("Save pencilmarks...");
            mitSavePencilMarks.setMnemonic(java.awt.event.KeyEvent.VK_P);
            mitSavePencilMarks.setToolTipText("Open the file selector to save the pencilmarks to a file");
            mitSavePencilMarks.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new TextFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showSaveDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            try {
                                if (!file.getName().endsWith(".txt")) // &&
                                    //  file.getName().indexOf('.') < 0)
                                    file = new File(file.getCanonicalPath() + ".txt");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (file.exists()) {
                                if (JOptionPane.showConfirmDialog(SudokuFrame.this,
                                        "The file \"" + file.getName() + "\" already exists.\n" +
                                        "Do you want to replace the existing file ?",
                                        "Save", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                                    return;
                            }
                            engine.savePencilMarks(file);
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitSavePencilMarks;
    }

    private JMenuItem getMitSaveAsImage() {
        if (mitSaveAsImage == null) {
            mitSaveAsImage = new JMenuItem();
            mitSaveAsImage.setText("Save as image...");
            mitSaveAsImage.setToolTipText("Open the file selector to save grid as a png image to a file");
            mitSaveAsImage.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new PngFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showSaveDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            try {
                                if (!file.getName().endsWith(".png")) // &&
                                    //  file.getName().indexOf('.') < 0)
                                    file = new File(file.getCanonicalPath() + ".png");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (file.exists()) {
                                if (JOptionPane.showConfirmDialog(SudokuFrame.this,
                                        "The file \"" + file.getName() + "\" already exists.\n" +
                                        "Do you want to replace the existing file ?",
                                        "Save", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                                    return;
                            }
                            sudokuPanel.saveAsImage(file);
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitSaveAsImage;
    }

    private JMenu getMitRecentFile() {
        if (mitRecentFile == null) {
            mitRecentFile = new JMenu();
            mitRecentFile.setText("Recent File (load only)");
            mitRecentFile.add(getMitRecentFile01());
            mitRecentFile.add(getMitRecentFile02());
            mitRecentFile.add(getMitRecentFile03());
            mitRecentFile.add(getMitRecentFile04());
            mitRecentFile.add(getMitRecentFile05());
            mitRecentFile.add(getMitRecentFile06());
            mitRecentFile.add(getMitRecentFile07());
            mitRecentFile.add(getMitRecentFile08());
            mitRecentFile.add(getMitRecentFile09());
            mitRecentFile.add(getMitRecentFile10());
            mitRecentFile.addSeparator();
            mitRecentFile.add(getMitClearList());
        }
        return mitRecentFile;
    }

    private JMenuItem getMitRecentFile01() {
        if (mitRecentFile01 == null) {
            mitRecentFile01 = new JMenuItem();
            mitRecentFile01.setText("No File");
            mitRecentFile01.setVisible(true);
            mitRecentFile01.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile01.getText();
                  if ( !filepath.equals("No File") ) {
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                  }
                }
            });
        }
        return mitRecentFile01;
    }

    private JMenuItem getMitRecentFile02() {
        if (mitRecentFile02 == null) {
            mitRecentFile02 = new JMenuItem();
            mitRecentFile02.setText("No File");
            mitRecentFile02.setVisible(false);
            mitRecentFile02.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile02.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile02;
    }

    private JMenuItem getMitRecentFile03() {
        if (mitRecentFile03 == null) {
            mitRecentFile03 = new JMenuItem();
            mitRecentFile03.setText("No File");
            mitRecentFile03.setVisible(false);
            mitRecentFile03.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile03.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile03;
    }

    private JMenuItem getMitRecentFile04() {
        if (mitRecentFile04 == null) {
            mitRecentFile04 = new JMenuItem();
            mitRecentFile04.setText("No File");
            mitRecentFile04.setVisible(false);
            mitRecentFile04.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile04.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile04;
    }

    private JMenuItem getMitRecentFile05() {
        if (mitRecentFile05 == null) {
            mitRecentFile05 = new JMenuItem();
            mitRecentFile05.setText("No File");
            mitRecentFile05.setVisible(false);
            mitRecentFile05.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile05.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile05;
    }

    private JMenuItem getMitRecentFile06() {
        if (mitRecentFile06 == null) {
            mitRecentFile06 = new JMenuItem();
            mitRecentFile06.setText("No File");
            mitRecentFile06.setVisible(false);
            mitRecentFile06.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile06.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile06;
    }

    private JMenuItem getMitRecentFile07() {
        if (mitRecentFile07 == null) {
            mitRecentFile07 = new JMenuItem();
            mitRecentFile07.setText("No File");
            mitRecentFile07.setVisible(false);
            mitRecentFile07.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile07.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile07;
    }

    private JMenuItem getMitRecentFile08() {
        if (mitRecentFile08 == null) {
            mitRecentFile08 = new JMenuItem();
            mitRecentFile08.setText("No File");
            mitRecentFile08.setVisible(false);
            mitRecentFile08.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile08.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile08;
    }

    private JMenuItem getMitRecentFile09() {
        if (mitRecentFile09 == null) {
            mitRecentFile09 = new JMenuItem();
            mitRecentFile09.setText("No File");
            mitRecentFile09.setVisible(false);
            mitRecentFile09.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile09.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile09;
    }

    private JMenuItem getMitRecentFile10() {
        if (mitRecentFile10 == null) {
            mitRecentFile10 = new JMenuItem();
            mitRecentFile10.setText("No File");
            mitRecentFile10.setVisible(false);
            mitRecentFile10.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String filepath = mitRecentFile10.getText();
                    File selectedFile = new File(filepath);
                    engine.loadGrid(selectedFile);
                }
            });
        }
        return mitRecentFile10;
    }

    private JMenuItem getMitClearList() {
        if (mitClearList == null) {
            mitClearList = new JMenuItem();
            mitClearList.setText("Clear List");
            mitClearList.setVisible(false);
            mitClearList.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    mitRecentFile01.setText("No File"); mitRecentFile01.setVisible(true);
                    mitRecentFile02.setText("No File"); mitRecentFile02.setVisible(false);
                    mitRecentFile03.setText("No File"); mitRecentFile03.setVisible(false);
                    mitRecentFile04.setText("No File"); mitRecentFile04.setVisible(false);
                    mitRecentFile05.setText("No File"); mitRecentFile05.setVisible(false);
                    mitRecentFile06.setText("No File"); mitRecentFile06.setVisible(false);
                    mitRecentFile07.setText("No File"); mitRecentFile07.setVisible(false);
                    mitRecentFile08.setText("No File"); mitRecentFile08.setVisible(false);
                    mitRecentFile09.setText("No File"); mitRecentFile09.setVisible(false);
                    mitRecentFile10.setText("No File"); mitRecentFile10.setVisible(false);
                    mitClearList.setVisible(false);
                }
            });
        }
        return mitClearList;
    }

    private void addRecentFile( String filepath) {
        int found = 0;
        // check filepath does not already exist in recent file list
        if ( found == 0 && mitRecentFile01.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile02.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile03.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile04.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile05.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile06.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile07.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile08.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile09.getText().equals(filepath) ) { found = 1; }
        if ( found == 0 && mitRecentFile10.getText().equals(filepath) ) { found = 1; }
        // add filepath in unused slot of recent file list
        if ( found == 0 && mitRecentFile01.getText().equals("No File") ) { mitRecentFile01.setText(filepath); mitRecentFile01.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile02.getText().equals("No File") ) { mitRecentFile02.setText(filepath); mitRecentFile02.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile03.getText().equals("No File") ) { mitRecentFile03.setText(filepath); mitRecentFile03.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile04.getText().equals("No File") ) { mitRecentFile04.setText(filepath); mitRecentFile04.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile05.getText().equals("No File") ) { mitRecentFile05.setText(filepath); mitRecentFile05.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile06.getText().equals("No File") ) { mitRecentFile06.setText(filepath); mitRecentFile06.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile07.getText().equals("No File") ) { mitRecentFile07.setText(filepath); mitRecentFile07.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile08.getText().equals("No File") ) { mitRecentFile08.setText(filepath); mitRecentFile08.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile09.getText().equals("No File") ) { mitRecentFile09.setText(filepath); mitRecentFile09.setVisible(true); found = 1; }
        if ( found == 0 && mitRecentFile10.getText().equals("No File") ) { mitRecentFile10.setText(filepath); mitRecentFile10.setVisible(true); found = 1; }
        // recent file list is full, shuffle up, and add at bottom
        if ( found == 0 ) {
            mitRecentFile01.setText(mitRecentFile02.getText());
            mitRecentFile02.setText(mitRecentFile03.getText());
            mitRecentFile03.setText(mitRecentFile04.getText());
            mitRecentFile04.setText(mitRecentFile05.getText());
            mitRecentFile05.setText(mitRecentFile06.getText());
            mitRecentFile06.setText(mitRecentFile07.getText());
            mitRecentFile07.setText(mitRecentFile08.getText());
            mitRecentFile08.setText(mitRecentFile09.getText());
            mitRecentFile09.setText(mitRecentFile10.getText());
            mitRecentFile10.setText(filepath);
            found = 1;
        }
        if ( found == 1 )
        {
            mitClearList.setVisible(true);
        }
    }

    private JMenuItem getMitAddNote() {
        if (mitAddNote == null) {
            mitAddNote = new JMenuItem();
            mitAddNote.setText("Add a Note...");
            mitAddNote.setToolTipText("Add a note to the solution path (at current position)");
            mitAddNote.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String inputtext = (String)JOptionPane.showInputDialog(
                        SudokuFrame.this, "Add a note (freeform text) to the solution path, it is lost if you use Undo.",
                        "Add a Note", JOptionPane.PLAIN_MESSAGE, null, null, "");
                    if ( inputtext != null && inputtext.length() > 0 ) {
                        engine.addNote( inputtext);
                    }
                }
            });
        }
        return mitAddNote;
    }

    private JMenuItem getMitShowPath() {
        if (mitShowPath == null) {
            mitShowPath = new JMenuItem();
            mitShowPath.setText("Show Solution Path (hints only)");
            mitShowPath.setToolTipText("Show the sudoku (partial/complete) solution path so far (hints only)");
            mitShowPath.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.showPath();
                }
            });
        }
        return mitShowPath;
    }

    private JMenuItem getMitCopyPath() {
        if (mitCopyPath == null) {
            mitCopyPath = new JMenuItem();
            mitCopyPath.setText("Copy Solution Path (hints only)");
            mitCopyPath.setToolTipText("Copy the sudoku (partial/complete) solution path so far (hints only)");
            mitCopyPath.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.copyPath();
                }
            });
        }
        return mitCopyPath;
    }

    private JMenuItem getMitSavePath() {
        if (mitSavePath == null) {
            mitSavePath = new JMenuItem();
            mitSavePath.setText("Save Solution Path...");
            mitSavePath.setToolTipText("Open the file selector to save the sudoku (partial/complete) solution path to a file");
            mitSavePath.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new TextFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showSaveDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            try {
                                if (!file.getName().endsWith(".txt")) // &&
                                    //  file.getName().indexOf('.') < 0)
                                    file = new File(file.getCanonicalPath() + ".txt");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (file.exists()) {
                                if (JOptionPane.showConfirmDialog(SudokuFrame.this,
                                        "The file \"" + file.getName() + "\" already exists.\n" +
                                        "Do you want to replace the existing file ?",
                                        "Save", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                                    return;
                            }
                            engine.savePath(file,mitIncludePencils.isSelected());
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitSavePath;
    }

    private JCheckBoxMenuItem getMitIncludePencils() {
        if (mitIncludePencils == null) {
            mitIncludePencils = new JCheckBoxMenuItem();
            mitIncludePencils.setText("Include pencilmarks");
            mitIncludePencils.setToolTipText("Include pencilmarks in the saved solution");
            mitIncludePencils.setSelected(true);
            mitIncludePencils.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    /* nop */ ;
                }
            });
        }
        return mitIncludePencils;
    }

    private JMenuItem getMitQuit() {
        if (mitQuit == null) {
            mitQuit = new JMenuItem();
            mitQuit.setText("Quit");
            mitQuit.setMnemonic(java.awt.event.KeyEvent.VK_Q);
            mitQuit.setToolTipText("Bye bye");
            mitQuit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    quit();
                }
            });
        }
        return mitQuit;
    }

    private JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setText("Edit");
            editMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
            editMenu.add(getMitCopy81());
            editMenu.add(getMitCopy());
            setCommand(getMitCopy(), 'C');
            editMenu.add(getMitCopySukaku());
            setCommand(getMitCopySukaku(), 'K');
            editMenu.add(getMitCopyPencilMarks());
            setCommand(getMitCopyPencilMarks(), 'M');
            editMenu.add(getMitPaste());
            setCommand(getMitPaste(), 'V');
            editMenu.addSeparator();
            editMenu.add(getMitClear());
            setCommand(getMitClear(), 'E');
        }
        return editMenu;
    }

    private JMenuItem getMitCopy() {
        if (mitCopy == null) {
            mitCopy = new JMenuItem();
            mitCopy.setText("Copy Sudoku");
            mitCopy.setMnemonic(KeyEvent.VK_C);
            mitCopy.setToolTipText("Copy the (sudoku) grid to the clipboard as plain text");
            mitCopy.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        engine.copyGrid();
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitCopy;
    }

    private JMenuItem getMitCopy81() {
        if (mitCopy81 == null) {
            mitCopy81 = new JMenuItem();
            mitCopy81.setText("Copy 81-chars");
            mitCopy81.setToolTipText("Copy the (sudoku) grid to the clipboard as plain text");
            mitCopy81.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        engine.copyGrid81();
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitCopy81;
    }

    private JMenuItem getMitCopySukaku() {
        if (mitCopySukaku == null) {
            mitCopySukaku = new JMenuItem();
            mitCopySukaku.setText("Copy Sukaku");
            mitCopySukaku.setMnemonic(KeyEvent.VK_K);
            mitCopySukaku.setToolTipText("Copy the (sukaku) grid to the clipboard as plain text");
            mitCopySukaku.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        engine.copySukaku();
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitCopySukaku;
    }

    private JMenuItem getMitCopyPencilMarks() {
        if (mitCopyPencilMarks == null) {
            mitCopyPencilMarks = new JMenuItem();
            mitCopyPencilMarks.setText("Copy pencilmarks");
            mitCopyPencilMarks.setMnemonic(KeyEvent.VK_M);
            mitCopyPencilMarks.setToolTipText("Copy the pencilmarks to the clipboard as plain text");
            mitCopyPencilMarks.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        engine.copyPencilMarks();
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitCopyPencilMarks;
    }

    private JMenuItem getMitPaste() {
        if (mitPaste == null) {
            mitPaste = new JMenuItem();
            mitPaste.setText("Paste grid");
            mitPaste.setMnemonic(KeyEvent.VK_V);
            mitPaste.setToolTipText("Replace the grid with the content of the clipboard");
            mitPaste.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        engine.pasteGrid();
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitPaste;
    }

    private JMenuItem getMitClear() {
        if (mitClear == null) {
            mitClear = new JMenuItem();
            mitClear.setText("Clear grid");
            mitClear.setMnemonic(KeyEvent.VK_E);
            mitClear.setToolTipText("Clear the grid");
            mitClear.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.clearGrid();
                }
            });
        }
        return mitClear;
    }

    private JMenu getToolMenu() {
        if (toolMenu == null) {
            toolMenu = new JMenu();
            toolMenu.setText("Tools");
            toolMenu.setMnemonic(java.awt.event.KeyEvent.VK_T);
            toolMenu.add(getMitResetPotentials());
            setCommand(getMitResetPotentials(), 'R');
            toolMenu.add(getMitClearHints());
            setCommand(getMitClearHints(), 'D');
            toolMenu.addSeparator();
            toolMenu.add(getMitCheckValidity());
            getMitCheckValidity().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
            toolMenu.add(getMitSolveStep());
            getMitSolveStep().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
            toolMenu.add(getMitGetNextHint());
            getMitGetNextHint().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
            toolMenu.add(getMitApplyHint());
            getMitApplyHint().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
            toolMenu.add(getMitGetAllHints());
            getMitGetAllHints().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
            toolMenu.add(getMitUndoStep());
            setCommand(getMitUndoStep(), 'Z');
            toolMenu.addSeparator();
            toolMenu.add(getMitGetSmallClue());
            getMitGetSmallClue().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
            toolMenu.add(getMitGetBigClue());
            getMitGetBigClue().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.SHIFT_MASK));
            toolMenu.addSeparator();
            toolMenu.add(getMitSolve());
            getMitSolve().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
            toolMenu.add(getMitAnalyse());
            getMitAnalyse().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
            toolMenu.add(getMitAnalyseClipboard());
        }
        return toolMenu;
    }

    private JMenuItem getMitResetPotentials() {
        if (mitResetPotentials == null) {
            mitResetPotentials = new JMenuItem();
            mitResetPotentials.setText("Reset potential values");
            mitResetPotentials.setToolTipText("Recompute the remaining possible values for the empty cells");
            mitResetPotentials.setMnemonic(java.awt.event.KeyEvent.VK_R);
            mitResetPotentials.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.resetPotentials();
                }
            });
        }
        return mitResetPotentials;
    }

    private JMenuItem getMitClearHints() {
        if (mitClearHints == null) {
            mitClearHints = new JMenuItem();
            mitClearHints.setText("Clear hint(s)");
            mitClearHints.setMnemonic(KeyEvent.VK_D);
            mitClearHints.setToolTipText("Clear the hint list");
            mitClearHints.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.clearHints();
                }
            });
        }
        return mitClearHints;
    }

    private JMenuItem getMitCheckValidity() {
        if (mitCheckValidity == null) {
            mitCheckValidity = new JMenuItem();
            mitCheckValidity.setText("Check validity");
            mitCheckValidity.setMnemonic(KeyEvent.VK_F2);
            mitCheckValidity.setToolTipText("Check if the Sudoku has exactly one solution");
            mitCheckValidity.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (engine.checkValidity())
                        setExplanations(HtmlLoader.loadHtml(this, "Valid.html"));
                }
            });
        }
        return mitCheckValidity;
    }

    private JMenuItem getMitSolveStep() {
        if (mitSolveStep == null) {
            mitSolveStep = new JMenuItem();
            mitSolveStep.setText("Solve step");
            mitSolveStep.setMnemonic(KeyEvent.VK_F3);
            mitSolveStep.setToolTipText(getBtnApplyHintAndGet().getToolTipText());
            mitSolveStep.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.applySelectedHintsAndContinue();
                }
            });
        }
        return mitSolveStep;
    }

    private JMenuItem getMitGetNextHint() {
        if (mitGetNextHint == null) {
            mitGetNextHint = new JMenuItem();
            mitGetNextHint.setText("Get next hint");
            mitGetNextHint.setMnemonic(KeyEvent.VK_F4);
            mitGetNextHint.setToolTipText(getBtnGetNextHint().getToolTipText());
            mitGetNextHint.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.getNextHint();
                }
            });
        }
        return mitGetNextHint;
    }

    private JMenuItem getMitApplyHint() {
        if (mitApplyHint == null) {
            mitApplyHint = new JMenuItem();
            mitApplyHint.setText("Apply hint");
            mitApplyHint.setEnabled(false);
            mitApplyHint.setMnemonic(KeyEvent.VK_F5);
            mitApplyHint.setToolTipText(getBtnApplyHint().getToolTipText());
            mitApplyHint.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.applySelectedHints();
                }
            });
        }
        return mitApplyHint;
    }

    private JMenuItem getMitGetAllHints() {
        if (mitGetAllHints == null) {
            mitGetAllHints = new JMenuItem();
            mitGetAllHints.setText("Get all hints");
            mitGetAllHints.setMnemonic(KeyEvent.VK_F6);
            mitGetAllHints.setToolTipText(getBtnGetAllHints().getToolTipText());
            mitGetAllHints.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.getAllHints();
                }
            });
        }
        return mitGetAllHints;
    }

    private JMenuItem getMitUndoStep() {
        if (mitUndoStep == null) {
            mitUndoStep = new JMenuItem();
            mitUndoStep.setText("Undo step");
            mitUndoStep.setMnemonic(KeyEvent.VK_Z);
            mitUndoStep.setToolTipText(getBtnUndoStep().getToolTipText());
            mitUndoStep.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.UndoStep();
                }
            });
        }
        return mitUndoStep;
    }

    private JMenuItem getMitGetSmallClue() {
        if (mitGetSmallClue == null) {
            mitGetSmallClue = new JMenuItem();
            mitGetSmallClue.setText("Get a small clue");
            mitGetSmallClue.setMnemonic(KeyEvent.VK_M);
            mitGetSmallClue.setToolTipText("Get some information on the next solving step");
            mitGetSmallClue.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.getClue(false);
                }
            });
        }
        return mitGetSmallClue;
    }

    private JMenuItem getMitGetBigClue() {
        if (mitGetBigClue == null) {
            mitGetBigClue = new JMenuItem();
            mitGetBigClue.setText("Get a big clue");
            mitGetBigClue.setMnemonic(KeyEvent.VK_B);
            mitGetBigClue.setToolTipText("Get more information on the next solving step");
            mitGetBigClue.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.getClue(true);
                }
            });
        }
        return mitGetBigClue;
    }

    private JMenuItem getMitSolve() {
        if (mitSolve == null) {
            mitSolve = new JMenuItem();
            mitSolve.setText("Solve");
            mitSolve.setMnemonic(KeyEvent.VK_F8);
            mitSolve.setToolTipText("Highlight the solution");
            mitSolve.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    engine.solve();
                }
            });
        }
        return mitSolve;
    }

    private JMenuItem getMitAnalyse() {
        if (mitAnalyse == null) {
            mitAnalyse = new JMenuItem();
            mitAnalyse.setText("Analyze");
            mitAnalyse.setMnemonic(KeyEvent.VK_F9);
            mitAnalyse.setToolTipText("List the rules required to solve the Sudoku " +
            "and get its average difficulty");
            mitAnalyse.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        engine.analyse();
                    } catch (UnsupportedOperationException ex) {
                        JOptionPane.showMessageDialog(SudokuFrame.this,
                                "The Sukaku Explainer failed to solve this Sudoku\n" +
                                "using the solving techniques that are currently enabled.",
                                "Analysis", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return mitAnalyse;
    }

    private JCheckBoxMenuItem getMitAnalyseClipboard() {
        if (mitAnalyseClipboard == null) {
            mitAnalyseClipboard = new JCheckBoxMenuItem();
            mitAnalyseClipboard.setText("... and Copy to Clipboard");
            mitAnalyseClipboard.setToolTipText("Copy the Analysis to the clipboard");
            mitAnalyseClipboard.setSelected(false);
            mitAnalyseClipboard.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    Settings.getInstance().setAnalyseToClipboard(mitAnalyseClipboard.isSelected());
                }
            });
        }
        return mitAnalyseClipboard;
    }

    private JMenu getOptionsMenu() {
        if (optionsMenu == null) {
            optionsMenu = new JMenu();
            optionsMenu.setText("Options");
            optionsMenu.setMnemonic(java.awt.event.KeyEvent.VK_O);
            optionsMenu.add(getMitFilter());
            optionsMenu.add(getMitShowCandidates());
            optionsMenu.add(getMitShowCandidateMasks());
            optionsMenu.add(getMitSelectTechniques());
            optionsMenu.addSeparator();
            optionsMenu.add(getMitSinglesMode());
            optionsMenu.add(getMitBasicsMode());
            optionsMenu.addSeparator();
            optionsMenu.add(getMitChessMode());
            optionsMenu.add(getMitMathMode());
            optionsMenu.addSeparator();
            optionsMenu.add(getMitLookAndFeel());
            optionsMenu.add(getMitAntiAliasing());
            optionsMenu.add(getMitBig());
            optionsMenu.add(getMitRotateClockwise());
            optionsMenu.add(getMitRotateAntiClockwise());
            optionsMenu.add(getMitRotateHorizontal());
            optionsMenu.add(getMitRotateVertical());
            optionsMenu.add(getMitRotateDiagonal());
            optionsMenu.add(getMitRotateAntiDiagonal());
            ButtonGroup group = new ButtonGroup();
            group.add(getMitChessMode());
            group.add(getMitMathMode());
            ButtonGroup apply = new ButtonGroup();
            apply.add(getMitSinglesMode());
            apply.add(getMitBasicsMode());
        }
        return optionsMenu;
    }

    private JCheckBoxMenuItem getMitFilter() {
        if (mitFilter == null) {
            mitFilter = new JCheckBoxMenuItem();
            mitFilter.setText("Filter hints with similar outcome");
            mitFilter.setSelected(true);
            mitFilter.setEnabled(false);
            mitFilter.setMnemonic(KeyEvent.VK_F);
            mitFilter.setToolTipText(getChkFilter().getToolTipText());
            mitFilter.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    engine.setFiltered(mitFilter.isSelected());
                }
            });
        }
        return mitFilter;
    }

    private JCheckBoxMenuItem getMitShowCandidates() {
        if (mitShowCandidates == null) {
            mitShowCandidates = new JCheckBoxMenuItem();
            mitShowCandidates.setText("Show candidates");
            mitShowCandidates.setToolTipText("Display all possible values as small digits in empty cells");
            mitShowCandidates.setMnemonic(KeyEvent.VK_C);
            mitShowCandidates.setSelected(Settings.getInstance().isShowingCandidates());
            mitShowCandidates.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    Settings.getInstance().setShowingCandidates(mitShowCandidates.isSelected());
                    repaint();
                }
            });
        }
        return mitShowCandidates;
    }

    private JCheckBoxMenuItem getMitShowCandidateMasks() {
        if (mitShowCandidateMasks == null) {
            mitShowCandidateMasks = new JCheckBoxMenuItem();
            mitShowCandidateMasks.setText("Show candidate masks");
            mitShowCandidateMasks.setToolTipText("Highlight all possible cells that can fill the same digit");
            mitShowCandidateMasks.setMnemonic(KeyEvent.VK_M);
            mitShowCandidateMasks.setSelected(Settings.getInstance().isShowingCandidateMasks());
            mitShowCandidateMasks.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    Settings.getInstance().setShowingCandidateMasks(mitShowCandidateMasks.isSelected());
                    repaint();
                }
            });
        }
        return mitShowCandidateMasks;
    }

    private JMenuItem getMitSelectTechniques() {
        if (mitSelectTechniques == null) {
            mitSelectTechniques = new JMenuItem();
            mitSelectTechniques.setMnemonic(KeyEvent.VK_T);
            mitSelectTechniques.setToolTipText("Open a dialog window to enable and disable individual solving techniques");
            mitSelectTechniques.setText("Solving techniques...");
            mitSelectTechniques.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectTechniques();
                }
            });
        }
        return mitSelectTechniques;
    }

    private void selectTechniques() {
        if (selectDialog == null || !selectDialog.isVisible()) {
            selectDialog = new TechniquesSelectDialog(this, SudokuFrame.this.engine);
            selectDialog.pack();
            centerDialog(selectDialog);
        }
        selectDialog.setVisible(true);
        refreshSolvingTechniques();
        engine.rebuildSolver();
    }

    private JPanel getPnlEnabledTechniques() {
        if (pnlEnabledTechniques == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setAlignment(FlowLayout.LEFT);
            lblEnabledTechniques = new JLabel();
            lblEnabledTechniques.setToolTipText("<html><body>Not all the available solving techniques are enabled.<br>Use the <b>Options</b>-&gt;<b>Solving techniques</b> menu to<br>enable or disable individual solving techniques.</body></html>");
            lblEnabledTechniques.setIcon(new ImageIcon(getClass().getResource("/diuf/sudoku/gui/Warning.gif")));
            lblEnabledTechniques.setText("");
            lblEnabledTechniques.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        selectTechniques();
                    }
                }
            });
            pnlEnabledTechniques = new JPanel();
            pnlEnabledTechniques.setLayout(flowLayout1);
            pnlEnabledTechniques.add(lblEnabledTechniques, null);
            pnlEnabledTechniques.setVisible(false);
        }
        return pnlEnabledTechniques;
    }

    private JRadioButtonMenuItem getMitSinglesMode() {
        if (mitSinglesMode == null) {
            mitSinglesMode = new JRadioButtonMenuItem();
            mitSinglesMode.setText("Apply button to: Singles (1.0-1.5,2.3)");
            mitSinglesMode.setSelected((Settings.getInstance().getApply()==23));
            mitSinglesMode.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (mitSinglesMode.isSelected()) {
                        Settings.getInstance().setApply(23);
                        btnApplySingles.setText("Apply Singles");
                        btnApplySingles.setToolTipText("Apply all (hidden and naked) singles");
                        repaint();
                    }
                }
            });
        }
        return mitSinglesMode;
    }

    private JRadioButtonMenuItem getMitBasicsMode() {
        if (mitBasicsMode == null) {
            mitBasicsMode = new JRadioButtonMenuItem();
            mitBasicsMode.setText("Apply button to: Basics (1.0-2.8)");
            mitBasicsMode.setSelected((Settings.getInstance().getApply()==28));
            mitBasicsMode.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (mitBasicsMode.isSelected()) {
                        Settings.getInstance().setApply(28);
                        btnApplySingles.setText("Apply Basics");
                        btnApplySingles.setToolTipText("Apply all (hidden and naked) singles and basics");
                        repaint();
                    }
                }
            });
        }
        return mitBasicsMode;
    }

    private JRadioButtonMenuItem getMitChessMode() {
        if (mitChessMode == null) {
            mitChessMode = new JRadioButtonMenuItem();
            mitChessMode.setText("A1 - I9 cell notation");
            mitChessMode.setMnemonic(KeyEvent.VK_A);
            mitChessMode.setSelected(!Settings.getInstance().isRCNotation());
            mitChessMode.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (mitChessMode.isSelected()) {
                        Settings.getInstance().setRCNotation(false);
                        repaint();
                    }
                }
            });
        }
        return mitChessMode;
    }

    private JRadioButtonMenuItem getMitMathMode() {
        if (mitMathMode == null) {
            mitMathMode = new JRadioButtonMenuItem();
            mitMathMode.setText("R1C1 - R9C9 cell notation");
            mitMathMode.setMnemonic(KeyEvent.VK_R);
            mitMathMode.setSelected(Settings.getInstance().isRCNotation());
            mitMathMode.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (mitMathMode.isSelected()) {
                        Settings.getInstance().setRCNotation(true);
                        repaint();
                    }
                }
            });
        }
        return mitMathMode;
    }

    private JMenu getMitLookAndFeel() {
        if (mitLookAndFeel == null) {
            mitLookAndFeel = new JMenu();
            mitLookAndFeel.setText("Look & Feel");
            mitLookAndFeel.setMnemonic(KeyEvent.VK_L);
            mitLookAndFeel.setToolTipText("Change the appearance of the application by choosing one of the available schemes");
        }
        return mitLookAndFeel;
    }

    private JCheckBoxMenuItem getMitAntiAliasing() {
        if (mitAntiAliasing == null) {
            mitAntiAliasing = new JCheckBoxMenuItem();
            mitAntiAliasing.setText("High quality rendering");
            mitAntiAliasing.setSelected(Settings.getInstance().isAntialiasing());
            mitAntiAliasing.setMnemonic(KeyEvent.VK_H);
            mitAntiAliasing.setToolTipText("Use high quality (but slow) rendering");
            mitAntiAliasing.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    Settings.getInstance().setAntialiasing(mitAntiAliasing.isSelected());
                    repaint();
                }
            });
        }
        return mitAntiAliasing;
    }

    private JCheckBoxMenuItem getMitBig() {
        if (mitBig == null) {
            mitBig = new JCheckBoxMenuItem();
            mitBig.setText("Bigger Cells (requires Restart)");
            mitBig.setSelected(Settings.getInstance().isBigCell());
            mitBig.setToolTipText("Make cell size bigger (better on 1920x1080 screen)");
            mitBig.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    Settings.getInstance().setBigCell(mitBig.isSelected());
                }
            });
        }
        return mitBig;
    }

    private JMenuItem getMitRotateClockwise() {
        if (mitRotateClockwise == null) {
            mitRotateClockwise = new JMenuItem();
            mitRotateClockwise.setText("Rotate sudoku \u21bb Clockwise 90\u00b0");
            mitRotateClockwise.setToolTipText("Rotates the sudoku Clockwise 90 degrees");
            mitRotateClockwise.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateClockwise();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitRotateClockwise;
    }

    private JMenuItem getMitRotateAntiClockwise() {
        if (mitRotateAntiClockwise == null) {
            mitRotateAntiClockwise = new JMenuItem();
            mitRotateAntiClockwise.setText("Rotate sudoku \u21ba Anti-Clockwise 90\u00b0");
            mitRotateAntiClockwise.setToolTipText("Rotates the sudoku Anti-Clockwise 90 degrees");
            mitRotateAntiClockwise.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateAntiClockwise();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitRotateAntiClockwise;
    }

    private JMenuItem getMitRotateHorizontal() {
        if (mitRotateHorizontal == null) {
            mitRotateHorizontal = new JMenuItem();
            mitRotateHorizontal.setText("Rotate sudoku \u2015 Horizontally");
            mitRotateHorizontal.setToolTipText("Rotates the sudoku Horizontally");
            mitRotateHorizontal.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateHorizontal();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitRotateHorizontal;
    }

    private JMenuItem getMitRotateVertical() {
        if (mitRotateVertical == null) {
            mitRotateVertical = new JMenuItem();
            mitRotateVertical.setText("Rotate sudoku \ufe31 Vertically");
            mitRotateVertical.setToolTipText("Rotates the sudoku Vertically");
            mitRotateVertical.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateVertical();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitRotateVertical;
    }

    private JMenuItem getMitRotateDiagonal() {
        if (mitRotateDiagonal == null) {
            mitRotateDiagonal = new JMenuItem();
            mitRotateDiagonal.setText("Rotate sudoku \u2571 Diagonally");
            mitRotateDiagonal.setToolTipText("Rotates the sudoku Diagonally");
            mitRotateDiagonal.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateDiagonal();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitRotateDiagonal;
    }

    private JMenuItem getMitRotateAntiDiagonal() {
        if (mitRotateAntiDiagonal == null) {
            mitRotateAntiDiagonal = new JMenuItem();
            mitRotateAntiDiagonal.setText("Rotate sudoku \u2572 Anti-Diagonally");
            mitRotateAntiDiagonal.setToolTipText("Rotates the sudoku Anti-Diagonally");
            mitRotateAntiDiagonal.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateAntiDiagonal();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitRotateAntiDiagonal;
    }

    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setText("Help");
            helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
            helpMenu.add(getMitShowWelcome());
            getMitShowWelcome().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
            helpMenu.add(getMitShowFeatures());
            helpMenu.addSeparator();
            helpMenu.add(getMitAbout());
        }
        return helpMenu;
    }

    private JMenuItem getMitShowWelcome() {
        if (mitShowWelcome == null) {
            mitShowWelcome = new JMenuItem();
            mitShowWelcome.setMnemonic(java.awt.event.KeyEvent.VK_W);
            mitShowWelcome.setToolTipText("Show the explanation text displayed when the application is started");
            mitShowWelcome.setText("Show welcome message");
            mitShowWelcome.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showWelcomeText();
                }
            });
        }
        return mitShowWelcome;
    }

    private JMenuItem getMitShowFeatures() {
        if (mitShowFeatures == null) {
            mitShowFeatures = new JMenuItem();
            mitShowFeatures.setMnemonic(java.awt.event.KeyEvent.VK_F);
            mitShowFeatures.setToolTipText("Show details of new features added by 1to9only");
            mitShowFeatures.setText("Show new features");
            mitShowFeatures.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showFeaturesText();
                }
            });
        }
        return mitShowFeatures;
    }

    private JMenuItem getMitAbout() {
        if (mitAbout == null) {
            mitAbout = new JMenuItem();
            mitAbout.setText("About");
            mitAbout.setToolTipText("Get information about the Sukaku Explainer application");
            mitAbout.setMnemonic(java.awt.event.KeyEvent.VK_A);
            mitAbout.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (dummyFrameKnife == null) {
                        dummyFrameKnife = new JFrame();
                        ImageIcon icon = createImageIcon("Knife.gif");
                        dummyFrameKnife.setIconImage(icon.getImage());
                    }
                    AboutDialog dlg = new AboutDialog(dummyFrameKnife);
                    centerDialog(dlg);
                    dlg.setVisible(true);
                }
            });
        }
        return mitAbout;
    }

    private JMenu getVariantsMenu() {
        if (VariantsMenu == null) {
            VariantsMenu = new JMenu();
            VariantsMenu.setText("Variants");
            VariantsMenu.setMnemonic(java.awt.event.KeyEvent.VK_V);
            VariantsMenu.add(getMitVanilla());
            VariantsMenu.add(getMitLatinSquare());
            VariantsMenu.addSeparator();
            VariantsMenu.add(getMitDiagonals());
            VariantsMenu.add(getMitDisjointGroups());
            VariantsMenu.add(getMitWindoku());
            VariantsMenu.add(getMitClover());
            VariantsMenu.addSeparator();
            VariantsMenu.add(getMitAsterisk());
            VariantsMenu.add(getMitCenterDot());
            VariantsMenu.add(getMitGirandola());
            VariantsMenu.addSeparator();
            VariantsMenu.add(getMitHalloween());
            VariantsMenu.add(getMitPerCent());
            VariantsMenu.add(getMitSdoku());
            VariantsMenu.add(getMitExtraRegions());
            VariantsMenu.add(getMitCustomText());
        //  VariantsMenu.add(getMitCustomFile());
        //  VariantsMenu.add(getMitCustomCopy());
            VariantsMenu.addSeparator();
            VariantsMenu.add(getMitCustomClockwise());
            VariantsMenu.add(getMitCustomAntiClockwise());
            VariantsMenu.add(getMitCustomHorizontal());
            VariantsMenu.add(getMitCustomVertical());
            VariantsMenu.add(getMitCustomDiagonal());
            VariantsMenu.add(getMitCustomAntiDiagonal());
        }
        return VariantsMenu;
    }

    private JCheckBoxMenuItem getMitLatinSquare() {
        if (mitLatinSquare == null) {
            mitLatinSquare = new JCheckBoxMenuItem();
            mitLatinSquare.setText("Latin Square");
            mitLatinSquare.setToolTipText("Sets the puzzle type to Latin Square");
            mitLatinSquare.setSelected(Settings.getInstance().isLatinSquare());
            mitLatinSquare.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                // if ( mitLatinSquare.isSelected() ) {
                //  if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                //  if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                //  if ( Settings.getInstance().isSdoku() )   { mitSdoku.setSelected(false); }
                // }
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().setLatinSquare(mitLatinSquare.isSelected());
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateLatinSquare();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitLatinSquare;
    }

    private JMenuItem getMitVanilla() {
        if (mitVanilla == null) {
            mitVanilla = new JMenuItem();
            mitVanilla.setText("Classic Sudoku");
            mitVanilla.setToolTipText("Sets the puzzle type to Vanilla Sudoku (unselects all variants)");
            mitVanilla.setSelected(false);
            mitVanilla.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if ( Settings.getInstance().isLatinSquare() )    { mitLatinSquare.setSelected(false); }
                    if ( Settings.getInstance().isDiagonals() )      { mitDiagonals.setSelected(false); }
                    if ( Settings.getInstance().isDisjointGroups() ) { mitDisjointGroups.setSelected(false); }
                    if ( Settings.getInstance().isWindoku() )        { mitWindoku.setSelected(false); }
                    if ( Settings.getInstance().isClover() )         { mitClover.setSelected(false); }
                    if ( Settings.getInstance().isAsterisk() )       { mitAsterisk.setSelected(false); }
                    if ( Settings.getInstance().isCenterDot() )      { mitCenterDot.setSelected(false); }
                    if ( Settings.getInstance().isGirandola() )      { mitGirandola.setSelected(false); }
                    if ( Settings.getInstance().isHalloween() )      { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )        { mitPerCent.setSelected(false); }
                    if ( Settings.getInstance().isSdoku() )          { mitSdoku.setSelected(false); }
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateVanilla();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitVanilla;
    }

    private JCheckBoxMenuItem getMitDiagonals() {
        if (mitDiagonals == null) {
            mitDiagonals = new JCheckBoxMenuItem();
            mitDiagonals.setText("Diagonals (X)");
            mitDiagonals.setToolTipText("Sets the puzzle type to Diagonals (X)");
            mitDiagonals.setSelected(Settings.getInstance().isDiagonals());
            mitDiagonals.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                // if ( mitDiagonals.isSelected() ) {
                //  if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                //  if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                //  if ( Settings.getInstance().isSdoku() )     { mitSdoku.setSelected(false); }
                // }
                    Settings.getInstance().setDiagonals(mitDiagonals.isSelected());
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateDiagonals();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitDiagonals;
    }

    private JCheckBoxMenuItem getMitDisjointGroups() {
        if (mitDisjointGroups == null) {
            mitDisjointGroups = new JCheckBoxMenuItem();
            mitDisjointGroups.setText("Disjoint Groups");
            mitDisjointGroups.setToolTipText("Sets the puzzle type to Disjoint Groups");
            mitDisjointGroups.setSelected(Settings.getInstance().isDisjointGroups());
            mitDisjointGroups.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                   if ( mitDisjointGroups.isSelected() ) {
                    if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                    if ( Settings.getInstance().isSdoku() )     { mitSdoku.setSelected(false); }
                   }
                    Settings.getInstance().setDisjointGroups(mitDisjointGroups.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateDisjointGroups();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitDisjointGroups;
    }

    private JCheckBoxMenuItem getMitWindoku() {
        if (mitWindoku == null) {
            mitWindoku = new JCheckBoxMenuItem();
            mitWindoku.setText("Windoku");
            mitWindoku.setToolTipText("Sets the puzzle type to Windoku");
            mitWindoku.setSelected(Settings.getInstance().isWindoku());
            mitWindoku.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if ( mitWindoku.isSelected() != Settings.getInstance().isWindoku() ) {
                        Settings.getInstance().setWindoku(mitWindoku.isSelected());
                        if ( Settings.getInstance().isWindoku() && Settings.getInstance().isAsterisk() ) {
                            if ( !Settings.getInstance().isClover() ) {
                                Settings.getInstance().setClover(true);
                                mitClover.setSelected(true);
                            }
                        }
                        else {
                            if ( Settings.getInstance().isClover() ) {
                                Settings.getInstance().setClover(false);
                                mitClover.setSelected(false);
                            }
                        }
                    }
                   if ( mitWindoku.isSelected() ) {
                    if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                //  if ( Settings.getInstance().isSdoku() )     { mitSdoku.setSelected(false); }
                   }
                    Settings.getInstance().setWindoku(mitWindoku.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateWindoku();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitWindoku;
    }

    private JCheckBoxMenuItem getMitClover() {
        if (mitClover == null) {
            mitClover = new JCheckBoxMenuItem();
            mitClover.setText("Clover (W+A)");
            mitClover.setToolTipText("Sets the puzzle type to Clover (W+A)");
            mitClover.setSelected(Settings.getInstance().isClover());
            mitClover.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if ( mitClover.isSelected() != Settings.getInstance().isClover() ) {
                        Settings.getInstance().setClover(mitClover.isSelected());
                        Settings.getInstance().setWindoku(mitClover.isSelected());
                        Settings.getInstance().setAsterisk(mitClover.isSelected());
                        mitWindoku.setSelected(Settings.getInstance().isWindoku());
                        mitAsterisk.setSelected(Settings.getInstance().isAsterisk());
                    }
                   if ( mitClover.isSelected() ) {
                    if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                    if ( Settings.getInstance().isSdoku() )     { mitSdoku.setSelected(false); }
                   }
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    repaint();
                }
            });
        }
        return mitClover;
    }

    private JCheckBoxMenuItem getMitAsterisk() {
        if (mitAsterisk == null) {
            mitAsterisk = new JCheckBoxMenuItem();
            mitAsterisk.setText("Asterisk");
            mitAsterisk.setToolTipText("Sets the puzzle type to Asterisk");
            mitAsterisk.setSelected(Settings.getInstance().isAsterisk());
            mitAsterisk.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if ( mitAsterisk.isSelected() != Settings.getInstance().isAsterisk() ) {
                        Settings.getInstance().setAsterisk(mitAsterisk.isSelected());
                        if ( Settings.getInstance().isWindoku() && Settings.getInstance().isAsterisk() ) {
                            if ( !Settings.getInstance().isClover() ) {
                                Settings.getInstance().setClover(true);
                                mitClover.setSelected(true);
                            }
                        }
                        else {
                            if ( Settings.getInstance().isClover() ) {
                                Settings.getInstance().setClover(false);
                                mitClover.setSelected(false);
                            }
                        }
                    }
                   if ( mitAsterisk.isSelected() ) {
                    if ( Settings.getInstance().isCenterDot() ) { mitCenterDot.setSelected(false); }
                    if ( Settings.getInstance().isGirandola() ) { mitGirandola.setSelected(false); }
                    if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                    if ( Settings.getInstance().isSdoku() )     { mitSdoku.setSelected(false); }
                   }
                    Settings.getInstance().setAsterisk(mitAsterisk.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateAsterisk();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitAsterisk;
    }

    private JCheckBoxMenuItem getMitCenterDot() {
        if (mitCenterDot == null) {
            mitCenterDot = new JCheckBoxMenuItem();
            mitCenterDot.setText("Center Dot");
            mitCenterDot.setToolTipText("Sets the puzzle type to Center Dot");
            mitCenterDot.setSelected(Settings.getInstance().isCenterDot());
            mitCenterDot.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                   if ( mitCenterDot.isSelected() ) {
                    if ( Settings.getInstance().isAsterisk() ) { mitAsterisk.setSelected(false); }
                    if ( Settings.getInstance().isGirandola() ) { mitGirandola.setSelected(false); }
                    if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                    if ( Settings.getInstance().isSdoku() )     { mitSdoku.setSelected(false); }
                   }
                    Settings.getInstance().setCenterDot(mitCenterDot.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateCenterDot();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitCenterDot;
    }

    private JCheckBoxMenuItem getMitGirandola() {
        if (mitGirandola == null) {
            mitGirandola = new JCheckBoxMenuItem();
            mitGirandola.setText("Girandola");
            mitGirandola.setToolTipText("Sets the puzzle type to Girandola");
            mitGirandola.setSelected(Settings.getInstance().isGirandola());
            mitGirandola.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                   if ( mitGirandola.isSelected() ) {
                    if ( Settings.getInstance().isAsterisk() ) { mitAsterisk.setSelected(false); }
                    if ( Settings.getInstance().isCenterDot() ) { mitCenterDot.setSelected(false); }
                    if ( Settings.getInstance().isHalloween() ) { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )   { mitPerCent.setSelected(false); }
                    if ( Settings.getInstance().isSdoku() )     { mitSdoku.setSelected(false); }
                   }
                    Settings.getInstance().setGirandola(mitGirandola.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateGirandola();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitGirandola;
    }

    private JCheckBoxMenuItem getMitHalloween() {
        if (mitHalloween == null) {
            mitHalloween = new JCheckBoxMenuItem();
            mitHalloween.setText("Halloween");
            mitHalloween.setToolTipText("Sets the puzzle type to Halloween");
            mitHalloween.setSelected(Settings.getInstance().isHalloween());
            mitHalloween.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if ( mitHalloween.isSelected() ) {
                        if ( Settings.getInstance().isLatinSquare() )    { mitLatinSquare.setSelected(false); }
                        if ( Settings.getInstance().isDiagonals() )      { mitDiagonals.setSelected(false); }
                        if ( Settings.getInstance().isDisjointGroups() ) { mitDisjointGroups.setSelected(false); }
                        if ( Settings.getInstance().isWindoku() )        { mitWindoku.setSelected(false); }
                        if ( Settings.getInstance().isClover() )         { mitClover.setSelected(false); }
                        if ( Settings.getInstance().isAsterisk() )       { mitAsterisk.setSelected(false); }
                        if ( Settings.getInstance().isCenterDot() )      { mitCenterDot.setSelected(false); }
                        if ( Settings.getInstance().isGirandola() )      { mitGirandola.setSelected(false); }
                        if ( Settings.getInstance().isPerCent() )        { mitPerCent.setSelected(false); }
                        if ( Settings.getInstance().isSdoku() )          { mitSdoku.setSelected(false); }
                    }
                    Settings.getInstance().setHalloween(mitHalloween.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateHalloween();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitHalloween;
    }

    private JCheckBoxMenuItem getMitPerCent() {
        if (mitPerCent == null) {
            mitPerCent = new JCheckBoxMenuItem();
            mitPerCent.setText("Per Cent");
            mitPerCent.setToolTipText("Sets the puzzle type to Per Cent");
            mitPerCent.setSelected(Settings.getInstance().isPerCent());
            mitPerCent.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if ( mitPerCent.isSelected() ) {
                        if ( Settings.getInstance().isLatinSquare() )    { mitLatinSquare.setSelected(false); }
                        if ( Settings.getInstance().isDiagonals() )      { mitDiagonals.setSelected(false); }
                        if ( Settings.getInstance().isDisjointGroups() ) { mitDisjointGroups.setSelected(false); }
                        if ( Settings.getInstance().isWindoku() )        { mitWindoku.setSelected(false); }
                        if ( Settings.getInstance().isClover() )         { mitClover.setSelected(false); }
                        if ( Settings.getInstance().isAsterisk() )       { mitAsterisk.setSelected(false); }
                        if ( Settings.getInstance().isCenterDot() )      { mitCenterDot.setSelected(false); }
                        if ( Settings.getInstance().isGirandola() )      { mitGirandola.setSelected(false); }
                        if ( Settings.getInstance().isHalloween() )      { mitHalloween.setSelected(false); }
                        if ( Settings.getInstance().isSdoku() )          { mitSdoku.setSelected(false); }
                    }
                    Settings.getInstance().setPerCent(mitPerCent.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updatePerCent();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitPerCent;
    }

    private JCheckBoxMenuItem getMitSdoku() {
        if (mitSdoku == null) {
            mitSdoku = new JCheckBoxMenuItem();
            mitSdoku.setText("S-doku");
            mitSdoku.setToolTipText("Sets the puzzle type to S-doku");
            mitSdoku.setSelected(Settings.getInstance().isSdoku());
            mitSdoku.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if ( mitSdoku.isSelected() ) {
                        if ( Settings.getInstance().isLatinSquare() )    { mitLatinSquare.setSelected(false); }
                        if ( Settings.getInstance().isDiagonals() )      { mitDiagonals.setSelected(false); }
                        if ( Settings.getInstance().isDisjointGroups() ) { mitDisjointGroups.setSelected(false); }
                        if ( Settings.getInstance().isWindoku() )        { mitWindoku.setSelected(false); }
                        if ( Settings.getInstance().isClover() )         { mitClover.setSelected(false); }
                        if ( Settings.getInstance().isAsterisk() )       { mitAsterisk.setSelected(false); }
                        if ( Settings.getInstance().isCenterDot() )      { mitCenterDot.setSelected(false); }
                        if ( Settings.getInstance().isGirandola() )      { mitGirandola.setSelected(false); }
                        if ( Settings.getInstance().isHalloween() )      { mitHalloween.setSelected(false); }
                        if ( Settings.getInstance().isPerCent() )        { mitPerCent.setSelected(false); }
                    }
                    Settings.getInstance().setSdoku(mitSdoku.isSelected());
                    Settings.getInstance().setCustom(false);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateSdoku();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(false);
                    mitCustomClockwise.setVisible(false);
                    mitCustomAntiClockwise.setVisible(false);
                    mitCustomHorizontal.setVisible(false);
                    mitCustomVertical.setVisible(false);
                    mitCustomDiagonal.setVisible(false);
                    mitCustomAntiDiagonal.setVisible(false);
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitSdoku;
    }

    private JMenuItem getMitExtraRegions() {
        if (mitExtraRegions == null) {
            mitExtraRegions = new JMenuItem();
            mitExtraRegions.setText("Extra Regions (of 48)");
            mitExtraRegions.setToolTipText("Load a Random Extra Regions variant layout");
            mitExtraRegions.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Random random = new Random();
                    int index = random.nextInt(48) + 1;
                    String line = null;
                    if ( index ==  1 ) line = new String( "..........11........1..22....1..2.2...1...22...1....2..111...2.......2...........  1 19");
                    if ( index ==  2 ) line = new String( ".....1........11.......111......11.....2.1.....22......222.......22........2.....  2 2FLAGS1");
                    if ( index ==  3 ) line = new String( ".............111.......111.....111.............222.....222.......222.............  3 2FLAGS2");
                    if ( index ==  4 ) line = new String( ".1111.....1...1....1..1.....1............2.2.......2.........2........2.....2222.  4 2XR1");
                    if ( index ==  5 ) line = new String( "..........111......1.11.....11........1...2........22.....22.2......222..........  5 2XR2");
                    if ( index ==  6 ) line = new String( "...........111.....111......11.......1.....2.......22......222.....222...........  6 2XR3");
                    if ( index ==  7 ) line = new String( "............1.2.....1...2....1...2.....1.2.....1...2...11...22.11.....22.........  7 2XRABBIT1");
                    if ( index ==  8 ) line = new String( "............1...22..1...22...1...2.....1.2.....1...2...11...2..11...2............  8 2XRABBIT2");
                    if ( index ==  9 ) line = new String( "....1...1...1.1111.2....1.12..33....2....3...222.33.....2.3.....22..3.........33.  9 3XR1");
                    if ( index == 10 ) line = new String( ".111........1.22.2.1.1.2..2.111.2222.........3333.444.3..3.4.4.3.33.4........444. 10 4XRG");
                    if ( index == 11 ) line = new String( "..11.22....11.22..111...22211.....22.........33.....44333...444..33.44....33.44.. 11 4XRL");
                    if ( index == 12 ) line = new String( "...1.2....11...22..111.222.1.11.22.2.........3.33.44.4.333.444..33...44....3.4... 12 4XRLIFE");
                    if ( index == 13 ) line = new String( ".111......1.1.2222.1.1....2.1.1.2222.........3333.4.4.3....4.4.3333.4.4......444. 13 4XRU");
                    if ( index == 14 ) line = new String( "11.....221111.2222.11.3.22..1.333.2.....3.....4.333.5..44.3.55.4444.555544.....55 14 5XR1");
                    if ( index == 15 ) line = new String( "1.......1.1.....1...1...1.....1.1...2...1...2.2.....2...2...2.....2.2.......2.... 15 DOWN");
                    if ( index == 16 ) line = new String( "..........1.....2...1...2.....1.2....111.222....1.2.....1...2...1.....2.......... 16 EE");
                    if ( index == 17 ) line = new String( "..........22......2..2.....222...11.2....1...2....1........1.11......11.......... 17 PG");
                    if ( index == 18 ) line = new String( "111.......1........1.222....1..2....111.2.333....2..3....222.3........3.......333 18 III");
                    if ( index == 19 ) line = new String( "....1...2...1...2...1...2...1...2...1...2.....1...2.....1...2.....1...2.....1...2 19 LEFT");
                    if ( index == 20 ) line = new String( "1....333.1.....3..1.....3..1.....3..1....333.1111.........22.22....2.2.2....2...2 20 LIM");
                    if ( index == 21 ) line = new String( "1.1.......1.1......1.1......1.1........1..........22......2..2.....2..2......22.2 21 NQ1");
                    if ( index == 22 ) line = new String( ".1.1.....1.1......1.1......1.1........1............22......2..2.....2..2....2.22. 22 NQ2");
                    if ( index == 23 ) line = new String( "...1.2.....1...2...1.....2.1.......2.1.....2.1.......2.1.....2...1...2.....1.2... 23 O");
                    if ( index == 24 ) line = new String( "........2.11111.2..1....2...1...2.3..1..2..3..1.2...3...2....3..2.33333.2........ 24 PC1");
                    if ( index == 25 ) line = new String( "11111...21......2.1.....2..1....2...1...2...3...2....3..2.....3.2......32...33333 25 PC2");
                    if ( index == 26 ) line = new String( "..11111.....1.1...22.1.1.332.......3222...3332.......322.4.4.33...4.4.....44444.. 26 PI");
                    if ( index == 27 ) line = new String( ".2.2.111..2.2.1.1..2.2.111..222.1.............3...444..333.4....3.3.4.4..333.444. 27 UPBG");
                    if ( index == 28 ) line = new String( ".11111.....111...2...1...223.....22233.....22333.....233...4...3...444.....44444. 28 PYRAMIDS");
                    if ( index == 29 ) line = new String( "..........111.222..1.1.2.2....1...2...1...2....1...2.............1...2........... 29 QQ");
                    if ( index == 30 ) line = new String( "1...2.....1...2.....1...2.....1...2.....1...2...1...2...1...2...1...2...1...2.... 30 RIGHT");
                    if ( index == 31 ) line = new String( "..11111....1......2.1.333332.1.....32.1...4.32.....4.322222.4.3......4....44444.. 31 SPIRALS");
                    if ( index == 32 ) line = new String( "..............1........111...2.111....2...1....222.1...222........2.............. 32 STAR");
                    if ( index == 33 ) line = new String( "....1.......1.1.....1...1...1.....1.1...2...1...2.2.....2...2...2.....2.2.......2 33 UP");
                    if ( index == 34 ) line = new String( "....11111........1........1........12.......12........2........2........22222.... 34 VV1");
                    if ( index == 35 ) line = new String( "............11111........1..2.....1..2.....1..2.....1..2........22222............ 35 VV2");
                    if ( index == 36 ) line = new String( "...................2.11111..2.....1..2.....1..2.....1..22222.1................... 36 VV3");
                    if ( index == 37 ) line = new String( "..........1...1.1.1.1.1.1.1...1...............2.2...2.2.2.2.2.2.....2............ 37 WAVES1");
                    if ( index == 38 ) line = new String( "..........1...1.1.1.1.1.1.1...1...................2...2.2.2.2.2.2.2...2.......... 38 WAVES2");
                    if ( index == 39 ) line = new String( "....11........1........1........11112.......12222........2........2........22.... 39 WW1");
                    if ( index == 40 ) line = new String( "....1........1......111......1......111...222......2......222......2........2.... 40 WW2");
                    if ( index == 41 ) line = new String( "1.......2.1.....2...1...2.....1.2......1.2......1.2.....1...2...1.....2.1.......2 41 X1");
                    if ( index == 42 ) line = new String( "1.......2.1.....2...1...2.....1.2.....1...2.....1.2.....1...2...1.....2.1.......2 42 X2");
                    if ( index == 43 ) line = new String( ".........1...1....11111......1........1...2........2......22222....2...2......... 43 YY1");
                    if ( index == 44 ) line = new String( "..................1...1....11111.2....1...2....1.22222....2...2.................. 44 YY2");
                    if ( index == 45 ) line = new String( "................1..1111111..1..............2..2222222..2......................... 45 ZZ1");
                    if ( index == 46 ) line = new String( "................1..1111111..1.......................2..2222222..2................ 46 ZZ2");
                    if ( index == 47 ) line = new String( ".......1..1111111..1..............2..2222222..2..............3..3333333..3....... 47 ZZ3");
                    if ( index == 48 ) line = new String( "...1.2.....11.22...11...22.11.....221...3...2.1..3..2.....3........3......33333.. 48 TR3");
                    line = line.replace( ".", "0");
                    Settings settings = Settings.getInstance();
                    settings.setCustom( line.substring( 0, 81));
                    sudokuPanel.getSudokuGrid().customInitialize( line.substring( 0, 81));

                    if ( Settings.getInstance().isLatinSquare() )    { mitLatinSquare.setSelected(false); }
                    if ( Settings.getInstance().isDiagonals() )      { mitDiagonals.setSelected(false); }
                    if ( Settings.getInstance().isDisjointGroups() ) { mitDisjointGroups.setSelected(false); }
                    if ( Settings.getInstance().isWindoku() )        { mitWindoku.setSelected(false); }
                    if ( Settings.getInstance().isClover() )         { mitClover.setSelected(false); }
                    if ( Settings.getInstance().isAsterisk() )       { mitAsterisk.setSelected(false); }
                    if ( Settings.getInstance().isCenterDot() )      { mitCenterDot.setSelected(false); }
                    if ( Settings.getInstance().isGirandola() )      { mitGirandola.setSelected(false); }
                    if ( Settings.getInstance().isHalloween() )      { mitHalloween.setSelected(false); }
                    if ( Settings.getInstance().isPerCent() )        { mitPerCent.setSelected(false); }
                    if ( Settings.getInstance().isSdoku() )          { mitSdoku.setSelected(false); }
                    Settings.getInstance().setCustom(true);
                    Settings.getInstance().saveChanged();
                    sudokuPanel.getSudokuGrid().updateCustom();
        //          mitCustomCopy.setVisible(true);
                    mitCustomClockwise.setVisible(true);
                    mitCustomAntiClockwise.setVisible(true);
                    mitCustomHorizontal.setVisible(true);
                    mitCustomVertical.setVisible(true);
                    mitCustomDiagonal.setVisible(true);
                    mitCustomAntiDiagonal.setVisible(true);
                    engine.clearGrid();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitExtraRegions;
    }

    private JMenuItem getMitCustomText() {
        if (mitCustomText == null) {
            mitCustomText = new JMenuItem();
            mitCustomText.setText("Custom... (text input)");
            mitCustomText.setToolTipText("Load a Custom variant layout (text input)");
            mitCustomText.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                  String inputtext = null;
                  boolean isValidInput = false;
                  while ( !isValidInput ) {
                    inputtext = (String)JOptionPane.showInputDialog(
                        SudokuFrame.this, "Enter custom variant layout (81-chars), must be valid, 2-4 extra regions best.",
                        "Load Custom", JOptionPane.PLAIN_MESSAGE, null, null, "");
                    if ( inputtext != null && inputtext.length() >= 81 ) {
                        isValidInput = true;
                        int chcount = 0;
                        for (int i=0; i<81; i++ ) {
                            char ch = inputtext.charAt(i);
                            if ( (ch>='1' && ch<='9') || (ch>='A' && ch<='I') ) { chcount++; }
                            else
                            if ( ch!='.' && ch !='0' ) { JOptionPane.showMessageDialog(SudokuFrame.this, "Invalid char: "+ch, "Load Custom", JOptionPane.WARNING_MESSAGE); isValidInput = false; break; }
                        }
                      if ( isValidInput ) {
                        for (int value=1; value<=9; value++ ) {
                            char ch = (char)('0'+value);
                            chcount = 0;
                            for (int i=0; i<81; i++ ) {
                                if ( ch == inputtext.charAt(i) ) { chcount++; }
                            }
                            if ( chcount != 0 ) {
                                if ( chcount > 9 ) { JOptionPane.showMessageDialog(SudokuFrame.this, "Too many: "+ch, "Load Custom", JOptionPane.WARNING_MESSAGE); isValidInput = false; break; }
                                if ( chcount < 9 ) { JOptionPane.showMessageDialog(SudokuFrame.this, "Too few: "+ch, "Load Custom", JOptionPane.WARNING_MESSAGE); isValidInput = false; break; }
                            }
                        }
                      }
                      if ( isValidInput ) {
                        for (int value=1; value<=9; value++ ) {
                            char ch = (char)('@'+value);
                            chcount = 0;
                            for (int i=0; i<81; i++ ) {
                                if ( ch == inputtext.charAt(i) ) { chcount++; }
                            }
                            if ( chcount != 0 ) {
                                if ( chcount > 9 ) { JOptionPane.showMessageDialog(SudokuFrame.this, "Too many: "+ch, "Load Custom", JOptionPane.WARNING_MESSAGE); isValidInput = false; break; }
                                if ( chcount < 9 ) { JOptionPane.showMessageDialog(SudokuFrame.this, "Too few: "+ch, "Load Custom", JOptionPane.WARNING_MESSAGE); isValidInput = false; break; }
                            }
                        }
                      }
                      if ( isValidInput ) {
                        int adjoins = 0;
                        for (int y=0; y<8; y++ ) {
                          for (int x=0; x<8; x++ ) {
                            int index = y*9+x;
                            char ch = inputtext.charAt(index);
                            if ( ch != '.' && ch != '0' ) {
                              char cri = inputtext.charAt(index+1);
                              if ( cri != '.' && cri != '0' && cri != ch ) {
                                 adjoins += 1;
                              }
                              char cbe = inputtext.charAt(index+9);
                              if ( cbe != '.' && cbe != '0' && cbe != ch ) {
                                 adjoins += 1;
                              }
                            }
                          }
                        }
                        if ( adjoins != 0 ) {
                          JOptionPane.showMessageDialog(SudokuFrame.this, "Warning: Extra regions in contact, i.e. touching! ("+adjoins+")", "Load Custom", JOptionPane.WARNING_MESSAGE); isValidInput = false; break;
                        }
                      }
                    }
                    else
                    if ( inputtext == null ) {      // Cancelled
                        isValidInput = true;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(SudokuFrame.this, "Error: Text input: Incorrect length (must be 81-chars)", "Load Custom", JOptionPane.WARNING_MESSAGE); isValidInput = false;
                    }
                  }
                  if ( isValidInput ) {
                    if ( inputtext != null && inputtext.length() >= 81 ) {
                        inputtext = inputtext.replace( "A", "1"); inputtext = inputtext.replace( "B", "2"); inputtext = inputtext.replace( "C", "3"); inputtext = inputtext.replace( "D", "4"); inputtext = inputtext.replace( "E", "5"); inputtext = inputtext.replace( "F", "6"); inputtext = inputtext.replace( "G", "7"); inputtext = inputtext.replace( "H", "8"); inputtext = inputtext.replace( "I", "9");
                        Settings settings = Settings.getInstance();
                        settings.setCustom( inputtext.substring( 0, 81));
                        sudokuPanel.getSudokuGrid().customInitialize( inputtext.substring( 0, 81));
                        if ( Settings.getInstance().isLatinSquare() )    { mitLatinSquare.setSelected(false); }
                        if ( Settings.getInstance().isDiagonals() )      { mitDiagonals.setSelected(false); }
                        if ( Settings.getInstance().isDisjointGroups() ) { mitDisjointGroups.setSelected(false); }
                        if ( Settings.getInstance().isWindoku() )        { mitWindoku.setSelected(false); }
                        if ( Settings.getInstance().isClover() )         { mitClover.setSelected(false); }
                        if ( Settings.getInstance().isAsterisk() )       { mitAsterisk.setSelected(false); }
                        if ( Settings.getInstance().isCenterDot() )      { mitCenterDot.setSelected(false); }
                        if ( Settings.getInstance().isGirandola() )      { mitGirandola.setSelected(false); }
                        if ( Settings.getInstance().isHalloween() )      { mitHalloween.setSelected(false); }
                        if ( Settings.getInstance().isPerCent() )        { mitPerCent.setSelected(false); }
                        if ( Settings.getInstance().isSdoku() )          { mitSdoku.setSelected(false); }
                        Settings.getInstance().setCustom(true);
                        Settings.getInstance().saveChanged();
                        sudokuPanel.getSudokuGrid().updateCustom();
        //              mitCustomCopy.setVisible(true);
                        mitCustomClockwise.setVisible(true);
                        mitCustomAntiClockwise.setVisible(true);
                        mitCustomHorizontal.setVisible(true);
                        mitCustomVertical.setVisible(true);
                        mitCustomDiagonal.setVisible(true);
                        mitCustomAntiDiagonal.setVisible(true);
                        engine.clearGrid();
                        engine.rebuildSolver();
                        engine.resetPotentials();
                        repaint();
                    }
                  }
                }
            });
        }
        return mitCustomText;
    }

    private JMenuItem getMitCustomFile() {
        if (mitCustomFile == null) {
            mitCustomFile = new JMenuItem();
            mitCustomFile.setText("Custom... (from file)");
            mitCustomFile.setToolTipText("Load a Custom variant layout (from file)");
            mitCustomFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new TextFileFilter());
                        if (defaultDirectory != null)
                            chooser.setCurrentDirectory(defaultDirectory);
                        int result = chooser.showOpenDialog(SudokuFrame.this);
                        defaultDirectory = chooser.getCurrentDirectory();
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = chooser.getSelectedFile();
                            if ( engine.loadCustom(selectedFile) == 1 ) {
                                if ( Settings.getInstance().isLatinSquare() )    { mitLatinSquare.setSelected(false); }
                                if ( Settings.getInstance().isDiagonals() )      { mitDiagonals.setSelected(false); }
                                if ( Settings.getInstance().isDisjointGroups() ) { mitDisjointGroups.setSelected(false); }
                                if ( Settings.getInstance().isWindoku() )        { mitWindoku.setSelected(false); }
                                if ( Settings.getInstance().isClover() )         { mitClover.setSelected(false); }
                                if ( Settings.getInstance().isAsterisk() )       { mitAsterisk.setSelected(false); }
                                if ( Settings.getInstance().isCenterDot() )      { mitCenterDot.setSelected(false); }
                                if ( Settings.getInstance().isGirandola() )      { mitGirandola.setSelected(false); }
                                if ( Settings.getInstance().isHalloween() )      { mitHalloween.setSelected(false); }
                                if ( Settings.getInstance().isPerCent() )        { mitPerCent.setSelected(false); }
                                if ( Settings.getInstance().isSdoku() )          { mitSdoku.setSelected(false); }
                                Settings.getInstance().setCustom(true);
                                Settings.getInstance().saveChanged();
                                sudokuPanel.getSudokuGrid().updateCustom();
        //                      mitCustomCopy.setVisible(true);
                                mitCustomClockwise.setVisible(true);
                                mitCustomAntiClockwise.setVisible(true);
                                mitCustomHorizontal.setVisible(true);
                                mitCustomVertical.setVisible(true);
                                mitCustomDiagonal.setVisible(true);
                                mitCustomAntiDiagonal.setVisible(true);
                                engine.clearGrid();
                                engine.rebuildSolver();
                                engine.resetPotentials();
                                repaint();
                            }
                        }
                    } catch (AccessControlException ex) {
                        warnAccessError(ex);
                    }
                }
            });
        }
        return mitCustomFile;
    }

    private JMenuItem getMitCustomCopy() {
        if (mitCustomCopy == null) {
            mitCustomCopy = new JMenuItem();
            mitCustomCopy.setText("Copy Custom");
            mitCustomCopy.setToolTipText("Copy the Custom variant layout to the clipboard as plain text");
            mitCustomCopy.setVisible( Settings.getInstance().isCustom());
            mitCustomCopy.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                  if ( Settings.getInstance().isCustom() ) {
                    StringSelection data = new StringSelection( Settings.getInstance().getCustom());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data, data);
                  }
                  else {
                    JOptionPane.showMessageDialog(SudokuFrame.this, "Oops! NO extra regions here!", "Copy Custom", JOptionPane.WARNING_MESSAGE);
                  }
                }
            });
        }
        return mitCustomCopy;
    }

    private JMenuItem getMitCustomClockwise() {
        if (mitCustomClockwise == null) {
            mitCustomClockwise = new JMenuItem();
            mitCustomClockwise.setText("Rotate custom \u21bb Clockwise 90\u00b0");
            mitCustomClockwise.setToolTipText("Rotates the custom Clockwise 90 degrees");
            mitCustomClockwise.setVisible( Settings.getInstance().isCustom());
            mitCustomClockwise.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateCustomClockwise();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitCustomClockwise;
    }

    private JMenuItem getMitCustomAntiClockwise() {
        if (mitCustomAntiClockwise == null) {
            mitCustomAntiClockwise = new JMenuItem();
            mitCustomAntiClockwise.setText("Rotate custom \u21ba Anti-Clockwise 90\u00b0");
            mitCustomAntiClockwise.setToolTipText("Rotates the custom Anti-Clockwise 90 degrees");
            mitCustomAntiClockwise.setVisible( Settings.getInstance().isCustom());
            mitCustomAntiClockwise.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateCustomAntiClockwise();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitCustomAntiClockwise;
    }

    private JMenuItem getMitCustomHorizontal() {
        if (mitCustomHorizontal == null) {
            mitCustomHorizontal = new JMenuItem();
            mitCustomHorizontal.setText("Rotate custom \u2015 Horizontally");
            mitCustomHorizontal.setToolTipText("Rotates the custom Horizontally");
            mitCustomHorizontal.setVisible( Settings.getInstance().isCustom());
            mitCustomHorizontal.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateCustomHorizontal();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitCustomHorizontal;
    }

    private JMenuItem getMitCustomVertical() {
        if (mitCustomVertical == null) {
            mitCustomVertical = new JMenuItem();
            mitCustomVertical.setText("Rotate custom \ufe31 Vertically");
            mitCustomVertical.setToolTipText("Rotates the custom Vertically");
            mitCustomVertical.setVisible( Settings.getInstance().isCustom());
            mitCustomVertical.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateCustomVertical();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitCustomVertical;
    }

    private JMenuItem getMitCustomDiagonal() {
        if (mitCustomDiagonal == null) {
            mitCustomDiagonal = new JMenuItem();
            mitCustomDiagonal.setText("Rotate custom \u2571 Diagonally");
            mitCustomDiagonal.setToolTipText("Rotates the custom Diagonally");
            mitCustomDiagonal.setVisible( Settings.getInstance().isCustom());
            mitCustomDiagonal.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateCustomDiagonal();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitCustomDiagonal;
    }

    private JMenuItem getMitCustomAntiDiagonal() {
        if (mitCustomAntiDiagonal == null) {
            mitCustomAntiDiagonal = new JMenuItem();
            mitCustomAntiDiagonal.setText("Rotate custom \u2572 Anti-Diagonally");
            mitCustomAntiDiagonal.setToolTipText("Rotates the custom Anti-Diagonally");
            mitCustomAntiDiagonal.setVisible( Settings.getInstance().isCustom());
            mitCustomAntiDiagonal.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sudokuPanel.getSudokuGrid().rotateCustomAntiDiagonal();
                    engine.rebuildSolver();
                    engine.resetPotentials();
                    repaint();
                }
            });
        }
        return mitCustomAntiDiagonal;
    }

    void quit() {
        SudokuFrame.this.setVisible(false);
        SudokuFrame.this.dispose();
        if (selectDialog != null)
            selectDialog.dispose();
        if (generateDialog != null)
            generateDialog.dispose();
        if (dummyFrameKnife != null)
            dummyFrameKnife.dispose();
    }

}
