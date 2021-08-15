package com.fithealthteam.fithealth.huawei.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.fithealthteam.fithealth.huawei.authentication.passResetActivity;
import com.huawei.agconnect.auth.AGConnectAuth;


public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);

        Button logoutBtn = v.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AGConnectAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), authenticateActivity.class);
                startActivity(intent);
            }
        });

        TextView userLFname = v.findViewById(R.id.user_firstlast_name);
        TextView healthpt =  v.findViewById(R.id.healthpt);

        //all achievement get
        ImageView fullachievement_badge = v.findViewById(R.id.achievement_badge);
        ImageView gym_badge = v.findViewById(R.id.exercise_king_badge);
        ImageView runner_badge = v.findViewById(R.id.runer_badge);
        ImageView swimmer_badge = v.findViewById(R.id.swimer_badge);
        ImageView biking_badge = v.findViewById(R.id.biking_badge);

        //if do any exercise more than 100 time will light
        fullachievement_badge.setImageResource(R.drawable.trophy_badge_light);
        gym_badge.setImageResource(R.drawable.exerciseking_badge_light);
        runner_badge.setImageResource(R.drawable.runner_badge_light);
        swimmer_badge.setImageResource(R.drawable.swimmer_badge_light);
        biking_badge.setImageResource(R.drawable.biking_badge_light);
        //check later using health point



        //switch
        Switch excessCalorySwitch = v.findViewById(R.id.execess_calory_warn);
        Switch drinkWaterReminderSwitch = v.findViewById(R.id.drinkwater_remind);
        Switch subscriptionSwitch = v.findViewById(R.id.tip_subscribe);

        /* Execess calories formula
        For men:
        BMR = 10W + 6.25H - 5A + 5
        For women:
        BMR = 10W + 6.25H - 5A - 161
        * */

        //store data into system
        excessCalorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(excessCalorySwitch.getText().toString().toUpperCase().equals("ON")){
                    //store on to db
                } else {
                    //store off to db
                }
            }
        });

        drinkWaterReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(drinkWaterReminderSwitch.getText().toString().toUpperCase().equals("ON")) {
                    //store on to db
                } else {
                    //store off to db
                }
            }
        });

        subscriptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(subscriptionSwitch.getText().toString().toUpperCase().equals("ON")) {
                    //store on to db
                } else {
                    //store off to db
                }
            }
        });

        ImageView profileUpdate = v.findViewById(R.id.info_update);
        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), passResetActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}