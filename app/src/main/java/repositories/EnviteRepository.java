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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import daos.HomeEnviteDao;
import daos.MyEnviteDao;
import daos.ReceivedRequestDao;
import daos.SentRequestDao;
import database.EnviteRoomDatabase;
import entities.Envite;
import entities.HomeEnvite;
import entities.MyEnvite;
import entities.ReceivedRequest;
import entities.SentRequest;
import entities.User;
import interfaces.VolleyCallbackForAdapters;

public class EnviteRepository {
    private MyEnviteDao myEnviteDao;
    private ReceivedRequestDao receivedRequestDao;
    private SentRequestDao sentRequestDao;
    private HomeEnviteDao homeEnviteDao;

    private String token;
    private Application application;
    String BASE_URL = BuildConfig.API_BASE_URL;

    public EnviteRepository(Application application) {
        EnviteRoomDatabase db = EnviteRoomDatabase.getDatabase(application);
        myEnviteDao = db.myEnviteDao();
        receivedRequestDao = db.receivedRequestDao();
        sentRequestDao = db.sentRequestDao();
        homeEnviteDao = db.homeEnviteDao();
        SharedPreferences sharedPref = application.getSharedPreferences(application.getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);
        this.token = sharedPref.getString(application.getString(R.string.sharedPrefToken), "");
        this.application = application;
    }

    //GET MY ENVITES
    public LiveData<List<MyEnvite>> getMyEnvitesFromRoom() {
        return myEnviteDao.fetchAll();
    }

