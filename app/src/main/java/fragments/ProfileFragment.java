package fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bumptech.glide.Glide;
import com.example.envite.BuildConfig;
import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import activities.MainActivity;

public class ProfileFragment extends Fragment {

    File imageFile = null;
    ImageView profileImage;
    TextInputLayout firstNameInputLayout;
    TextInputLayout lastNameInputLayout;
    TextInputLayout emailInputLayout;
    TextInputLayout q1InputLayout;
    TextInputLayout q2InputLayout;
    Button updateProfileButton;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
    String BASE_URL = BuildConfig.API_BASE_URL;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // HANDLE BUTTON CLICKS
        Button changeProfileImage = (Button) rootView.findViewById(R.id.changeProfileImageButton);

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        // HANDLE HIDE NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.GONE);

        // ON CLICK UPDATE BUTTON
        Button update = (Button) rootView.findViewById(R.id.profileUpdateButton);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUpdateProfile(view);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // POPULATE FIELDS
        populateFields(view);

        // INITIALIZE VALIDATION
        mAwesomeValidation.addValidation(getActivity(), firstNameInputLayout.getId(), RegexTemplate.NOT_EMPTY, R.string.firstNameRequired);
        mAwesomeValidation.addValidation(getActivity(), lastNameInputLayout.getId(), RegexTemplate.NOT_EMPTY, R.string.lastNameRequired);
        mAwesomeValidation.addValidation(getActivity(), emailInputLayout.getId(), Patterns.EMAIL_ADDRESS, R.string.emailInvalid);
        mAwesomeValidation.addValidation(getActivity(), q1InputLayout.getId(), RegexTemplate.NOT_EMPTY, R.string.questionRequired);
        mAwesomeValidation.addValidation(getActivity(), q2InputLayout.getId(), RegexTemplate.NOT_EMPTY, R.string.questionRequired);

    }

    public void populateFields (View view) {
        profileImage = (ImageView) view.findViewById(R.id.profileImageView);
        firstNameInputLayout = (TextInputLayout) view.findViewById(R.id.profileFirstName);
        lastNameInputLayout = (TextInputLayout) view.findViewById(R.id.profileLastName);
        emailInputLayout = (TextInputLayout) view.findViewById(R.id.profileEmail);
        q1InputLayout = (TextInputLayout) view.findViewById(R.id.profileQuestion1);
        q2InputLayout = (TextInputLayout) view.findViewById(R.id.profileQuestion2);


        // GET DATA FROM SHARED CONTEXT
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);

        String firstName = sharedPref.getString(getString(R.string.sharedPrefFirstName), "");
        String lastName = sharedPref.getString(getString(R.string.sharedPrefLastName), "");
        String email = sharedPref.getString(getString(R.string.sharedPrefEmail), "");
        String profileUrl = sharedPref.getString(getString(R.string.sharedPrefProfileUrl), "");
        String q1 = sharedPref.getString(getString(R.string.sharedPrefQ1), "");
        String q2 = sharedPref.getString(getString(R.string.sharedPrefQ2), "");

        firstNameInputLayout.getEditText().setText(firstName);
        lastNameInputLayout.getEditText().setText(lastName);
        emailInputLayout.getEditText().setText(email);
        q1InputLayout.getEditText().setText(q1);
        q2InputLayout.getEditText().setText(q2);
        Glide.with(getContext()).load(profileUrl).into(profileImage);
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
                        Glide.with(getContext()).load(uri).into(profileImage);

                        // STORE IMAGE PATH IN VARIABLE
                        imageFile = file;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            });

    public void handleUpdateProfile (View view) {
        updateProfileButton = (Button) view;
        boolean isValid = mAwesomeValidation.validate();

        if(!isValid){
            return;
        }

        String firstName = firstNameInputLayout.getEditText().getText().toString();
        String lastName =lastNameInputLayout.getEditText().getText().toString();
        String email = emailInputLayout.getEditText().getText().toString();
        String q1 = q1InputLayout.getEditText().getText().toString();
        String q2 = q2InputLayout.getEditText().getText().toString();


        JSONObject data = new JSONObject();
        try {
            data.put("firstName", firstName);
            data.put("lastName", lastName);
            data.put("email", email);
            data.put("q1", q1);
            data.put("q2", q2);

        } catch (JSONException e){
            Log.i("JSON", e.toString());
        }

       updateProfileWithIon(data);
    }


    public void updateProfileWithIon(JSONObject postData) {
        try {
            String updateProfileURL = BASE_URL + "/user";
            updateProfileButton.setText("Please Wait");

            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);

            String token = sharedPref.getString(getString(R.string.sharedPrefToken), "");
            SharedPreferences.Editor editor = sharedPref.edit();


            if(imageFile == null){
                Ion.with(getContext())
                        .load("PATCH", updateProfileURL)
                        .setHeader("Authorization", "Bearer " + token)
                        .setMultipartParameter("firstName", postData.getString("firstName"))
                        .setMultipartParameter("lastName", postData.getString("lastName"))
                        .setMultipartParameter("email", postData.getString("email"))
                        .setMultipartParameter("q1", postData.getString("q1"))
                        .setMultipartParameter("q2", postData.getString("q2"))
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                updateProfileButton.setText("Update");
                                if(e instanceof FileNotFoundException){
                                    e.printStackTrace();
                                    return;
                                }

                                String status = result.get("status").getAsString();
                                JsonElement type = result.get("type");
                                JsonElement message = result.get("message");
                                if(type != null && type.getAsString().equals("FORBIDDEN")){
                                    Log.i("JD", "YES");
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            "Please login again", Snackbar.LENGTH_LONG).show();
                                    ((MainActivity) getActivity()).goToSignIn();
                                    return;
                                }

                                if(status.equals("fail")){
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            message.getAsString(), Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                                JsonObject data = result.get("data").getAsJsonObject();

                                editor.putString(getString(R.string.sharedPrefUid), data.get("uid").getAsString());
                                editor.putString(getString(R.string.sharedPrefEmail), data.get("email").getAsString());
                                editor.putString(getString(R.string.sharedPrefFirstName), data.get("firstName").getAsString());
                                editor.putString(getString(R.string.sharedPrefLastName), data.get("lastName").getAsString());
                                editor.putString(getString(R.string.sharedPrefProfileUrl), data.get("profileUrl").getAsString());
                                editor.putString(getString(R.string.sharedPrefCreatedAt), data.get("createdAt").getAsString());
                                editor.putString(getString(R.string.sharedPrefQ1), data.get("q1").getAsString());
                                editor.putString(getString(R.string.sharedPrefQ2), data.get("q2").getAsString());
                                editor.apply();

                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();
                                getActivity().onBackPressed();
                            }
                        });
            } else {
                Ion.with(getContext())
                        .load("PATCH", updateProfileURL)
                        .setHeader("Authorization", "Bearer " + token)
                        .setMultipartParameter("firstName", postData.getString("firstName"))
                        .setMultipartParameter("lastName", postData.getString("lastName"))
                        .setMultipartParameter("email", postData.getString("email"))
                        .setMultipartParameter("q1", postData.getString("q1"))
                        .setMultipartParameter("q2", postData.getString("q2"))
                        .setMultipartFile("image", "image/*", imageFile)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                updateProfileButton.setText("Update");
                                if(e instanceof FileNotFoundException){
                                    e.printStackTrace();
                                    return;
                                }

                                String status = result.get("status").getAsString();
                                JsonElement type = result.get("type");
                                JsonElement message = result.get("message");
                                if(type != null && type.getAsString().equals("FORBIDDEN")){
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            "Please login again", Snackbar.LENGTH_LONG).show();
                                    ((MainActivity) getActivity()).goToSignIn();
                                    return;
                                }

                                if(status.equals("fail")){
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            message.getAsString(), Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                                JsonObject data = result.get("data").getAsJsonObject();

                                editor.putString(getString(R.string.sharedPrefUid), data.get("uid").getAsString());
                                editor.putString(getString(R.string.sharedPrefEmail), data.get("email").getAsString());
                                editor.putString(getString(R.string.sharedPrefFirstName), data.get("firstName").getAsString());
                                editor.putString(getString(R.string.sharedPrefLastName), data.get("lastName").getAsString());
                                editor.putString(getString(R.string.sharedPrefProfileUrl), data.get("profileUrl").getAsString());
                                editor.putString(getString(R.string.sharedPrefCreatedAt), data.get("createdAt").getAsString());
                                editor.putString(getString(R.string.sharedPrefQ1), data.get("q1").getAsString());
                                editor.putString(getString(R.string.sharedPrefQ2), data.get("q2").getAsString());
                                editor.apply();

                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();
                                getActivity().onBackPressed();
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}