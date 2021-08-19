package com.fithealthteam.fithealth.huawei.ui.home;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fithealthteam.fithealth.huawei.BMIInput.BMIInput_Activity;
import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.CloudDB.user;
import com.fithealthteam.fithealth.huawei.CreateDietPlan.CreateDietPlan_Activity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.fithealthteam.fithealth.huawei.databinding.FragmentHomeBinding;
import com.fithealthteam.fithealth.huawei.ui.settings.SettingsFragment;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements CloudDBZoneWrapper.userUICallBack, CloudDBZoneWrapper.exerciseUICallBack {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    // slider images list
    SliderView mSliderView;
    int[] images = {R.drawable.healthtips_1,
            R.drawable.healthtips_2,
            R.drawable.healthtips_3,
            R.drawable.healthtips_4,
            R.drawable.healthtips_5};

    CalendarView mCalenderView;
    TextView dateSelected;
    ImageView editBMI;
    TextView tvBMI;
    TextView burnedCalories;

    private int currentProgress = 0;
    private ProgressBar progressBar;
    private Button btnSet,btnTimePick;

    int hours,minutes;

    private Handler handler = new Handler();
    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

    public HomeFragment(){ cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();}




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //view object pass in
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        if(user == null){
            Intent intent = new Intent(v.getContext(), authenticateActivity.class);
            startActivity(intent);
        }else{
            Log.d("HMS Auth User", user.getEmail());
            Log.d("HMS Auth User UID", user.getUid());

            //proceed to initialize cloudDBZoneWrapper
            handler.post(()->{
                initCloudDBZone();
            });

            //execute the cloudDB task, delayed a bit to wait for initialization complete
            handler.post(()->{
                queryAll();
            });


            // Slider Image View
            mSliderView = v.findViewById(R.id.image_slider);

            SliderAdapter sliderAdapter = new SliderAdapter(images);

            mSliderView.setSliderAdapter(sliderAdapter);
            mSliderView.setIndicatorAnimation(IndicatorAnimationType.NONE);
            mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            mSliderView.startAutoCycle();


            // Intent BMI Input Page
            editBMI = v.findViewById(R.id.editBMI);

            editBMI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), BMIInput_Activity.class);
                    startActivity(intent);
                }
            });

            // Retrieve Calories value
            burnedCalories = v.findViewById(R.id.tvBurnedCaloriesResult);

            // Retrieve BMI value
            tvBMI = v.findViewById(R.id.tvBMIResult);


            // Progress Bar
            progressBar = v.findViewById(R.id.bmiIndicator);

        /*if(passedBMI != null){
            float i = Float.parseFloat(passedBMI);
            //int a = Math.round(i);
            if(i>=18.5 && i<=24.9){
                currentProgress = 70;
            }else if(i>=25 && i<=29.9){
                currentProgress = 45;
            }else if(i>=30 && i<=39.9){
                currentProgress = 30;
            }else {
                currentProgress = 10;
            }
            progressBar.setProgress(currentProgress);
        }*/


            // Time Picker
            btnTimePick = v.findViewById(R.id.btnTimePick);

            btnTimePick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                            hours = selectedHour;
                            minutes = selectedMinute;
                            btnTimePick.setText(String.format(Locale.getDefault(),"%02d:%02d",hours,minutes));
                        }
                    };
                    int style = AlertDialog.THEME_HOLO_LIGHT;

                    TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),style,onTimeSetListener,hours,minutes,true);

                    timePickerDialog.setTitle("Select Time");
                    timePickerDialog.show();
                }
            });

            // for fragment
            //SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

            // Reminder Notification
            createNotificationChannel();

            btnSet = v.findViewById(R.id.btnSet);

            btnSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(hours == 0 && minutes == 0 ){
                        Toast.makeText(v.getContext(), "Please Pick a Time First!", Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(v.getContext(), "Reminder Set!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(v.getContext(),ReminderBroadcast.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(),0,intent,0);

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

                        long timeAtButtonClick = System.currentTimeMillis();

                        long hourFromPick = hours * 1000 * 60 * 60;
                        long minuteFromPick = minutes * 1000 * 60;

                        //long tenSecondsInMillis = 1000 * 10;

                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                timeAtButtonClick + hourFromPick + minuteFromPick,pendingIntent);
                    }

                    btnTimePick.setText("SET!");


                }
            });
        }

        return v;
    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Channel for User Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //init cloudDBZoneWrapper and update the value
        handler = new Handler(Looper.getMainLooper());
        handler.post(()->{
            initCloudDBZone();
        });
    }

    //initialize cloudDBZone
    private void initCloudDBZone(){
        handler.post(()->{
            //add callback into cloudDBZoneWrapper
            cloudDBZoneWrapperInstance.addExerciseCallBack(HomeFragment.this);
            cloudDBZoneWrapperInstance.addUserCallBack(HomeFragment.this);

            //initialize
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
        });
    }

    public void queryAll(){
        handler.postDelayed(()->{
            CloudDBZoneQuery<exercise> query1 = CloudDBZoneQuery.where(exercise.class).equalTo("uid", user.getUid()).equalTo("completeStatus", true).equalTo("deleteStatus",false);
            cloudDBZoneWrapperInstance.queryExercise(query1);

            CloudDBZoneQuery<com.fithealthteam.fithealth.huawei.CloudDB.user> query2 = CloudDBZoneQuery.where(user.class).equalTo("id",user.getUid());
            cloudDBZoneWrapperInstance.queryUser(query2);
        },500);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // User
    @Override
    public void userOnAddorQuery(List<com.fithealthteam.fithealth.huawei.CloudDB.user> userList) {
        if(userList.size()>0) {
            com.fithealthteam.fithealth.huawei.CloudDB.user tempUser = userList.get(0);
            double weight = 0;
            double height = 0;
            double bmi;

            weight = tempUser.getWeight();
            height = tempUser.getHeight();

            // if user don't have weight and height value = new user
            if (tempUser.getWeight() == 0 || tempUser.getHeight() == 0) {
                Intent intent = new Intent(getActivity().getApplicationContext(), BMIInput_Activity.class);
                startActivity(intent);
            } else {
                bmi = weight / (height * height);
                String bmiResult = String.format("%.2f", bmi);

                tvBMI.setText(bmiResult + " kg/m2");

                if (bmi >= 18.5 && bmi <= 24.9) {
                    currentProgress = 70;
                } else if (bmi >= 25 && bmi <= 29.9) {
                    currentProgress = 45;
                } else if (bmi >= 30 && bmi <= 39.9) {
                    currentProgress = 30;
                } else {
                    currentProgress = 10;
                }
                progressBar.setProgress(currentProgress);

            }
        }

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

    // Exercise
    @Override
    public void onAddorQuery(List<exercise> exerciseList) {

        if(exerciseList.size() >0) {
            double calories = 0;
            double totalCalories = 0;

            for (exercise temp : exerciseList) {
                calories = temp.getCalories();
                totalCalories += calories;
            }

            if (totalCalories != 0) {
                String burnedCaloriesResult = String.format("%.2f", totalCalories);
                burnedCalories.setText(burnedCaloriesResult + " kal");
            }
        }

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