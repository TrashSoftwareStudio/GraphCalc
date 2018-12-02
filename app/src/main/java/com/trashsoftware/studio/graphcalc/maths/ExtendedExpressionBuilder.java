package com.trashsoftware.studio.graphcalc.maths;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

public class ExtendedExpressionBuilder extends ExpressionBuilder {

    public ExtendedExpressionBuilder(String expression) {
        super(expression);
        Function logAB = new Function("log", 2) {
            @Override
            public double apply(double... doubles) {
                return Math.log(doubles[1]) / Math.log(doubles[0]);
            }
        };

        Function ln = new Function("ln", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.log(doubles[0]);
            }
        };

        Function permutation = new Function("P", 2) {
            @Override
            public double apply(double... doubles) {
                return permutationInside(doubles[0], doubles[1]);
            }
        };

        Function combination = new Function("C", 2) {
            @Override
            public double apply(double... doubles) {
                return permutationInside(doubles[0], doubles[1]) / factorialInside(doubles[1]);
            }
        };

        Function gcd = new Function("gcd", 2) {
            @Override
            public double apply(double... doubles) {
                return gcdEuclid(doubles[0], doubles[1]);
            }
        };

        Function lcm = new Function("lcm", 2) {
            @Override
            public double apply(double... doubles) {
                return lcmEuclid(doubles[0], doubles[1]);
            }
        };

        Function cosDeg = new Function("cosDeg", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.cos(Math.toRadians(doubles[0]));
            }
        };

        Function sinDeg = new Function("sinDeg", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.sin(Math.toRadians(doubles[0]));
            }
        };

        Function tanDeg = new Function("tanDeg", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.tan(Math.toRadians(doubles[0]));
            }
        };

        Function acosDeg = new Function("acosDeg", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.toDegrees(Math.acos(doubles[0]));
            }
        };

        Function asinDeg = new Function("asinDeg", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.toDegrees(Math.asin(doubles[0]));
            }
        };

        Function atanDeg = new Function("atanDeg", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.toDegrees(Math.atan(doubles[0]));
            }
        };

        Function root = new Function("root", 2) {
            @Override
            public double apply(double... doubles) {
                return Math.pow(doubles[1], 1d / doubles[0]);
            }
        };

        final Operator factorial = new Operator("!", 1, true,
                Operator.PRECEDENCE_POWER + 1) {
            @Override
            public double apply(double... doubles) {
                return factorialInside(doubles[0]);
            }
        };

        functions(ln, logAB, permutation, combination, gcd, lcm, cosDeg, sinDeg, tanDeg,
                acosDeg, asinDeg, atanDeg, root);
        operator(factorial);
    }

    private double factorialInside(double number) {
        if ((long) number != number) {
            throw new IllegalArgumentException("Factorial number must be integer");
        } else if (number < 0) {
            throw new IllegalArgumentException("Factorial number less than 0");
        } else {
            double result = 1;
            for (int i = 1; i <= number; i++) {
                result *= i;
                if (result < 1)
                    throw new NumberTooLargeException("Factorial number too large");
            }
            return result;
        }
    }

    private double permutationInside(double n, double r) {
        return factorialInside(n) / factorialInside(n - r);
    }

    private double gcdEuclid(double a, double b) {
        if (a != (int) a || b != (int) b) {
            throw new IllegalArgumentException("GCD not integers");
        } else if (a == 0) {
            return b;
        } else if (b == 0) {
            return a;
        } else {
            int x = (int) Math.max(a, b);
            int y = (int) Math.min(a, b);
            while (true) {
                int q = x / y;
                int r = x - y * q;
                if (r == 0) {
                    return y;
                } else if (y < r) {
                    x = r;
                } else {
                    x = y;
                    y = r;
                }
            }
        }
    }

    private double lcmEuclid(double a, double b) {
        if (a != (int) a || b != (int) b) {
            throw new IllegalArgumentException("LCM not integers");
        } else if (a == 0 || b == 0) {
            return 0;
        } else {
            int x = (int) Math.max(a, b);
            int y = (int) Math.min(a, b);
            int m = x * y;
            while (true) {
                int r = x % y;
                if (r == 0) {
                    return m / y;
                } else {
                    x = y;
                    y = r;
                }
            }
        }
    }
}
