package com.trashsoftware.studio.graphcalc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.trashsoftware.studio.graphcalc.maths.Calculus;
import com.trashsoftware.studio.graphcalc.maths.ExtendedExpressionBuilder;
import com.trashsoftware.studio.graphcalc.maths.NumberTooLargeException;
import com.trashsoftware.studio.graphcalc.util.GraphUnit;

import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class CustomView extends View {

    private final static int INTERVAL = 2;

    private final static int INTEGRAL_INTERVAL = 5;

    private final static double POLAR_INTERVAL = 0.05;

    private int interval;

    private GraphActivity context;

    private static Paint invisiblePaint = new Paint();

    private static Paint axisPaint = new Paint();

    private static Paint curvePaint = new Paint();

    private static Paint dashPaint = new Paint();

    private static TextPaint textPaintX = new TextPaint();

    private static TextPaint textPaintY = new TextPaint();

//    private final static float INITIAL_SCREEN_WIDTH = 1080;

    private float screenWidth;

//    private final static float INITIAL_SCREEN_HEIGHT = 2220;

    private float screenHeight;

    private float widthCenter;

    private float heightCenter;

    // 0 for axis, 1 for dotted line, 2 for curve
//    int drawingType = 0;

    float scalar = 108;

    private float lastX;

    private float lastY;

    private ScaleGestureDetector scaleDetector;

    private DrawFailedDialogFragment dialogFragment = new DrawFailedDialogFragment();

    private String equation;

    private float lastTouchX, lastTouchY;

    private int activePointerId;

    private boolean hasCalculus;

    private ArrayList<String> parts = new ArrayList<>();

    final static String INTEGRAL_WORD = "integral";

    final static String DERIVATIVE_WORD = "ddx";

    private boolean isFirstDraw = true;

    private boolean scaled;

    private double lastTan;

    boolean isRTheta;

    double lower;

    double upper;

    public CustomView(Context context, AttributeSet attr) {
        super(context, attr);
        setWillNotDraw(false);

        initialize(context);
    }

    public CustomView(Context context) {
        super(context);
        setWillNotDraw(false);

        initialize(context);
    }

    private void initialize(Context context) {
        this.context = (GraphActivity) context;

        invisiblePaint.setColor(Color.WHITE);
        invisiblePaint.setStrokeWidth(0);

        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(6);
        curvePaint.setColor(Color.BLUE);
        curvePaint.setStrokeWidth(6);
        dashPaint.setColor(Color.GRAY);
        dashPaint.setStrokeWidth(3);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 100));

        textPaintX.setTextSize(60);
        textPaintX.setColor(Color.BLACK);
        textPaintX.setAntiAlias(true);
        textPaintX.setTextAlign(Paint.Align.CENTER);

        textPaintY.setTextSize(60);
        textPaintY.setColor(Color.BLACK);
        textPaintY.setAntiAlias(true);
        textPaintY.setTextAlign(Paint.Align.RIGHT);

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setEquation(String equationParsed) {
        equation = equationParsed;
        if (equation.contains(INTEGRAL_WORD)) {
            parseIndefiniteIntegral();
            hasCalculus = true;
            interval = INTEGRAL_INTERVAL;
        } else {
            interval = INTERVAL;
        }
        if (equation.contains(DERIVATIVE_WORD)) {
            parseIndefiniteDerivative();
            // Keep this boolean value after the above line !!!
            hasCalculus = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleDetector.onTouchEvent(event);

        int pointerIndex;
        float x;
        float y;

        if (scaled) {
            scaled = false;
            pointerIndex = event.getActionIndex();
            lastTouchX = event.getX(pointerIndex);
            lastTouchY = event.getY(pointerIndex);
            return true;
        }

        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pointerIndex = event.getActionIndex();
                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                lastTouchX = x;
                lastTouchY = y;

                activePointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(activePointerId);

                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                final float dx = x - lastTouchX;
                final float dy = y - lastTouchY;

                widthCenter += dx;
                heightCenter += dy;

                invalidate();

                lastTouchX = x;
                lastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = event.getX(newPointerIndex);
                    lastTouchY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
        }
        return true;
    }

    private double[][] calculatePolar() {
        if (lower == GraphUnit.MIN_VALUE || upper == GraphUnit.MAX_VALUE) {
            throw new NumberTooLargeException();
        }
        double[][] points = new double[(int) ((upper - lower) / POLAR_INTERVAL) + 2][];

        int index = 0;
        if (hasCalculus) {

        } else {
            Expression ex = new ExtendedExpressionBuilder(equation).variable("x").build();

            for (double theta = lower; theta < upper + POLAR_INTERVAL; theta += POLAR_INTERVAL) {
                if (theta > upper) {
                    ex.setVariable("x", upper);
                } else {
                    ex.setVariable("x", theta);
                }
                double r;
                try {
                    r = ex.evaluate();
                } catch (ArithmeticException | IllegalArgumentException ae) {
                    points[index++] = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
                    continue;
                }
//                System.out.println(r + " " + theta);
                double x = r * Math.cos(theta);
                double y = r * Math.sin(theta);
//                if (index == points.length) break;
                points[index++] = new double[]{x, y};
            }

        }
        for (int i = index; i < points.length; i++) {
            points[i] = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
        }
        return points;
    }

    private double[][] calculate() {
        double[][] points = new double[(int) screenWidth / interval][];
//        System.out.println("succeed init arrays");

        if (hasCalculus) {
//            System.out.println(parts);
            int index = 0;
            for (float screenX = 0; screenX < screenWidth; screenX += interval) {
                double x = getNumberX(screenX);
                if (x < lower || x > upper) {
                    points[index++] = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
                    continue;
                }
                double y;
                try {
                    y = calculateIntegral(x);
                } catch (ArithmeticException | IllegalArgumentException ae) {
                    y = Double.MAX_VALUE;
                }
                points[index++] = new double[]{x, y};
            }
        } else {
//            System.out.println(equation);
            Expression ex = new ExtendedExpressionBuilder(equation).variable("x").build();
            int index = 0;
            for (float screenX = 0; screenX < screenWidth; screenX += interval) {
                double x = getNumberX(screenX);
//                System.out.println((x < lower) + " " + lower);
                if (x < lower || x > upper) {
//                    System.out.println("我日你妈");
                    points[index++] = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
                    continue;
                }
//                System.out.println(x);
                ex.setVariable("x", x);
                double y;
                try {
                    y = ex.evaluate();
                } catch (ArithmeticException | IllegalArgumentException ae) {
                    y = Double.MAX_VALUE;
                }
                points[index++] = new double[]{x, y};
            }
        }
//        System.out.println("succeed3");
        return points;
    }

