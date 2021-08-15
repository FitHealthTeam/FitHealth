package com.fithealthteam.fithealth.huawei.myplan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;


import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.customListViewAdapter.ExerciseEventListAdapter;


import java.util.ArrayList;
import java.util.List;

public class MyPlanActivity extends AppCompatActivity implements CloudDBZoneWrapper.exerciseUICallBack {

    static ListView listView;
    static ArrayList<exercise> list = new ArrayList<>();
    static ExerciseEventListAdapter adapter;

    private Handler handler = null;
    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    public MyPlanActivity(){
        cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plan);
        //hide the top action bar and title
        getSupportActionBar().hide();

        //initialize handle for later use and assign with main thread
        handler = new Handler(Looper.getMainLooper());

        //prepare cloudDBZoneWrapper
        handler.post(() -> {
            initCloudDBWrapper();
        });

        //List View
        listView = findViewById(R.id.eventListView);

        //testing purpose
        /*
        exercise ex1 = new exercise();
        ex1.setExerciseType("Badminton");
        ex1.setCalories(200.00);
        ex1.setCompleteStatus(false);

        exercise ex2 = new exercise();
        ex1.setExerciseType("Swiming");
        ex1.setCalories(200.00);
        ex1.setCompleteStatus(false);

        exercise ex3 = new exercise();
        ex1.setExerciseType("Jogging");
        ex1.setCalories(200.00);
        ex1.setCompleteStatus(false);

        exercise ex4 = new exercise();
        ex1.setExerciseType("Hiking");
        ex1.setCalories(600.00);
        ex1.setCompleteStatus(false);

        list.add(ex1);
        list.add(ex2);
        list.add(ex3);
        list.add(ex4);*/

        //setup list adapter
        adapter = new ExerciseEventListAdapter(getApplicationContext(),list);
        //listView.setAdapter(adapter);

    }


    //remove item from list
    public static void removeListViewItem(int position){
        list.remove(position);
        listView.setAdapter(adapter);
    }

    //update the check box status in the object
    public static void completeItem(int position, boolean checkStatus){
        Log.d("Status", position + " is " + checkStatus);
        list.get(position).setCompleteStatus(checkStatus);
        adapter.setNotifyOnChange(true);
    }

    //Initialize Cloud DB Wrapper to use
    public void initCloudDBWrapper(){
        handler.postDelayed(() -> {
            cloudDBZoneWrapperInstance.addCallBack(MyPlanActivity.this);
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
            cloudDBZoneWrapperInstance.queryAllExercise();
        }, 1000);
    }

    //call back function from the CloudDBZoneWrapper
    @Override
    public void onAddorQuery(List<exercise> exerciseList) {
        handler.post(()->{
            list.clear();
            list.addAll(exerciseList);
            listView.setAdapter(adapter);
        });
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