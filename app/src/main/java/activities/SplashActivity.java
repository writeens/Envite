package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.envite.R;

public class SplashActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        this.emptySharedPref();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);

        String uid = sharedPref.getString(getString(R.string.sharedPrefToken), "");

        if(uid.isEmpty()){
            intent = new Intent(getApplicationContext(), OnboardingActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void emptySharedPref (){
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.enviteUserSharedPreferencesFile),
                Context.MODE_PRIVATE );

        // UPDATE SHARED PREFERENCES WITH USER DATA
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getString(R.string.sharedPrefUid));
        editor.remove(getString(R.string.sharedPrefEmail));
        editor.remove(getString(R.string.sharedPrefFirstName));
        editor.remove(getString(R.string.sharedPrefLastName));
        editor.remove(getString(R.string.sharedPrefProfileUrl));
        editor.remove(getString(R.string.sharedPrefCreatedAt));
        editor.remove(getString(R.string.sharedPrefToken));
        editor.apply();
    }
}