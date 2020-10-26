/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku;

import java.util.*;
//port java.util.prefs.*;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.UIManager;

/**
 * Global settings of the application.
 * Implemented using the singleton pattern.
 */
public class Settings {

    public final static int VERSION = 1;
    public final static int REVISION = 2;
    public final static String SUBREV = ".1";

    private static Settings instance = null;

    private static String jsonFilename = "SukakuExplainer.json";

    private boolean isRCNotation = true;
    private boolean isAntialiasing = true;
    private boolean isShowingCandidates = true;
    private boolean isShowingCandidateMasks = false;
    private String  lookAndFeelClassName = null;

    private EnumSet<SolvingTechnique> techniques;

    private boolean isVertical = false;             // generate dialog
    private boolean isHorizontal = false;
    private boolean isDiagonal = false;
    private boolean isAntiDiagonal = false;
    private boolean isBiDiagonal = false;
    private boolean isOrthogonal = false;
    private boolean isRotational180 = false;
    private boolean isRotational90 = false;
    private boolean isNone = true;
    private boolean isFull = false;

    private boolean isEasy = false;
    private boolean isMedium = false;
    private boolean isHard = true;
    private boolean isFiendish = false;
    private boolean isDiabolical = false;

    private boolean isExact = false;

    private boolean isLatinSquare = false;          // variants
    private boolean isDiagonals = false;
    private boolean isDisjointGroups = false;
    private boolean isWindoku = false;
    private boolean isClover = false;
    private boolean isAsterisk = false;
    private boolean isCenterDot = false;
    private boolean isGirandola = false;
    private boolean isHalloween = false;
    private boolean isPerCent = false;

    private int isChanged = 0;          // =1 if a variant setting changed

    private int LoadError = 0;          // =1 if settings load error, a save is done

    private boolean noSaves = false;    // =true no saves done, is set from command line utils

    private Settings() {
        init();
        load();
    }

    public static Settings getInstance() {
        if (instance == null)
            instance = new Settings();
        return instance;
    }

    public void setNoSaves() {      // call from command line utils, no saves done
        noSaves = true;
        isLatinSquare = false;      // reset variants, i.e. set to vanilla sudoku
        isDiagonals = false;
        isDisjointGroups = false;
        isWindoku = false;
        isClover = false;
        isAsterisk = false;
        isCenterDot = false;
        isGirandola = false;
        isHalloween = false;
        isPerCent = false;
    }

    public void setRCNotation(boolean isRCNotation) {
        this.isRCNotation = isRCNotation;
        save();
    }

    public boolean isRCNotation() {
        return isRCNotation;
    }

    public void setAntialiasing(boolean isAntialiasing) {
        this.isAntialiasing = isAntialiasing;
        save();
    }

    public boolean isAntialiasing() {
        return this.isAntialiasing;
    }

    public void setShowingCandidates(boolean value) {
        this.isShowingCandidates = value;
        save();
    }

    public boolean isShowingCandidates() {
        return this.isShowingCandidates;
    }

    public void setShowingCandidateMasks(boolean value) {
        this.isShowingCandidateMasks = value;
        save();
    }

    public boolean isShowingCandidateMasks() {
        return this.isShowingCandidateMasks;
    }

    public String getLookAndFeelClassName() {
        return lookAndFeelClassName;
    }

    public void setLookAndFeelClassName(String lookAndFeelClassName) {
        this.lookAndFeelClassName = lookAndFeelClassName;
        save();
    }

    public EnumSet<SolvingTechnique> getTechniques() {
        return EnumSet.copyOf(this.techniques);
    }

    public void setTechniques(EnumSet<SolvingTechnique> techniques) {
        this.techniques = techniques;
    }

    public boolean isUsingAllTechniques() {
        EnumSet<SolvingTechnique> all = EnumSet.allOf(SolvingTechnique.class);
        return this.techniques.equals(all);
    }

    public boolean isUsingOneOf(SolvingTechnique... solvingTechniques) {
        for (SolvingTechnique st : solvingTechniques) {
            if (this.techniques.contains(st))
                return true;
        }
        return false;
    }

    public boolean isusingAll(SolvingTechnique... solvingTechniques) {
        for (SolvingTechnique st : solvingTechniques) {
            if (!this.techniques.contains(st))
                return false;
        }
        return true;
    }

    public boolean isUsingAllButMaybeNot(SolvingTechnique... solvingTechniques) {
        List<SolvingTechnique> list = Arrays.asList(solvingTechniques);
        for (SolvingTechnique st : EnumSet.allOf(SolvingTechnique.class)) {
            if (!this.techniques.contains(st) && !list.contains(st))
                return false;
        }
        return true;
    }

