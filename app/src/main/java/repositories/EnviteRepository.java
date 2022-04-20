package repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import daos.EnviteDao;
import daos.EnviteRequestDao;
import daos.MyEnvitesDao;
import daos.UserDao;
import database.EnviteRoomDatabase;
import entities.Envite;
import entities.EnviteRequest;
import entities.MyEnvites;
import entities.User;
import interfaces.VolleyCallbackForAdapters;
import interfaces.VolleyCallbackForEnviteDetails;

public class EnviteRepository {
//    private EnviteDao enviteDao;
//    private EnviteRequestDao enviteRequestDao;
//    private UserDao userDao;
    private MyEnvitesDao myEnvitesDao;

    private String token;
    private String uid;
    private Application application;
    String BASE_URL = BuildConfig.API_BASE_URL;

    public EnviteRepository(Application application) {
        EnviteRoomDatabase db = EnviteRoomDatabase.getDatabase(application);
//        enviteDao = db.enviteDao();
//        userDao = db.userDao();
//        enviteRequestDao = db.enviteRequestDao();
        myEnvitesDao = db.myEnvitesDao();
        SharedPreferences sharedPref = application.getSharedPreferences(application.getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);
        this.token = sharedPref.getString(application.getString(R.string.sharedPrefToken), "");
        this.uid = sharedPref.getString(application.getString(R.string.sharedPrefUid), "");
        this.application = application;
    }

    //GET MY ENVITES
    public LiveData<List<MyEnvites>> getMyEnvitesFromRoom (){
        return myEnvitesDao.fetchMyEnvites();
    }
    //INSERT MY ENVITE
    public void insertMyEnvite(MyEnvites myEnvites) {
        EnviteRoomDatabase.databaseWriteExecutor.execute(() -> {
            myEnvitesDao.insertMyEnvites(myEnvites);
        });
    }

    public MyEnvites getLastMyEnvites () {
        MyEnvites lastMyEnvites = null;
        try {
            lastMyEnvites = myEnvitesDao.getLastItemInMyEnvites().get();
        } catch (ExecutionException |InterruptedException e) {
            e.printStackTrace();
        }
        return lastMyEnvites;
    }

