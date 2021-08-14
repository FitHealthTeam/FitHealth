package com.fithealthteam.fithealth.huawei.customListViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.myplan.MyPlanActivity;

import java.util.ArrayList;

public class ExerciseEventListAdapter extends ArrayAdapter<Exercise> {

    ArrayList<Exercise> list = new ArrayList<>();
    Context context;

    public ExerciseEventListAdapter(Context context, ArrayList<Exercise> listItems){
        super(context, R.layout.custom_exercise_event_list_adapter, listItems);
        this.context = context;
        list = listItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_exercise_event_list_adapter,null);

            //place the data to the list view layout
            TextView eventName = convertView.findViewById(R.id.EventName);
            eventName.setText(list.get(position).getExerciseType());

            TextView eventCalories = convertView.findViewById(R.id.EventCalories);
            eventCalories.setText(list.get(position).getCalories() + " kcal");

            CheckBox completeStatus = convertView.findViewById(R.id.EventCompleteStatus);
            completeStatus.setChecked(list.get(position).getCompleteStatus());

            //check box event listener for completeStatus
            completeStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //process the complete check box when there is changes
                    MyPlanActivity.completeItem(position, completeStatus.isChecked());
                }
            });

            //remove button in list
            ImageView removeBtn = convertView.findViewById(R.id.removeEventItem);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //invoke remove method in implemented activity
                    MyPlanActivity.removeListViewItem(position);
                }
            });

        }
        //return super.getView(position, convertView, parent);
        return convertView;
    }

}
