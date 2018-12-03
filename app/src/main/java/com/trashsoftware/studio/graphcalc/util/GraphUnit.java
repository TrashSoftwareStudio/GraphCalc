package com.trashsoftware.studio.graphcalc.util;

import android.support.annotation.NonNull;


import com.trashsoftware.studio.graphcalc.MainActivity;
import com.trashsoftware.studio.graphcalc.maths.ExtendedExpressionBuilder;

import java.io.Serializable;

public class GraphUnit implements Serializable {

    public final static double MIN_VALUE = -1024;

    public final static double MAX_VALUE = 1024;

    public String equation;

    private String equationShowing;

    public boolean polar;

    public double lower = MIN_VALUE;

    public double upper = MAX_VALUE;

    public GraphUnit(MainActivity parent, String equationText, boolean polar) {
        String[] parts = parse(equationText);
        equation = parent.parse(parts[0]);
        equationShowing = "y = " + parts[0];
        this.polar = polar;
        if (parts.length == 3) {
            lower = new ExtendedExpressionBuilder(parts[1]).build().evaluate();
            upper = new ExtendedExpressionBuilder(parts[2]).build().evaluate();
        }
    }

    public String getEquationShowing() {
        if (polar) return equationShowing.replaceAll("x", "θ").replaceAll("y","r");
        return equationShowing;
    }

    private static String[] parse(String equationText) {
        int index = 0;
        int count = 0;
        boolean hasPar = false;
        boolean hasDomain = false;
        while (index < equationText.length()) {
            char c = equationText.charAt(index++);
            if (c == '(') {
                hasPar = true;
                count += 1;
            } else if (c == ')') {
                count -= 1;
            } else if (c == ',' && (!hasPar || count == 0)) {
                hasDomain = true;
                break;
            }
        }
        if (hasDomain) {
            String[] temp = equationText.split(",");
            int len = temp.length;
            return new String[]{equationText.substring(0, index - 1), temp[len - 2], temp[len - 1]};
        } else {
            return new String[]{equationText};
        }
    }

    @NonNull
    @Override
    public String toString() {
        return equation;
    }
}
