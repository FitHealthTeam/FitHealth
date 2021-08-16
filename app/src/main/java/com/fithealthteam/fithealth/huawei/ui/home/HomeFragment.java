package com.fithealthteam.fithealth.huawei.ui.home;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fithealthteam.fithealth.huawei.BMIInput.BMIInput_Activity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.databinding.FragmentHomeBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.Locale;

public class HomeFragment extends Fragment {

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

    private int currentProgress = 0;
    private ProgressBar progressBar;
    private Button btnSet,btnTimePick;

    int hours,minutes;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /*homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;*/


        //view object pass in
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        // slider image
        mSliderView = v.findViewById(R.id.image_slider);

        SliderAdapter sliderAdapter = new SliderAdapter(images);

        mSliderView.setSliderAdapter(sliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.NONE);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.startAutoCycle();


        /*// calender view
        mCalenderView = v.findViewById(R.id.calendarView);
        dateSelected = v.findViewById(R.id.dateSelected);

        //mCalenderView.setDate(20210814);

        mCalenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateSelected.setText(date);
            }
        });*/


        // TO BMI Input Page
        editBMI = v.findViewById(R.id.editBMI);

        editBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BMIInput_Activity.class);
                startActivity(intent);
            }
        });

        // Retrieve BMI value

        TextView tvBMI = v.findViewById(R.id.tvBMIResult);

        //String passedBMI = getArguments().getString("BMI");
        //tvBMI.setText(passedBMI);

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

        // Notification
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}