    // generate dialog

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
        save();
    }
    public boolean isVertical() {
        return isVertical;
    }

    public void setHorizontal(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        save();
    }
    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setDiagonal(boolean isDiagonal) {
        this.isDiagonal = isDiagonal;
        save();
    }
    public boolean isDiagonal() {
        return isDiagonal;
    }

    public void setAntiDiagonal(boolean isAntiDiagonal) {
        this.isAntiDiagonal = isAntiDiagonal;
        save();
    }
    public boolean isAntiDiagonal() {
        return isAntiDiagonal;
    }

    public void setBiDiagonal(boolean isBiDiagonal) {
        this.isBiDiagonal = isBiDiagonal;
        save();
    }
    public boolean isBiDiagonal() {
        return isBiDiagonal;
    }

    public void setOrthogonal(boolean isOrthogonal) {
        this.isOrthogonal = isOrthogonal;
        save();
    }
    public boolean isOrthogonal() {
        return isOrthogonal;
    }

    public void setRotational180(boolean isRotational180) {
        this.isRotational180 = isRotational180;
        save();
    }
    public boolean isRotational180() {
        return isRotational180;
    }

    public void setRotational90(boolean isRotational90) {
        this.isRotational90 = isRotational90;
        save();
    }
    public boolean isRotational90() {
        return isRotational90;
    }

    public void setNone(boolean isNone) {
        this.isNone = isNone;
        save();
    }
    public boolean isNone() {
        return isNone;
    }

    public void setFull(boolean isFull) {
        this.isFull = isFull;
        save();
    }
    public boolean isFull() {
        return isFull;
    }

    public void setEasy(boolean isEasy) {
        this.isEasy = isEasy;
        isChanged = 1;
    }
    public boolean isEasy() {
        return isEasy;
    }

    public void setMedium(boolean isMedium) {
        this.isMedium = isMedium;
        isChanged = 1;
    }
    public boolean isMedium() {
        return isMedium;
    }

    public void setHard(boolean isHard) {
        this.isHard = isHard;
        isChanged = 1;
    }
    public boolean isHard() {
        return isHard;
    }

    public void setFiendish(boolean isFiendish) {
        this.isFiendish = isFiendish;
        isChanged = 1;
    }
    public boolean isFiendish() {
        return isFiendish;
    }

    public void setDiabolical(boolean isDiabolical) {
        this.isDiabolical = isDiabolical;
        isChanged = 1;
    }
    public boolean isDiabolical() {
        return isDiabolical;
    }

    public void setExact(boolean isExact) {
        this.isExact = isExact;
        save();
    }
    public boolean isExact() {
        return isExact;
    }

    // variants

    public void setLatinSquare(boolean isLatinSquare) {
        this.isLatinSquare = isLatinSquare;
        isChanged = 1;
    }
    public boolean isLatinSquare() {
        return isLatinSquare;
    }

    public void setDiagonals(boolean isDiagonals) {
        this.isDiagonals = isDiagonals;
        isChanged = 1;
    }
    public boolean isDiagonals() {
        return isDiagonals;
    }

    public void setDisjointGroups(boolean isDisjointGroups) {
        this.isDisjointGroups = isDisjointGroups;
        isChanged = 1;
    }
    public boolean isDisjointGroups() {
        return isDisjointGroups;
    }

    public void setWindoku(boolean isWindoku) {
        this.isWindoku = isWindoku;
        isChanged = 1;
    }
    public boolean isWindoku() {
        return isWindoku;
    }

    public void setClover(boolean isClover) {
        this.isClover = isClover;
        isChanged = 1;
    }
    public boolean isClover() {
        return isClover;
    }

    public void setAsterisk(boolean isAsterisk) {
        this.isAsterisk = isAsterisk;
        isChanged = 1;
    }
    public boolean isAsterisk() {
        return isAsterisk;
    }

    public void setCenterDot(boolean isCenterDot) {
        this.isCenterDot = isCenterDot;
        isChanged = 1;
    }
    public boolean isCenterDot() {
        return isCenterDot;
    }

    public void setGirandola(boolean isGirandola) {
        this.isGirandola = isGirandola;
        isChanged = 1;
    }
    public boolean isGirandola() {
        return isGirandola;
    }

    public void setHalloween(boolean isHalloween) {
        this.isHalloween = isHalloween;
        isChanged = 1;
    }
    public boolean isHalloween() {
        return isHalloween;
    }

    public void setPerCent(boolean isPerCent) {
        this.isPerCent = isPerCent;
        isChanged = 1;
    }
    public boolean isPerCent() {
        return isPerCent;
    }

