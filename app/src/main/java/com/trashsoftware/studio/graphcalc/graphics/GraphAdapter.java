package com.trashsoftware.studio.graphcalc.graphics;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trashsoftware.studio.graphcalc.GraphActivity;
import com.trashsoftware.studio.graphcalc.R;
import com.trashsoftware.studio.graphcalc.util.SavedEquation;

import java.util.ArrayList;

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.GraphViewHolder> {

    public ArrayList<SavedEquation> dataSet;

    private GraphActivity activity;

    public static class GraphViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        private boolean selected = true;
//        private LinearLayout itemView;
        private TextView textView;
//        private int color;
        private GraphActivity activity;
        SavedEquation se;

        GraphViewHolder(@NonNull View itemView, GraphActivity activity) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.graph_item_text);
//            this.itemView = (LinearLayout) itemView;
            this.activity = activity;
        }

        void setColor(int color) {
//            this.color = color;
            textView.setTextColor(color);
        }

        void bindView(SavedEquation se) {
            this.se = se;
            textView.setText(se.plainText);
        }

        @Override
        public void onClick(View v) {
//            selected = !selected;
//            if (selected) {
//                textView.setBackgroundColor(Color.LTGRAY);
//            } else {
//                textView.setBackgroundColor(Color.WHITE);
//            }
//            activity.inputText(textView.getText());
        }
    }

    public GraphAdapter(GraphActivity activity, ArrayList<SavedEquation> dataSet) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    public boolean removeItem(GraphViewHolder mvh) {
        SavedEquation se = mvh.se;
        return dataSet.remove(se);
    }

    @NonNull
    @Override
    public GraphViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.drawer_list_item_graph, viewGroup, false);

        return new GraphViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull GraphViewHolder viewHolder, int i) {
        viewHolder.bindView(dataSet.get(i));
        viewHolder.setColor(CustomView.COLORS[i % CustomView.COLORS.length]);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
