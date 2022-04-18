package viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.envite.R;

import java.util.ArrayList;
import java.util.List;

import entities.Envite;
import interfaces.VolleyCallbackForAdapters;
import repositories.EnviteRepository;

public class EnviteViewModel extends AndroidViewModel {

    private EnviteRepository enviteRepository;

    private LiveData<List<Envite>> myEnvites;

    public EnviteViewModel (Application application) {
        super(application);
       enviteRepository = new EnviteRepository(application);
    }

    public LiveData<List<Envite>> getMyEnvites() {
        if(myEnvites == null){
            myEnvites = new MutableLiveData<List<Envite>>();
            myEnvites = enviteRepository.getMyEnvites();
        }
        return myEnvites;
    }

    public void getMyEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.getMyEnvitesFromAPI(callback);
    }

    public Integer getCountOfMyEnvites () {
        return enviteRepository.getRowCount();
    }

    public void loadMoreEnvitesFromAPI(VolleyCallbackForAdapters callback) {
        enviteRepository.loadMoreEnvitesFromAPI(callback);
    }

    public void deleteAllEnvites (){
        enviteRepository.deleteAll();
    }

    public void insert(Envite envite) { enviteRepository.insert(envite); }
}