//    private double calculateCalculus(double x) {
//
//    }
//
//    private String parse() {
//
//    }

    private double calculateIntegral(double x) {
        StringBuilder sb = new StringBuilder();
        for (String s : parts) {
            if (s.contains(INTEGRAL_WORD)) {
                double low;
                double up;
                int mul;
                if (x >= 0) {
                    low = 0;
                    up = x;
                    mul = 1;
                } else {
                    low = x;
                    up = 0;
                    mul = -1;
                }
                double res = mul * Calculus.definiteIntegral(low, up, 0.01, s.substring(INTEGRAL_WORD.length() + 1));
                sb.append(String.valueOf(res));
            } else if (s.contains(DERIVATIVE_WORD)) {
                String de = s.substring(4);
                double res = Calculus.definiteDerivative(x, 0.000_000_1, de);
                sb.append(String.valueOf(res));
//                sb.append(calculateDerivativeNested(s, x));
            } else {
                sb.append(s);
            }
        }
        Expression ex = new ExtendedExpressionBuilder(sb.toString()).variable("x").build();
        ex.setVariable("x", x);
        return ex.evaluate();
    }

    double getNumberX(float screenX) {
        return (screenX - widthCenter) / scalar;
    }

    double getNumberY(float screenY) {
        return (heightCenter - screenY) / scalar;
    }

    private void drawOnePoint(double numberX, double numberY, Canvas canvas) {
        if (numberY == Double.MAX_VALUE) {
            lastX = 0;
            lastY = 0;
        } else {
            float x = (float) numberX * scalar + widthCenter;
            float y = heightCenter - (float) numberY * scalar;
            double tanValue;
            if (lastX == x) {
                tanValue = y < lastY ? 1.57 : 4.72;
            } else {
                tanValue = Math.atan((y - lastY) / (x - lastX));
            }
//            System.out.println(tanValue);
            if (x >= 0 && x < screenWidth && y >= 0 && y < screenHeight) {
                if ((lastX == 0 && lastY == 0)
                        || ((Math.abs(lastX - x) > interval + 1) && !isRTheta)
//                    || (Math.abs(lastY - y) > 1000)
                        || Calculus.isSharpAngle(lastTan, tanValue)
                        ) {
                    canvas.drawPoint(x, y, curvePaint);
                } else {
                    canvas.drawLine(lastX, lastY, x, y, curvePaint);
                }
            }
            lastX = x;
            lastY = y;
            lastTan = tanValue;
        }
    }

    private float[] getFirstLinePosition(float interval) {
        float x0 = widthCenter % interval;
        float y0 = heightCenter % interval;
//        float x0 = focusX % interval;
//        float y0 = focusY % interval;
        return new float[]{x0, y0};
    }

    private String getLabels(float jump, double p1) {
        String xs;
        if (jump >= 1) {
            xs = String.valueOf(Math.round(p1));
        } else {
            double x1 = (double) Math.round(p1 * 1000) / 1000;
            xs = String.valueOf(x1);
        }
        return xs;
    }

    private void drawLabels(Canvas canvas) {
        float jump = getMultiplier();
        float plus = scalar * jump;
        float[] startPositions = getFirstLinePosition(plus);
        for (float f = startPositions[0]; f < screenWidth; f += plus) {
            canvas.drawLine(f, 0, f, screenHeight, dashPaint);
            String xs = getLabels(jump, getNumberX(f));
            canvas.drawText(xs, f, screenHeight, textPaintX);
        }
        for (float f = startPositions[1]; f < screenHeight; f += plus) {
            canvas.drawLine(0, f, screenWidth, f, dashPaint);
            String ys = getLabels(jump, getNumberY(f));
            canvas.drawText(ys, screenWidth, f + 20, textPaintY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        System.out.println(-1 < Double.MIN_VALUE);
        // Draw axises
        if (isFirstDraw) {
            canvas.drawPoint(0, 0, invisiblePaint);
            screenHeight = getHeight();
            screenWidth = getWidth();
            heightCenter = screenHeight / 2;
            widthCenter = screenWidth / 2;
            isFirstDraw = false;
        }
        canvas.drawLine(widthCenter, 0, widthCenter, screenHeight, axisPaint);
        canvas.drawLine(0, heightCenter, screenWidth, heightCenter, axisPaint);

        // Draw dash lines
        drawLabels(canvas);

        // Draw curve
        try {
            lastX = 0;
            lastY = 0;
            double[][] points;
            if (isRTheta) {
//                System.out.println(11111);
                points = calculatePolar();
//                System.out.println(22222);
            } else {
                points = calculate();
            }
            for (double[] point : points) {
                drawOnePoint(point[0], point[1], canvas);
            }
        } catch (NumberTooLargeException ntl) {
            showCannotDrawDialog(R.string.noLimit);
        } catch (Exception e) {
            e.printStackTrace();
            showCannotDrawDialog(R.string.drawFailed);
        }
    }


    private float getMultiplier() {
        if (scalar <= 108) {
            return (float) (int) (108 / scalar);
        }
        // When labels with ".", let them 2x looser
        else if (scalar <= 216) {
            return 1;
        } else if (scalar <= 432) {
            return 0.5f;
        } else if (scalar <= 864) {
            return 0.25f;
        } else {
            return 0.125f;
        }
    }

    private void parseIndefiniteIntegral() {
        int integralIndex;
        String s = equation;
        while ((integralIndex = s.indexOf(INTEGRAL_WORD)) != -1) {
            int endIndex = s.indexOf(")dx");
            String front = s.substring(0, integralIndex);
            String integral = s.substring(integralIndex, endIndex);
            parts.add(front);
            parts.add(integral);

            s = s.substring(endIndex + 3);
        }
        parts.add(s);
    }

    private void parseIndefiniteDerivative() {
        if (hasCalculus) {
            ArrayList<String> result = new ArrayList<>();
            for (String s : parts) {
                ArrayList<String> list = parseDerivativeForEachPart(s);
                result.addAll(list);
            }
            parts = result;
        } else {
            parts = parseDerivativeForEachPart(equation);
        }
    }

    private ArrayList<String> parseDerivativeForEachPart(String s) {
        ArrayList<String> list = new ArrayList<>();
        int derivativeIndex;
        while ((derivativeIndex = s.indexOf(DERIVATIVE_WORD)) != -1) {
            int pCount = 1;
            int index = derivativeIndex + 4;
            while (pCount != 0) {
                char next = s.charAt(index++);
                if (next == '(') {
                    pCount += 1;
                } else if (next == ')') {
                    pCount -= 1;
                }
            }
            String front = s.substring(0, derivativeIndex);
            String derivative = s.substring(derivativeIndex, index - 1);
            list.add(front);
            list.add(derivative);
            s = s.substring(index);
        }
        list.add(s);
        return list;
    }

    private void showCannotDrawDialog(int reason) {
        dialogFragment.show(context.getSupportFragmentManager(), context.getString(reason));
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = (float) Math.max(0.1, Math.min(10, scaleFactor));
            scalar *= scaleFactor;
            final float focusX = detector.getFocusX();
            final float focusY = detector.getFocusY();
            final float focusXOffset = (widthCenter - focusX) * scaleFactor;
            final float focusYOffset = (heightCenter - focusY) * scaleFactor;
            widthCenter = focusX + focusXOffset;
            heightCenter = focusY + focusYOffset;
            scaled = true;

            invalidate();
            return true;
        }
    }
}
