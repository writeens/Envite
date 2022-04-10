package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import activities.MainActivity;
import activities.SplashScreenActivity;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // HANDLE SHOW NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.VISIBLE);

        //HANDLE MY ENVITES BUTTON CLICK
        Button myEnvites = (Button) rootView.findViewById(R.id.myEnvitesButton);

        myEnvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);

                navController.navigate(R.id.action_settingsFragment_to_myEnviteFragment);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button viewProfile = (Button) view.findViewById(R.id.viewProfileButton);

        Button logOut = (Button) view.findViewById(R.id.logOutButton);


        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);

                navController.navigate(R.id.action_settingsFragment_to_profileFragment);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSplashScreen();
            }
        });
    }

    public void navigateToSplashScreen () {
        Intent intent = new Intent(getActivity().getApplicationContext(), SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}