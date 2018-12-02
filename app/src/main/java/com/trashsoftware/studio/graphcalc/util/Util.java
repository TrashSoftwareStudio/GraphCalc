package com.trashsoftware.studio.graphcalc.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    /**
     * Returns the String representation of a double.
     * <p>
     * For number within {@code double}'s precision, returns normal string.
     * For larger number, returns string using scientific notation.
     *
     * @param d the double
     * @return the {@code String} representation of <code>d</code>
     */
    public static String doubleToString(double d) {
//        System.out.println(d);
        if (Math.abs(d) > 2_251_799_813_685_248L) {
            return String.valueOf(d);
        } else {
            long decimal = (long) d;
            double fractional = d - decimal;
//            System.out.println(fractional);
//            System.out.println();
            String decimalString = NumberFormat.getNumberInstance(Locale.US).format(decimal);
            if (Math.abs(fractional) <= 0.1E-15) {
                return decimalString;
            } else {
                int begin = fractional < 0 ? 1 : 0;
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(16 - decimalString.length());
                String fractionalString = df.format(fractional).substring(begin);
//                System.out.println(fractionalString);
                return decimalString + fractionalString;
            }
        }
    }
}
