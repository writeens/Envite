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
import com.google.common.util.concurrent.ListenableFuture;
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

import daos.MyEnvitesDao;
import daos.ReceivedRequestDao;
import daos.SentRequestDao;
import database.EnviteRoomDatabase;
import entities.Envite;
import entities.MyEnvites;
import entities.ReceivedRequest;
import entities.SentRequest;
import entities.User;
import interfaces.VolleyCallbackForAdapters;

public class EnviteRepository {
    private MyEnvitesDao myEnvitesDao;
    private ReceivedRequestDao receivedRequestDao;
    private SentRequestDao sentRequestDao;


    private String token;
    private String uid;
    private Application application;
    String BASE_URL = BuildConfig.API_BASE_URL;

    public EnviteRepository(Application application) {
        EnviteRoomDatabase db = EnviteRoomDatabase.getDatabase(application);
        myEnvitesDao = db.myEnvitesDao();
        receivedRequestDao = db.receivedRequestDao();
        sentRequestDao = db.sentRequestDao();
        SharedPreferences sharedPref = application.getSharedPreferences(application.getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);
        this.token = sharedPref.getString(application.getString(R.string.sharedPrefToken), "");
        this.uid = sharedPref.getString(application.getString(R.string.sharedPrefUid), "");
        this.application = application;
    }

    //GET MY ENVITES
    public LiveData<List<MyEnvites>> getMyEnvitesFromRoom() {
        return myEnvitesDao.fetchAll();
    }

