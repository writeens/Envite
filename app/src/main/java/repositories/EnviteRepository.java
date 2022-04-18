package repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.envite.BuildConfig;
import com.example.envite.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import daos.EnviteDao;
import database.EnviteRoomDatabase;
import entities.Envite;
import entities.EnviteRequest;
import entities.User;
import interfaces.VolleyCallbackForAdapters;
import interfaces.VolleyCallbackForEnviteDetails;

public class EnviteRepository {
    private EnviteDao enviteDao;
    private LiveData<List<Envite>> myEnvites;
    private String token;
    private Application application;
    String BASE_URL = BuildConfig.API_BASE_URL;

    public EnviteRepository(Application application) {
        EnviteRoomDatabase db = EnviteRoomDatabase.getDatabase(application);
        enviteDao = db.enviteDao();
        myEnvites = enviteDao.fetchEnviteByDisplayTag("my_envites");
        SharedPreferences sharedPref = application.getSharedPreferences(application.getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);
        this.token = sharedPref.getString(application.getString(R.string.sharedPrefToken), "");
        this.application = application;
    }

    public LiveData<List<Envite>> getMyEnvites() {
        return myEnvites;
    }

    public Integer getRowCount() {
        int count = 0;
        try {
            count = enviteDao.getRowCount().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void insert(Envite envite) {
        EnviteRoomDatabase.databaseWriteExecutor.execute(() -> {
            enviteDao.insertEnvite(envite);
        });
    }

    public Envite getOldestEnvite () {
        Envite oldestEnvite = null;
        try {
            oldestEnvite = enviteDao.getOldestEnvite("my_envites").get();
        } catch (ExecutionException |InterruptedException e) {
            e.printStackTrace();
        }
        return oldestEnvite;
    }

    public void deleteAll() {
        EnviteRoomDatabase.databaseWriteExecutor.execute(() -> {
            enviteDao.deleteAllEnvites();
        });
    }

    // GET MY ENVITES FROM API/UPDATE ROOM
    public void getMyEnvitesFromAPI (VolleyCallbackForAdapters callback) {
        String BASE_URL = BuildConfig.API_BASE_URL;
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own";

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status =  response.getString("status");
                JSONObject data =  response.getJSONObject("data");
                        JSONArray items = data.getJSONArray("items");

                        Log.i("TEST", data.toString());
                        Log.i("TESTING", items.toString());

                for(int i=0; i<items.length(); i++){
                    JSONObject obj=items.getJSONObject(i);
                    Gson gson = new Gson();
                    Envite envite = gson.fromJson(String.valueOf(obj), Envite.class);
                    envite.setDisplayTag("my_envites");
                            // TODO - STORE DATA IN DB
                            insert(envite);

                            callback.onSuccess(status);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }, error -> {
                    if(error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);
                            callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                        } catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchMyEnvitesRequest);

    }

    // GET MORE OF MY ENVITES/UPDATE ROOM
    public void loadMoreEnvitesFromAPI (VolleyCallbackForAdapters callback) {

        Envite oldestEnvite = getOldestEnvite();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own?startAfter=" + oldestEnvite.getCreatedAt();
        Log.i("FETCH URL", fetchMyEnvitesURL);

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status =  response.getString("status");
                JSONObject data =  response.getJSONObject("data");
                JSONArray items = data.getJSONArray("items");

                Log.i("TEST", data.toString());
                Log.i("TESTING", items.toString());

                for(int i=0; i<items.length(); i++){
                    JSONObject obj=items.getJSONObject(i);
                    Gson gson = new Gson();
                    Envite envite = gson.fromJson(String.valueOf(obj), Envite.class);
                    envite.setDisplayTag("my_envites");
                    // TODO - STORE DATA IN DB
                    insert(envite);

                    callback.onSuccess(status);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }, error -> {
            if(error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchMyEnvitesRequest);

    }

    // GET DETAILS FOR ENVITE
    public void fetchEnviteDetailsFromAPI (String eid, String tag, VolleyCallbackForEnviteDetails callback) {
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchEnviteURL = BASE_URL + "/envite/" + eid;
        Log.i("FETCH URL", fetchEnviteURL);

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchEnviteURL, null, response -> {
            try {
                Gson gson = new Gson();
                String status =  response.getString("status");
                JSONObject data =  response.getJSONObject("data");
                Boolean createdByUserObjIsNull = data.isNull("createdByUser");
                Boolean requestObjIsNull = data.isNull("request");

                Envite envite = gson.fromJson(String.valueOf(data), Envite.class);
                envite.setDisplayTag(tag);
                User createdByUser = null;
                if(!createdByUserObjIsNull){
                    JSONObject createdByUserObj =  data.getJSONObject("createdByUser");
                    createdByUser = gson.fromJson(String.valueOf(createdByUserObj), User.class);
                }
                EnviteRequest enviteRequest = null;
                if(!requestObjIsNull){
                    JSONObject requestObj = data.getJSONObject("request");
                    enviteRequest = gson.fromJson(String.valueOf(requestObj), EnviteRequest.class);
                }

                callback.onSuccess(status, envite, createdByUser, enviteRequest);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }, error -> {
            if(error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"),
                            data.getString("type"),
                            data.getString("status"));

                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchMyEnvitesRequest);
    }
}
