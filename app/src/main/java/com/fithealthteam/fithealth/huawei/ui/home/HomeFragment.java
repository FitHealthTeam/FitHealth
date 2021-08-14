package com.fithealthteam.fithealth.huawei.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fithealthteam.fithealth.huawei.BMIInput.BMIInput_Activity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.databinding.FragmentHomeBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    // slider images list
    SliderView mSliderView;
    int[] images = {R.drawable.healthtips_1,
            R.drawable.healthtips_2,
            R.drawable.healthtips_3,
            R.drawable.healthtips_4,
            R.drawable.healthtips_5};
    CalendarView mCalenderView;
    TextView dateSelected;
    ImageView editBMI;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /*homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;*/


        //view object pass in
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        // slider image
        mSliderView = v.findViewById(R.id.image_slider);

        SliderAdapter sliderAdapter = new SliderAdapter(images);

        mSliderView.setSliderAdapter(sliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.NONE);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.startAutoCycle();


        // calender view
        mCalenderView = v.findViewById(R.id.calendarView);
        dateSelected = v.findViewById(R.id.dateSelected);

        //mCalenderView.setDate(20210814);

        mCalenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateSelected.setText(date);
            }
        });

/*
        // BMI Input
        editBMI = v.findViewById(R.id.editBMI);

        editBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BMIInput_Activity.class);
                startActivity(intent);
            }
        });

        // BMI value

        TextView tvBMI = v.findViewById(R.id.tvBMIResult);

        String bmi = getArguments().getString("BMI");
        tvBMI.setText(bmi);
*/



        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}