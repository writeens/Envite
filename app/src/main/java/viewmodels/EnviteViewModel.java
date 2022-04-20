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
import interfaces.VolleyCallbackForAdapters;
import interfaces.VolleyCallbackForEnviteDetails;
import repositories.EnviteRepository;

public class EnviteViewModel extends AndroidViewModel {

    private EnviteRepository enviteRepository;

    private LiveData<List<MyEnvites>> myEnvites;
//    private LiveData<Map<EnviteRequest, List<Envite>>> sentEnvites;
//    private LiveData<Map<EnviteRequest, List<Envite>>> receivedEnvites;


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

    //COUNT MY ENVITES
    public Integer getCountEnvites () {
        return enviteRepository.getRowCountForMyEnvites();
    }

    // GET MYENVITE BY ID
    public MyEnvites getMyEnviteById(String enviteId) {return enviteRepository.getMyEnvitesById(enviteId);}

//    public LiveData<Map<EnviteRequest, List<Envite>>> getSentEnvites() {
//        if(sentEnvites == null){
//            sentEnvites = new MutableLiveData<>();
//            sentEnvites = enviteRepository.getSentEnvites();
//        }
//        return sentEnvites;
//    }
//
//    public LiveData<Map<EnviteRequest, List<Envite>>> getReceivedEnvites() {
//        if(receivedEnvites == null){
//            receivedEnvites = new MutableLiveData<>();
//            receivedEnvites = enviteRepository.getReceivedEnvites();
//        }
//        return receivedEnvites;
//    }

//    public Integer getCountEnvites (String tag) {
//        return enviteRepository.getRowCount(tag);
//    }
//    public void deleteAllEnvites (){
//        enviteRepository.deleteAll();
//    }
//    public void fetchEnviteDetails (String enviteId, String tag, VolleyCallbackForEnviteDetails callback) {
//        enviteRepository.fetchEnviteDetailsFromAPI(enviteId, tag, callback);
//    }




//    // HANDLE SENT ENVITES
//    public void getSentEnvitesFromAPI(VolleyCallbackForAdapters callback) {
//        enviteRepository.getSentEnvitesFromAPI(callback);
//    }
//    public void loadMoreSentEnvitesFromAPI(String tag, VolleyCallbackForAdapters callback) {
//        enviteRepository.loadMoreSentEnvitesFromAPI(tag, callback);
//    }
//
//    // HANDLE RECEIVED ENVITES
//    public void getReceivedEnvitesFromAPI(VolleyCallbackForAdapters callback) {
//        enviteRepository.getReceivedEnvitesFromAPI(callback);
//    }
//    public void loadMoreReceivedEnvitesFromAPI(String tag, VolleyCallbackForAdapters callback) {
//        enviteRepository.loadMoreReceivedEnvitesFromAPI(tag, callback);
//    }
}
