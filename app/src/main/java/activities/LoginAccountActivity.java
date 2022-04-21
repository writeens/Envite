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
import com.android.volley.VolleyError;
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

public class LoginAccountActivity extends AppCompatActivity {

    AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

    String BASE_URL = BuildConfig.API_BASE_URL;

    View buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.loginAccountToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // INITIALIZE VALIDATION
        mAwesomeValidation.addValidation(this, R.id.loginAccountEmail, Patterns.EMAIL_ADDRESS, R.string.emailInvalid);
        mAwesomeValidation.addValidation(this, R.id.loginAccountPassword, RegexTemplate.NOT_EMPTY, R.string.passwordRequired);
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

    public void onClickCreateOne (View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);
    }

    public void navigateToHome () {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onLogin (View view) {
        boolean isValid = mAwesomeValidation.validate();

        if(!isValid){
            return;
        }

        buttonView = view;

        TextInputLayout emailInputLayout = findViewById(R.id.loginAccountEmail);
        TextInputLayout passwordInputLayout = findViewById(R.id.loginAccountPassword);

        String email = emailInputLayout.getEditText().getText().toString();
        String password = passwordInputLayout.getEditText().getText().toString();

        JSONObject data = new JSONObject();
        try {
            data.put("email", email);
            data.put("password", password);

        } catch (JSONException e){
            Log.i("JSON", e.toString());
        }

       loginUser(data);
    }

    public void loginUser (JSONObject postData) {
        RequestQueue queue = Volley.newRequestQueue(this);
        Button loginAccountButton = (Button) findViewById(R.id.loginAccountButton);
        String loginURL = BASE_URL + "/login";
        loginAccountButton.setText("Please Wait");
        JsonObjectRequest loginUserRequest = new JsonObjectRequest
                (Request.Method.POST, loginURL, postData, response -> {
                    try {
                        loginAccountButton.setText("Login");

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
                    loginAccountButton.setText("Login");
                    if(error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);
                            Snackbar.make(buttonView, data.getString("message"), Snackbar.LENGTH_SHORT)
                                    .show();
                            loginAccountButton.setText("Login");
                        } catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });

        queue.add(loginUserRequest);
    }
}