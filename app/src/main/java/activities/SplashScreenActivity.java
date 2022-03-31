package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envite.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    public void onClickSignUp (View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);
    }

    public void onClickLogIn (View view) {
        Intent intent = new Intent(getApplicationContext(), LoginAccountActivity.class);
        startActivity(intent);
    }
}