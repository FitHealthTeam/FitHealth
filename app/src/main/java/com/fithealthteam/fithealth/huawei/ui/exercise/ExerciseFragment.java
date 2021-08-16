package com.fithealthteam.fithealth.huawei.ui.exercise;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.databinding.ExerciseFragmentBinding;
import com.fithealthteam.fithealth.huawei.databinding.FragmentHomeBinding;
import com.fithealthteam.fithealth.huawei.myplan.MyPlanActivity;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;

import java.util.List;


public class ExerciseFragment extends Fragment implements CloudDBZoneWrapper.exerciseUICallBack {

    private ExerciseViewModel mViewModel;
    private ExerciseFragmentBinding binding;

    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    private Handler handler;
    private View root;

    public ExerciseFragment(){
        cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();
    }

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
        root = binding.getRoot();

        ConstraintLayout TaskIndicateLayout = root.findViewById(R.id.TaskIndicateLayout);

        TaskIndicateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MyPlanActivity.class);
                startActivity(i);
            }
        });

        //init cloudDBZoneWrapper and update the percentage Indicator
        handler = new Handler(Looper.getMainLooper());
        handler.post(()->{
            initCloudDBWrapper();
        });



        //return the view object
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        //init cloudDBZoneWrapper and update the percentage Indicator
        handler = new Handler(Looper.getMainLooper());
        handler.post(()->{
            initCloudDBWrapper();
        });
    }

    //Initialize Cloud DB Wrapper to use
    public void initCloudDBWrapper(){
        handler.postDelayed(() -> {
            cloudDBZoneWrapperInstance.addCallBack(ExerciseFragment.this);
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
            AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
            CloudDBZoneQuery<exercise> query = CloudDBZoneQuery.where(exercise.class)
                    .equalTo("uid", user.getUid())
                    .equalTo("deleteStatus", false);
            cloudDBZoneWrapperInstance.queryExercise(query);

        }, 500);
    }


    @Override
    public void onAddorQuery(List<exercise> exerciseList) {
        //calculate the count
        TextView completionText = root.findViewById(R.id.taskCompletion);
        int count = 0;
        for (exercise item: exerciseList) {
            if(item.getCompleteStatus()){
                count++;
            }
        }

        int listSize = exerciseList.size();

        if(listSize > 0){
            completionText.setText(count + " of "+ listSize +" has completed");
        }else {
            completionText.setText("0 of 0 has completed");
        }

        //update the percentage circle indicator in my plan activity
        TextView percentageIndicator = root.findViewById(R.id.percentageIndicator);

        if(listSize > 0){
            double percentageResult = ((double)count/listSize)*100;
            percentageIndicator.setText((String.format("%.2f",percentageResult)+"%"));
        }else {
            percentageIndicator.setText(("0%"));
        }
    }

    @Override
    public void onSubscribe(List<exercise> exerciseList) {

    }

    @Override
    public void onDelete(List<exercise> exerciseList) {

    }

    @Override
    public void showError(String error) {

    }
}