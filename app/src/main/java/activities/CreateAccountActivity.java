package activities;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.envite.BuildConfig;
import com.example.envite.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CreateAccountActivity extends AppCompatActivity {
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

    String BASE_URL = BuildConfig.API_BASE_URL;

    View buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.createAccountToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // INITIALIZE VALIDATION
        String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        mAwesomeValidation.addValidation(this, R.id.createAccountFirstName, RegexTemplate.NOT_EMPTY, R.string.firstNameRequired);
        mAwesomeValidation.addValidation(this, R.id.createAccountLastName, RegexTemplate.NOT_EMPTY, R.string.lastNameRequired);
        mAwesomeValidation.addValidation(this, R.id.createAccountEmail, Patterns.EMAIL_ADDRESS, R.string.emailInvalid);
        mAwesomeValidation.addValidation(this, R.id.createAccountPassword, regexPassword, R.string.passwordRequired);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void onClickLogIn (View view) {
        Intent intent = new Intent(getApplicationContext(), LoginAccountActivity.class);
        startActivity(intent);
    }

    public void navigateToHome () {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void onCreateAccount (View view) {
        boolean isValid = mAwesomeValidation.validate();

        if(!isValid){
            return;
        }
        buttonView = view;

        TextInputLayout firstNameInputLayout = findViewById(R.id.createAccountFirstName);
        TextInputLayout lastNameInputLayout = findViewById(R.id.createAccountLastName);
        TextInputLayout emailInputLayout = findViewById(R.id.createAccountEmail);
        TextInputLayout passwordInputLayout = findViewById(R.id.createAccountPassword);

        String firstName = firstNameInputLayout.getEditText().getText().toString();
        String lastName =lastNameInputLayout.getEditText().getText().toString();
        String email = emailInputLayout.getEditText().getText().toString();
        String password = passwordInputLayout.getEditText().getText().toString();

        JSONObject data = new JSONObject();
        try {
            data.put("firstName", firstName);
            data.put("lastName", lastName);
            data.put("email", email);
            data.put("password", password);

        } catch (JSONException e){
            Log.i("JSON", e.toString());
        }

        registerUser(data);
    }

    public void registerUser (JSONObject postData) {
        RequestQueue queue = Volley.newRequestQueue(this);
        Button createAccountButton = (Button) findViewById(R.id.loginAccountButton);
        String registerURL = BASE_URL + "/register";
        createAccountButton.setText("Please Wait");
        JsonObjectRequest registerUserRequest = new JsonObjectRequest
                (Request.Method.POST, registerURL, postData, response -> {
                    try {
                        createAccountButton.setText("Create Account");

                        Log.i("JSON", response.toString());

                        JSONObject data = (JSONObject) response.get("data");

                        // TODO - STORE USER DETAILS IN SHARED PREFERENCES
                        SharedPreferences sharedPref = getSharedPreferences(
                                getString(R.string.enviteUserSharedPreferencesFile),
                                Context.MODE_PRIVATE );

                        // UPDATE SHARED PREFERENCES WITH USER DATA
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.sharedPrefUid), data.getString("uid"));
                        editor.putString(getString(R.string.sharedPrefEmail), data.getString("email"));
                        editor.putString(getString(R.string.sharedPrefFirstName), data.getString("firstName"));
                        editor.putString(getString(R.string.sharedPrefLastName), data.getString("lastName"));
                        editor.putString(getString(R.string.sharedPrefProfileUrl), data.getString("profileUrl"));
                        editor.putString(getString(R.string.sharedPrefCreatedAt), data.getString("createdAt"));
                        editor.putString(getString(R.string.sharedPrefToken), data.getString("token"));
                        editor.putString(getString(R.string.sharedPrefQ1), data.getString("q1"));
                        editor.putString(getString(R.string.sharedPrefQ2), data.getString("q2"));
                        editor.apply();

                        navigateToHome();

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }, error -> {
                    createAccountButton.setText("Create Account");
                    if(error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);
                            Snackbar.make(buttonView, data.getString("message"), Snackbar.LENGTH_SHORT)
                                    .show();
                            createAccountButton.setText("Create Account");
                        } catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        queue.add(registerUserRequest);
    }

}