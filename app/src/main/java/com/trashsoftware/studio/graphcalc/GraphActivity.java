package com.trashsoftware.studio.graphcalc;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.trashsoftware.studio.graphcalc.util.GraphUnit;

public class GraphActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GraphUnit graphUnit;
    private CustomView view;
    private TextView text;
    private String xyString;
    private String rThetaString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        initialize();

        NavigationView navigationView = findViewById(R.id.nav_view_graph);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.nav_rTheta) {
//            MenuItem polarItem = findViewById(R.id.nav_rTheta);
            view.isRTheta = !view.isRTheta;
            if (view.isRTheta) {
                text.setText(rThetaString);
//                polarItem.setTitle(R.string.cartesian);
            } else {
                text.setText(xyString);
//                polarItem.setTitle(R.string.polar);
            }
            view.invalidate();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_graph);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void initialize() {
        text = findViewById(R.id.equationText);

        graphUnit = (GraphUnit) getIntent().getSerializableExtra(MainActivity.UNIT_KEY);

        xyString = "y = " + graphUnit.equationShowing;
        text.setText(xyString);

        String thetaEquation = graphUnit.equationShowing.replaceAll("x", "θ");
        rThetaString = "r = " + thetaEquation;

        drawGraph();
    }

    private void drawGraph() {
        view = findViewById(R.id.customView);
        view.setEquation(graphUnit.equation);
        view.lower = graphUnit.lower;
        view.upper = graphUnit.upper;
    }
}
