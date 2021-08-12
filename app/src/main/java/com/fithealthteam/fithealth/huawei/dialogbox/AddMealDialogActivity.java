package com.fithealthteam.fithealth.huawei.dialogbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.R;

public class AddMealDialogActivity extends AppCompatDialogFragment {

    private EditText newMealInput;
    private EditText newMealCaloriesInput;
    private String newMealName, newMealCalories;
    private AddMealDialogListener dialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_meal_dialog, null);

        builder.setView(view)
                .setTitle("Add New Meal")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing to do when user click cancel
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newMealName = newMealInput.getText().toString();//convert to string
                        newMealCalories = newMealCaloriesInput.getText().toString();
                        dialogListener.applynewMeal(newMealName, newMealCalories);//pass back to main activity
                    }
                });

        newMealInput = view.findViewById(R.id.mealName);
        newMealCaloriesInput = view.findViewById(R.id.mealCalories);

        return builder.create();
    }

    public interface AddMealDialogListener{
        void applynewMeal(String newMealName, String newMealCalories);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            dialogListener = (AddMealDialogListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + " You must implement AddMealDialogListener !");
        }
    }
}