//  Load / Save

    private void init() {
        techniques = EnumSet.allOf(SolvingTechnique.class);
    }

    public void NoDirectTechniques() {
        techniques = EnumSet.allOf(SolvingTechnique.class);
        techniques.remove(SolvingTechnique.DirectPointing);
        techniques.remove(SolvingTechnique.DirectHiddenPair);
        techniques.remove(SolvingTechnique.DirectHiddenTriplet);
    }

    @SuppressWarnings("unchecked")
    public void load() {
        LoadError = 0;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFilename)) {
            Object obj = jsonParser.parse(reader);
            JSONArray jSettings = (JSONArray)obj;
            jSettings.forEach( Item -> {
                JSONObject stgObject = (JSONObject)Item;
                JSONObject stgDetails = (JSONObject)stgObject.get("Settings");
                String s = "";

                try {
                    s = (String)stgDetails.get("isRCNotation");
                    isRCNotation = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isAntialiasing");
                    isAntialiasing = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isShowingCandidates");
                    isShowingCandidates = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isShowingCandidateMasks");
                    isShowingCandidateMasks = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }

                try {
                    lookAndFeelClassName = (String)stgDetails.get("lookAndFeelClassName");
                }
                catch (NullPointerException e) { LoadError = 1;
                    lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
                }

                //generate dialog

                try {
                    s = (String)stgDetails.get("isVertical");
                    isVertical = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isHorizontal");
                    isHorizontal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDiagonal");
                    isDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isAntiDiagonal");
                    isAntiDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isBiDiagonal");
                    isBiDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isOrthogonal");
                    isOrthogonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isRotational180");
                    isRotational180 = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isRotational90");
                    isRotational90 = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isNone");
                    isNone = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isFull");
                    isFull = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isEasy");
                    isEasy = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isMedium");
                    isMedium = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isHard");
                    isHard = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isFiendish");
                    isFiendish = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDiabolical");
                    isDiabolical = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isExact");
                    isExact = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }

                // variants

                try {
                    s = (String)stgDetails.get("isLatinSquare");
                    isLatinSquare = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDiagonals");
                    isDiagonals = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDisjointGroups");
                    isDisjointGroups = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isWindoku");
                    isWindoku = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isClover");
                    isClover = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isAsterisk");
                    isAsterisk = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isCenterDot");
                    isCenterDot = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isGirandola");
                    isGirandola = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isHalloween");
                    isHalloween = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isPerCent");
                    isPerCent = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
            });
            if ( LoadError == 1 ) {
                save();
            }
        } catch (FileNotFoundException e) {
        //  create new json file
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
            save();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveChanged() {
        if ( isChanged == 1 ) {
            isChanged = 0;
            save();
        }
    }

    @SuppressWarnings("unchecked")
    public void save() {
      if ( !noSaves ) {
        JSONObject stgDetails = new JSONObject();
        stgDetails.put("isRCNotation", isRCNotation?"true":"false");
        stgDetails.put("isAntialiasing", isAntialiasing?"true":"false");
        stgDetails.put("isShowingCandidates", isShowingCandidates?"true":"false");
        stgDetails.put("isShowingCandidateMasks", isShowingCandidateMasks?"true":"false");
        stgDetails.put("lookAndFeelClassName", lookAndFeelClassName);

        // generate dialog

        stgDetails.put("isVertical", isVertical?"true":"false");
        stgDetails.put("isHorizontal", isHorizontal?"true":"false");
        stgDetails.put("isDiagonal", isDiagonal?"true":"false");
        stgDetails.put("isAntiDiagonal", isAntiDiagonal?"true":"false");
        stgDetails.put("isBiDiagonal", isBiDiagonal?"true":"false");
        stgDetails.put("isOrthogonal", isOrthogonal?"true":"false");
        stgDetails.put("isRotational180", isRotational180?"true":"false");
        stgDetails.put("isRotational90", isRotational90?"true":"false");
        stgDetails.put("isNone", isNone?"true":"false");
        stgDetails.put("isFull", isFull?"true":"false");
        stgDetails.put("isEasy", isEasy?"true":"false");
        stgDetails.put("isMedium", isMedium?"true":"false");
        stgDetails.put("isHard", isHard?"true":"false");
        stgDetails.put("isFiendish", isFiendish?"true":"false");
        stgDetails.put("isDiabolical", isDiabolical?"true":"false");
        stgDetails.put("isExact", isExact?"true":"false");

        // variants

        stgDetails.put("isLatinSquare", isLatinSquare?"true":"false");
        stgDetails.put("isDiagonals", isDiagonals?"true":"false");
        stgDetails.put("isDisjointGroups", isDisjointGroups?"true":"false");
        stgDetails.put("isWindoku", isWindoku?"true":"false");
        stgDetails.put("isClover", isClover?"true":"false");
        stgDetails.put("isAsterisk", isAsterisk?"true":"false");
        stgDetails.put("isCenterDot", isCenterDot?"true":"false");
        stgDetails.put("isGirandola", isGirandola?"true":"false");
        stgDetails.put("isHalloween", isHalloween?"true":"false");
        stgDetails.put("isPerCent", isPerCent?"true":"false");

        JSONObject stgObject = new JSONObject();
        stgObject.put("Settings", stgDetails);

        JSONArray jSettings = new JSONArray();
        jSettings.add(stgObject);

        try (FileWriter file = new FileWriter(jsonFilename)) {
            file.write(jSettings.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
      }
    }

}
