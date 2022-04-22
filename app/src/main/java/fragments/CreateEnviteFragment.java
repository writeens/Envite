package fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bumptech.glide.Glide;
import com.example.envite.BuildConfig;
import com.example.envite.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import activities.MainActivity;
import entities.HomeEnvite;
import entities.User;
import viewmodels.EnviteViewModel;

public class CreateEnviteFragment extends Fragment {

    File imageFile = null;
    ImageView enviteImage;
    String location = null;
    String placeId = null;
    private TextInputLayout titleInputLayout;
    private TextInputLayout noteInputLayout;
    private TextInputLayout priceInputLayout;
    private Button createEnviteButton;
    private Button selectImageButton;
    private EnviteViewModel enviteViewModel;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
    String BASE_URL = BuildConfig.API_BASE_URL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the SDK
        String apiKey = BuildConfig.MAPS_API_KEY;
        Places.initialize(getActivity().getApplicationContext(), apiKey);
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_envite, container, false);

        //INITIALIZE VIEW
        initializeViews(rootView);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        // HANDLE HIDE NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.GONE);

        //INITIALIZE VIEW MODEL
        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        // HANDLE ON CLICK CREATE ENVITE
        createEnviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCreateEnvite(view);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // INITIALIZE VALIDATION
        mAwesomeValidation.addValidation(getActivity(), titleInputLayout.getId(), RegexTemplate.NOT_EMPTY, R.string.titleRequired);
        mAwesomeValidation.addValidation(getActivity(), noteInputLayout.getId(), RegexTemplate.NOT_EMPTY, R.string.noteRequired);
        mAwesomeValidation.addValidation(getActivity(), priceInputLayout.getId(), RegexTemplate.NOT_EMPTY, R.string.priceRequired);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_location_fragment);
        EditText searchBox = autocompleteFragment.getView()
                .findViewById(R.id.places_autocomplete_search_input);
        searchBox.setTextColor(Color.WHITE);
        searchBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS));

        // Set Location Bias
        autocompleteFragment.setCountries("GB");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                location = place.getName();
                placeId = place.getId();
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Place not selected, please try again", Snackbar.LENGTH_LONG).show();
                return;
            }
        });

    }

    private void initializeViews (View rootView) {
        titleInputLayout = rootView.findViewById(R.id.enviteTitle);
        noteInputLayout = rootView.findViewById(R.id.enviteNote);
        priceInputLayout = rootView.findViewById(R.id.envitePrice);
        createEnviteButton = rootView.findViewById(R.id.createEnviteButton);
        selectImageButton = rootView.findViewById(R.id.changeEnviteImageButton);
        enviteImage = rootView.findViewById(R.id.enviteImageView);

    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri

                    final InputStream imageStream;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(uri);
                        File file = new File(getContext().getCacheDir(), "profileUrl.png");
                        try (OutputStream output = new FileOutputStream(file)) {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = imageStream.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }

                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // SET IMAGE VIEW
                        Glide.with(getContext()).load(uri).into(enviteImage);

                        // STORE IMAGE PATH IN VARIABLE
                        imageFile = file;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            });

    private void handleCreateEnvite (View view) {
        createEnviteButton = (Button) view;
        boolean isValid = mAwesomeValidation.validate();
        if(!isValid){
            return;
        }

        if(imageFile == null){
            Snackbar.make(view,
                    "Please select an image", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(location == null || placeId == null){
            Snackbar.make(view,
                    "Please select a valid location", Snackbar.LENGTH_LONG).show();
            return;
        }

        String title = titleInputLayout.getEditText().getText().toString();
        String note = noteInputLayout.getEditText().getText().toString();
        String price = priceInputLayout.getEditText().getText().toString();


        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("location", location);
            data.put("placeId", placeId);
            data.put("note", note);
            data.put("price", price);

        } catch (JSONException e){
            Log.i("JSON", e.toString());
        }

        Log.i("DATA TO SEND", data.toString());
        createEnviteWithIon(data);
    }



    private void createEnviteWithIon (JSONObject postData) {
        try {
            String createEnviteURL = BASE_URL + "/envite";
            createEnviteButton.setText("Please Wait");

            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);

            String token = sharedPref.getString(getString(R.string.sharedPrefToken), "");

            Ion.with(getContext())
                    .load("POST", createEnviteURL)
                    .setHeader("Authorization", "Bearer " + token)
                    .setMultipartParameter("title", postData.getString("title"))
                    .setMultipartParameter("location", postData.getString("location"))
                    .setMultipartParameter("placeId", postData.getString("placeId"))
                    .setMultipartParameter("note", postData.getString("note"))
                    .setMultipartParameter("price", postData.getString("price"))
                    .setMultipartFile("image", "image/*", imageFile)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            createEnviteButton.setText("Create Envite");
                            if(e instanceof FileNotFoundException){
                                e.printStackTrace();
                                return;
                            }
                            Gson gson = new Gson();
                            String status = result.get("status").getAsString();

                            if(status.equals("fail") && result.get("type").getAsString().equals("FORBIDDEN")){
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Please login again", Snackbar.LENGTH_LONG).show();
                                ((MainActivity) getActivity()).goToSignIn();
                                return;
                            }

                            if(status.equals("fail")){
                                String message = result.get("message").getAsString();
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        message, Snackbar.LENGTH_LONG).show();
                                return;
                            }


                            JsonObject homeEnviteData = result.getAsJsonObject("data");

                            JsonObject enviteData = homeEnviteData.getAsJsonObject("envite");
                            JsonObject createdByUserData = homeEnviteData.getAsJsonObject("createdBy");
                            String enviteStatus = homeEnviteData.get("status").getAsString();

                            User createdByUser = gson.fromJson(String.valueOf(createdByUserData), User.class);
                            HomeEnvite homeEnvite = gson.fromJson(String.valueOf(enviteData), HomeEnvite.class);
                            homeEnvite.setCreatedByUser(createdByUser);
                            homeEnvite.setStatus(enviteStatus);

                            enviteViewModel.insertOneHomeEnvite(homeEnvite);

                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "Envite Created Successfully", Snackbar.LENGTH_LONG).show();
                            getActivity().onBackPressed();

                        }
                    });
        } catch (JSONException e){

        }

    }
}