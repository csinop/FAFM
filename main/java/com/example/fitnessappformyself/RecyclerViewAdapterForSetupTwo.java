package com.example.fitnessappformyself;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterForSetupTwo extends RecyclerView .Adapter<RecyclerViewAdapterForSetupTwo.ViewHolder>{
    private final String[] dataArray;
    private final Context context;

    public RecyclerViewAdapterForSetupTwo(Context ct, String[] s){
        context = ct;
        dataArray = s;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.setuptwo_recycle_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //set weekdays
        holder.weekdayButton.setText(dataArray[position]);

        //set onClick listeners for buttons
        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeeklyPlanActivity.class);
            intent.putExtra("weekday",dataArray[position]);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataArray.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView weekdayButton;
        private final ConstraintLayout layout;

        public ViewHolder (@NonNull View itemView){
            super(itemView);
            weekdayButton = itemView.findViewById(R.id.weekdayButton);
            layout = itemView.findViewById(R.id.someWeekdayLayout);
        }
    }
}
