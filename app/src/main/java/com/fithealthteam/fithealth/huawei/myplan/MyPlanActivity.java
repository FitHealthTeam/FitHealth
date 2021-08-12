package com.fithealthteam.fithealth.huawei.myplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.DataModel.BalanceDietData;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.customListViewAdapter.ExerciseEventListAdapter;
import com.fithealthteam.fithealth.huawei.dialogbox.AddMealDialogActivity;

import java.util.ArrayList;

public class MyPlanActivity extends AppCompatActivity implements AddMealDialogActivity.AddMealDialogListener {

    static ListView listView;
    static ArrayList<BalanceDietData> list = new ArrayList<>();
    static ExerciseEventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plan);
        //hide the top action bar and title
        getSupportActionBar().hide();

        //List View
        listView = findViewById(R.id.eventListView);
        list.add(new BalanceDietData("Fish n Chip", 300, false));
        list.add(new BalanceDietData("Swimming", -600, false));
        list.add(new BalanceDietData("KFC", 500, false));
        list.add(new BalanceDietData("Badminton", -300, false));

        //setup list adapter
        adapter = new ExerciseEventListAdapter(getApplicationContext(),list);
        listView.setAdapter(adapter);

        //Add Meal Btn
        Button addMealBtn = findViewById(R.id.addMealBtn);

        addMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMealDialogActivity newMealDialog = new AddMealDialogActivity();
                newMealDialog.show(getSupportFragmentManager(), "Add New Meal Dialog");
            }
        });
    }

    @Override
    public void applynewMeal(String newMealName, String newMealCalories) {
        //process the data from add new meal dialog here
        if(newMealName.trim().isEmpty() || newMealCalories.trim().isEmpty()){
            //warn user about the empty data
            Toast.makeText(getApplicationContext(),"You should not enter the empty meal name or calories !", Toast.LENGTH_SHORT).show();
        }else {
            //continue process data
        }
    }

    //remove item from list
    public static void removeListViewItem(int position){

    }

    //update the check box status in the object
    public static void completeItem(int position, boolean checkStatus){
        Log.d("Status", position + " is " + checkStatus);
    }
}