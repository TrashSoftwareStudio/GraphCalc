package com.trashsoftware.studio.graphcalc.graphics;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trashsoftware.studio.graphcalc.MainActivity;
import com.trashsoftware.studio.graphcalc.R;
import com.trashsoftware.studio.graphcalc.util.Util;

import java.util.ArrayList;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> {

    public ArrayList<String> dataSet;

    private MainActivity activity;

    public static class MemoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView;
        private MainActivity activity;

        MemoryViewHolder(@NonNull View itemView, MainActivity activity) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.memory_item_text);
            this.activity = activity;
        }

        void bindView(String text) {
            textView.setText(text);
        }

        @Override
        public void onClick(View v) {
            String s = Util.parseDoubleString(textView.getText().toString());
            activity.inputText(s);
            activity.closeDrawer();
        }
    }

    public MemoryAdapter(MainActivity activity, ArrayList<String> dataSet) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    public boolean removeItem(MemoryViewHolder mvh) {
        String text = mvh.textView.getText().toString();
        return dataSet.remove(text);
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.drawer_list_item_main, viewGroup, false);

        return new MemoryViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder viewHolder, int i) {
        viewHolder.bindView(dataSet.get(i));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
