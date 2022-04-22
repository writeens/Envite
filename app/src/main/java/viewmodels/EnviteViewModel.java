package viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import entities.HomeEnvite;
import entities.MyEnvite;
import entities.ReceivedRequest;
import entities.SentRequest;
import interfaces.VolleyCallbackForAdapters;
import repositories.EnviteRepository;

public class EnviteViewModel extends AndroidViewModel {

    private EnviteRepository enviteRepository;

    private LiveData<List<MyEnvite>> myEnvites;
    private LiveData<List<ReceivedRequest>> receivedRequests;
    private LiveData<List<SentRequest>> sentRequests;
    private LiveData<List<HomeEnvite>> homeEnvites;



    public EnviteViewModel (Application application) {
        super(application);
       enviteRepository = new EnviteRepository(application);
    }

    public LiveData<List<MyEnvite>> getMyEnvites() {
        if(myEnvites == null){
            myEnvites = new MutableLiveData<>();
            myEnvites = enviteRepository.getMyEnvitesFromRoom();
        }
        return myEnvites;
    }

    // HANDLE MY ENVITES
    public void getMyEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getMyEnvitesFromAPI(callback);
    }
    public void loadMoreEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreEnvitesFromAPI(callback);
    }

    // COUNT MY ENVITES
    public Integer getCountEnvites () {
        return enviteRepository.getRowCountForMyEnvites();
    }

    // GET MY ENVITE BY ID
    public MyEnvite getMyEnviteById(String enviteId) {return enviteRepository.getMyEnvitesById(enviteId);}


    public LiveData<List<ReceivedRequest>> getReceivedRequests() {
        if(receivedRequests == null){
            receivedRequests = new MutableLiveData<>();
            receivedRequests = enviteRepository.getReceivedRequestsFromRoom();
        }
        return receivedRequests;
    }

    // HANDLE MY ENVITES
    public void getReceivedRequestsFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getReceivedRequestsFromAPI(callback);
    }
    public void loadMoreReceivedRequestsFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreReceivedRequestsFromAPI(callback);
    }

    // COUNT MY ENVITES
    public Integer getCountReceivedRequests () {
        return enviteRepository.getRowCountForReceivedRequests();
    }

    // GET RECEIVED REQUEST BY ID
    public ReceivedRequest getReceivedRequestById(String id) {return enviteRepository.getReceivedRequestById(id);}


    public LiveData<List<SentRequest>> getSentRequests() {
        if(sentRequests == null){
            sentRequests = new MutableLiveData<>();
            sentRequests = enviteRepository.getSentRequestsFromRoom();
        }
        return sentRequests;
    }

    // HANDLE SENT REQUESTS
    public void getSentRequestsFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getSentRequestsFromAPI(callback);
    }

    public void loadMoreSentRequestsFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreSentRequestsFromAPI(callback);
    }

    // COUNT MY ENVITES
    public Integer getCountSentRequests () {
        return enviteRepository.getRowCountForSentRequests();
    }

    // GET RECEIVED REQUEST BY ID
    public SentRequest getSentRequestById(String id) {return enviteRepository.getSentRequestById(id);}

    // ACCEPT A REQUEST
    public void acceptRequest (String requestId, VolleyCallbackForAdapters callback){
        enviteRepository.acceptRequest(requestId, callback);
    }
    // DECLINE A REQUEST
    public void declineRequest (String requestId, VolleyCallbackForAdapters callback){
        enviteRepository.declineRequest(requestId, callback);
    }

    // REQUEST AN ENVITE
    public void requestEnvite (String enviteId, VolleyCallbackForAdapters callback){
        enviteRepository.requestEnvite(enviteId, callback);
    }

    public LiveData<List<HomeEnvite>> getHomeEnvites() {
        if(homeEnvites == null){
            homeEnvites = new MutableLiveData<>();
            homeEnvites = enviteRepository.getHomeEnvitesFromRoom();
        }
        return homeEnvites;
    }

    // HANDLE MY ENVITES
    public void getHomeEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getHomeEnvitesFromAPI(callback);
    }
    public void loadMoreHomeEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreHomeEnvitesFromAPI(callback);
    }

    public void insertOneHomeEnvite(HomeEnvite homeEnvite){
        enviteRepository.insertOneHomeEnvite(homeEnvite);
    }

    // COUNT MY ENVITES
    public Integer getCountHomeEnvites () {
        return enviteRepository.getRowCountForHomeEnvites();
    }

    // GET RECEIVED REQUEST BY ID
    public HomeEnvite getHomeEnviteById(String id) {return enviteRepository.getHomeEnviteById(id);}

    // EMPTY DATABASE
    public void emptyDatabase() {
        enviteRepository.resetDatabase();
    }

}
