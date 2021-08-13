package com.fithealthteam.fithealth.huawei.myplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.Exercise;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.customListViewAdapter.ExerciseEventListAdapter;


import java.util.ArrayList;
import java.util.List;

public class MyPlanActivity extends AppCompatActivity implements CloudDBZoneWrapper.exerciseUICallBack {

    static ListView listView;
    static ArrayList<Exercise> list = new ArrayList<>();
    static ExerciseEventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plan);
        //hide the top action bar and title
        getSupportActionBar().hide();

        //List View
        listView = findViewById(R.id.eventListView);
        list.add(new Exercise("Fish n Chip", 300.00, false));
        list.add(new Exercise("Swimming", -600.00, false));
        list.add(new Exercise("KFC", 500.00, false));
        list.add(new Exercise("Badminton", -300.00, false));

        //setup list adapter
        adapter = new ExerciseEventListAdapter(getApplicationContext(),list);
        listView.setAdapter(adapter);

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

    //call back function from the CloudDBZoneWrapper
    @Override
    public void onAddorQuery(List<Exercise> exerciseList) {
        list.addAll(exerciseList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onSubscribe(List<Exercise> exerciseList) {

    }

    @Override
    public void onDelete(List<Exercise> exerciseList) {

    }

    @Override
    public void showError(String error) {

    }

}