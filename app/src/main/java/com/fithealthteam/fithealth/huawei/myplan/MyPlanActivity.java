package com.fithealthteam.fithealth.huawei.myplan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;


import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.customListViewAdapter.ExerciseEventListAdapter;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPlanActivity extends AppCompatActivity implements CloudDBZoneWrapper.exerciseUICallBack, ExerciseEventListAdapter.ExerciseListCallBack {

    private ListView listView;
    private ArrayList<exercise> list = new ArrayList<>();
    private ExerciseEventListAdapter adapter;

    private HashMap<Integer, Integer> listPosition = new HashMap<>();

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
        adapter.addExerciseAdapterCallBack(MyPlanActivity.this);
        //listView.setAdapter(adapter);

    }


    //remove item from list
    public void removeItem(int position){
        //execute delete function to delete item in cloud db
        cloudDBZoneWrapperInstance.deleteExercise(list.get(position));

        list.remove(position);
        //update the list view
        listView.setAdapter(adapter);
    }

    //update the check box status in the object
    public void completeItem(int position, boolean checkStatus){
        Log.d("Status", position + " is " + checkStatus);
        list.get(position).setCompleteStatus(checkStatus);

        //get currentposition to exercise ID
        Integer currentPosition = listPosition.get(position);

        //execute the upsert function to update the exercise item
        cloudDBZoneWrapperInstance.upsertExercise(list.get(position));
        adapter.setNotifyOnChange(true);
    }

    //Initialize Cloud DB Wrapper to use
    public void initCloudDBWrapper(){
        handler.postDelayed(() -> {
            cloudDBZoneWrapperInstance.addCallBack(MyPlanActivity.this);
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
            AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
            CloudDBZoneQuery<exercise> query = CloudDBZoneQuery.where(exercise.class)
                    .equalTo("uid", user.getUid());
            cloudDBZoneWrapperInstance.queryExercise(query);
        }, 1000);
    }

    //call back function from the CloudDBZoneWrapper
    @Override
    public void onAddorQuery(List<exercise> exerciseList) {
        handler.post(()->{
            double inputCalories = 0.00;

            list.clear();
            listPosition.clear();
            for(int i = 0; i<exerciseList.size(); i++){
                list.add(exerciseList.get(i));
                listPosition.put(i, exerciseList.get(i).getId());
                inputCalories += exerciseList.get(i).getCalories();
            }
            listView.setAdapter(adapter);

            TextView inputCaloriesText = findViewById(R.id.dailyCaloriesInput);
            inputCaloriesText.setText( inputCalories + " kcal");

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