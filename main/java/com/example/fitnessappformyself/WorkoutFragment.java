package com.example.fitnessappformyself;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static WorkoutFragment newInstance(String param1, String param2) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        String[] s6 = getResources().getStringArray(R.array.SHOULDERS);
        String[] s7 = getResources().getStringArray(R.array.LEGS);
        String[] s8 = getResources().getStringArray(R.array.CARDIO);

        MovesRecyclerViewAdapter newAdapter = new MovesRecyclerViewAdapter(getActivity(), s1, s2, s3, s4, s5, s6, s7, s8);
        recyclerView.setAdapter(newAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}