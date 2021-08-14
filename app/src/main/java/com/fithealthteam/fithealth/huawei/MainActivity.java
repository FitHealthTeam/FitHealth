package com.fithealthteam.fithealth.huawei;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fithealthteam.fithealth.huawei.databinding.ActivityMainBinding;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide the top action bar and title
        getSupportActionBar().hide();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), authenticateActivity.class);
            startActivity(intent);
        }else{
            Log.d("HMS Auth User", user.getEmail());
        }
        // if (not login) --> another user activity --
        //if (is user) --> login  --> come back to main activity (boolean newUser = false)
        //else --> register user --> store to Db --> go back to main activity (boolean newUser = true)

        //li-hao check for whether newUser is true --> if (newUser == true) --> go to create Diet plan activity to load data --> store to db --> intent back to here

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.exerciseFragment, R.id.communityFragment, R.id.settingsFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }


}