    // GET MY ENVITES FROM API/UPDATE ROOM
    public void getMyEnvitesFromAPI (VolleyCallbackForAdapters callback) {
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own";

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status =  response.getString("status");
                JSONArray data =  response.getJSONArray("data");

                for(int i=0; i<data.length(); i++){
                    JSONObject obj=data.getJSONObject(i);
                    Gson gson = new Gson();
                    MyEnvites myEnvites = gson.fromJson(String.valueOf(obj), MyEnvites.class);
                    // TODO - STORE DATA IN DB
                    insertMyEnvite(myEnvites);
                }
                callback.onSuccess(status);
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
    public void loadMoreEnvitesFromAPI (String tag, VolleyCallbackForAdapters callback) {
        MyEnvites oldestEnvite = getLastMyEnvites();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own?startAfter=" + oldestEnvite.getCreatedAt();

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status =  response.getString("status");
                JSONArray data =  response.getJSONArray("data");

                for(int i=0; i<data.length(); i++){
                    JSONObject obj=data.getJSONObject(i);
                    Gson gson = new Gson();
                    MyEnvites myEnvites = gson.fromJson(String.valueOf(obj), MyEnvites.class);
                    insertMyEnvite(myEnvites);
                }
                callback.onSuccess(status);
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

    // COUNT THE NUMBER OF ENVITES
    public Integer getRowCountForMyEnvites() {
        int count = 0;
        try {
            count = myEnvitesDao.getRowCount().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    public MyEnvites getMyEnvitesById(String enviteId) {
        MyEnvites myEnvites = null;
        try {
            myEnvites = myEnvitesDao.getMyEnviteById(enviteId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return myEnvites;
    }

//    public LiveData<List<Envite>> getMyEnvites() {
//        return enviteDao.fetchEnviteByDisplayTag("my_envites");
//    }
//
//    public LiveData<Map<EnviteRequest, List<Envite>>> getSentEnvites() {
//        return enviteRequestDao.fetchSentEnvites(uid);
//    }
//
//    public LiveData<Map<EnviteRequest, List<Envite>>> getReceivedEnvites() {
//        return enviteRequestDao.fetchReceivedEnvites(uid);
//    }


    // COUNT THE NUMBER OF ENVITES
//    public Integer getRowCount(String tag) {
//        int count = 0;
//        try {
//            count = enviteDao.getRowCount(tag).get();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return count;
//    }
//
//    // INSERT ENVITE
//    public void insertEnvite(Envite envite) {
//        EnviteRoomDatabase.databaseWriteExecutor.execute(() -> {
//            enviteDao.insertEnvite(envite);
//        });
//    }
//
//    // INSERT USER
//    public void insertUser(User user) {
//        EnviteRoomDatabase.databaseWriteExecutor.execute(() -> {
//            userDao.insertUser(user);
//        });
//    }
    // INSERT ENVITE REQUEST
//    public void insertEnviteRequest(EnviteRequest enviteRequest) {
//        EnviteRoomDatabase.databaseWriteExecutor.execute(() -> {
//            enviteRequestDao.insertEnviteRequest(enviteRequest);
//        });
//    }

    // GET OLDEST ENVITE
//    public Envite getOldestEnvite (String tag) {
//        Envite oldestEnvite = null;
//        try {
//            oldestEnvite = enviteDao.getOldestEnvite(tag).get();
//        } catch (ExecutionException |InterruptedException e) {
//            e.printStackTrace();
//        }
//        return oldestEnvite;
//    }

//    public void deleteAll() {
//        EnviteRoomDatabase.databaseWriteExecutor.execute(() -> {
//            enviteDao.deleteAllEnvites();
//        });
//    }

    // GET DETAILS FOR ENVITE
//    public void fetchEnviteDetailsFromAPI (String eid, String tag, VolleyCallbackForEnviteDetails callback) {
//        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
//        String fetchEnviteURL = BASE_URL + "/envite/" + eid;
//
//        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchEnviteURL, null, response -> {
//            try {
//                Gson gson = new Gson();
//                String status =  response.getString("status");
//                JSONObject data =  response.getJSONObject("data");
//                Boolean createdByUserObjIsNull = data.isNull("createdByUser");
//                Boolean requestObjIsNull = data.isNull("request");
//
//                Envite envite = gson.fromJson(String.valueOf(data), Envite.class);
//                envite.setDisplayTag(tag);
//                User createdByUser = null;
//                if(!createdByUserObjIsNull){
//                    JSONObject createdByUserObj =  data.getJSONObject("createdByUser");
//                    createdByUser = gson.fromJson(String.valueOf(createdByUserObj), User.class);
//                }
//                EnviteRequest enviteRequest = null;
//                if(!requestObjIsNull){
//                    JSONObject requestObj = data.getJSONObject("request");
//                    enviteRequest = gson.fromJson(String.valueOf(requestObj), EnviteRequest.class);
//                }
//
//                callback.onSuccess(status, envite, createdByUser, enviteRequest);
//
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//        }, error -> {
//            if(error.networkResponse.data != null) {
//                try {
//                    String responseBody = new String(error.networkResponse.data, "utf-8");
//                    JSONObject data = new JSONObject(responseBody);
//                    callback.onError(data.getString("message"),
//                            data.getString("type"),
//                            data.getString("status"));
//
//                } catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer " + token);
//                return params;
//            }
//        };
//        queue.add(fetchMyEnvitesRequest);
//    }



    // GET SENT ENVITES FROM API/UPDATE ROOM
//    public void getSentEnvitesFromAPI (VolleyCallbackForAdapters callback) {
//        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
//        String fetchMyEnvitesURL = BASE_URL + "/envites/sent";
//
//        JsonObjectRequest fetchSentEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
//            try {
//
//                String status =  response.getString("status");
//                JSONObject data =  response.getJSONObject("data");
//                JSONArray items = data.getJSONArray("items");
//
//                for(int i=0; i<items.length(); i++){
//                    JSONObject obj=items.getJSONObject(i);
//                    JSONObject enviteData = obj.getJSONObject("envite");
//                    JSONObject requestedByData = obj.getJSONObject("requestedBy");
//                    JSONObject requestedToData = obj.getJSONObject("requestedTo");
//                    JSONObject requestData = obj.getJSONObject("request");
//                    Gson gson = new Gson();
//                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
//                    envite.setDisplayTag("sent_envites");
//                    User requestedBy = gson.fromJson(String.valueOf(requestedByData), User.class);
//                    User requestedTo = gson.fromJson(String.valueOf(requestedToData), User.class);
//                    EnviteRequest request = gson.fromJson(String.valueOf(requestData), EnviteRequest.class);
//
//                    // TODO - STORE DATA IN DB
//                    insertEnvite(envite);
//                    insertUser(requestedBy);
//                    insertUser(requestedTo);
//                    insertEnviteRequest(request);
//                }
//                callback.onSuccess(status);
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//        }, error -> {
//            if(error.networkResponse.data != null) {
//                try {
//                    String responseBody = new String(error.networkResponse.data, "utf-8");
//                    JSONObject data = new JSONObject(responseBody);
//                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));
//
//                } catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer " + token);
//                return params;
//            }
//        };
//        queue.add(fetchSentEnvitesRequest);
//
//    }
//
//    // GET MORE OF SENT ENVITES/UPDATE ROOM
//    public void loadMoreSentEnvitesFromAPI (String tag, VolleyCallbackForAdapters callback) {
//        Envite oldestEnvite = getOldestEnvite(tag);
//        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
//        String fetchSentEnvitesURL = BASE_URL + "/envites/sent?startAfter=" + oldestEnvite.getCreatedAt();
//
//        JsonObjectRequest fetchSentEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchSentEnvitesURL, null, response -> {
//            try {
//
//                String status =  response.getString("status");
//                JSONObject data =  response.getJSONObject("data");
//                JSONArray items = data.getJSONArray("items");
//
//                for(int i=0; i<items.length(); i++){
//                    JSONObject obj=items.getJSONObject(i);
//                    JSONObject enviteData = obj.getJSONObject("envite");
//                    JSONObject requestedByData = obj.getJSONObject("requestedBy");
//                    JSONObject requestedToData = obj.getJSONObject("requestedTo");
//                    JSONObject requestData = obj.getJSONObject("request");
//                    Gson gson = new Gson();
//                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
//                    envite.setDisplayTag(tag);
//                    User requestedBy = gson.fromJson(String.valueOf(requestedByData), User.class);
//                    User requestedTo = gson.fromJson(String.valueOf(requestedToData), User.class);
//                    EnviteRequest request = gson.fromJson(String.valueOf(requestData), EnviteRequest.class);
//
//                    // TODO - STORE DATA IN DB
//                    insertEnvite(envite);
//                    insertUser(requestedBy);
//                    insertUser(requestedTo);
//                    insertEnviteRequest(request);
//
//                    callback.onSuccess(status);
//                }
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//        }, error -> {
//            if(error.networkResponse.data != null) {
//                try {
//                    String responseBody = new String(error.networkResponse.data, "utf-8");
//                    JSONObject data = new JSONObject(responseBody);
//                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));
//
//                } catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer " + token);
//                return params;
//            }
//        };
//        queue.add(fetchSentEnvitesRequest);
//
//    }
//
//    // GET RECEIVED ENVITES FROM API/UPDATE ROOM
//    public void getReceivedEnvitesFromAPI (VolleyCallbackForAdapters callback) {
//        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
//        String fetchReceivedEnvitesURL = BASE_URL + "/envites/received";
//
//        JsonObjectRequest fetchReceivedEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchReceivedEnvitesURL, null, response -> {
//            try {
//
//                String status =  response.getString("status");
//                JSONObject data =  response.getJSONObject("data");
//                JSONArray items = data.getJSONArray("items");
//
//                for(int i=0; i<items.length(); i++){
//                    JSONObject obj=items.getJSONObject(i);
//                    JSONObject enviteData = obj.getJSONObject("envite");
//                    JSONObject requestedByData = obj.getJSONObject("requestedBy");
//                    JSONObject requestedToData = obj.getJSONObject("requestedTo");
//                    JSONObject requestData = obj.getJSONObject("request");
//                    Gson gson = new Gson();
//                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
//                    envite.setDisplayTag("received_envites");
//                    User requestedBy = gson.fromJson(String.valueOf(requestedByData), User.class);
//                    User requestedTo = gson.fromJson(String.valueOf(requestedToData), User.class);
//                    EnviteRequest request = gson.fromJson(String.valueOf(requestData), EnviteRequest.class);
//
//                    // TODO - STORE DATA IN DB
//                    insertEnvite(envite);
//                    insertUser(requestedBy);
//                    insertUser(requestedTo);
//                    insertEnviteRequest(request);
//                }
//                callback.onSuccess(status);
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//        }, error -> {
//            if(error.networkResponse.data != null) {
//                try {
//                    String responseBody = new String(error.networkResponse.data, "utf-8");
//                    JSONObject data = new JSONObject(responseBody);
//                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));
//
//                } catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer " + token);
//                return params;
//            }
//        };
//        queue.add(fetchReceivedEnvitesRequest);
//
//    }
//
//    // GET RECEIVED ENVITES / UPDATE ROOM
//    public void loadMoreReceivedEnvitesFromAPI (String tag, VolleyCallbackForAdapters callback) {
//        Envite oldestEnvite = getOldestEnvite(tag);
//        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
//        String fetchReceivedEnvitesURL = BASE_URL + "/envites/received?startAfter=" + oldestEnvite.getCreatedAt();
//
//        JsonObjectRequest fetchReceivedEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchReceivedEnvitesURL, null, response -> {
//            try {
//
//                String status =  response.getString("status");
//                JSONObject data =  response.getJSONObject("data");
//                JSONArray items = data.getJSONArray("items");
//
//                for(int i=0; i<items.length(); i++){
//                    JSONObject obj=items.getJSONObject(i);
//                    JSONObject enviteData = obj.getJSONObject("envite");
//                    JSONObject requestedByData = obj.getJSONObject("requestedBy");
//                    JSONObject requestedToData = obj.getJSONObject("requestedTo");
//                    JSONObject requestData = obj.getJSONObject("request");
//                    Gson gson = new Gson();
//                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
//                    envite.setDisplayTag(tag);
//                    User requestedBy = gson.fromJson(String.valueOf(requestedByData), User.class);
//                    User requestedTo = gson.fromJson(String.valueOf(requestedToData), User.class);
//                    EnviteRequest request = gson.fromJson(String.valueOf(requestData), EnviteRequest.class);
//
//                    // TODO - STORE DATA IN DB
//                    insertEnvite(envite);
//                    insertUser(requestedBy);
//                    insertUser(requestedTo);
//                    insertEnviteRequest(request);
//                }
//                callback.onSuccess(status);
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//        }, error -> {
//            if(error.networkResponse.data != null) {
//                try {
//                    String responseBody = new String(error.networkResponse.data, "utf-8");
//                    JSONObject data = new JSONObject(responseBody);
//                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));
//
//                } catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer " + token);
//                return params;
//            }
//        };
//        queue.add(fetchReceivedEnvitesRequest);
//
//    }
}
