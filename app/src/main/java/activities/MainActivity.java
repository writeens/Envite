package activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //MAKE FULL SCREEN
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();

        //CONNECT BOTTOM NAVIGATION TO NAVIGATION CONTROLLER
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //ADD LISTENER TO BOTTOM NAVIGATION LISTENER
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeFragment:
                        navController.navigate(R.id.homeFragment);
                        return true;
                    case R.id.enviteFragment:
                        navController.navigate(R.id.enviteFragment);
                        return true;
                    case R.id.settingsFragment:
                        navController.navigate(R.id.settingsFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void logOut () {

        Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        emptySharedPref();
        startActivity(intent);
    }

    public void goToSignIn () {
        Snackbar.make(this.findViewById(android.R.id.content),
                "Please login again", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LoginAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        emptySharedPref();
        startActivity(intent);
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
        editor.remove(getString(R.string.sharedPrefQ1));
        editor.remove(getString(R.string.sharedPrefQ2));

        editor.apply();
    }
}