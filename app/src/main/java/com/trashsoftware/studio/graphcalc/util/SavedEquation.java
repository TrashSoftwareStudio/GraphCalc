package com.trashsoftware.studio.graphcalc.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class SavedEquation {

    private final static String SEPARATOR = ";";
    public String plainText;
    public String equation;
    public ArrayList<String> parts;
    public double lower;
    public double upper;
    public boolean hasCalculus;
    public boolean isPolar;

    public SavedEquation(String equation, String plainText, boolean isPolar, double lower, double upper) {
        this.equation = equation;
        this.plainText = plainText;
        hasCalculus = false;
        this.isPolar = isPolar;
        this.lower = lower;
        this.upper = upper;
    }

    public SavedEquation(ArrayList<String> parts, String plainText, boolean isPolar, double lower, double upper) {
        this.parts = parts;
        this.plainText = plainText;
        hasCalculus = true;
        this.isPolar = isPolar;
        this.lower = lower;
        this.upper = upper;
    }

    public static SavedEquation parseEquation(String stringContent) {
        String[] sep = stringContent.split(SEPARATOR);
        boolean hasCalculus = Boolean.valueOf(sep[0]);
        boolean isPolar = Boolean.valueOf(sep[1]);
        double lower = Double.valueOf(sep[2]);
        double upper = Double.valueOf(sep[3]);
        if (hasCalculus) {
            ArrayList<String> parts = new ArrayList<>(Arrays.asList(sep).subList(5, sep.length));
            return new SavedEquation(parts, sep[4], isPolar, lower, upper);
        } else {
            return new SavedEquation(sep[5], sep[4], isPolar, lower, upper);
        }
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(hasCalculus)).append(SEPARATOR).append(String.valueOf(isPolar))
                .append(SEPARATOR).append(String.valueOf(lower)).append(SEPARATOR)
                .append(String.valueOf(upper)).append(SEPARATOR).append(plainText);
        if (hasCalculus) {
            for (String p : parts) {
                sb.append(SEPARATOR).append(p);
            }
        } else {
            sb.append(SEPARATOR).append(equation);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SavedEquation && ((SavedEquation) obj).plainText.equals(plainText);
    }
}