    //INSERT MY ENVITE
    public List<Long> insertMyEnvite(List<MyEnvite> myEnvites) {
        List<Long> id = null;
        try {
            id = myEnviteDao.insert(myEnvites).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    private MyEnvite getLastMyEnvites() {
        MyEnvite lastMyEnvite = null;
        try {
            lastMyEnvite = myEnviteDao.getLastItem().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return lastMyEnvite;
    }

    // COUNT THE NUMBER OF ENVITES
    public Integer getRowCountForMyEnvites() {
        int count = 0;
        try {
            count = myEnviteDao.getRowCount().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    public MyEnvite getMyEnvitesById(String enviteId) {
        MyEnvite myEnvite = null;
        try {
            myEnvite = myEnviteDao.getById(enviteId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return myEnvite;
    }

    // GET MY ENVITES FROM API/UPDATE ROOM
    public void getMyEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own";

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                List<MyEnvite> allReceivedRequests = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    Gson gson = new Gson();
                    MyEnvite myEnvite = gson.fromJson(String.valueOf(obj), MyEnvite.class);
                    allReceivedRequests.add(myEnvite);

                }
                insertMyEnvite(allReceivedRequests);
                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callback.onError(data.getString("message"), data.getString("type"), data.getString("status"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");

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
    public void loadMoreEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        MyEnvite oldestEnvite = getLastMyEnvites();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchMyEnvitesURL = BASE_URL + "/envites/own?startAfter=" + oldestEnvite.getCreatedAt();

        JsonObjectRequest fetchMyEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchMyEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                List<MyEnvite> allReceivedRequests = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    Gson gson = new Gson();
                    MyEnvite myEnvite = gson.fromJson(String.valueOf(obj), MyEnvite.class);
                    allReceivedRequests.add(myEnvite);
                }
                insertMyEnvite(allReceivedRequests);
                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");
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
        String fetchReceivedRequestsURL = BASE_URL + "/envites/received";

        JsonObjectRequest fetchReceivedRequestsRequest = new JsonObjectRequest(Request.Method.GET, fetchReceivedRequestsURL, null, response -> {
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
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchReceivedRequestsRequest);

    }

    // GET RECEIVED ENVITES / UPDATE ROOM
    public void loadMoreReceivedRequestsFromAPI(VolleyCallbackForAdapters callback) {
        ReceivedRequest last = getLastReceivedRequest();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchReceivedRequestsURL = BASE_URL + "/envites/received?startAfter=" + last.getCreatedAt();

        JsonObjectRequest fetchReceivedRequestsRequest = new JsonObjectRequest(Request.Method.GET, fetchReceivedRequestsURL, null, response -> {
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
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchReceivedRequestsRequest);

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

        JsonObjectRequest fetchSentRequestsRequest = new JsonObjectRequest(Request.Method.GET, fetchSentRequestURL, null, response -> {
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
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchSentRequestsRequest);

    }

    // GET MORE SENT REQUESTS
    public void loadMoreSentRequestsFromAPI(VolleyCallbackForAdapters callback) {
        SentRequest last = getLastSentRequest();
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchSentRequestsURL = BASE_URL + "/envites/sent?startAfter=" + last.getCreatedAt();

        JsonObjectRequest fetchSentRequestsRequest = new JsonObjectRequest(Request.Method.GET, fetchSentRequestsURL, null, response -> {
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
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchSentRequestsRequest);
    }

    // GET HOME ENVITES
    public LiveData<List<HomeEnvite>> getHomeEnvitesFromRoom() {
        return homeEnviteDao.fetchAll();
    }

    // INSERT HOME ENVITES
    public List<Long> insertHomeEnvites(List<HomeEnvite> homeEnvites) {
        List<Long> id = null;
        try {
            id = homeEnviteDao.insert(homeEnvites).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    // INSERT ONE HOME ENVITES
    public Long insertOneHomeEnvite(HomeEnvite homeEnvite) {
        Long id = null;
        try {
            id = homeEnviteDao.insertOne(homeEnvite).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    private HomeEnvite getLastHomeEnvite() {
        HomeEnvite last = null;
        try {
            last = homeEnviteDao.getLastItem().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return last;
    }

    // COUNT THE NUMBER OF HOME ENVITES
    public Integer getRowCountForHomeEnvites() {
        int count = 0;
        try {
            count = homeEnviteDao.getRowCount().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    public HomeEnvite getHomeEnviteById(String id) {
        HomeEnvite homeEnvite = null;
        try {
            homeEnvite = homeEnviteDao.getById(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return homeEnvite;
    }

    private Integer deleteAllHomeEnvites() {
        Integer ids = null;
        try {
            ids = homeEnviteDao.deleteAll().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return ids;
    }

    // GET HOME ENVITE FROM API/UPDATE ROOM
    public void getHomeEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchHomeEnvitesURL = BASE_URL + "/envites";

        JsonObjectRequest fetchHomeEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchHomeEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                Gson gson = new Gson();
                List<HomeEnvite> allHomeEnvites = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    JSONObject homeEnviteData = obj.getJSONObject("envite");
                    JSONObject createdByUserData = obj.getJSONObject("createdByUser");
                    String statusData = obj.getString("status");
                    User createdByUser = gson.fromJson(String.valueOf(createdByUserData), User.class);
                    HomeEnvite homeEnvite = gson.fromJson(String.valueOf(homeEnviteData), HomeEnvite.class);
                    homeEnvite.setCreatedByUser(createdByUser);
                    homeEnvite.setStatus(statusData);
                    allHomeEnvites.add(homeEnvite);
                }
                deleteAllHomeEnvites();
                if(!allHomeEnvites.isEmpty()){
                    insertHomeEnvites(allHomeEnvites);
                }

                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchHomeEnvitesRequest);

    }

    // GET HOME ENVITES / UPDATE ROOM
    public void loadMoreHomeEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        HomeEnvite last = getLastHomeEnvite();
        Log.i("TEST", last.getTitle());
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String fetchHomeEnvitesURL = BASE_URL + "/envites?startAfter=" + last.getCreatedAt();

        JsonObjectRequest fetchHomeEnvitesRequest = new JsonObjectRequest(Request.Method.GET, fetchHomeEnvitesURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONArray data = response.getJSONArray("data");
                Gson gson = new Gson();
                List<HomeEnvite> allHomeEnvites = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    JSONObject homeEnviteData = obj.getJSONObject("envite");
                    JSONObject createdByUserData = obj.getJSONObject("createdByUser");
                    String statusData = obj.getString("status");
                    User createdByUser = gson.fromJson(String.valueOf(createdByUserData), User.class);
                    HomeEnvite homeEnvite = gson.fromJson(String.valueOf(homeEnviteData), HomeEnvite.class);
                    homeEnvite.setCreatedByUser(createdByUser);
                    homeEnvite.setStatus(statusData);
                    allHomeEnvites.add(homeEnvite);
                }
                if(!allHomeEnvites.isEmpty()){
                    insertHomeEnvites(allHomeEnvites);
                }

                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(fetchHomeEnvitesRequest);

    }

    // ACCEPT A REQUEST
    public void acceptRequest(String requestId, VolleyCallbackForAdapters callback) {
        ReceivedRequest receivedRequest = getReceivedRequestById(requestId);
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String acceptRequestURL = BASE_URL + "/envite/" + requestId + "/accept";

        JsonObjectRequest acceptRequestRequest = new JsonObjectRequest(Request.Method.POST, acceptRequestURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONObject data = response.getJSONObject("data");
                String requestStatus = data.getString("requestStatus");
                Integer updatedAt = data.getInt("updatedAt");

                receivedRequest.setStatus(requestStatus);
                receivedRequest.setUpdatedAt(updatedAt);
                List<ReceivedRequest> allReceivedRequests = new ArrayList<>();
                allReceivedRequests.add(receivedRequest);

                insertReceivedRequest(allReceivedRequests);
                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(acceptRequestRequest);
    }

    // DECLINE A REQUEST
    public void declineRequest(String requestId, VolleyCallbackForAdapters callback) {
        ReceivedRequest receivedRequest = getReceivedRequestById(requestId);
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String declineRequestURL = BASE_URL + "/envite/" + requestId + "/decline";

        JsonObjectRequest declineRequestRequest = new JsonObjectRequest(Request.Method.POST, declineRequestURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONObject data = response.getJSONObject("data");
                String requestStatus = data.getString("requestStatus");
                Integer updatedAt = data.getInt("updatedAt");

                receivedRequest.setStatus(requestStatus);
                receivedRequest.setUpdatedAt(updatedAt);
                List<ReceivedRequest> allReceivedRequests = new ArrayList<>();
                allReceivedRequests.add(receivedRequest);

                insertReceivedRequest(allReceivedRequests);
                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(declineRequestRequest);
    }

    // REQUEST AN ENVITE
    public void requestEnvite(String enviteId, VolleyCallbackForAdapters callback) {
        HomeEnvite homeEnvite = getHomeEnviteById(enviteId);
        RequestQueue queue = Volley.newRequestQueue(application.getApplicationContext());
        String requestEnviteURL = BASE_URL + "/envite/" + enviteId + "/request";

        JsonObjectRequest requestEnviteRequest = new JsonObjectRequest(Request.Method.POST, requestEnviteURL, null, response -> {
            try {

                String status = response.getString("status");
                JSONObject data = response.getJSONObject("data");
                String requestStatus = data.getString("requestStatus");

                homeEnvite.setStatus(requestStatus);
                List<HomeEnvite> allHomeEnvites = new ArrayList<>();
                allHomeEnvites.add(homeEnvite);

                insertHomeEnvites(allHomeEnvites);
                callback.onSuccess(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
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
            callback.onError("Network Error, Check your connection and try again", "NETWORK_ERROR", "fail");

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(requestEnviteRequest);
    }
}