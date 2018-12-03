package com.trashsoftware.studio.graphcalc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trashsoftware.studio.graphcalc.graphics.CustomView;
import com.trashsoftware.studio.graphcalc.graphics.GraphAdapter;
import com.trashsoftware.studio.graphcalc.graphics.MemoryAdapter;
import com.trashsoftware.studio.graphcalc.util.GraphUnit;
import com.trashsoftware.studio.graphcalc.util.SavedEquation;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String SAVED_EQUATIONS_KEY = "SAVED_EQUATIONS";

    public GraphUnit graphUnit;
    private CustomView view;
//    private TextView text;
//    private MenuItem polarItem;
    private NavigationView navigationView;
    GraphAdapter graphAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        initialize(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> savedStrings = saveEquationsAsString(graphAdapter.dataSet);
        outState.putStringArrayList(SAVED_EQUATIONS_KEY, savedStrings);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<SavedEquation> ses = restoreGraphs(savedInstanceState);
        initRecyclerView(ses);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_graph);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent parent = new Intent();
            parent.putExtra(SAVED_EQUATIONS_KEY, saveEquationsAsString(graphAdapter.dataSet));
            setResult(Activity.RESULT_OK, parent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.nav_rTheta) {
//            CustomView.isRTheta = !CustomView.isRTheta;
//            text.setText(graphUnit.getEquationShowing(CustomView.isRTheta));
//            if (CustomView.isRTheta) {
//                polarItem.setTitle(R.string.cartesian);
//            } else {
//                polarItem.setTitle(R.string.polar);
//            }
//            view.invalidate();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_graph);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void initialize(Bundle savedInstanceState) {
        navigationView = findViewById(R.id.nav_view_graph);
        navigationView.setNavigationItemSelectedListener(this);
//        polarItem = navigationView.getMenu().findItem(R.id.nav_rTheta);

//        text = findViewById(R.id.equationText);
        graphUnit = (GraphUnit) getIntent().getSerializableExtra(MainActivity.UNIT_KEY);
//        text.setText(graphUnit.getEquationShowing(false));

        ArrayList<SavedEquation> ses = restoreGraphs(savedInstanceState);
        initRecyclerView(ses);

        drawGraph();
    }

    private ArrayList<SavedEquation> restoreGraphs(Bundle savedInstanceState) {
        ArrayList<SavedEquation> ses = new ArrayList<>();
        if (savedInstanceState != null) {
            ArrayList<String> strings = savedInstanceState.getStringArrayList(SAVED_EQUATIONS_KEY);
            if (strings != null) {
                for (String s : strings) {
                    ses.add(SavedEquation.parseEquation(s));
                }
            }
        }
        ArrayList<String> strings = getIntent().getStringArrayListExtra(SAVED_EQUATIONS_KEY);
        if (strings != null) {
            for (String s : strings) {
                ses.add(SavedEquation.parseEquation(s));
            }
        }
        return ses;
    }

    private ArrayList<String> saveEquationsAsString(ArrayList<SavedEquation> list) {
        ArrayList<String> savedStrings = new ArrayList<>();
        for (SavedEquation se : list) {
            savedStrings.add(se.toString());
        }
        return savedStrings;
    }

    private void initRecyclerView(ArrayList<SavedEquation> ses) {
        RecyclerView graphList = navigationView.getHeaderView(0).findViewById(R.id.graph_list);
        graphList.setLayoutManager(new GridLayoutManager(this, 1));
        graphAdapter = new GraphAdapter(this, ses);
        graphList.setAdapter(graphAdapter);
    }

    private void drawGraph() {
        view = findViewById(R.id.customView);
        view.setGraphs(graphAdapter, graphUnit);
//        view.setEquation(graphUnit.equation);
//        view.lower = graphUnit.lower;
//        view.upper = graphUnit.upper;
    }

    public void graphClear(View view) {

    }
}
