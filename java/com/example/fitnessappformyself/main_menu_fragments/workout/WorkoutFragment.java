package com.example.fitnessappformyself.main_menu_fragments.workout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitnessappformyself.main_menu_fragments.workout.MovesRecyclerViewAdapter;
import com.example.fitnessappformyself.R;

public class WorkoutFragment extends Fragment {

    public WorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        initializeRecyclerView(rootView);

        return rootView;
    }

    public void initializeRecyclerView(@NonNull View rootView){
        RecyclerView recyclerView = rootView.findViewById(R.id.movesRecyclerView);

        String[] s1 = getResources().getStringArray(R.array.bodyParts);
        String[] s2 = getResources().getStringArray(R.array.CHEST);
        String[] s3 = getResources().getStringArray(R.array.BICEPS);
        String[] s4 = getResources().getStringArray(R.array.TRICEPS);
        String[] s5 = getResources().getStringArray(R.array.BACK);
        String[] s6 = getResources().getStringArray(R.array.LEGS);
        String[] s7 = getResources().getStringArray(R.array.SHOULDERS);
        String[] s8 = getResources().getStringArray(R.array.CARDIO);

        MovesRecyclerViewAdapter newAdapter = new MovesRecyclerViewAdapter(getActivity(), s1, s2, s3, s4, s5, s6, s7, s8);
        recyclerView.setAdapter(newAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}