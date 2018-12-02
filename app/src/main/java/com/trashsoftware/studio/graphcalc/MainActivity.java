package com.trashsoftware.studio.graphcalc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.trashsoftware.studio.graphcalc.graphics.CustomView;
import com.trashsoftware.studio.graphcalc.graphics.MemoryAdapter;
import com.trashsoftware.studio.graphcalc.maths.Calculus;
import com.trashsoftware.studio.graphcalc.maths.ExtendedExpressionBuilder;
import com.trashsoftware.studio.graphcalc.maths.NumberTooLargeException;
import com.trashsoftware.studio.graphcalc.util.GraphUnit;
import com.trashsoftware.studio.graphcalc.util.Util;

import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String[][] degreesFunctions = new String[][]{
            new String[]{"cos", "cosDeg"},
            new String[]{"sin", "sinDeg"},
            new String[]{"tan", "tanDeg"}
    };

    private final static String[][] normalSubstitutions = new String[][]{
            new String[]{"π", "pi"},
            new String[]{"Mod", "%"}
    };

    public final static String INTEGRAL_SIGN = "∫";

    public final static String COMMA_SIGN = ",";

    final static String UNIT_KEY = "UNIT_KEY";

    final static String MEMORY_KEY = "MEMORY_RESULT";

    final static String EQUATION_TEXT_KEY = "EQUATION_TEXT";

    final static String RESULT_KEY = "TEXT_RESULT";

    private TextView number2;

    private EditText editText;

    private Button cosBtn, sinBtn, tanBtn;

    private Button radDegBtn;

    private boolean isInverse;

    private boolean isDegrees;

    private int lastRandIndex = -1;

    private MemoryAdapter memoryAdapter2;

    private double currentResult;

    private NavigationView navigationView;

    private Button msBtn, mrBtn, mcBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_graph) {
            drawGraphAction();
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(MEMORY_KEY, memoryAdapter2.dataSet);
        outState.putString(RESULT_KEY, number2.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RESULT_KEY)) {
                number2.setText(savedInstanceState.getString(RESULT_KEY));
            } else {
                msBtn.setEnabled(false);
            }
        } else {
            msBtn.setEnabled(false);
        }
    }

    private void initialize(Bundle savedInstanceState) {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        msBtn = navigationView.getHeaderView(0).findViewById(R.id.button_m_save);
        mrBtn = navigationView.getHeaderView(0).findViewById(R.id.button_m_recall);
        mcBtn = navigationView.getHeaderView(0).findViewById(R.id.button_m_clear);

        number2 = findViewById(R.id.number2);
        editText = findViewById(R.id.editText);
        editText.setShowSoftInputOnFocus(false);

        cosBtn = findViewById(R.id.buttonCos);
        sinBtn = findViewById(R.id.buttonSin);
        tanBtn = findViewById(R.id.buttonTan);
        radDegBtn = findViewById(R.id.buttonDeg);

        ArrayList<String> list;
        if (savedInstanceState != null) {
            list = savedInstanceState.getStringArrayList(MEMORY_KEY);
            if (list != null && list.size() > 0) {
                mrBtn.setEnabled(true);
                mcBtn.setEnabled(true);
            } else {
                list = new ArrayList<>();
            }
        } else {
            list = new ArrayList<>();
        }
        initRecyclerView(list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setLongClickListeners();
        }

        setNumber2Listener();
    }

    private void initRecyclerView(ArrayList<String> strings) {
        RecyclerView memoryList = navigationView.getHeaderView(0).findViewById(R.id.memory_list);
        memoryList.setLayoutManager(new LinearLayoutManager(this));
        memoryAdapter2 = new MemoryAdapter(this, strings);
        memoryList.setAdapter(memoryAdapter2);
    }

    private void setNumber2Listener() {
        number2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    msBtn.setEnabled(false);
                } else {
                    msBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void drawGraphAction() {
        GraphUnit gu = new GraphUnit(this, editText.getText().toString());
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra(UNIT_KEY, gu);
        startActivity(intent);
    }

    public void memorySave(View view) {
        String text = number2.getText().toString();
        if (text.length() > 0) {
            memoryAdapter2.dataSet.add(number2.getText().toString());
            memoryAdapter2.notifyItemInserted(memoryAdapter2.dataSet.size() - 1);
            mrBtn.setEnabled(true);
            mcBtn.setEnabled(true);
        }
    }

    public void memoryRecall(View view) {
        if (memoryAdapter2.getItemCount() > 0) {
            inputText(memoryAdapter2.dataSet.get(0));
        }
    }

    public void memoryClear(View view) {
        memoryAdapter2.dataSet.clear();
        memoryAdapter2.notifyDataSetChanged();
        mrBtn.setEnabled(false);
        mcBtn.setEnabled(false);
    }

    public void inputNormal(View view) {
        CharSequence text = ((Button) view).getText();
        inputText(text);
    }

    public void inputFunction(View view) {
        CharSequence text = ((Button) view).getText();
        String str = text + "()";
        inputText(str);
        editText.setSelection(editText.getSelectionStart() - 1);
    }

    public void inputFunctionIntegral(View view) {
        CharSequence text = ((Button) view).getText();
        String str = text + "()dx";
        inputText(str);
        editText.setSelection(editText.getSelectionStart() - 3);
    }

    public void inputPermutation(View view) {
        inputText("P()");
        editText.setSelection(editText.getSelectionStart() - 1);
    }

    public void inputCombination(View view) {
        inputText("C()");
        editText.setSelection(editText.getSelectionStart() - 1);
    }

    public void generateRandom(View view) {

        if (lastRandIndex != -1) {
            editText.getText().delete(lastRandIndex, editText.getSelectionEnd());
        }
        double r = Math.random();
        int index = editText.getSelectionEnd();
        inputText(String.valueOf(r));
        lastRandIndex = index;
    }

    public void inverseFunctions(View view) {
        isInverse = !isInverse;
        if (isInverse) {
            cosBtn.setText(R.string.acos);
            sinBtn.setText(R.string.asin);
            tanBtn.setText(R.string.atan);
        } else {
            cosBtn.setText(R.string.cos);
            sinBtn.setText(R.string.sin);
            tanBtn.setText(R.string.tan);
        }
    }

    public void switchRadDeg(View view) {
        isDegrees = !isDegrees;
        if (isDegrees) {
            radDegBtn.setText(R.string.rad);
        } else {
            radDegBtn.setText(R.string.deg);
        }
    }

    public void inputSpecialMultiply(View view) {
        inputText("*");
    }

    public void backspace(View view) {
        Editable current = editText.getText();
        int st = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (st == end) {
            if (st > 0) {
                current.delete(st - 1, end);
            }
        } else {
            current.delete(st, end);
        }
        lastRandIndex = -1;
    }

    public void clear(View view) {
        editText.getText().clear();
        number2.setText("");
        lastRandIndex = -1;
    }

    private String getEquation() {
        return parse(editText.getText().toString());
    }

    public void getResult(View view) {
        try {
            String text = getEquation();
            Expression ex = new ExtendedExpressionBuilder(text).build();
            currentResult = ex.evaluate();
            String textResult = Util.doubleToString(currentResult);
            number2.setText(textResult);
        } catch (NumberTooLargeException nte) {
            number2.setText(R.string.tooLarge);
        } catch (Exception e) {
            e.printStackTrace();
            number2.setText(R.string.error);
        }
    }

    public void inputText(CharSequence text) {
        Editable current = editText.getText();
        current.replace(editText.getSelectionStart(), editText.getSelectionEnd(), text);
        lastRandIndex = -1;
    }

    public String parse(String s) {
        s = parseNormal(s);
        s = parseRoot(s);
        s = parseSigma(s);
        s = parseBigPi(s);
        if (isDegrees) {
            s = degreesSubstitution(s);
        }
        int integralIndex;
        while ((integralIndex = s.indexOf(INTEGRAL_SIGN)) != -1) {
            s = parseIntegral(s, integralIndex);
        }
        int derivativeIndex;
        while ((derivativeIndex = s.indexOf("d/dx(")) != -1) {
            s = parseDerivative(s, derivativeIndex);
        }
        return s;
    }

    private static String parseNormal(String s) {
        for (String[] pair : normalSubstitutions) {
            s = s.replaceAll(pair[0], pair[1]);
        }
        return s;
    }

    private static String degreesSubstitution(String s) {
        for (String[] pair : degreesFunctions) {
            s = s.replaceAll(pair[0], pair[1]);
        }
        return s;
    }

    private static String parseIntegral(String s, int beginIndex) {
        String front = s.substring(0, beginIndex);
        String integral = getInsidePar(s, beginIndex + 2);
        String back = s.substring(beginIndex + 2 + integral.length() + 3);
        if (integral.contains(COMMA_SIGN)) {
            String[] sep = integral.split(COMMA_SIGN);
            double low = new ExtendedExpressionBuilder(sep[1]).build().evaluate();
            double up = new ExtendedExpressionBuilder(sep[2]).build().evaluate();
            double result = Calculus.definiteIntegral(low, up, 0.001, sep[0]);
            double roundedResult = (double) Math.round(result * 1_000_000) / 1_000_000;
            return front + String.valueOf(roundedResult) + back;
        } else {
            return front + CustomView.INTEGRAL_WORD + "(" + integral + ")dx" + back;
        }
    }

    private static String parseDerivative(String s, int beginIndex) {
        String derivative = getInsidePar(s, beginIndex + 5);
        String front = s.substring(0, beginIndex);
        String back = s.substring(beginIndex + derivative.length() + 6);
        if (derivative.contains(COMMA_SIGN)) {
            String[] sep = derivative.split(COMMA_SIGN);
            double point = new ExtendedExpressionBuilder(sep[1]).build().evaluate();
            double result = Calculus.definiteDerivative(point, 0.0_000_000_1, sep[0]);
            double roundedResult = (double) Math.round(result * 1_000_000) / 1_000_000;
            return front + String.valueOf(roundedResult) + back;
        } else {
            return front + "ddx(" + derivative + ")" + back;
        }
    }

    private static String parseRoot(String s) {
        int index;
        while ((index = s.indexOf("√(")) != -1) {
            s = parseRootSingle(s, index);
        }
        return s;
    }

    private static String parseRootSingle(String s, int beginIndex) {
        String mid = getInsidePar(s, beginIndex + 2);
        String front = s.substring(0, beginIndex);
        String back = s.substring(beginIndex + mid.length() + 3);
        boolean origHasComma = mid.contains(COMMA_SIGN);
        int nestedIndex = mid.indexOf("√(");
        if (nestedIndex != -1) {
            mid = parseRootSingle(mid, nestedIndex);
        }
        if (mid.contains(COMMA_SIGN) && origHasComma) {
            return front + "root(" + mid + ")" + back;
        } else {
            return front + "root(2," + mid + ")" + back;
        }
    }

    private static String parseSigma(String s) {
        int index;
        while ((index = s.indexOf("∑(")) != -1) {
            s = parseSigmaOrBPiSingle(s, index, true);
        }
        return s;
    }

    private static String parseBigPi(String s) {
        int index;
        while ((index = s.indexOf("∏(")) != -1) {
            s = parseSigmaOrBPiSingle(s, index, false);
        }
        return s;
    }

    private static String parseSigmaOrBPiSingle(String s, int beginIndex,
                                                boolean isSigma) {
        String mid = getInsidePar(s, beginIndex + 2);
        String front = s.substring(0, beginIndex);
        String back = s.substring(beginIndex + mid.length() + 3);
        String[] parts = mid.split(COMMA_SIGN);
        double low = new ExtendedExpressionBuilder(parts[1]).build().evaluate();
        double up = new ExtendedExpressionBuilder(parts[2]).build().evaluate();
        double result;
        if (isSigma) {
            result = Calculus.sigma(low, up, parts[0]);
        } else {
            result = Calculus.bigPi(low, up, parts[0]);
        }
        return front + String.valueOf(result) + back;
    }

    /**
     * @param s          the string
     * @param beginIndex the begin index of function content
     * @return substring between parentheses, without back parentheses
     */
    static String getInsidePar(String s, int beginIndex) {
        int pCount = 1;
        int index = beginIndex;
        while (pCount != 0) {
            char next = s.charAt(index++);
            if (next == '(') {
                pCount += 1;
            } else if (next == ')') {
                pCount -= 1;
            }
        }
        return s.substring(beginIndex, index - 1);
    }

    private void setLongClickListeners() {
        findViewById(R.id.buttonDx).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TooltipCompat.setTooltipText(v, getString(R.string.ddxHelp));
                return true;
            }
        });

        findViewById(R.id.buttonIntegral).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TooltipCompat.setTooltipText(v, getString(R.string.integralHelp));
                return true;
            }
        });

        findViewById(R.id.buttonSigma).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TooltipCompat.setTooltipText(v, getString(R.string.sigmaHelp));
                return true;
            }
        });

        findViewById(R.id.buttonBigPi).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TooltipCompat.setTooltipText(v, getString(R.string.bigPiHelp));
                return true;
            }
        });

        findViewById(R.id.buttonRoot).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TooltipCompat.setTooltipText(v, getString(R.string.rootHelp));
                return true;
            }
        });

        findViewById(R.id.buttonLog).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TooltipCompat.setTooltipText(v, getString(R.string.logHelp));
                return true;
            }
        });
    }
}
