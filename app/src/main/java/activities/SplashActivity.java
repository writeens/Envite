package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.envite.R;

import java.io.File;

import daos.EnviteDao;
import database.EnviteRoomDatabase;

public class SplashActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);

        String token = sharedPref.getString(getString(R.string.sharedPrefToken), "");

        if(token.isEmpty()){
            intent = new Intent(getApplicationContext(), OnboardingActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}