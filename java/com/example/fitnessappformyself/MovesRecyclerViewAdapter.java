package com.example.fitnessappformyself;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MovesRecyclerViewAdapter extends RecyclerView .Adapter<MovesRecyclerViewAdapter.ViewHolder>{
    //input arrays
    //these arrays are used to get information from main activity
    private final String[] dataArray;
    private final String[] chestArray;
    private final String[] bicepsArray;
    private final String[] tricepsArray;
    private final String[] backArray;
    private final String[] shouldersArray;
    private final String[] legsArray;
    private final String[] cardioArray;
    private final Context context;

    public MovesRecyclerViewAdapter(Context ct, String[] s1, String[] s2, String[] s3, String[] s4, String[] s5, String[] s6, String[] s7, String[] s8){
        context = ct;

        //this array holds the body part names
        dataArray = s1;

        //these arrays hold the move names
        chestArray = s2;
        bicepsArray = s3;
        tricepsArray = s4;
        backArray = s5;
        legsArray = s6;
        shouldersArray = s7;
        cardioArray = s8;
    }

    @Override
    public int getItemCount() {
        return dataArray.length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.moves_recycle_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentBodyPart = dataArray[position];

        //get which body part
        holder.bodyPartText.setText(currentBodyPart);

        //Toolbar on click listener
        holder.someBodyPartBar.setOnClickListener(v -> onToolBarClicked(currentBodyPart));
    }

    public void onToolBarClicked(String currentBodyPart){
        Intent intent = new Intent(context, SelectedBodyPartActivity.class);
        intent.putExtra("bodyPart",currentBodyPart);
        intent.putStringArrayListExtra("moveList", new ArrayList<>(Arrays.asList(returnArrayByBodyPartName(currentBodyPart))));
        context.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final Toolbar someBodyPartBar;
        private final TextView bodyPartText;

        public ViewHolder (@NonNull View itemView){
            super(itemView);
            someBodyPartBar = itemView.findViewById(R.id.someBodyPartBar);
            bodyPartText = itemView.findViewById(R.id.bodyPartButton);
        }
    }

    //to be removed
    public String[] returnArrayByBodyPartName(@NonNull String bodyPartName){
        switch (bodyPartName){
            case "CHEST":
                return chestArray;
            case "BICEPS":
                return bicepsArray;
            case "TRICEPS":
                return tricepsArray;
            case "BACK":
                return backArray;
            case "SHOULDERS":
                return shouldersArray;
            case "LEGS":
                return legsArray;
            case "CARDIO":
                return cardioArray;
            default:
                return null;
        }
    }
}
