package com.fithealthteam.fithealth.huawei.ui.exercise;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.databinding.ExerciseFragmentBinding;
import com.fithealthteam.fithealth.huawei.databinding.FragmentHomeBinding;
import com.fithealthteam.fithealth.huawei.myplan.MyPlanActivity;

public class ExerciseFragment extends Fragment {

    private ExerciseViewModel mViewModel;
    private ExerciseFragmentBinding binding;

    public static ExerciseFragment newInstance() {
        return new ExerciseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //view object pass in
        View v = inflater.inflate(R.layout.exercise_fragment, container, false);

        //You may use the v to direct v.findViewById() OR binding.ITEM_ID_YOU_HAD_NAMED to reference to the item

        //binding to the fragment layout element
        binding = ExerciseFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ConstraintLayout TaskIndicateLayout = v.findViewById(R.id.TaskIndicateLayout);

        TaskIndicateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MyPlanActivity.class);
                startActivity(i);
            }
        });

        //return the view object
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        // TODO: Use the ViewModel
    }
}