package viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;

import entities.Envite;
import entities.EnviteRequest;
import entities.MyEnvites;
import entities.ReceivedRequest;
import entities.SentRequest;
import interfaces.VolleyCallbackForAdapters;
import interfaces.VolleyCallbackForEnviteDetails;
import repositories.EnviteRepository;

public class EnviteViewModel extends AndroidViewModel {

    private EnviteRepository enviteRepository;

    private LiveData<List<MyEnvites>> myEnvites;
    private LiveData<List<ReceivedRequest>> receivedRequests;
    private LiveData<List<SentRequest>> sentRequests;



    public EnviteViewModel (Application application) {
        super(application);
       enviteRepository = new EnviteRepository(application);
    }

    public LiveData<List<MyEnvites>> getMyEnvites() {
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
    public void loadMoreEnvitesFromAPI(String tag, VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreEnvitesFromAPI(tag, callback);
    }

    // COUNT MY ENVITES
    public Integer getCountEnvites () {
        return enviteRepository.getRowCountForMyEnvites();
    }

    // GET MY ENVITE BY ID
    public MyEnvites getMyEnviteById(String enviteId) {return enviteRepository.getMyEnvitesById(enviteId);}


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
}
