package com.fithealthteam.fithealth.huawei.myplan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.customListViewAdapter.ExerciseEventListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPlanActivity extends AppCompatActivity implements CloudDBZoneWrapper.exerciseUICallBack,
        ExerciseEventListAdapter.ExerciseListCallBack,
        AddExerciseDialogActivity.AddExerciseDialogListener {

    private ListView listView;
    private ArrayList<exercise> list = new ArrayList<>();
    private ExerciseEventListAdapter adapter;

    private HashMap<Integer, Integer> listPosition = new HashMap<>();

    private Handler handler = null;
    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

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

        FloatingActionButton addExercise = findViewById(R.id.addNewExercise);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddExerciseDialogActivity addExerciseDialog = new AddExerciseDialogActivity();
                addExerciseDialog.show(getSupportFragmentManager(),"Add New Exercise Dialog");
            }
        });

    }

    //call back for add new exercise dialog
    @Override
    public void passExerciseInformation(exercise item) {

        item.setUid(user.getUid());

        CloudDBZoneQuery<exercise> tempQuery  = CloudDBZoneQuery.where(exercise.class)
                .equalTo("uid", user.getUid());

        //cloudDBZoneWrapperInstance.queryExercise(tempQuery);

        //insert the new exercise into CloudDB
        handler.post(()->{
            cloudDBZoneWrapperInstance.upsertExercise(item);
        });
    }

    @Override
    protected void onDestroy() {
        handler.post(cloudDBZoneWrapperInstance::closeCloudDBZone);
        super.onDestroy();
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

        //show the burning calories
        double burntCalories = 0.00;

        for (exercise item: list) {
            if (item.getCompleteStatus()){
                burntCalories += item.getCalories();
            }
        }

        TextView burntCaloriesText = findViewById(R.id.BurntCalories);
        burntCaloriesText.setText(burntCalories + " kcal");

        //calculate the count
        TextView completionText = findViewById(R.id.taskCompletionIndoor);
        int count = 0;
        for (exercise item: list) {
            if(item.getCompleteStatus()){
                count++;
            }
        }
        completionText.setText(count + " of "+ list.size() +" has completed");

        //update the percentage circle indicator in my plan acitivty
        TextView percentageIndicator = findViewById(R.id.percentageIndicatorIndoor);
        percentageIndicator.setText((count/list.size()*100)+"%");
    }

    //Initialize Cloud DB Wrapper to use
    public void initCloudDBWrapper(){
        handler.postDelayed(() -> {
            cloudDBZoneWrapperInstance.addCallBack(MyPlanActivity.this);
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
            user = AGConnectAuth.getInstance().getCurrentUser();
            CloudDBZoneQuery<exercise> query = CloudDBZoneQuery.where(exercise.class)
                    .equalTo("uid", user.getUid());
            cloudDBZoneWrapperInstance.queryExercise(query);

        }, 1000);
    }

    //call back function from the CloudDBZoneWrapper
    @Override
    public void onAddorQuery(List<exercise> exerciseList) {
        handler.post(()->{
            double burntCalories = 0.00;

            list.clear();
            listPosition.clear();
            for(int i = 0; i<exerciseList.size(); i++){
                list.add(exerciseList.get(i));
                listPosition.put(i, exerciseList.get(i).getId());
                if(exerciseList.get(i).getCompleteStatus()){
                    burntCalories += exerciseList.get(i).getCalories();
                }
            }
            listView.setAdapter(adapter);

            //show the burning calories
            TextView burntCaloriesText = findViewById(R.id.BurntCalories);
            burntCaloriesText.setText( burntCalories + " kcal");

            //calculate the count
            TextView completionText = findViewById(R.id.taskCompletionIndoor);
            int count = 0;
            for (exercise item: list) {
                if(item.getCompleteStatus()){
                    count++;
                }
            }
            completionText.setText(count + " of "+ list.size() +" has completed");

            //update the percentage circle indicator in my plan acitivty
            TextView percentageIndicator = findViewById(R.id.percentageIndicatorIndoor);
            percentageIndicator.setText((count/list.size()*100)+"%");

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
        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT);
    }
}