    //INSERT MY ENVITE
    public List<Long> insertMyEnvite(List<MyEnvites> myEnvites) {
        List<Long> id = null;
        try {
            id = myEnvitesDao.insert(myEnvites).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    private MyEnvites getLastMyEnvites() {
        MyEnvites lastMyEnvites = null;
        try {
            lastMyEnvites = myEnvitesDao.getLastItem().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return lastMyEnvites;
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
            myEnvites = myEnvitesDao.getById(enviteId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return myEnvites;
    }

    // GET MY ENVITES FROM API/UPDATE ROOM
    public void getMyEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own";

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                List<MyEnvites> allReceivedEnvites = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    Gson gson = new Gson();
                    MyEnvites myEnvites = gson.fromJson(String.valueOf(obj), MyEnvites.class);
                    allReceivedEnvites.add(myEnvites);

                }
                insertMyEnvite(allReceivedEnvites);
                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
    public void loadMoreEnvitesFromAPI(String tag, VolleyCallbackForAdapters callback) {
        MyEnvites oldestEnvite = getLastMyEnvites();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own?startAfter=" + oldestEnvite.getCreatedAt();

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                List<MyEnvites> allReceivedEnvites = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    Gson gson = new Gson();
                    MyEnvites myEnvites = gson.fromJson(String.valueOf(obj), MyEnvites.class);
                    allReceivedEnvites.add(myEnvites);
                }
                insertMyEnvite(allReceivedEnvites);
                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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


    // GET RECEIVED REQUESTS
    public LiveData<List<ReceivedRequest>> getReceivedRequestsFromRoom() {
        return receivedRequestDao.fetchAll();
    }

    // INSERT RECEIVED REQUESTS
    public List<Long> insertReceivedRequest(List<ReceivedRequest> receivedRequest) {
        List<Long> id = null;
        try {
            id = receivedRequestDao.insert(receivedRequest).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    private ReceivedRequest getLastReceivedRequest() {
        ReceivedRequest last = null;
        try {
            last = receivedRequestDao.getLastItem().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return last;
    }

    // COUNT THE NUMBER OF ENVITES
    public Integer getRowCountForReceivedRequests() {
        int count = 0;
        try {
            count = receivedRequestDao.getRowCount().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    public ReceivedRequest getReceivedRequestById(String id) {
        ReceivedRequest receivedRequest = null;
        try {
            receivedRequest = receivedRequestDao.getById(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return receivedRequest;
    }

    // GET RECEIVED ENVITES FROM API/UPDATE ROOM
    public void getReceivedRequestsFromAPI(VolleyCallbackForAdapters callback) {
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchReceivedEnvitesURL = BASE_URL + "/envites/received";

        JsonObjectRequest fetchReceivedEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchReceivedEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                Gson gson = new Gson();
                List<ReceivedRequest> allReceivedRequests = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    JSONObject enviteData = obj.getJSONObject("envite");
                    JSONObject requestedByData = obj.getJSONObject("requestedBy");
                    JSONObject requestData = obj.getJSONObject("request");
                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
                    User requestedBy = gson.fromJson(String.valueOf(requestedByData), User.class);
                    ReceivedRequest receivedRequest = gson.fromJson(String.valueOf(requestData), ReceivedRequest.class);
                    receivedRequest.setEnvite(envite);
                    receivedRequest.setRequestedBy(requestedBy);
                    allReceivedRequests.add(receivedRequest);
                }
                if(!allReceivedRequests.isEmpty()){
                    insertReceivedRequest(allReceivedRequests);
                }

                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
        queue.add(fetchReceivedEnvitesRequest);

    }

    // GET RECEIVED ENVITES / UPDATE ROOM
    public void loadMoreReceivedRequestsFromAPI(VolleyCallbackForAdapters callback) {
        ReceivedRequest last = getLastReceivedRequest();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchReceivedEnvitesURL = BASE_URL + "/envites/received?startAfter=" + last.getCreatedAt();

        JsonObjectRequest fetchReceivedEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchReceivedEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                Gson gson = new Gson();

                List<ReceivedRequest> allReceivedRequests = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    JSONObject enviteData = obj.getJSONObject("envite");
                    JSONObject requestedByData = obj.getJSONObject("requestedBy");
                    JSONObject requestData = obj.getJSONObject("request");
                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
                    User requestedBy = gson.fromJson(String.valueOf(requestedByData), User.class);
                    ReceivedRequest receivedRequest = gson.fromJson(String.valueOf(requestData), ReceivedRequest.class);
                    receivedRequest.setEnvite(envite);
                    receivedRequest.setRequestedBy(requestedBy);
                    allReceivedRequests.add(receivedRequest);
                }
                if(!allReceivedRequests.isEmpty()){
                    insertReceivedRequest(allReceivedRequests);
                }

                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
        queue.add(fetchReceivedEnvitesRequest);

    }


    // GET SENT REQUESTS
    public LiveData<List<SentRequest>> getSentRequestsFromRoom() {
        return sentRequestDao.fetchAll();
    }

    // INSERT SENT REQUESTS
    public List<Long> insertSentRequest(List<SentRequest> sentRequest) {
        List<Long> id = null;
        try {
            id = sentRequestDao.insert(sentRequest).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    private SentRequest getLastSentRequest() {
        SentRequest last = null;
        try {
            last = sentRequestDao.getLastItem().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return last;
    }

    // COUNT THE NUMBER OF SENT REQUESTS
    public Integer getRowCountForSentRequests() {
        int count = 0;
        try {
            count = sentRequestDao.getRowCount().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    public SentRequest getSentRequestById(String id) {
        SentRequest sentRequest = null;
        try {
            sentRequest = sentRequestDao.getById(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return sentRequest;
    }

    // GET SENT ENVITES FROM API/UPDATE ROOM
    public void getSentRequestsFromAPI(VolleyCallbackForAdapters callback) {
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchSentRequestURL = BASE_URL + "/envites/sent";

        JsonObjectRequest fetchSentEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchSentRequestURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                Gson gson = new Gson();
                List<SentRequest> allSentRequests = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    JSONObject enviteData = obj.getJSONObject("envite");
                    JSONObject requestedByData = obj.getJSONObject("requestedTo");
                    JSONObject requestData = obj.getJSONObject("request");
                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
                    User requestedTo = gson.fromJson(String.valueOf(requestedByData), User.class);
                    SentRequest sentRequest = gson.fromJson(String.valueOf(requestData), SentRequest.class);
                    sentRequest.setEnvite(envite);
                    sentRequest.setRequestedTo(requestedTo);
                    allSentRequests.add(sentRequest);
                }
                if(!allSentRequests.isEmpty()){
                    insertSentRequest(allSentRequests);
                }

                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
        queue.add(fetchSentEnvitesRequest);

    }

    // GET MORE SENT REQUESTS
    public void loadMoreSentRequestsFromAPI(VolleyCallbackForAdapters callback) {
        SentRequest last = getLastSentRequest();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchSentEnvitesURL = BASE_URL + "/envites/sent?startAfter=" + last.getCreatedAt();

        JsonObjectRequest fetchSentEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchSentEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                Gson gson = new Gson();

                List<SentRequest> allSentRequests = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    JSONObject enviteData = obj.getJSONObject("envite");
                    JSONObject requestedToData = obj.getJSONObject("requestedTo");
                    JSONObject requestData = obj.getJSONObject("request");
                    Envite envite = gson.fromJson(String.valueOf(enviteData), Envite.class);
                    User requestedTo = gson.fromJson(String.valueOf(requestedToData), User.class);
                    SentRequest sentRequest = gson.fromJson(String.valueOf(requestData), SentRequest.class);
                    sentRequest.setEnvite(envite);
                    sentRequest.setRequestedTo(requestedTo);
                    allSentRequests.add(sentRequest);
                }
                if(!allSentRequests.isEmpty()){
                    insertSentRequest(allSentRequests);
                }

                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
        queue.add(fetchSentEnvitesRequest);
    }
}