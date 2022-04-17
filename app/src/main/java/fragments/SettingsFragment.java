package fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import activities.MainActivity;
import activities.OnboardingActivity;

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

        // POPULATE FIELDS
        populateViewFields(rootView);

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
                ((MainActivity) getActivity()).logOut();
            }
        });
    }

    public void populateViewFields (View view) {
        ImageView imageContainer = (ImageView) view.findViewById(R.id.settingsImageView);
        TextView fullNameContainer = (TextView) view.findViewById(R.id.settingsFullNameTextView);
        TextView emailContainer = (TextView) view.findViewById(R.id.settingsEmailTextView);

        // GET DATA FROM SHARED CONTEXT
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);

        String firstName = sharedPref.getString(getString(R.string.sharedPrefFirstName), "");
        String lastName = sharedPref.getString(getString(R.string.sharedPrefLastName), "");
        String email = sharedPref.getString(getString(R.string.sharedPrefEmail), "");
        String profileUrl = sharedPref.getString(getString(R.string.sharedPrefProfileUrl), "");

        // SET FIELD VALUES
        Glide.with(getContext()).load(profileUrl).into(imageContainer);
        fullNameContainer.setText(firstName + " " + lastName);
        emailContainer.setText(email);
    }
}