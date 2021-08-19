package com.fithealthteam.fithealth.huawei.ui.settings;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.CloudDB.user;
import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.fithealthteam.fithealth.huawei.authentication.passResetActivity;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;

import java.util.List;


public class SettingsFragment extends Fragment implements CloudDBZoneWrapper.userUICallBack, CloudDBZoneWrapper.exerciseUICallBack {

    private SettingsViewModel mViewModel;

    private ImageView fullachievement_badge, gym_badge, runner_badge, swimmer_badge, biking_badge;
    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    private Handler handler = new Handler();

    private TextView healthpt;
    private TextView userLFname;

    Switch excessCalorySwitch, drinkWaterReminderSwitch, subscriptionSwitch;

    SharedPreferences sharedPref;

    SharedPreferences.Editor prefEditor;

    //authenticated user access here
    AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

    public SettingsFragment(){
        cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);

        //proceed to initialize cloudDBZoneWrapper
        handler.post(()->{
            initCloudDBZone();
        });

        //execute the cloudDB task, delayed a bit to wait for initialization complete
        handler.post(()->{
            queryAll();
        });

        //prepare shared preferences to store a copy of some of the settings
        sharedPref = getContext().getSharedPreferences("MySharedPreferences", getContext().MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        Button logoutBtn = v.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AGConnectAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), authenticateActivity.class);
                startActivity(intent);
            }
        });

        userLFname = v.findViewById(R.id.user_firstlast_name);
        healthpt =  v.findViewById(R.id.healthpt);

        //all achievement get
        fullachievement_badge = v.findViewById(R.id.achievement_badge);
        gym_badge = v.findViewById(R.id.exercise_king_badge);
        runner_badge = v.findViewById(R.id.runer_badge);
        swimmer_badge = v.findViewById(R.id.swimer_badge);
        biking_badge = v.findViewById(R.id.biking_badge);

        //if do any exercise more than 100 time will light
        /*
        fullachievement_badge.setImageResource(R.drawable.trophy_badge_light);
        gym_badge.setImageResource(R.drawable.exerciseking_badge_light);
        runner_badge.setImageResource(R.drawable.runner_badge_light);
        swimmer_badge.setImageResource(R.drawable.swimmer_badge_light);
        biking_badge.setImageResource(R.drawable.biking_badge_light);
        */


        //switch
        excessCalorySwitch = v.findViewById(R.id.execess_calory_warn);
        drinkWaterReminderSwitch = v.findViewById(R.id.drinkwater_remind);
        subscriptionSwitch = v.findViewById(R.id.tip_subscribe);


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
                handler.post(()->{
                    cloudDBZoneWrapperInstance.updatecaloriesWarning(user.getUid(),isChecked);
                });
                prefEditor.putBoolean("excessCalories",isChecked);
                prefEditor.commit();
            }
        });

        drinkWaterReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handler.post(()->{
                    cloudDBZoneWrapperInstance.updateDrinkWater(user.getUid(),isChecked);
                });

                prefEditor.putBoolean("drinkWaterReminder",isChecked);
                prefEditor.commit();
            }
        });

        subscriptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handler.post(()->{
                    cloudDBZoneWrapperInstance.updateSubscribedTips(user.getUid(),isChecked);
                });
                prefEditor.putBoolean("subscription",isChecked);
                prefEditor.commit();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        cloudDBZoneWrapperInstance.closeCloudDBZone();
    }

    //initialize cloudDBZone
    private void initCloudDBZone(){
        handler.post(()->{
            //add callback into cloudDBZoneWrapper
            cloudDBZoneWrapperInstance.addExerciseCallBack(SettingsFragment.this);
            cloudDBZoneWrapperInstance.addUserCallBack(SettingsFragment.this);

            //initialize
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
        });
    }

    public void queryAll(){
        handler.postDelayed(()->{
            CloudDBZoneQuery<exercise> query1 = CloudDBZoneQuery.where(exercise.class).equalTo("uid", user.getUid()).equalTo("completeStatus", true);
            cloudDBZoneWrapperInstance.queryExercise(query1);

            CloudDBZoneQuery<user> query2 = CloudDBZoneQuery.where(user.class).equalTo("id",user.getUid());
            cloudDBZoneWrapperInstance.queryUser(query2);
        },500);
    }

    //determine count total of a type of exercise is done by user
    public int countExercise(List<exercise> list, String targetExercise){
        int count = 0;
        for(exercise temp : list){
            if(temp.getExerciseType().equals(targetExercise)){
                count++;
            }
        }
        return count;
    }

    //validate user is valid to get the badge or not
    public boolean canGetBadge(List<exercise> list, String targetExercise){
        int count = 0;
        for(exercise temp : list){
            if(temp.getExerciseType().equals(targetExercise)){
                count++;
            }
        }

        if(count >= 100){
            return true;
        }else{
            return false;
        }
    }

    //exercise object callback
    @Override
    public void onAddorQuery(List<exercise> exerciseList) {

        //set the health point
        healthpt.setText(String.valueOf(exerciseList.size()));

        if(canGetBadge(exerciseList, "Gym")){
            gym_badge.setImageResource(R.drawable.exerciseking_badge_light);
        }

        if(canGetBadge(exerciseList, "Jogging")){
            runner_badge.setImageResource(R.drawable.runner_badge_light);
        }

        if(canGetBadge(exerciseList, "Swimming")){
            swimmer_badge.setImageResource(R.drawable.swimmer_badge_light);
        }

        if(canGetBadge(exerciseList, "Biking")){
            biking_badge.setImageResource(R.drawable.biking_badge_light);
        }

        if(canGetBadge(exerciseList, "Gym")
        && canGetBadge(exerciseList, "Jogging")
        && canGetBadge(exerciseList, "Swimming")
        && canGetBadge(exerciseList, "Biking")
        ){
            fullachievement_badge.setImageResource(R.drawable.trophy_badge_light);
        }

        //store a copy if does not exist in shared preferences
        boolean resultexcessCalories = sharedPref.getBoolean("excessCalories", false);
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


    //call back from cloudDBWrapper for user object
    @Override
    public void userOnAddorQuery(List<com.fithealthteam.fithealth.huawei.CloudDB.user> userList) {
        com.fithealthteam.fithealth.huawei.CloudDB.user tempUser = userList.get(0);

        userLFname.setText(tempUser.getFirstName() + " " + tempUser.getLastName());

        excessCalorySwitch.setChecked(tempUser.getExcessiveCalories());
        drinkWaterReminderSwitch.setChecked(tempUser.getDrinkWater());
        subscriptionSwitch.setChecked(tempUser.getSubscribeTips());


    }

    @Override
    public void userOnSubscribe(List<com.fithealthteam.fithealth.huawei.CloudDB.user> userList) {

    }

    @Override
    public void userOnDelete(List<com.fithealthteam.fithealth.huawei.CloudDB.user> userList) {

    }

    @Override
    public void userShowError(String error) {

    }





}