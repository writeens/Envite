package viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import entities.Envite;
import interfaces.VolleyCallbackForAdapters;
import interfaces.VolleyCallbackForEnviteDetails;
import repositories.EnviteRepository;

public class EnviteViewModel extends AndroidViewModel {

    private EnviteRepository enviteRepository;

    private LiveData<List<Envite>> myEnvites;
    private LiveData<List<Envite>> sentEnvites;
    private LiveData<List<Envite>> receivedEnvites;


    public EnviteViewModel (Application application) {
        super(application);
       enviteRepository = new EnviteRepository(application);
    }

    public LiveData<List<Envite>> getMyEnvites() {
        if(myEnvites == null){
            myEnvites = new MutableLiveData<>();
            myEnvites = enviteRepository.getMyEnvites();
        }
        return myEnvites;
    }

    public LiveData<List<Envite>> getSentEnvites() {
        if(sentEnvites == null){
            sentEnvites = new MutableLiveData<>();
            sentEnvites = enviteRepository.getSentEnvites();
        }
        return sentEnvites;
    }

    public LiveData<List<Envite>> getReceivedEnvites() {
        if(receivedEnvites == null){
            receivedEnvites = new MutableLiveData<>();
            receivedEnvites = enviteRepository.getReceivedEnvites();
        }
        return receivedEnvites;
    }



    public Integer getCountEnvites (String tag) {
        return enviteRepository.getRowCount(tag);
    }
    public void deleteAllEnvites (){
        enviteRepository.deleteAll();
    }
    public void fetchEnviteDetails (String enviteId, String tag, VolleyCallbackForEnviteDetails callback) {
        enviteRepository.fetchEnviteDetailsFromAPI(enviteId, tag, callback);
    }

    // HANDLE MY ENVITES
    public void getMyEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getMyEnvitesFromAPI(callback);
    }
    public void loadMoreEnvitesFromAPI(String tag, VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreEnvitesFromAPI(tag, callback);
    }

    // HANDLE SENT ENVITES
    public void getSentEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getSentEnvitesFromAPI(callback);
    }
    public void loadMoreSentEnvitesFromAPI(String tag, VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreSentEnvitesFromAPI(tag, callback);
    }

    // HANDLE RECEIVED ENVITES
    public void getReceivedEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getReceivedEnvitesFromAPI(callback);
    }
    public void loadMoreReceivedEnvitesFromAPI(String tag, VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreReceivedEnvitesFromAPI(tag, callback);
    }
}
