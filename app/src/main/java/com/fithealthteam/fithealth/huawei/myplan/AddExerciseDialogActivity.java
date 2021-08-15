package com.fithealthteam.fithealth.huawei.myplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.R;

import java.util.Calendar;
import java.util.Locale;

public class AddExerciseDialogActivity extends AppCompatDialogFragment {

    private AddExerciseDialogListener listener;

    private EditText caloriesInput;
    private RadioGroup exerciseTypeRadioGroup;
    private String selectedExercise;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_add_exercise_dialog,null);

        builder.setView(v)
                .setTitle("Add Exercise")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing to do when cancel
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exercise temp = new exercise();
                        temp.setCompleteStatus(false);
                        temp.setCalories(Double.parseDouble(caloriesInput.getText().toString()));
                        temp.setDate(Calendar.getInstance().getTime());
                        temp.setExerciseType(selectedExercise);
                        //get info from layout and invoke the callback to activity
                        listener.passExerciseInformation(temp);
                    }
                });

        //exercise type selection
        exerciseTypeRadioGroup = v.findViewById(R.id.exerciseTypeRadioGroup);
        exerciseTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Toast.makeText(getContext(), "Selected Button is "+ ((RadioButton) v.findViewById(checkedId)).getText().toString(), Toast.LENGTH_SHORT).show();
                selectedExercise = ((RadioButton) v.findViewById(checkedId)).getText().toString();
            }
        });

        //calories for the exercise to insert
        caloriesInput = (EditText) v.findViewById(R.id.exerciseCalories);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddExerciseDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+ " You must implement AddExerciseDialogListener");
        }
    }

    public interface AddExerciseDialogListener{
        void passExerciseInformation(exercise item);
    }
}


