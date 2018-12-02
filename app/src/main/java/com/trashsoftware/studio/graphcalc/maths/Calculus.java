package com.trashsoftware.studio.graphcalc.maths;

import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;

public class Calculus {

    /**
     * Returns the definite integral value.
     *
     * @param lowerLimit the lower limit
     * @param upperLimit the upper limit
     * @param expression the expression
     * @param dx         the delta x
     * @return the integral result
     */
    public static double definiteIntegral(double lowerLimit, double upperLimit, double dx, String expression) {
        double result = 0;
        Expression ex = new ExtendedExpressionBuilder(expression).variable("x").build();
        for (double x = lowerLimit + dx / 2; x < upperLimit; x += dx) {
            ex.setVariable("x", x);
            double y = ex.evaluate();
            result += y * dx;
        }
        return result;
    }

    /**
     * Returns the definite derivative value.
     *
     * @param point      the derivation point
     * @param dx         the delta x
     * @param expression expression
     * @return the derivative result
     */
    public static double definiteDerivative(double point, double dx, String expression) {
        double st = point - dx;
        Expression ex = new ExtendedExpressionBuilder(expression).variable("x").build();
        ex.setVariable("x", st);
        double y1 = ex.evaluate();
        ex.setVariable("x", point);
        double y2 = ex.evaluate();
        return (y2 - y1) / dx;
    }

    public static double sigma(double low, double up, String expression) {
        if (low != (long) low || up != (long) up) {
            throw new IllegalArgumentException("Sigma limit should be integers");
        }
        Expression ex = new ExtendedExpressionBuilder(expression).variable("x").build();
        double sum = 0;
        for (long i = (long) low; i <= up; i++) {
            ex.setVariable("x", i);
            sum += ex.evaluate();
        }
        return sum;
    }

    public static double bigPi(double low, double up, String expression) {
        if (low != (long) low || up != (long) up) {
            throw new IllegalArgumentException("BigPi limit should be integers");
        }
        Expression ex = new ExtendedExpressionBuilder(expression).variable("x").build();
        double mul = 1;
        for (long i = (long) low; i <= up; i++) {
            ex.setVariable("x", i);
            mul *= ex.evaluate();
        }
        return mul;
    }

    public static boolean isSharpAngle(double lastAngle, double thisAngle) {
        double diff = Math.abs(lastAngle - thisAngle);
        return diff > 0.785 && diff < 2.357;
    }
}
